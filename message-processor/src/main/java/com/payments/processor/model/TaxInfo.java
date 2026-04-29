package com.payments.processor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TaxInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taxId;
    private String taxType;
    private String taxCategory;
    private final List<String> taxRecords;

    public TaxInfo() {
        this.taxRecords = new ArrayList<>();
    }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getTaxType() { return taxType; }
    public void setTaxType(String taxType) { this.taxType = taxType; }

    public String getTaxCategory() { return taxCategory; }
    public void setTaxCategory(String taxCategory) { this.taxCategory = taxCategory; }

    public List<String> getTaxRecords() { return taxRecords; }
    public void addTaxRecord(String record) { this.taxRecords.add(record); }
}
