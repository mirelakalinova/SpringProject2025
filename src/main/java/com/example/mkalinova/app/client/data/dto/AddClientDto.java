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


}
