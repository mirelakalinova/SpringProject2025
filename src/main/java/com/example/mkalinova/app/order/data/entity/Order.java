package com.example.mkalinova.app.order.data.entity;


import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.orderPart.data.OrderPart;
import com.example.mkalinova.app.orderRepair.data.OrderRepair;
import com.example.mkalinova.app.parts.data.entity.Part;
import com.example.mkalinova.app.repair.data.entity.Repair;
import jakarta.persistence.*;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Default;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private UUID id;


    private double subtotal;
    private Double discount;
    @Column(name = "discount_amount")
    private Double discountAmount;
    @Column(name = "discount_percent")
    private Double discountPercent;
    private double tax;
    private double total;
    private String note;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column
    private LocalDateTime date; //todo rename to createdAt ...

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "orders_parts", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "part_id"))
    private List<OrderPart> partList;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "orders_repair", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "repair_id"))
    private List<OrderRepair> repairList;

    public Order() {
        this.partList = new ArrayList<>();
        this.repairList = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        if (discount == null) {
            this.discount = 0D;
        } else {

            this.discount = discount;
        }
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        if (discountAmount == null) {
            this.discountAmount = 0D;
        } else {

            this.discountAmount = discountAmount;
        }
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        if (discountPercent == null) {
            this.discountPercent = 0D;
        } else {

            this.discountPercent = discountPercent;
        }
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
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

    public List<OrderPart> getPartList() {
        return partList;
    }

    public void setPartList(List<OrderPart> partList) {
        this.partList = partList;
    }

    public List<OrderRepair> getRepairList() {
        return repairList;
    }

    public void setRepairList(List<OrderRepair> repairList) {
        this.repairList = repairList;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }
}