package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Group Header (GrpHdr) – common information for all transactions in the message.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class GroupHeader {

    /** Unique message identifier assigned by the instructing agent. Max 35 chars. */
    @XmlElement(name = "MsgId", required = true)
    private String msgId;

    /** Creation date and time of the message (ISO 8601). */
    @XmlElement(name = "CreDtTm", required = true)
    private String creDtTm;

    /** Number of individual transactions in the message. */
    @XmlElement(name = "NbOfTxs", required = true)
    private String nbOfTxs;

    /** Settlement information. */
    @XmlElement(name = "SttlmInf", required = true)
    private SettlementInstruction sttlmInf;
}
