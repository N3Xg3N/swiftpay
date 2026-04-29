package com.payments.processor.model;

import java.io.Serializable;
import java.time.LocalDate;

public class PersonalId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String personalId;
    private String personalIdType;
    private LocalDate dateOfBirth;
    private String countryOfIssue;

    public String getPersonalId() { return personalId; }
    public void setPersonalId(String personalId) { this.personalId = personalId; }

    public String getPersonalIdType() { return personalIdType; }
    public void setPersonalIdType(String personalIdType) { this.personalIdType = personalIdType; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getCountryOfIssue() { return countryOfIssue; }
    public void setCountryOfIssue(String countryOfIssue) { this.countryOfIssue = countryOfIssue; }
}
