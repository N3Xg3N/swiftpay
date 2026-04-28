package com.fx.payment.model.pacs009;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Credit Transfer Transaction Information (CdtTrfTxInf).
 *
 * <p>Each pacs.009 message may carry one or more of these.  In FX workflows a
 * single transaction per message is most common (e.g. one FX trade settlement).
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreditTransferTransaction {

    // ── Identification ────────────────────────────────────────────────────
    @XmlElement(name = "PmtId", required = true)
    private PaymentIdentification pmtId;

    // ── Settlement ────────────────────────────────────────────────────────
    /** Interbank settlement amount and currency. */
    @XmlElement(name = "IntrBkSttlmAmt", required = true)
    private ActiveCurrencyAndAmount intrBkSttlmAmt;

    /** Interbank settlement date (YYYY-MM-DD). */
    @XmlElement(name = "IntrBkSttlmDt", required = true)
    private String intrBkSttlmDt;

    /** FX rate applied – present for cross-currency transactions. */
    @XmlElement(name = "XchgRate")
    private BigDecimal xchgRate;

    /**
     * Charge bearer.
     * CRED = creditor, DEBT = debtor, SHAR = shared, SLEV = service level.
     */
    @XmlElement(name = "ChrgBr", required = true)
    private String chrgBr;

    // ── Debtor (sending financial institution) ───────────────────────────
    @XmlElement(name = "Dbtr", required = true)
    private BranchAndFinancialInstitutionIdentification dbtr;

    @XmlElement(name = "DbtrAcct")
    private CashAccount dbtrAcct;

    @XmlElement(name = "DbtrAgt", required = true)
    private BranchAndFinancialInstitutionIdentification dbtrAgt;

    // ── Creditor (receiving financial institution) ────────────────────────
    @XmlElement(name = "CdtrAgt", required = true)
    private BranchAndFinancialInstitutionIdentification cdtrAgt;

    @XmlElement(name = "Cdtr", required = true)
    private BranchAndFinancialInstitutionIdentification cdtr;

    @XmlElement(name = "CdtrAcct")
    private CashAccount cdtrAcct;

    // ── Remittance & Purpose ──────────────────────────────────────────────
    @XmlElement(name = "Purp")
    private Purpose purp;

    @XmlElement(name = "RmtInf")
    private RemittanceInformation rmtInf;
}
