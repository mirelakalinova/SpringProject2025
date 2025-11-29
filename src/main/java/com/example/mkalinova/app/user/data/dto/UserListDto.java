package com.example.mkalinova.app.user.data.dto;

import com.example.mkalinova.app.user.data.entity.UsersRole;

import java.util.UUID;

public class UserListDto {
	
	private UUID id;
	private String username;
	private String email;
	private UsersRole role;
	
	public UserListDto() {
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public UsersRole getRole() {
		return role;
	}
	
	public void setRole(UsersRole role) {
		this.role = role;
	}
}
