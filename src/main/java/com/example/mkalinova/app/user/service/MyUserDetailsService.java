package com.example.mkalinova.app.user.service;

import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {
	private static final Logger log = LoggerFactory.getLogger(MyUserDetailsService.class);
	private final UserRepository userRepository;
	
	public MyUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Attempt to load user by username: {}", username);
		User user = userRepository.findByUsername(username);
		
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				user.isEnabled(),
				true,
				true,
				user.isEnabled(),
				Collections.singletonList(authority));
	}
}
