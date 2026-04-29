package com.payments.processor.model;

import java.io.Serializable;

public class OrganisationId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String organisationId;
    private String schemeName;
    private String issuer;

    public String getOrganisationId() { return organisationId; }
    public void setOrganisationId(String organisationId) { this.organisationId = organisationId; }

    public String getSchemeName() { return schemeName; }
    public void setSchemeName(String schemeName) { this.schemeName = schemeName; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
}
