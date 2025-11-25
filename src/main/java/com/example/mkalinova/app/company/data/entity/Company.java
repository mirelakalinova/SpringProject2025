package com.example.mkalinova.app.company.data.entity;

import com.example.mkalinova.app.client.data.entity.Client;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "companies")
@Entity
public class Company {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String uic;
    @Column(name = "vat_number", nullable = false)
    private String vatNumber;
    @Column(nullable = false)
    private String address;

    @Column(name = "accountable_person", nullable = false)
    private String accountablePerson;

    @ManyToOne
    @JoinColumn(name = "client_id") // Един клиент има много фирми
    private Client client;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;



    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
    //todo -> safe delete - fields -> created, updated, deleted



    public Company() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
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

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public LocalDateTime deletedAt() {
        return deletedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeleteAd(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

}
