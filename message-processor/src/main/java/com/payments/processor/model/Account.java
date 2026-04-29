package com.payments.processor.model;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private String identificationType;
    private String identificationValue;
    private String name;
    private String currency;
    private String typeCode;
    private String proxy;

    public String getIdentificationType() { return identificationType; }
    public void setIdentificationType(String identificationType) { this.identificationType = identificationType; }

    public String getIdentificationValue() { return identificationValue; }
    public void setIdentificationValue(String identificationValue) { this.identificationValue = identificationValue; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }

    public String getProxy() { return proxy; }
    public void setProxy(String proxy) { this.proxy = proxy; }

    // Aliases for compatibility
    public String getAccountNumber() { return identificationValue; }
    public void setAccountNumber(String accountNumber) { this.identificationValue = accountNumber; }

    public String getAccountType() { return typeCode; }
    public void setAccountType(String accountType) { this.typeCode = accountType; }

    public String getAccountName() { return name; }
    public void setAccountName(String accountName) { this.name = accountName; }
}
