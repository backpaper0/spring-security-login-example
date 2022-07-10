package com.example.config;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(c -> c
						.mvcMatchers("/").hasAuthority("NO_NEED_TO_CHANGE_PASSWORD")
						.mvcMatchers("/change-password").authenticated()
						.mvcMatchers("/login").permitAll())

				.formLogin(c -> c.successHandler(new SimpleUrlAuthenticationSuccessHandler("/") {
					@Override
					protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
							Authentication authentication) {
						Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
						if (authorities.contains("NO_NEED_TO_CHANGE_PASSWORD")) {
							return super.determineTargetUrl(request, response, authentication);
						}
						return "/change-password";
					}
				}))

				.build();
	}

	@Bean
	public Pbkdf2PasswordEncoder passwordEncoder() {
		Pbkdf2PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder("", 16, 10000, 256);
		passwordEncoder.setAlgorithm(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
		passwordEncoder.setEncodeHashAsBase64(true);
		return passwordEncoder;
	}
}
