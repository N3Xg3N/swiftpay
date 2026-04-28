package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/** Account identification block (Id inside CashAccount). */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class AccountIdentification {

    /** IBAN – up to 34 alphanumeric characters. */
    @XmlElement(name = "IBAN")
    private String iban;
}
