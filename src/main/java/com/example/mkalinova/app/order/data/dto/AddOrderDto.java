package com.example.mkalinova.app.order.data.dto;

import com.example.mkalinova.app.orderPart.data.dto.AddOrderPartDto;
import com.example.mkalinova.app.orderRepair.data.dto.AddOrderRepairDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddOrderDto {


    @NotNull
    private double subtotal;
    @NotNull
    private double discount;
    private double discountAmount;
    private double discountPercent;
    @NotNull
    private double tax;
    @NotNull
    private double total;
    private String note;

    @NotNull
    private long car;
    private long client;
    private long company;
    private List<AddOrderPartDto> parts;
    @NotNull
    @NotEmpty
    private List<AddOrderRepairDto> repairs;

    public AddOrderDto() {
        this.parts = new ArrayList<>();
        this.repairs = new ArrayList<>();
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

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
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


    public long getCar() {
        return car;
    }

    public void setCar(long car) {
        this.car = car;
    }


    public long getClient() {
        return client;
    }

    public void setClient(long client) {
        this.client = client;
    }

    public long getCompany() {
        return company;
    }

    public void setCompany(long company) {
        this.company = company;
    }

    public List<AddOrderPartDto> getParts() {
        return parts;
    }

    public void setParts(List<AddOrderPartDto> parts) {
        this.parts = parts;
    }

    public List<AddOrderRepairDto> getRepairs() {
        return repairs;
    }

    public void setRepairs(List<AddOrderRepairDto> repairs) {
        this.repairs = repairs;
    }
}
