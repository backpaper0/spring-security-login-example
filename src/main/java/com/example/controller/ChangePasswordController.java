package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.form.ChangePasswordForm;
import com.example.service.ChangePasswordService;

@Controller
@RequestMapping("/change-password")
public class ChangePasswordController {

	@Autowired
	private ChangePasswordService changePasswordService;

	@GetMapping
	public String index() {
		return "change-password";
	}

	@PostMapping
	public String changePassword(ChangePasswordForm form) {
		changePasswordService.changePassword(form.password);
		return "redirect:/";
	}

	@ModelAttribute
	public ChangePasswordForm form() {
		return new ChangePasswordForm();
	}
}
