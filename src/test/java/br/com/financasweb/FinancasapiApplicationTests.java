package br.com.financasweb;

import br.com.financasweb.configurations.AbstractIntegrationTest;
import br.com.financasweb.dtos.CategoriaRequest;
import br.com.financasweb.dtos.CategoriaResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FinancasapiApplicationTests extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("Deve criar uma categoria com sucesso")
	void deveCriarUmaCategoriaComSucesso() throws Exception {

		var request = new CategoriaRequest("Categoria teste");

		var result = mockMvc.perform(
				post("/api/v1/categorias/criar")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andReturn();
		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var response = objectMapper.readValue(jsonContent, CategoriaResponse.class);

		assertNotNull(response.id());

		assertEquals(request.nome(),response.nome());
	}

	@Test
	@DisplayName("Deve retornar a lista de categorias")
	void deveRetornarListaDeCategorias() throws Exception {

		var request = new CategoriaRequest("Categoria lista");

		mockMvc.perform(
				post("/api/v1/categorias/criar")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());

		var resultGet = mockMvc.perform(get("/api/v1/categorias"))
				.andExpect(status().isOk())
				.andReturn();

		var jsonContent = resultGet.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var response = objectMapper.readValue(jsonContent, CategoriaResponse[].class);

		assertTrue(response.length >= 1);
		boolean encontrado = false;
		for (var c : response) {
			if (c.nome().equals(request.nome())) {
				encontrado = true;
				break;
			}
		}
		assertTrue(encontrado);
	}

	@Test
	@DisplayName("Deve obter categoria por id com sucesso")
	void deveObterCategoriaPorIdComSucesso() throws Exception {

		var request = new CategoriaRequest("Categoria por id");
		var createResult = mockMvc.perform(
				post("/api/v1/categorias/criar")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andReturn();

		var jsonCreate = createResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var criado = objectMapper.readValue(jsonCreate, CategoriaResponse.class);

		var getResult = mockMvc.perform(get("/api/v1/categorias/{id}", criado.id()))
				.andExpect(status().isOk())
				.andReturn();

		var jsonGet = getResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var got = objectMapper.readValue(jsonGet, CategoriaResponse.class);

		assertNotNull(got);
		assertEquals(criado.id(), got.id());
		assertEquals(criado.nome(), got.nome());
	}

	@Test
	@DisplayName("Deve alterar categoria com sucesso")
	void deveAlterarCategoriaComSucesso() throws Exception {

		var request = new CategoriaRequest("Categoria alterar");

		var result = mockMvc.perform(
				post("/api/v1/categorias/criar")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andReturn();

		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var response = objectMapper.readValue(jsonContent, CategoriaResponse.class);

		var update = new CategoriaRequest("Categoria alterada");

		var putResult = mockMvc.perform(put("/api/v1/categorias/" + response.id())
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(update)))
			.andExpect(status().isOk())
			.andReturn();

		var jsonPut = putResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var updated = objectMapper.readValue(jsonPut, CategoriaResponse.class);

		assertEquals(response.id(), updated.id());
		assertEquals("Categoria alterada", updated.nome());
	}

	@Test
	@DisplayName("Deve excluir categoria com sucesso")
	void deveExcluirCategoriaComSucesso() throws Exception {

		var request = new CategoriaRequest("Categoria excluir");

		var result = mockMvc.perform(
				post("/api/v1/categorias/criar")
					.contentType("application/json")
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andReturn();

		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var response = objectMapper.readValue(jsonContent, CategoriaResponse.class);

		var delResult = mockMvc.perform(delete("/api/v1/categorias/" + response.id()))
				.andExpect(status().isOk())
				.andReturn();

		var jsonDel = delResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var deleted = objectMapper.readValue(jsonDel, CategoriaResponse.class);

		assertEquals(response.id(), deleted.id());
		assertEquals(response.nome(), deleted.nome());
	}

	@Test
	@DisplayName("Deve retornar 404 ao alterar categoria inexistente")
	void deveRetornar404AoAlterarCategoriaInexistente() throws Exception {

		var id = UUID.randomUUID();
		var update = new CategoriaRequest("Nome qualquer");

		var result = mockMvc.perform(put("/api/v1/categorias/" + id)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(update)))
			.andExpect(status().isNotFound())
			.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("Categoria não encontrada."));
	}

	@Test
	@DisplayName("Deve retornar 404 ao excluir categoria inexistente")
	void deveRetornar404AoExcluirCategoriaInexistente() throws Exception {

		var id = UUID.randomUUID();

		var result = mockMvc.perform(delete("/api/v1/categorias/" + id))
				.andExpect(status().isNotFound())
				.andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(content.contains("Categoria não encontrada."));
	}

	@Test
	@DisplayName("Deve retornar erro se o nome da categoria estiver vazio")
	void validarNomeDaCategoriaTest() throws Exception {

		var request = new CategoriaRequest("");

		var result = mockMvc.perform(
						post("/api/v1/categorias/criar")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andReturn();
		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(jsonContent.contains("O nome da categoria é obrigatório."));
	}

	@Test
	@DisplayName("Deve retornar erro se o nome da categoria tiver menos de 6 caracteres")
	void validarNomeDaCategoriaMinimoDeCaracteres() throws Exception {

		var request = new CategoriaRequest("Test");

		var result = mockMvc.perform(
						post("/api/v1/categorias/criar")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andReturn();
		var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		assertTrue(jsonContent.contains("O nome da categoria deve ter no mínimo 6 caracteres."));
	}
}