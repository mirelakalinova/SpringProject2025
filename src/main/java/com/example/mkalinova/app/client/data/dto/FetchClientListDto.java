package com.example.mkalinova.app.client.data.dto;

import com.example.mkalinova.app.car.data.dto.CarIdDto;
import com.example.mkalinova.app.company.data.dto.CompanyIdDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FetchClientListDto {
	
	private UUID id;
	private String firstName;
	
	private String lastName;
	
	private String phone;
	
	private String email;
	
	private LocalDateTime deleteAd;
	
	private List<CarIdDto> cars = new ArrayList<>();
	
	private List<CompanyIdDto> companies = new ArrayList<>();
	
	public FetchClientListDto() {
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public List<CarIdDto> getCars() {
		return cars;
	}
	
	public void setCars(List<CarIdDto> cars) {
		this.cars = cars;
	}
	
	public List<CompanyIdDto> getCompanies() {
		return companies;
	}
	
	public void setCompanies(List<CompanyIdDto> companies) {
		this.companies = companies;
	}
	
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public LocalDateTime getDeleteAd() {
		return deleteAd;
	}
	
	
	public void setDeleteAd(LocalDateTime deleteAd) {
		this.deleteAd = deleteAd;
	}
	
	@Override
	public String toString() {
		return firstName +
				" " + lastName + " - " + phone;
	}
}
