package com.example.mkalinova.app.client.data.entity;

import com.example.mkalinova.app.car.data.entity.Car;

import com.example.mkalinova.app.company.data.entity.Company;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(nullable = false, unique = true)
    private String phone;
    @Column
    private String email;
    @Column(name="deleted_at")
    private LocalDateTime deleteAd;
    private String name;

    @OneToMany(mappedBy = "client",fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Car> cars = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL,  fetch = FetchType.EAGER)
    private List<com.example.mkalinova.app.company.data.entity.Company> companies = new ArrayList<>();

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = new ArrayList<>();
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = new ArrayList<>();
    }

    public Client() {
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

    public String getName() {
        return firstName + " " +  lastName;
    }

    private void setName(String name) {
        this.name = name;
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

    public LocalDateTime getDeleteAd() {
        return deleteAd;
    }


    public void setDeleteAd(LocalDateTime deleteAd) {
        this.deleteAd = deleteAd;
    }

}
