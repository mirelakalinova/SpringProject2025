package com.example.mkalinova.app.carService.data.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;


public class EditCarServiceDto {

    private Long id;

    @NotNull
    @Size(min=3)
    private String name;

    @PositiveOrZero
    private Double price;

    public EditCarServiceDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
