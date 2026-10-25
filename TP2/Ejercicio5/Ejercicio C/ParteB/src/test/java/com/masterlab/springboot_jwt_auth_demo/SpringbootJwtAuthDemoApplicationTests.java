package com.masterlab.springboot_jwt_auth_demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class SpringbootJwtAuthDemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoadsAndJwtFlowWorks() throws Exception {
		// 1. Endpoint protegido sin token debe rechazar la petición (403 Forbidden)
		mockMvc.perform(get("/api/hello"))
				.andExpect(status().isForbidden());

		// 2. Login con credenciales del video (admin / password) devuelve 200 OK y el token JWT
		String loginJson = """
				{
					"username": "admin",
					"password": "password"
				}
				""";

		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Login successful"))
				.andExpect(jsonPath("$.token").isNotEmpty())
				.andReturn();

		JsonNode jsonNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
		String token = jsonNode.get("token").asText();

		// 3. Acceso al endpoint protegido con Bearer Token devuelve 200 OK
		mockMvc.perform(get("/api/hello")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("You accessed a protected endpoint!"));
	}

}

