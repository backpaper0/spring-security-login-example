package com.example.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.config.WebSecurityProperties;
import com.example.entity.Account;
import com.example.entity.AccountAuthority;
import com.example.entity.AccountPassword;
import com.example.exception.SystemException;
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
	@Autowired
	private WebSecurityProperties webSecurityProperties;

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
				.orElseThrow(() -> new SystemException());

		List<AccountAuthority> accountAuthorities = accountAuthorityRepository.findByAccountId(account.id());

		LocalDate today = LocalDate.now();
		LocalDateTime now = LocalDateTime.now();

		String password = accountPassword.hashedPassword();
		boolean enabled = account.enabled();
		boolean accountNonExpired = account.accountNonExpired(today);
		boolean credentialsNonExpired = accountPassword.credentialsNonExpired(today);
		boolean accountNonLocked = account.accountNonLocked(now);

		Collection<? extends GrantedAuthority> authorities = accountAuthorities.stream()
				.map(AccountAuthority::authority).map(SimpleGrantedAuthority::new).toList();

		if (!accountPassword.needsToChange()) {
			authorities = Stream.concat(
					authorities.stream(),
					Stream.of(new SimpleGrantedAuthority("NO_NEED_TO_CHANGE_PASSWORD")))
					.toList();
		}

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
		if (account == null) {
			return;
		}
		account = account.incrementAuthenticationFailureCount();
		if (account.authenticationFailureCount() >= webSecurityProperties.getAuthenticationFailureCountThreshold()) {
			account = account
					.lock(LocalDateTime.now().plusSeconds(webSecurityProperties.getLockoutTimeout().toSeconds()));
		}
		accountRepository.save(account);
	}

	@EventListener(PasswordChangedEvent.class)
	@PreAuthorize("authenticated")
	public void handlePasswordChangedEvent(PasswordChangedEvent event) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
