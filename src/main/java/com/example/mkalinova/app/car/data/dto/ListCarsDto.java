package com.example.mkalinova.app.car.data.dto;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

public class ListCarsDto {
    @Valid
    private List<EditCarDto> cars;

    public ListCarsDto() {
        this.cars = new ArrayList<>();
    }

    // Гетъри и сетъри
    public List<EditCarDto> getCars() {
        return cars;
    }

    public void setCars(List<EditCarDto> cars) {
        this.cars = cars;
    }


}
