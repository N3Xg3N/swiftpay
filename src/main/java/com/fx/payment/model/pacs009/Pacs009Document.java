package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/**
 * Root element of a pacs.009.001.08 ISO 20022 message.
 */
@XmlRootElement(name = "Document")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Pacs009Document {

    @XmlElement(name = "FIToFICstmrCdtTrf", required = true)
    private FIToFICstmrCdtTrf fiToFICstmrCdtTrf;
}
