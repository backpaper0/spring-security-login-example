package com.example.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.entity.AccountAuthority;

public interface AccountAuthorityRepository extends PagingAndSortingRepository<AccountAuthority, Long> {

	List<AccountAuthority> findByAccountId(Long accountId);
}
