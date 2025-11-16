package com.example.mkalinova.app.client.data.entity;

import com.example.mkalinova.app.car.data.entity.Car;

import com.example.mkalinova.app.company.data.entity.Company;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    // Persist -> за да може да се изтриват данните за всичко към клиента. После с поръчките ще стане мазало :)
    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST,  fetch = FetchType.EAGER)
    private List<Car> cars = new ArrayList<>();

    // Свързване с фирми (може да има много фирми)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL,  fetch = FetchType.EAGER)
    private List<com.example.mkalinova.app.company.data.entity.Company> companies = new ArrayList<>();
    //todo -> safe delete - fields -> created, updated, deleted


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

    @Override
    public String toString() {
        return "Клиент \n" +
                "================ \n" +
                "Име: '" + firstName + '\n' +
                "Фамилия: " + lastName + '\n' +
                "email: '" + email + '\n' +
                "телефон: '" + phone + '\n'
                ;
    }
}
