package com.payments.processor.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StructuredRemittance implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<ReferredDocument> referredDocuments = new ArrayList<>();
    private CreditorReference creditorReference;
    private Party invoiceParty;
    private Party payeeParty;
    private final List<String> additionalInfo = new ArrayList<>();

    public List<ReferredDocument> getReferredDocuments() { return referredDocuments; }
    public void addReferredDocument(ReferredDocument doc) { this.referredDocuments.add(doc); }
    public CreditorReference getCreditorReference() { return creditorReference; }
    public void setCreditorReference(CreditorReference creditorReference) { this.creditorReference = creditorReference; }
    public Party getInvoiceParty() { return invoiceParty; }
    public void setInvoiceParty(Party invoiceParty) { this.invoiceParty = invoiceParty; }
    public Party getPayeeParty() { return payeeParty; }
    public void setPayeeParty(Party payeeParty) { this.payeeParty = payeeParty; }
    public List<String> getAdditionalInfo() { return additionalInfo; }
    public void addAdditionalInfo(String info) { this.additionalInfo.add(info); }
}
