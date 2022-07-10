package com.example.config;

import java.util.Set;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

public class MyAuthorizationManagers {

	public static AuthorizationManager<RequestAuthorizationContext> hasRequiredAuthority() {
		return hasRequiredAuthorityAnd(null);
	}

	public static AuthorizationManager<RequestAuthorizationContext> hasRequiredAuthorityAnd(String authority) {
		return new RequiredAuthorityAuthorizationManager(authority);
	}

	public static AuthorizationManager<RequestAuthorizationContext> notAuthenticated() {
		AuthenticatedAuthorizationManager<RequestAuthorizationContext> aam = AuthenticatedAuthorizationManager
				.authenticated();
		return (a, o) -> new AuthorizationDecision(!aam.check(a, o).isGranted());
	}

	private static class RequiredAuthorityAuthorizationManager
			implements AuthorizationManager<RequestAuthorizationContext> {
		private final String authority;

		private RequiredAuthorityAuthorizationManager(String authority) {
			this.authority = authority;
		}

		@Override
		public AuthorizationDecision check(Supplier<Authentication> authentication,
				RequestAuthorizationContext object) {
			Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.get().getAuthorities());
			if (!authorities.contains("NO_NEED_TO_CHANGE_PASSWORD")) {
				return new AuthorizationDecision(false);
			}
			if (authority != null && !authorities.contains(authority)) {
				return new AuthorizationDecision(false);
			}
			return new AuthorizationDecision(true);
		}

	}
}
