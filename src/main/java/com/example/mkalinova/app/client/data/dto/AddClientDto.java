package com.example.mkalinova.app.client.data.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public class AddClientDto {


    @Length(min=3 , message = "Името трябва да е поне 3 символа")
    @NotNull
    @NotBlank
    private String firstName;

    @Length(min=3 , message = "Името трябва да е поне 3 символа")
    @NotNull
    @NotBlank
    private String lastName;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^0\\d{9}$", message = "Телефонният номер трябва да започва с 0 и да съдържа 10 цифри")
    private String phone;

    @Email
    private String email;

    @Length(min=4 , message = "Името на компанията трябва да е поне 3 символа")
    private String company;

    @NotNull(message = "ЕИК не трябва да е празно")
    @Digits(integer = 9, fraction = 0, message = "ЕИК трябва да съдържа точно 9 цифри.")
    private String vatNumber;

    private String address;

    private String accountablePerson;

    public AddClientDto() {
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccountablePerson() {
        return accountablePerson;
    }

    public void setAccountablePerson(String accountablePerson) {
        this.accountablePerson = accountablePerson;
    }
}
