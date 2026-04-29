package com.payments.processor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Remittance information for payment.
 */
public class RemittanceInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<String> unstructuredInfo;
    private StructuredRemittance structuredInfo;

    public RemittanceInfo() {
        this.unstructuredInfo = new ArrayList<>();
    }

    public List<String> getUnstructuredInfo() { return unstructuredInfo; }
    public void addUnstructuredInfo(String info) { this.unstructuredInfo.add(info); }

    public StructuredRemittance getStructuredInfo() { return structuredInfo; }
    public void setStructuredInfo(StructuredRemittance structuredInfo) { this.structuredInfo = structuredInfo; }
}
