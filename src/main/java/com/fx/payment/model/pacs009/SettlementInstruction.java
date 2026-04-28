package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

/** Settlement instruction shared across all transactions in the message. */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SettlementInstruction {

    /**
     * Method used to settle the (batch of) payment instructions.
     * Valid values: CLRG (ClearingSystem), COVE (CoverMethod),
     *               GROS (GrossSettlement), INDA (InstructedAgent).
     */
    @XmlElement(name = "SttlmMtd", required = true)
    private String sttlmMtd;
}
