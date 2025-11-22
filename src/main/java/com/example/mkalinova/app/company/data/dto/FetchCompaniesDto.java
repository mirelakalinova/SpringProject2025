package com.example.mkalinova.app.company.data.dto;

public class FetchCompaniesDto {

    private  Long id;
    private String name;
    private String uic;
    private String vatNumber;
    private String address;
    private String accountablePerson;

    public FetchCompaniesDto() {
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
}
