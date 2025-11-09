package com.example.mkalinova.app.car.data.entity;


import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.repair.data.entity.Repair;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "registration_number", unique = true)
    @Size(min = 7, max = 8)
    private String registrationNumber;
    @Column(name = "year_of_manufacture")
    @Min(value = 1900)
    @Max(value = 9999)
    private int year;

    @Min(value = 500)
    @Max(value = 8000)
    @Column(name = "engine_capacity")
    @NotNull
    private int cube;

    @Column
    @Size(min = 17, max = 17)
    private String vin;
    @Column
    private int kw;
    @Column
    private int hp;

    @Column
    private String make;

    @Column
    private String model;

    @OneToMany(mappedBy = "car", fetch = FetchType.EAGER)
    private List<Repair> repairs;
    // за да могат да се променят или изтриват коли без това да се отразява на другите свързани коли с клиента на конкретната
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "client_id")
    private Client client;

    public Car(List<Repair> repairs) {
        this.repairs = new ArrayList<>();
    }

    public Car() {
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCube() {
        return cube;
    }

    public void setCube(int cube) {
        this.cube = cube;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public int getKw() {
        return kw;
    }

    public void setKw(int kw) {
        this.kw = kw;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public List<Repair> getRepairs() {
        return repairs;
    }

    public void setRepairs(List<Repair> repairs) {
        this.repairs = repairs;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {

        return "Кола \n" +
                "================ \n" +
                String.format("Регистрационен номер: %-15s\n", registrationNumber) +
                String.format("Марка: %-20s\n", model) +
                String.format("Модел: %-20s\n", make) +
                String.format("Година на производство: %-4d\n", year) +
                String.format("Кубатура: %-10.2f\n", cube) +
                String.format("Шаси: %-17s\n", vin);


    }
}