package com.example.mkalinova.app.client.data.dto;

import com.example.mkalinova.app.car.data.dto.CarDto;
import com.example.mkalinova.app.company.data.dto.CompanyClientListDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ClientListDto {

    private Long id;
    private String firstName;

    private String lastName;

    private String phone;

    private String email;

    private LocalDateTime deleteAd;

    private List<CarDto> cars = new ArrayList<>();

    private List<CompanyClientListDto> companies = new ArrayList<>();

    public ClientListDto() {
    }


    public List<CarDto> getCars() {
        return cars;
    }

    public void setCars(List<CarDto> cars) {
        this.cars = cars;
    }

    public List<CompanyClientListDto> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CompanyClientListDto> companies) {
        this.companies = new ArrayList<>();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return  firstName +
                " " + lastName + " - " + phone               ;
    }
}
