package com.example.mkalinova.app.carServiceRepair.data.entity;


import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.repair.data.entity.Repair;
import com.example.mkalinova.app.repairService.data.entity.RepairService;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car_services_repairs")
public class
CarServiceRepair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private int quantity;
    @ManyToOne
    private RepairService service;

    @ManyToOne
    private Car car;

    private double price;

    @ManyToOne
    @JoinColumn(name = "repair_id")  // Добавяме колоната за свързване към Repair
    private Repair repair;

    public CarServiceRepair() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RepairService getService() {
        return service;
    }

    public void setService(RepairService service) {
        this.service = service;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Repair getRepair() {
        return repair;
    }

    public void setRepair(Repair repair) {
        this.repair = repair;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}