package com.example.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.config.WebSecurityProperties;
import com.example.entity.Account;
import com.example.entity.AccountAuthority;
import com.example.entity.AccountPassword;
import com.example.exception.SystemException;
import com.example.repository.AccountAuthorityRepository;
import com.example.repository.AccountPasswordRepository;
import com.example.repository.AccountRepository;

/**
 * {@link UserDetailsService}実装クラス。
 *
 */
@Component
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountPasswordRepository accountPasswordRepository;
	@Autowired
	private AccountAuthorityRepository accountAuthorityRepository;
	@Autowired
	private WebSecurityProperties webSecurityProperties;

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// usernameが入力されていなければエラー
		if (username == null || username.isEmpty()) {
			throw new UsernameNotFoundException(username);
		}

		// アカウントが存在しなければエラー
		Account account = accountRepository.findByUsername(username);
		if (account == null) {
			throw new UsernameNotFoundException(username);
		}

		// ハッシュ化されたパスワードを取得
		AccountPassword accountPassword = accountPasswordRepository.findById(account.id())
				// 取得できない場合はデータ不整合なためシステムエラーとする
				.orElseThrow(() -> new SystemException());

		// アカウントに関連付いた権限を取得
		List<AccountAuthority> accountAuthorities = accountAuthorityRepository.findByAccountId(account.id());

		LocalDate today = LocalDate.now();
		LocalDateTime now = LocalDateTime.now();

		// ハッシュ化されたパスワード
		String password = accountPassword.hashedPassword();
		// フラグ：アカウントが有効ならtrue
		boolean enabled = account.enabled();
		// フラグ：アカウントが有効期限切れでなければtrue
		boolean accountNonExpired = account.accountNonExpired(today);
		// フラグ：パスワードが有効期限切れでなければtrue
		boolean credentialsNonExpired = accountPassword.credentialsNonExpired(today);
		// フラグ：アカウントがロックされていなければtrue
		boolean accountNonLocked = account.accountNonLocked(now);

		// 権限をGrantedAuthority型へ変換
		Collection<? extends GrantedAuthority> authorities = accountAuthorities.stream()
				.map(AccountAuthority::authority).map(SimpleGrantedAuthority::new).toList();

		// パスワードを変更する必要がなければ権限をひとつ追加する
		// この権限がない場合はログイン後、強制的にパスワード変更画面へ遷移させる
		if (!accountPassword.needsToChange()) {
			authorities = Stream.concat(
					authorities.stream(),
					Stream.of(new SimpleGrantedAuthority("NO_NEED_TO_CHANGE_PASSWORD")))
					.toList();
		}

		return new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
				authorities);
	}

	/**
	 * ログイン成功時に発火されるイベントをハンドリングする。
	 * 
	 * @param event ログイン成功時に発火されるイベント
	 */
	@Transactional
	@EventListener(AuthenticationSuccessEvent.class)
	public void handleSuccess(AuthenticationSuccessEvent event) {
		String username = event.getAuthentication().getName();
		Account account = accountRepository.findByUsername(username);
		// ログイン失敗回数をリセットする
		accountRepository.save(account.clearAuthenticationFailureCount());
	}

	/**
	 * ログイン失敗時に発火されるイベントをハンドリングする。
	 * 
	 * @param event ログイン失敗時に発火されるイベント
	 */
	@Transactional
	@EventListener(AuthenticationFailureBadCredentialsEvent.class)
	public void handleFailureBadCredentials(AuthenticationFailureBadCredentialsEvent event) {
		String username = event.getAuthentication().getName();
		Account account = accountRepository.findByUsername(username);
		if (account == null) {
			// 存在しないアカウントのためにログイン失敗した場合は、
			// ここでアカウントを取得できない
			return;
		}
		// ログイン失敗回数をインクリメントする
		account = account.incrementAuthenticationFailureCount();
		// ログイン失敗回数が規定値を超えた場合はアカウントをロックする
		if (account.authenticationFailureCount() >= webSecurityProperties.getAuthenticationFailureCountThreshold()) {
			account = account
					.lock(LocalDateTime.now().plusSeconds(webSecurityProperties.getLockoutTimeout().toSeconds()));
		}
		accountRepository.save(account);
	}

	/**
	 * パスワード変更時に発火されるイベントをハンドリングする。
	 * 
	 * @param event パスワード変更時に発火されるイベント
	 */
	@EventListener(PasswordChangedEvent.class)
	@PreAuthorize("authenticated")
	public void handlePasswordChangedEvent(PasswordChangedEvent event) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		Object credentials = authentication.getCredentials();
		// ログイン時と同じようにパスワード変更を強制されない権限を追加する
		Collection<? extends GrantedAuthority> authorities = Stream
				.concat(
						authentication.getAuthorities().stream(),
						Stream.of(new SimpleGrantedAuthority("NO_NEED_TO_CHANGE_PASSWORD")))
				.toList();
		if (principal instanceof User) {
			User user = (User) principal;
			principal = new User(user.getUsername(), "", user.isEnabled(), user.isAccountNonExpired(),
					user.isCredentialsNonExpired(), user.isAccountNonLocked(), authorities);
		}
		authentication = UsernamePasswordAuthenticationToken.authenticated(principal, credentials, authorities);
		// SecurityContextへ保存されたAuthenticationを更新数r
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
