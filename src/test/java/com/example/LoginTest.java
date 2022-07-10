package com.example;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

	@Autowired
	MockMvc mvc;

	@Test
	void ログイン成功() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user01")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/"));
	}

	@Test
	void ログインは成功するがパスワードの変更を強制する() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user02")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/change-password"));
	}

	@Test
	void アカウント有効期限切れ() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user03")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(AccountExpiredException.class)));
	}

	@Test
	void ロック期間中() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user04")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(LockedException.class)));
	}

	@Test
	void ロック期間外() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user05")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/"));
	}

	@Test
	void 無効なアカウント() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user06")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(DisabledException.class)));
	}

	@Test
	void パスワード有効期限切れ() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user07")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(CredentialsExpiredException.class)));
	}

	@Test
	void ログインに失敗してロックされる() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user08")
				.param("password", "mistake")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(BadCredentialsException.class)));

		mvc.perform(post("/login")
				.param("username", "user08")
				.param("password", "mistake")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(LockedException.class)));
	}
}
