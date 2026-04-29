package com.payments.processor.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Garnishment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String garnishmentType;
    private LocalDate garnishmentDate;
    private String referenceNumber;
    private BigDecimal remittedAmount;

    public String getGarnishmentType() { return garnishmentType; }
    public void setGarnishmentType(String garnishmentType) { this.garnishmentType = garnishmentType; }

    public LocalDate getGarnishmentDate() { return garnishmentDate; }
    public void setGarnishmentDate(LocalDate garnishmentDate) { this.garnishmentDate = garnishmentDate; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public BigDecimal getRemittedAmount() { return remittedAmount; }
    public void setRemittedAmount(BigDecimal remittedAmount) { this.remittedAmount = remittedAmount; }
}
