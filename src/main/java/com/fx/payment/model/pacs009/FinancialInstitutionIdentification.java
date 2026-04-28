package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/** Financial Institution Identification (FinInstnId). */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FinancialInstitutionIdentification {

    /** BIC (Business Identifier Code) – 8 or 11 characters. */
    @XmlElement(name = "BICFI")
    private String bicfi;

    /** Institution name. */
    @XmlElement(name = "Nm")
    private String nm;
}
