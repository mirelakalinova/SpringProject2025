package com.example.mkalinova.app.company.data.dto;

public class CompanyDtoEditClient {
    private  Long companyId;
    private String name;
    private int uic;

    public CompanyDtoEditClient() {
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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
}
