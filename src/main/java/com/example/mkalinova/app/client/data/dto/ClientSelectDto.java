package com.example.mkalinova.app.client.data.dto;

import java.util.UUID;

public class ClientSelectDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String phone;

    public ClientSelectDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName + " - " + this.phone;
    }
}
