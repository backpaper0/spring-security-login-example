package com.example.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.entity.Account;

public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

	Account findByUsername(String username);
}
