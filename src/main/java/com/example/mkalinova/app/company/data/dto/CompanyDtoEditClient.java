package com.example.mkalinova.app.company.data.dto;

import java.util.UUID;

public class CompanyDtoEditClient {
	private UUID companyId;
	private String name;
	private int uic;
	
	public CompanyDtoEditClient() {
	}
	
	public UUID getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(UUID companyId) {
		this.companyId = companyId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getUic() {
		return uic;
	}
	
	public void setUic(int uic) {
		this.uic = uic;
	}
}
