package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/** Amount with currency attribute – e.g. {@code <IntrBkSttlmAmt Ccy="USD">1000.00</IntrBkSttlmAmt>}. */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ActiveCurrencyAndAmount {

    /** ISO 4217 3-letter currency code. */
    @XmlAttribute(name = "Ccy", required = true)
    private String ccy;

    /** Decimal amount value. */
    @XmlValue
    private BigDecimal value;
}
