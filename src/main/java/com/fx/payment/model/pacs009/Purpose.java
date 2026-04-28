package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/** Purpose of the credit transfer (ISO 20022 ExternalPurpose1Code). */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Purpose {

    /** Proprietary code; e.g. CORT, TREA, INTC, BEXP. */
    @XmlElement(name = "Cd")
    private String cd;
}
