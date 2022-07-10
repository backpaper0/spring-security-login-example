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

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;

@SpringBootTest
@AutoConfigureMockMvc
@DBRider
public class LoginTest {

	@Autowired
	MockMvc mvc;

	@Test
	@DataSet("dataset/login01.yaml")
	@ExpectedDataSet("expected/login01.yaml")
	void ログイン成功() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/"));
	}

	@Test
	@DataSet("dataset/login02.yaml")
	@ExpectedDataSet("expected/login02.yaml")
	void ログインは成功するがパスワードの変更を強制する() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/change-password"));
	}

	@Test
	@DataSet("dataset/login03.yaml")
	@ExpectedDataSet("expected/login03.yaml")
	void アカウント有効期限切れ() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(AccountExpiredException.class)));
	}

	@Test
	@DataSet("dataset/login04.yaml")
	@ExpectedDataSet("expected/login04.yaml")
	void ロック期間中() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(LockedException.class)));
	}

	@Test
	@DataSet("dataset/login05.yaml")
	@ExpectedDataSet("expected/login05.yaml")
	void ロック期間外() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/"));
	}

	@Test
	@DataSet("dataset/login06.yaml")
	@ExpectedDataSet("expected/login06.yaml")
	void 無効なアカウント() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(DisabledException.class)));
	}

	@Test
	@DataSet("dataset/login07.yaml")
	@ExpectedDataSet("expected/login07.yaml")
	void パスワード有効期限切れ() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(CredentialsExpiredException.class)));
	}

	@Test
	@DataSet("dataset/login08.yaml")
	@ExpectedDataSet("expected/login08.yaml")
	void ログインに失敗してロックされる() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user")
				.param("password", "mistake")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(BadCredentialsException.class)));

		mvc.perform(post("/login")
				.param("username", "user")
				.param("password", "mistake")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(LockedException.class)));
	}

	@Test
	@DataSet("dataset/login01.yaml")
	@ExpectedDataSet("expected/login01.yaml")
	void ユーザー名が未入力() throws Exception {
		mvc.perform(post("/login")
				.param("username", "")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(BadCredentialsException.class)));
	}

	@Test
	@DataSet("dataset/login01.yaml")
	@ExpectedDataSet("expected/login01.yaml")
	void ユーザー名が無し() throws Exception {
		mvc.perform(post("/login")
				//.param("username", "user")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(BadCredentialsException.class)));
	}

	@Test
	@DataSet("dataset/login01.yaml")
	@ExpectedDataSet("expected/login01b.yaml")
	void パスワードが未入力() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user")
				.param("password", "")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(BadCredentialsException.class)));
	}

	@Test
	@DataSet("dataset/login01.yaml")
	@ExpectedDataSet("expected/login01b.yaml")
	void パスワードが無し() throws Exception {
		mvc.perform(post("/login")
				.param("username", "user")
				//.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(BadCredentialsException.class)));
	}

	@Test
	@DataSet("dataset/login01.yaml")
	@ExpectedDataSet("expected/login01.yaml")
	void 存在しないユーザー() throws Exception {
		mvc.perform(post("/login")
				.param("username", "notexists")
				.param("password", "secret")
				.with(csrf()))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(request().sessionAttribute("SPRING_SECURITY_LAST_EXCEPTION",
						isA(BadCredentialsException.class)));
	}
}
