package com.example.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class DefaultControllerAdvice {

	@InitBinder
	public void init(WebDataBinder binder) {
		binder.initDirectFieldAccess();
	}
}
