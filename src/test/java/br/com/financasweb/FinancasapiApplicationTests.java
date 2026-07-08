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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

}
