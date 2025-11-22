package com.example.mkalinova.app.order.data.dto;

import com.example.mkalinova.app.car.data.entity.Car;
import com.example.mkalinova.app.client.data.entity.Client;
import com.example.mkalinova.app.company.data.entity.Company;
import com.example.mkalinova.app.orderPart.data.OrderPart;
import com.example.mkalinova.app.orderRepair.data.OrderRepair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderListDto {

     private Long id;


    private double subtotal;
    private double discount;
    private double tax;
    private double total;
    private String note;
    private LocalDateTime date;
    private Car car;
    private Client client;
    private Company company;
    private List<OrderRepair> repairsList;
    private List<OrderPart> partsList;

    public OrderListDto() {
        this.repairsList = new ArrayList<>();
        this.partsList = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
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

    public List<OrderRepair> getRepairsList() {
        return repairsList;
    }

    public void setRepairsList(List<OrderRepair> repairsList) {
        this.repairsList = repairsList;
    }

    public List<OrderPart> getPartsList() {
        return partsList;
    }

    public void setPartsList(List<OrderPart> partsList) {
        this.partsList = partsList;
    }
}
