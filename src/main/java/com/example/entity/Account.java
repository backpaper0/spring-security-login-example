package com.example.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "accounts")
public record Account(
		@Id Long id,
		String username,
		LocalDate expirationDate,
		int authenticationFailureCount,
		LocalDateTime lockedUntil,
		boolean enabled) {

	public Account clearAuthenticationFailureCount() {
		return new Account(id, username, expirationDate, 0, lockedUntil, enabled);
	}

	public Account incrementAuthenticationFailureCount() {
		return new Account(id, username, expirationDate, authenticationFailureCount + 1, lockedUntil, enabled);
	}

	public Account lock(LocalDateTime lockedUntil) {
		return new Account(id, username, expirationDate, 0, lockedUntil, enabled);
	}

	public boolean accountNonExpired(LocalDate today) {
		return !expirationDate.isBefore(today);
	}

	public boolean accountNonLocked(LocalDateTime now) {
		return lockedUntil == null || lockedUntil.isBefore(now);
	}
}
