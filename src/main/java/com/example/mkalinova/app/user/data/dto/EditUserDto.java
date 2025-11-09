package com.example.mkalinova.app.user.data.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class EditUserDto {

    private Long id;
    @Length(min = 3, message = "Името трябва да е поне 3 символа")
    @NotNull
    private String firstName;
    @Length(min = 3, message = "Името трябва да е поне 3 символа")
    private String lastName;
    @Length(min=5, message = "Потребителското име трябва да е поне 5 символа")

    private String username;
    @Email(message = "Моля, въведете валиден имейл адрес")

    private String email;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S{8,}$", message = "Паролата трябва да е най-малко 8 символа и да съдържа поне 1 цифра, поне 1 символ и поне 1 голяма буква!")
    private String password;

    private String role;

    public EditUserDto() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
