package com.example.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "account_passwords")
public record AccountPassword(
		@Id Long accountId,
		String hashedPassword,
		LocalDate expirationDate,
		boolean needsToChange) {

	public AccountPassword passwordIsChanged() {
		return new AccountPassword(accountId, hashedPassword, expirationDate, false);
	}
}
