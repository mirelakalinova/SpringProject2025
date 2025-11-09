package com.example.mkalinova.app.company.data.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddCompanyDto {
    private boolean checked;
    @NotNull(message = "Името на фирмата не трябва да е празно")
    @NotBlank(message = "Името на фирмата не трябва да е празно")
    private String name;

    @NotNull(message = "ЕИК не трябва да е празно")

    @Digits(integer = 9, fraction = 0, message = "ЕИК трябва да съдържа точно 9 цифри.")
    private int uic;

    @NotBlank(message = "Моля въведете адрес")
    @NotNull(message = "Моля въведете адрес")
    private String address;

    @NotBlank(message = "Моля въведете МОЛ")
    @NotNull(message = "Моля въведете МОЛ")
    private String accountablePerson;


    @NotBlank
    @NotNull
    private String vatNumber;

    public AddCompanyDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUic() {
        return uic;
    }

    public void setUic(int uic) {
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


    public boolean isAllFiledsNullOrEmpty() {
        return (name == null || name.isEmpty()) &&
                (uic == 0) &&
                (address == null || address.isEmpty()) &&
                (accountablePerson == null || accountablePerson.isEmpty());
    }
}
