package com.payments.processor.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about a referred document (invoice, credit note, etc.).
 */
public class ReferredDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    private String documentType;
    private String documentNumber;
    private LocalDate documentDate;
    private BigDecimal documentAmount;
    private final List<LineDetail> lineDetails;

    public ReferredDocument() {
        this.lineDetails = new ArrayList<>();
    }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public LocalDate getDocumentDate() { return documentDate; }
    public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }

    public BigDecimal getDocumentAmount() { return documentAmount; }
    public void setDocumentAmount(BigDecimal documentAmount) { this.documentAmount = documentAmount; }

    public List<LineDetail> getLineDetails() { return lineDetails; }
    public void addLineDetail(LineDetail detail) { this.lineDetails.add(detail); }
}

class LineDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private String lineType;
    private String lineNumber;
    private LocalDate lineDate;
    private String description;
    private BigDecimal amount;
    private String quantityUOM;
    private BigDecimal quantityValue;

    public String getLineType() { return lineType; }
    public void setLineType(String lineType) { this.lineType = lineType; }

    public String getLineNumber() { return lineNumber; }
    public void setLineNumber(String lineNumber) { this.lineNumber = lineNumber; }

    public LocalDate getLineDate() { return lineDate; }
    public void setLineDate(LocalDate lineDate) { this.lineDate = lineDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getQuantityUOM() { return quantityUOM; }
    public void setQuantityUOM(String quantityUOM) { this.quantityUOM = quantityUOM; }

    public BigDecimal getQuantityValue() { return quantityValue; }
    public void setQuantityValue(BigDecimal quantityValue) { this.quantityValue = quantityValue; }
}
