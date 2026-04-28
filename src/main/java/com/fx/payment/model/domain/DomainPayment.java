package com.fx.payment.model.domain;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Internal FX domain payment object.
 *
 * <p>This is the canonical representation used by downstream FX systems
 * (e.g. position keeper, confirmation engine, nostro reconciler).  It is
 * serialised to XML and published to {@code fx.payment.valid}.
 *
 * <p>The {@code paymentId} field carries the UUID assigned at persistence time,
 * providing a stable correlation key across all downstream systems.
 */
@XmlRootElement(name = "DomainPayment")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class DomainPayment {

    // ── Identity ──────────────────────────────────────────────────────────
    /** Internal UUID assigned on persistence – primary correlation key. */
    @XmlElement(name = "PaymentId", required = true)
    private String paymentId;

    /** Original pacs.009 MsgId. */
    @XmlElement(name = "OriginalMessageId", required = true)
    private String originalMessageId;

    /** pacs.009 TxId (EndToEndId propagated unchanged). */
    @XmlElement(name = "TransactionId", required = true)
    private String transactionId;

    /** End-to-end identification from the originator. */
    @XmlElement(name = "EndToEndId")
    private String endToEndId;

    /** UETR – Unique End-to-end Transaction Reference (UUID format). */
    @XmlElement(name = "UETR")
    private String uetr;

    // ── Settlement ────────────────────────────────────────────────────────
    @XmlElement(name = "SettlementAmount", required = true)
    private BigDecimal settlementAmount;

    @XmlElement(name = "SettlementCurrency", required = true)
    private String settlementCurrency;

    /** ISO date YYYY-MM-DD. */
    @XmlElement(name = "SettlementDate", required = true)
    private String settlementDate;

    /** Settlement method: CLRG | COVE | GROS | INDA. */
    @XmlElement(name = "SettlementMethod")
    private String settlementMethod;

    // ── FX specific ───────────────────────────────────────────────────────
    @XmlElement(name = "ExchangeRate")
    private BigDecimal exchangeRate;

    // ── Debtor ────────────────────────────────────────────────────────────
    @XmlElement(name = "DebtorBIC", required = true)
    private String debtorBic;

    @XmlElement(name = "DebtorName")
    private String debtorName;

    @XmlElement(name = "DebtorIBAN")
    private String debtorIban;

    @XmlElement(name = "DebtorAgentBIC")
    private String debtorAgentBic;

    // ── Creditor ──────────────────────────────────────────────────────────
    @XmlElement(name = "CreditorBIC", required = true)
    private String creditorBic;

    @XmlElement(name = "CreditorName")
    private String creditorName;

    @XmlElement(name = "CreditorIBAN")
    private String creditorIban;

    @XmlElement(name = "CreditorAgentBIC")
    private String creditorAgentBic;

    // ── Charge & Purpose ─────────────────────────────────────────────────
    /** CRED | DEBT | SHAR | SLEV. */
    @XmlElement(name = "ChargeBearer")
    private String chargeBearer;

    @XmlElement(name = "PurposeCode")
    private String purposeCode;

    @XmlElement(name = "RemittanceInfo")
    private String remittanceInfo;

    // ── Processing metadata ───────────────────────────────────────────────
    @XmlElement(name = "ProcessingTimestamp", required = true)
    private String processingTimestamp;

    @XmlElement(name = "PaymentStatus", required = true)
    private String paymentStatus;
}
