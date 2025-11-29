package com.example.mkalinova.app.company.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import java.util.UUID;

public class AddCompanyDto {
    private boolean checked;
    @NotNull(message = "Името на фирмата не трябва да е празно")
    @NotBlank(message = "Името на фирмата не трябва да е празно")
    private String name;

    @NotNull(message = "ЕИК не трябва да е празно")
    @Length(min = 9, max = 9, message = "ЕИК номерът трябва да е точно 9 цифри!")
    @Pattern(regexp = "^[0-9]{9}$")
    private String uic;

    @NotBlank(message = "Моля въведете адрес")
    @NotNull(message = "Моля въведете адрес")
    private String address;

    @NotBlank(message = "Моля въведете МОЛ")
    @NotNull(message = "Моля въведете МОЛ")
    private String accountablePerson;

    @NotBlank
    @NotNull
    private String vatNumber;


    private UUID clientId;

    public AddCompanyDto() {
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

    public boolean isShowForm() {
        return checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }


    public boolean isAllFieldsNullOrEmpty() {
        return (name == null || name.isEmpty()) &&
                (uic.isEmpty() || uic.isBlank()) &&
                (address == null || address.isEmpty()) &&
                (accountablePerson == null || accountablePerson.isEmpty());
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }
}
