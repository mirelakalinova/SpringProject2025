package com.example.mkalinova.app.company.data.dto;

import com.example.mkalinova.app.client.data.entity.Client;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class CompanyListDto {

    private Long id;

    private String name;

    private String uic;
    private String vatNumber;
    private String address;

    private String accountablePerson;


    private Client client;

    private LocalDateTime deletedAt;

    public CompanyListDto() {
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

    public String getUic() {
        return uic;
    }

    public void setUic(String uic) {
        this.uic = uic;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccountablePerson() {
        return accountablePerson;
    }

    public void setAccountablePerson(String accountablePerson) {
        this.accountablePerson = accountablePerson;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
