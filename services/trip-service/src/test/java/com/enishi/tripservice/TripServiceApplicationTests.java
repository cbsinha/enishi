package com.enishi.tripservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import com.enishi.tripservice.config.SecurityConfig;
import com.enishi.tripservice.controller.AuthController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class TripServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void unauthenticatedRequestShouldReturn401() throws Exception {
		mockMvc.perform(get("/api/v1/auth/me"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void authenticatedRequestShouldReturnUserId() throws Exception {
		Jwt jwt = Jwt.withTokenValue("test-token")
				.header("alg", "none")
				.subject("user_test123")
				.claim("iss", "https://wondrous-cod-4957.clerk.accounts.dev")
				.build();

		mockMvc.perform(get("/api/v1/auth/me")
						.with(jwt().jwt(jwt))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value("user_test123"));
	}
}