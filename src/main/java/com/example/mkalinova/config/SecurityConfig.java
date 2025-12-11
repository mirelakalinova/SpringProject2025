package com.example.mkalinova.config;

import com.example.mkalinova.app.user.repo.UserRepository;
import com.example.mkalinova.app.user.service.MyUserDetailsService;
import com.example.mkalinova.init.BlockedUserFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {
	private final MyUserDetailsService myUserDetailService;
	private final UserRepository userRepository;
	
	public SecurityConfig(MyUserDetailsService myUserDetailService, UserRepository userRepository) {
		this.myUserDetailService = myUserDetailService;
		this.userRepository = userRepository;
	}
	
	@Bean
	public BlockedUserFilter blockedUserFilter() {
		return new BlockedUserFilter(userRepository);
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
						.requestMatchers("/user/login").permitAll()
						.requestMatchers("/error").permitAll()
						.anyRequest().permitAll())
				
				.formLogin(formLogin -> {
					formLogin
							.loginPage("/user/login")
							.usernameParameter("username")
							.passwordParameter("password")
							.defaultSuccessUrl("/dashboard", true)
							.failureUrl("/user/login?error");
				})
				.logout(logout -> logout.logoutSuccessUrl("/"));
		http.addFilterBefore(blockedUserFilter(), UsernamePasswordAuthenticationFilter.class);
		http.userDetailsService(myUserDetailService);
		return http.build();
	}
}
