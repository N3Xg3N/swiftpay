package com.payments.processor.model;

import java.io.Serializable;

public class ContactInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String phone;
    private String mobilePhone;
    private String faxNumber;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }

    public String getFaxNumber() { return faxNumber; }
    public void setFaxNumber(String faxNumber) { this.faxNumber = faxNumber; }
}
