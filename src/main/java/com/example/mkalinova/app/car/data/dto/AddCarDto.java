package com.example.mkalinova.app.car.data.dto;


import com.example.mkalinova.app.client.data.entity.Client;
import jakarta.validation.constraints.*;


public class AddCarDto {
    @NotBlank(message = "Въведете регистрационен номер")
    @NotNull
    @Size(min=7, max=8, message = "Моля въведете коректен регистрационен номер!")
    @Pattern(regexp = "[A-Z0-9]+", message = "Регистрационният номер може да съдържа само букви и цифри.")
    private String registrationNumber;
    @NotNull(message = "Изберете марка")
    private String model;
    @NotNull(message = "Изберете модел")
    private String make;
    @Min(value = 1900, message = "Годината на производство трябва да бъде след 1900.")
    @Max(value = 9999, message = "Годината на производство не може да бъде по-голяма от 9999.")
    private int year;
    @NotNull(message = "Кубатурата не може да е празна стойност")

    @Min(value = 500, message = "Кубатурата не може да е по-малка от 500")
    @Max(value = 8000, message = "Кубатурата не може да надвишава 8000")
    private int cube;
    @NotNull(message = "Моля, въведете киловати")
    private int kw;
    @NotNull(message = "Моля, въведете конски сили")
    private int hp;
    @NotNull(message = "Номер на шаси не може да е празна стойност!")
    @NotBlank(message = "Номер на шаси не може да е празна стойност!")
    @Size(min=17, max=17, message = "Номер на шаси трябва да е точно 17 символа!")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Номер на шаси може да съдържа само букви и цифри.")
    private String vin;
    private Client client;

    public AddCarDto() {
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }



    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getCube() {
        return cube;
    }

    public void setCube(int cube) {
        this.cube = cube;
    }

    public int getKw() {
        return kw;
    }

    public void setKw(int kw) {
        this.kw = kw;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }



}
