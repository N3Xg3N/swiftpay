package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/** Unstructured remittance information. */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class RemittanceInformation {

    @XmlElement(name = "Ustrd")
    private String ustrd;
}
