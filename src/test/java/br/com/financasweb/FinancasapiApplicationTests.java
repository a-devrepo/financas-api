package br.com.financasweb;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FinancasapiApplicationTests {

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
