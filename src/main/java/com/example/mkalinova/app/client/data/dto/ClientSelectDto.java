package com.example.mkalinova.app.client.data.dto;

public class ClientSelectDto {

    private long id;
    private String firstName;
    private String lastName;
    private String phone;

    public ClientSelectDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
