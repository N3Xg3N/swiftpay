package com.payments.processor.model;

import java.io.Serializable;

public class PostalAddress implements Serializable {
    private static final long serialVersionUID = 1L;

    private String addressType;
    private String country;
    private String townName;
    private String countrySubDivision;
    private String postalCode;
    private String buildingNumber;
    private String streetName;
    private String addressLine;

    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getTownName() { return townName; }
    public void setTownName(String townName) { this.townName = townName; }

    public String getCountrySubDivision() { return countrySubDivision; }
    public void setCountrySubDivision(String countrySubDivision) { this.countrySubDivision = countrySubDivision; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getBuildingNumber() { return buildingNumber; }
    public void setBuildingNumber(String buildingNumber) { this.buildingNumber = buildingNumber; }

    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }

    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

    // Aliases for compatibility
    public String getStreet() { return streetName; }
    public void setStreet(String street) { this.streetName = street; }

    public String getCity() { return townName; }
    public void setCity(String city) { this.townName = city; }

    public String getRegion() { return countrySubDivision; }
    public void setRegion(String region) { this.countrySubDivision = region; }

    public void addAddressLine(String line) { this.addressLine = line; }
    public java.util.List<String> getAddressLines() {
        java.util.List<String> lines = new java.util.ArrayList<>();
        if (addressLine != null) lines.add(addressLine);
        return lines;
    }
}
