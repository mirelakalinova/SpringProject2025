package com.example.mkalinova.app.client.data.dto;


import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.company.data.entity.Company;
import jakarta.validation.constraints.*;

import java.util.List;

public class EditClientDto {

    @NotNull
    private Long id;
    @Size(min = 3)
    @NotNull
    @NotBlank
    private String firstName;
    @Size(min = 3)
    private String lastName;
    @Email
    private String email;
    @Size(min = 10, max=10)
    @NotNull
    @NotBlank
    @Pattern(regexp = "^0\\d{9}$", message = "Телефонният номер трябва да започва с 0 и да съдържа 10 цифри")
    private String phone;
    private Long carId;
    private Long companyId;


    private List<Car> cars;
    private List<Company> companies;



    public EditClientDto() {

    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
