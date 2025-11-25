package com.example.mkalinova.app.car.data.dto;

import java.util.UUID;

public class CarDtoEditClient {

    private UUID  carId;

    private String registrationNumber;

    public CarDtoEditClient() {
    }

    public UUID getCarId() {
        return carId;
    }

    public void setId(UUID id) {
        this.carId = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
