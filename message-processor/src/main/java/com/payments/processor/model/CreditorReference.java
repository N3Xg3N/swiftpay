package com.payments.processor.model;

import java.io.Serializable;

/**
 * Creditor reference information (structured remittance reference).
 */
public class CreditorReference implements Serializable {
    private static final long serialVersionUID = 1L;

    private String referenceType;
    private String referenceValue;
    private String issuer;

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public String getReferenceValue() { return referenceValue; }
    public void setReferenceValue(String referenceValue) { this.referenceValue = referenceValue; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
}
