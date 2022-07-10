package com.example.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Account;
import com.example.entity.AccountAuthority;
import com.example.entity.AccountPassword;
import com.example.repository.AccountAuthorityRepository;
import com.example.repository.AccountPasswordRepository;
import com.example.repository.AccountRepository;

@Component
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountPasswordRepository accountPasswordRepository;
	@Autowired
	private AccountAuthorityRepository accountAuthorityRepository;

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if (username == null || username.isEmpty()) {
			throw new UsernameNotFoundException(username);
		}

		Account account = accountRepository.findByUsername(username);
		if (account == null) {
			throw new UsernameNotFoundException(username);
		}

		AccountPassword accountPassword = accountPasswordRepository.findById(account.id())
				.orElseThrow(() -> new UsernameNotFoundException(username));

		List<AccountAuthority> accountAuthorities = accountAuthorityRepository.findByAccountId(account.id());

		LocalDate today = LocalDate.now();
		LocalDateTime now = LocalDateTime.now();

		String password = accountPassword.hashedPassword();
		boolean enabled = account.enabled();
		boolean accountNonExpired = !account.expirationDate().isBefore(today);
		boolean credentialsNonExpired = !accountPassword.expirationDate().isBefore(today);
		boolean accountNonLocked = account.lockedUntil() == null || account.lockedUntil().isBefore(now);
		Collection<? extends GrantedAuthority> authorities = Stream.concat(
				accountAuthorities.stream().map(AccountAuthority::authority),
				accountPassword.needsToChange() ? Stream.empty() : Stream.of("NO_NEED_TO_CHANGE_PASSWORD"))
				.map(SimpleGrantedAuthority::new)
				.toList();

		return new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
				authorities);
	}

	@Transactional
	@EventListener(AuthenticationSuccessEvent.class)
	public void handleSuccess(AuthenticationSuccessEvent event) {
		String username = event.getAuthentication().getName();
		Account account = accountRepository.findByUsername(username);
		accountRepository.save(account.clearAuthenticationFailureCount());
	}

	@Transactional
	@EventListener(AuthenticationFailureBadCredentialsEvent.class)
	public void handleFailureBadCredentials(AuthenticationFailureBadCredentialsEvent event) {
		String username = event.getAuthentication().getName();
		Account account = accountRepository.findByUsername(username);
		account = account.incrementAuthenticationFailureCount();
		if (account.authenticationFailureCount() >= 3) {
			account = account.lock(LocalDateTime.now().plusSeconds(10));
		}
		accountRepository.save(account);
	}
}
