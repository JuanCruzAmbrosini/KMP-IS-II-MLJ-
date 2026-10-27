package com.irojas.demojwt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class DemoJwtApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoadsAndJwtFlowWorks() throws Exception {
		// 1. Endpoint protegido sin token debe rechazar la petición (403 Forbidden)
		mockMvc.perform(post("/api/v1/demo"))
				.andExpect(status().isForbidden());

		// 2. Registro de nuevo usuario devuelve 200 OK y un token JWT
		String registerJson = """
				{
					"username": "testuser@gmail.com",
					"password": "123456",
					"firstname": "Test",
					"lastname": "User",
					"country": "Argentina"
				}
				""";

		MvcResult registerResult = mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(registerJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty())
				.andReturn();

		JsonNode jsonNode = objectMapper.readTree(registerResult.getResponse().getContentAsString());
		String token = jsonNode.get("token").asText();

		// 3. Acceso al endpoint protegido con Bearer Token devuelve 200 OK
		mockMvc.perform(post("/api/v1/demo")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(content().string("Welcome from secure endpoint"));
	}

}

