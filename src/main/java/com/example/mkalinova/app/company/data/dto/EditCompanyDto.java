package com.example.mkalinova.app.company.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

public class EditCompanyDto {
	
	
	protected UUID id;
	@NotNull(message = "Името на фирмата не трябва да е празно")
	@NotBlank(message = "Името на фирмата не трябва да е празно")
	private String name;
	
	@NotNull(message = "ЕИК не трябва да е празно")
	@Length(min = 9, max = 9, message = "ЕИК номерът трябва да е точно 9 цифри!")
	@Pattern(regexp = "^[0-9]{9}$")
	private String uic;
	
	@NotBlank(message = "Моля въведете адрес")
	@NotNull(message = "Моля въведете адрес")
	private String address;
	
	@NotBlank(message = "Моля въведете МОЛ")
	@NotNull(message = "Моля въведете МОЛ")
	private String accountablePerson;
	
	@NotBlank(message = "Моля въведете ДДС номер")
	@NotNull(message = "Моля въведете ДДС номер")
	private String vatNumber;
	
	private UUID clientId;
	
	public EditCompanyDto() {
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUic() {
		return uic;
	}
	
	public void setUic(String uic) {
		this.uic = uic;
	}
	
	public String getVatNumber() {
		return vatNumber;
	}
	
	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAccountablePerson() {
		return accountablePerson;
	}
	
	public void setAccountablePerson(String accountablePerson) {
		this.accountablePerson = accountablePerson;
	}
	
	public UUID getClientId() {
		return clientId;
	}
	
	public void setClientId(UUID clientId) {
		this.clientId = clientId;
	}
}
