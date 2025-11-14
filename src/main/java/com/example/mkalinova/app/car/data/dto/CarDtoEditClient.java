package com.example.mkalinova.app.car.data.dto;

public class CarDtoEditClient {

    private Long carId;

    private String registrationNumber;

    public CarDtoEditClient() {
    }

    public Long getId() {
        return carId;
    }

    public void setId(Long id) {
        this.carId = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
