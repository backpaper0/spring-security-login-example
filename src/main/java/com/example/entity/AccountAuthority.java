package com.example.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "account_authorities")
public record AccountAuthority(
		@Id Long id,
		Long accountId,
		String authority) {
}
