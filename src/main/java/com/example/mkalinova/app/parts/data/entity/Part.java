package com.example.mkalinova.app.parts.data.entity;


import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.repair.data.entity.Repair;
import com.example.mkalinova.app.repairService.data.entity.RepairService;
import jakarta.persistence.*;

@Entity
@Table(name = "parts")
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private double price;

    public Part() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}