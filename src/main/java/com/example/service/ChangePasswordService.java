package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Account;
import com.example.entity.AccountPassword;
import com.example.repository.AccountPasswordRepository;
import com.example.repository.AccountRepository;

@Component
public class ChangePasswordService implements ApplicationEventPublisherAware {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountPasswordRepository accountPasswordRepository;

	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Transactional
	@PreAuthorize("authenticated")
	public void changePassword(String password) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Account account = accountRepository.findByUsername(authentication.getName());
		AccountPassword accountPassword = accountPasswordRepository.findById(account.id()).get();
		accountPassword = accountPassword.passwordIsChanged();
		accountPasswordRepository.save(accountPassword);

		applicationEventPublisher.publishEvent(new PasswordChangedEvent());
	}
}
