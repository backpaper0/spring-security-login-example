package com.example.service;

import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Account;
import com.example.entity.AccountPassword;
import com.example.repository.AccountPasswordRepository;
import com.example.repository.AccountRepository;

@Component
public class ChangePasswordService {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountPasswordRepository accountPasswordRepository;

	@Transactional
	public void changePassword(String password) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			//TODO
			throw new RuntimeException();
		}

		Account account = accountRepository.findByUsername(authentication.getName());
		AccountPassword accountPassword = accountPasswordRepository.findById(account.id()).get();
		accountPassword = accountPassword.passwordIsChanged();
		accountPasswordRepository.save(accountPassword);

		Object principal = authentication.getPrincipal();
		Object credentials = authentication.getCredentials();
		Collection<? extends GrantedAuthority> authorities = Stream
				.concat(
						authentication.getAuthorities().stream(),
						Stream.of(new SimpleGrantedAuthority("NO_NEED_TO_CHANGE_PASSWORD")))
				.toList();
		if (principal instanceof User) {
			User user = (User) principal;
			principal = new User(user.getUsername(), "", user.isEnabled(), user.isAccountNonExpired(),
					user.isCredentialsNonExpired(), user.isAccountNonLocked(), authorities);
		}
		authentication = UsernamePasswordAuthenticationToken.authenticated(principal, credentials, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
