package com.example.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "myapp.security")
public class WebSecurityProperties {

	private int authenticationFailureCountThreshold;
	private Duration lockoutTimeout;

	public int getAuthenticationFailureCountThreshold() {
		return authenticationFailureCountThreshold;
	}

	public void setAuthenticationFailureCountThreshold(int authenticationFailureCountThreshold) {
		this.authenticationFailureCountThreshold = authenticationFailureCountThreshold;
	}

	public Duration getLockoutTimeout() {
		return lockoutTimeout;
	}

	public void setLockoutTimeout(Duration lockoutTimeout) {
		this.lockoutTimeout = lockoutTimeout;
	}
}
