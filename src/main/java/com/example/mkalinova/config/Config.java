package com.example.mkalinova.config;

import com.example.mkalinova.app.car.data.dto.AddCarDto;
import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.company.data.dto.AddCompanyDto;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.order.data.dto.AddOrderDto;
import com.example.mkalinova.app.order.data.entity.Order;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Config {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.typeMap(AddCarDto.class, Car.class)
				.addMappings(m -> m.skip(Car::setId));
		mapper.typeMap(AddCompanyDto.class, Company.class)
				.addMappings(m -> m.skip(Company::setId));
		mapper.addMappings(new PropertyMap<AddOrderDto, Order>() {
			@Override
			protected void configure() {
				skip().setId(null);
				skip().setCar(null);
				skip().setClient(null);
				skip().setCompany(null);
			}
		});
		
		return mapper;
		
	}
}
