package com.example.mkalinova.app.carService.data.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CarServiceDto
{

    @NotNull
    @Size(min=3)
    private String name;

    @PositiveOrZero
    private Double price;

    public CarServiceDto() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
