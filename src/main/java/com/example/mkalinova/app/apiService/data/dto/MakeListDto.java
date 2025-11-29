package com.example.mkalinova.app.apiService.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MakeListDto {
	@JsonProperty("makes")
	private List<MakeDto> makes;
	
	public MakeListDto() {
	}
	
	public List<MakeDto> getMakes() {
		return makes;
	}
	
	public void setMakes(List<MakeDto> makes) {
		this.makes = makes;
	}
}
