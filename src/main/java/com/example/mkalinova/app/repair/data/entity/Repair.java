package com.example.mkalinova.app.repair.data.entity;


import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.carParts.data.entity.CarPart;
import com.example.mkalinova.app.carServiceRepair.data.entity.CarServiceRepair;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.company.data.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repairs")
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;


    @OneToMany(mappedBy = "repair")
    private List<CarPart> parts = new ArrayList<>();  // Един ремонт има много части

    @OneToMany(mappedBy = "repair")
    private List<CarServiceRepair> services = new ArrayList<>();

    private double total;

    public Repair() {
        this.services = new ArrayList<>();
        this.parts=new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<CarPart> getParts() {
        return parts;
    }

    public void setParts(List<CarPart> parts) {
        this.parts = parts;
    }

    public List<CarServiceRepair> getServices() {
        return services;
    }

    public void setServices(List<CarServiceRepair> services) {
        this.services = services;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}