package com.example.mkalinova.app.user.data.dto;

import com.example.mkalinova.app.user.data.entity.UsersRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class AddUserDto {

    @Length(min=3 , message = "Името трябва да е поне 3 символа")
    @NotNull
    private String firstName;
    @Length(min=3 , message = "Името трябва да е поне 3 символа")
    private String lastName;
    @Length(min=5, message = "Потребителското име трябва да е поне 5 символа")
    @NotNull
    private String username;
    @Email(message = "Моля, въведете валиден имейл адрес")
    @NotNull
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S{8,}$", message = "Паролата трябва да е най-малко 8 символа и да съдържа поне 1 цифра, поне 1 символ и поне 1 голяма буква!")
    private String password;
    @NotNull(message = "Изберете роля!")
    private String role;

    public AddUserDto() {
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
}
