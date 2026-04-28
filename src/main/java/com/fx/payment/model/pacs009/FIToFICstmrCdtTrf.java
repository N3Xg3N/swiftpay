package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

/**
 * FI-to-FI Customer Credit Transfer (pacs.009) message body.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FIToFICstmrCdtTrf {

    @XmlElement(name = "GrpHdr", required = true)
    private GroupHeader grpHdr;

    @XmlElement(name = "CdtTrfTxInf", required = true)
    private List<CreditTransferTransaction> cdtTrfTxInf;
}
