package com.example.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.entity.AccountPassword;

public interface AccountPasswordRepository extends PagingAndSortingRepository<AccountPassword, Long> {
}
