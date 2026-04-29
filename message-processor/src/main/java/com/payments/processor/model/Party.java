package com.payments.processor.model;

import java.io.Serializable;

/**
 * Represents a party in a payment transaction (payer, payee, ultimate debtor/creditor).
 */
public class Party implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String lei;
    private String identificationType;
    private String identificationValue;
    private String countryOfResidence;
    private PostalAddress postalAddress;
    private ContactInfo contactInfo;
    private OrganisationId organisationId;
    private PersonalId personalId;
    private PartyType partyType;

    public enum PartyType {
        PERSON, ORGANISATION, UNKNOWN
    }

    public Party() {}
    public Party(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLei() { return lei; }
    public void setLei(String lei) { this.lei = lei; }

    public String getIdentificationType() { return identificationType; }
    public void setIdentificationType(String identificationType) { this.identificationType = identificationType; }

    public String getIdentificationValue() { return identificationValue; }
    public void setIdentificationValue(String identificationValue) { this.identificationValue = identificationValue; }

    public String getCountryOfResidence() { return countryOfResidence; }
    public void setCountryOfResidence(String countryOfResidence) { this.countryOfResidence = countryOfResidence; }

    public PostalAddress getPostalAddress() { return postalAddress; }
    public void setPostalAddress(PostalAddress postalAddress) { this.postalAddress = postalAddress; }

    public ContactInfo getContactInfo() { return contactInfo; }
    public void setContactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; }

    public OrganisationId getOrganisationId() { return organisationId; }
    public void setOrganisationId(OrganisationId organisationId) { this.organisationId = organisationId; }

    public PersonalId getPersonalId() { return personalId; }
    public void setPersonalId(PersonalId personalId) { this.personalId = personalId; }

    public PartyType getPartyType() { return partyType; }
    public void setPartyType(PartyType partyType) { this.partyType = partyType; }
}
