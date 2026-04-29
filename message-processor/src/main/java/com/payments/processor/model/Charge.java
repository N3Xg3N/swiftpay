package com.payments.processor.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Charge implements Serializable {
    private static final long serialVersionUID = 1L;

    private String chargeType;
    private BigDecimal chargeAmount;
    private String chargeCurrency;
    private String chargeDescription;

    public String getChargeType() { return chargeType; }
    public void setChargeType(String chargeType) { this.chargeType = chargeType; }

    public BigDecimal getChargeAmount() { return chargeAmount; }
    public void setChargeAmount(BigDecimal chargeAmount) { this.chargeAmount = chargeAmount; }

    public String getChargeCurrency() { return chargeCurrency; }
    public void setChargeCurrency(String chargeCurrency) { this.chargeCurrency = chargeCurrency; }

    public String getChargeDescription() { return chargeDescription; }
    public void setChargeDescription(String chargeDescription) { this.chargeDescription = chargeDescription; }

    // Aliases for compatibility
    public BigDecimal getAmount() { return chargeAmount; }
    public void setAmount(BigDecimal amount) { this.chargeAmount = amount; }

    public String getCurrency() { return chargeCurrency; }
    public void setCurrency(String currency) { this.chargeCurrency = currency; }

    private Agent chargingAgent;
    public Agent getChargingAgent() { return chargingAgent; }
    public void setChargingAgent(Agent chargingAgent) { this.chargingAgent = chargingAgent; }
}
