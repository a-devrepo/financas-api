package br.com.financasweb.controllers;

import br.com.financasweb.configurations.AbstractIntegrationTest;
import br.com.financasweb.dtos.CategoriaRequest;
import br.com.financasweb.dtos.CategoriaResponse;
import br.com.financasweb.dtos.MovimentacaoRequest;
import br.com.financasweb.dtos.MovimentacaoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MovimentacaoControllerTest extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	private UUID categoriaId;

	@BeforeEach
	void setUp() throws Exception {
		// Criar uma categoria para usar nos testes
		var categoriaRequest = new CategoriaRequest("Categoria teste");
		var result = mockMvc.perform(
				post("/api/v1/categorias")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(categoriaRequest)))
			.andExpect(status().isCreated())
			.andReturn();

		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var response = objectMapper.readValue(jsonContent, CategoriaResponse.class);
		categoriaId = response.id();
	}

	@Test
	@DisplayName("Deve criar uma movimentação com sucesso")
	void deveCriarUmaMovimentacaoComSucesso() throws Exception {

		var request = new MovimentacaoRequest(
			"Movimentação teste",
			LocalDate.now(),
			100.0,
			"RECEITA",
			categoriaId
		);

		var result = mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andReturn();

		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var response = objectMapper.readValue(jsonContent, MovimentacaoResponse.class);

		assertNotNull(response.id());
		assertEquals(request.nome(), response.nome());
		assertEquals(request.valor(), response.valor());
		assertEquals(request.tipo(), response.tipo());
		assertEquals(request.data(), response.data());
	}

	@Test
	@DisplayName("Deve retornar 404 ao criar movimentação com categoria inexistente")
	void deveRetornar404AoCriarMovimentacaoComCategoriaInexistente() throws Exception {

		var request = new MovimentacaoRequest(
			"Movimentação teste",
			LocalDate.now(),
			100.0,
			"RECEITA",
			UUID.randomUUID()
		);

		var result = mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("Categoria não encontrada"));
	}

	@Test
	@DisplayName("Deve retornar erro ao criar movimentação com nome vazio")
	void deveRetornarErroAoCriarMovimentacaoComNomeVazio() throws Exception {

		var request = new MovimentacaoRequest(
			"",
			LocalDate.now(),
			100.0,
			"RECEITA",
			categoriaId
		);

		var result = mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("O nome da movimentação é obrigatório"));
	}

	@Test
	@DisplayName("Deve retornar erro ao criar movimentação com nome menor que 6 caracteres")
	void deveRetornarErroAoCriarMovimentacaoComNomeMenorQue6Caracteres() throws Exception {

		var request = new MovimentacaoRequest(
			"Mov",
			LocalDate.now(),
			100.0,
			"RECEITA",
			categoriaId
		);

		var result = mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("O nome da movimentação deve ter pelo menos 6 caracteres"));
	}

	@Test
	@DisplayName("Deve retornar erro ao criar movimentação com valor menor ou igual a zero")
	void deveRetornarErroAoCriarMovimentacaoComValorMenorOuIgualZero() throws Exception {

		var request = new MovimentacaoRequest(
			"Movimentação teste",
			LocalDate.now(),
			0.0,
			"RECEITA",
			categoriaId
		);

		var result = mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("O valor da movimentação deve ser maior que zero"));
	}


	@Test
	@DisplayName("Deve obter movimentação por id com sucesso")
	void deveObterMovimentacaoPorIdComSucesso() throws Exception {

		var request = new MovimentacaoRequest(
			"Movimentação por id",
			LocalDate.now(),
			150.0,
			"DESPESA",
			categoriaId
		);

		var createResult = mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andReturn();

		var jsonCreate = createResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var criada = objectMapper.readValue(jsonCreate, MovimentacaoResponse.class);

		var getResult = mockMvc.perform(get("/api/v1/movimentacoes/{id}", criada.id()))
			.andExpect(status().isOk())
			.andReturn();

		var jsonGet = getResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var obtida = objectMapper.readValue(jsonGet, MovimentacaoResponse.class);

		assertNotNull(obtida);
		assertEquals(criada.id(), obtida.id());
		assertEquals(criada.nome(), obtida.nome());
		assertEquals(criada.valor(), obtida.valor());
		assertEquals(criada.tipo(), obtida.tipo());
	}

	@Test
	@DisplayName("Deve retornar 404 ao obter movimentação inexistente por id")
	void deveRetornar404AoObterMovimentacaoInexistentePorId() throws Exception {

		var id = UUID.randomUUID();

		var result = mockMvc.perform(get("/api/v1/movimentacoes/{id}", id))
			.andExpect(status().isNotFound())
			.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("Movimentação não encontrada"));
	}

	@Test
	@DisplayName("Deve alterar movimentação com sucesso")
	void deveAlterarMovimentacaoComSucesso() throws Exception {

		var request = new MovimentacaoRequest(
			"Movimentação alterar",
			LocalDate.now(),
			200.0,
			"RECEITA",
			categoriaId
		);

		var createResult = mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andReturn();

		var jsonCreate = createResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var criada = objectMapper.readValue(jsonCreate, MovimentacaoResponse.class);

		var update = new MovimentacaoRequest(
			"Movimentação alterada",
			LocalDate.now().plusDays(1),
			300.0,
			"DESPESA",
			categoriaId
		);

		var putResult = mockMvc.perform(
				put("/api/v1/movimentacoes/{id}", criada.id())
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(update)))
			.andExpect(status().isOk())
			.andReturn();

		var jsonPut = putResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var alterada = objectMapper.readValue(jsonPut, MovimentacaoResponse.class);

		assertEquals(criada.id(), alterada.id());
		assertEquals("Movimentação alterada", alterada.nome());
		assertEquals(300.0, alterada.valor());
		assertEquals("DESPESA", alterada.tipo());
	}

	@Test
	@DisplayName("Deve retornar 404 ao alterar movimentação inexistente")
	void deveRetornar404AoAlterarMovimentacaoInexistente() throws Exception {

		var id = UUID.randomUUID();
		var update = new MovimentacaoRequest(
			"Movimentação teste",
			LocalDate.now(),
			100.0,
			"RECEITA",
			categoriaId
		);

		var result = mockMvc.perform(
				put("/api/v1/movimentacoes/{id}", id)
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(update)))
			.andExpect(status().isNotFound())
			.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("Movimentação não encontrada"));
	}

	@Test
	@DisplayName("Deve retornar 404 ao alterar movimentação com categoria inexistente")
	void deveRetornar404AoAlterarMovimentacaoComCategoriaInexistente() throws Exception {

		var request = new MovimentacaoRequest(
			"Movimentação alterar",
			LocalDate.now(),
			200.0,
			"RECEITA",
			categoriaId
		);

		var createResult = mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andReturn();

		var jsonCreate = createResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var criada = objectMapper.readValue(jsonCreate, MovimentacaoResponse.class);

		var update = new MovimentacaoRequest(
			"Movimentação teste",
			LocalDate.now(),
			100.0,
			"RECEITA",
			UUID.randomUUID()
		);

		var result = mockMvc.perform(
				put("/api/v1/movimentacoes/{id}", criada.id())
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(update)))
			.andExpect(status().isNotFound())
			.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("Categoria não encontrada"));
	}

	@Test
	@DisplayName("Deve excluir movimentação com sucesso")
	void deveExcluirMovimentacaoComSucesso() throws Exception {

		var request = new MovimentacaoRequest(
			"Movimentação excluir",
			LocalDate.now(),
			250.0,
			"RECEITA",
			categoriaId
		);

		var createResult = mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andReturn();

		var jsonCreate = createResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var criada = objectMapper.readValue(jsonCreate, MovimentacaoResponse.class);

		var delResult = mockMvc.perform(delete("/api/v1/movimentacoes/{id}", criada.id()))
			.andExpect(status().isOk())
			.andReturn();

		var jsonDel = delResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var deletada = objectMapper.readValue(jsonDel, MovimentacaoResponse.class);

		assertEquals(criada.id(), deletada.id());
		assertEquals(criada.nome(), deletada.nome());
	}

	@Test
	@DisplayName("Deve retornar 404 ao excluir movimentação inexistente")
	void deveRetornar404AoExcluirMovimentacaoInexistente() throws Exception {

		var id = UUID.randomUUID();

		var result = mockMvc.perform(delete("/api/v1/movimentacoes/{id}", id))
			.andExpect(status().isNotFound())
			.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("Movimentação não encontrada"));
	}

	@Test
	@DisplayName("Deve consultar movimentações por período com sucesso")
	void deveConsultarMovimentacoesPorPeridoComSucesso() throws Exception {

		var dataInicio = LocalDate.now();
		var dataFim = LocalDate.now().plusDays(10);

		var request1 = new MovimentacaoRequest(
			"Movimentação 1",
			dataInicio.plusDays(2),
			100.0,
			"RECEITA",
			categoriaId
		);

		var request2 = new MovimentacaoRequest(
			"Movimentação 2",
			dataInicio.plusDays(5),
			200.0,
			"DESPESA",
			categoriaId
		);

		mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request1)))
			.andExpect(status().isCreated());

		mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request2)))
			.andExpect(status().isCreated());

		var result = mockMvc.perform(
				get("/api/v1/movimentacoes")
					.param("dataInicio", dataInicio.toString())
					.param("dataFim", dataFim.toString()))
			.andExpect(status().isOk())
			.andReturn();

		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertFalse(jsonContent.isEmpty());
		assertTrue(jsonContent.contains("Movimentação"));
	}

	@Test
	@DisplayName("Deve retornar erro ao consultar com data início maior que data fim")
	void deveRetornarErroAoConsultarComDataInicioMaiorQueDataFim() throws Exception {

		var dataInicio = LocalDate.now().plusDays(10);
		var dataFim = LocalDate.now();

		var result = mockMvc.perform(
				get("/api/v1/movimentacoes")
					.param("dataInicio", dataInicio.toString())
					.param("dataFim", dataFim.toString()))
			.andExpect(status().isBadRequest())
			.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("A data de início não pode ser maior do que a data de fim"));
	}

	@Test
	@DisplayName("Deve consultar movimentações com paginação")
	void deveConsultarMovimentacoesComPaginacao() throws Exception {

		var dataInicio = LocalDate.now();
		var dataFim = LocalDate.now().plusDays(10);

		var request = new MovimentacaoRequest(
			"Movimentação paginada",
			dataInicio.plusDays(2),
			100.0,
			"RECEITA",
			categoriaId
		);

		mockMvc.perform(
				post("/api/v1/movimentacoes")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());

		var result = mockMvc.perform(
				get("/api/v1/movimentacoes")
					.param("dataInicio", dataInicio.toString())
					.param("dataFim", dataFim.toString())
					.param("pageIndex", "0")
					.param("pageSize", "10"))
			.andExpect(status().isOk())
			.andReturn();

		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(jsonContent.contains("content"));
	}
}



