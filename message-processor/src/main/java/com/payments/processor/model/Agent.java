package com.payments.processor.model;

import java.io.Serializable;

public class Agent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String lei;
    private String bic;
    private String identificationType;
    private String identificationValue;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLei() { return lei; }
    public void setLei(String lei) { this.lei = lei; }

    public String getBic() { return bic; }
    public void setBic(String bic) { this.bic = bic; }

    public String getIdentificationType() { return identificationType; }
    public void setIdentificationType(String identificationType) { this.identificationType = identificationType; }

    public String getIdentificationValue() { return identificationValue; }
    public void setIdentificationValue(String identificationValue) { this.identificationValue = identificationValue; }

    // Aliases for compatibility
    public String getClearingSystemId() { return bic; }
    public void setClearingSystemId(String clearingSystemId) { this.bic = clearingSystemId; }
}
