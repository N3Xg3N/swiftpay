package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/** Payment identification block (PmtId). */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PaymentIdentification {

    /** Instruction identification – assigned by instructing agent. */
    @XmlElement(name = "InstrId")
    private String instrId;

    /** End-to-end identification – assigned by originator, travels unchanged. */
    @XmlElement(name = "EndToEndId", required = true)
    private String endToEndId;

    /** Transaction identification – unique reference for the transaction. */
    @XmlElement(name = "TxId", required = true)
    private String txId;

    /** Unique End-to-end Transaction Reference (UETR) – UUID format. */
    @XmlElement(name = "UETR")
    private String uetr;
}
