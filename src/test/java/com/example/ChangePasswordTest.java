//package com.example;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class ChangePasswordTest {
//
//	@Autowired
//	MockMvc mvc;
//
//	@Test
//	@WithMockUser(username = "user", authorities = { "AUH1" })
//	void パスワードを変更してフラグをOFFにする() throws Exception {
//		mvc.perform(post("/change-password")
//				.param("password", "changed")
//				.param("confirmPassword", "changed")
//				.with(csrf()))
//				.andExpect(redirectedUrl("/"));
//	}
//
//	@Test
//	@WithMockUser(username = "user", authorities = { "AUH1" })
//	void パスワードを変更する() throws Exception {
//		mvc.perform(post("/change-password")
//				.param("password", "changed")
//				.param("confirmPassword", "changed")
//				.with(csrf()))
//				.andExpect(redirectedUrl("/"));
//	}
//}
