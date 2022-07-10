package com.example.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import com.example.repository.AccountRepository;

@DataJdbcTest
public class AccountTest {

	@Autowired
	AccountRepository repository;

	@Test
	void testName() throws Exception {
		Iterable<Account> accounts = repository.findAll();
		for (Account account : accounts) {
			System.out.println(account);
		}
	}
}
