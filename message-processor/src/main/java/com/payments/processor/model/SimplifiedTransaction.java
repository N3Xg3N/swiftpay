package com.payments.processor.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single simplified payment transaction.
 * Flattens ISO 20022 pacs.009 structure into a more usable form.
 */
public class SimplifiedTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    // Transaction IDs
    private String transactionId;
    private String endToEndId;
    private String instructionId;
    private String uetr;
    private Integer sequenceNumber;

    // Amount & Currency
    private BigDecimal amount;
    private String currency;
    private BigDecimal instructedAmount;
    private BigDecimal exchangeRate;

    // Dates & Times
    private LocalDate transactionDate;
    private LocalDate settlementDate;
    private PriorityType priority;
    private LocalDateTime debitDateTime;
    private LocalDateTime creditDateTime;

    // Parties
    private Party debtor;
    private Account debtorAccount;
    private Agent debtorAgent;
    private Party ultimateDebtor;

    private Party creditor;
    private Account creditorAccount;
    private Agent creditorAgent;
    private Party ultimateCreditor;

    // Intermediaries
    private Agent instructingAgent;
    private Agent instructedAgent;
    private final List<Agent> intermediaryAgents;

    // Charges
    private final List<Charge> charges;
    private ChargeBearerType chargeBearer;

    // Payment Details
    private String purpose;
    private String paymentType;
    private String localInstrument;
    private String serviceLevel;

    // Remittance
    private RemittanceInfo remittanceInfo;
    private StructuredRemittance structuredRemittance;

    // Tax & Garnishment
    private TaxInfo taxInfo;
    private Garnishment garnishment;

    // Additional
    private final List<String> creditorAgentInstructions;
    private SimplifiedTransaction underlyingTransaction;
    private ProcessingStatus processingStatus;
    private final List<String> parsingNotes;

    // ═══════════════════════════════════════════════════════════
    // Constructors
    // ═══════════════════════════════════════════════════════════

    public SimplifiedTransaction() {
        this.intermediaryAgents = new ArrayList<>();
        this.charges = new ArrayList<>();
        this.creditorAgentInstructions = new ArrayList<>();
        this.parsingNotes = new ArrayList<>();
    }

    public SimplifiedTransaction(String transactionId, BigDecimal amount, String currency) {
        this();
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
    }

    // ═══════════════════════════════════════════════════════════
    // Getters & Setters
    // ═══════════════════════════════════════════════════════════

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getEndToEndId() { return endToEndId; }
    public void setEndToEndId(String endToEndId) { this.endToEndId = endToEndId; }

    public String getInstructionId() { return instructionId; }
    public void setInstructionId(String instructionId) { this.instructionId = instructionId; }

    public String getUetr() { return uetr; }
    public void setUetr(String uetr) { this.uetr = uetr; }

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getInstructedAmount() { return instructedAmount; }
    public void setInstructedAmount(BigDecimal instructedAmount) { this.instructedAmount = instructedAmount; }

    public BigDecimal getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public LocalDate getSettlementDate() { return settlementDate; }
    public void setSettlementDate(LocalDate settlementDate) { this.settlementDate = settlementDate; }

    public PriorityType getPriority() { return priority; }
    public void setPriority(PriorityType priority) { this.priority = priority; }

    public LocalDateTime getDebitDateTime() { return debitDateTime; }
    public void setDebitDateTime(LocalDateTime debitDateTime) { this.debitDateTime = debitDateTime; }

    public LocalDateTime getCreditDateTime() { return creditDateTime; }
    public void setCreditDateTime(LocalDateTime creditDateTime) { this.creditDateTime = creditDateTime; }

    public Party getDebtor() { return debtor; }
    public void setDebtor(Party debtor) { this.debtor = debtor; }

    public Account getDebtorAccount() { return debtorAccount; }
    public void setDebtorAccount(Account debtorAccount) { this.debtorAccount = debtorAccount; }

    public Agent getDebtorAgent() { return debtorAgent; }
    public void setDebtorAgent(Agent debtorAgent) { this.debtorAgent = debtorAgent; }

    public Party getUltimateDebtor() { return ultimateDebtor; }
    public void setUltimateDebtor(Party ultimateDebtor) { this.ultimateDebtor = ultimateDebtor; }

    public Party getCreditor() { return creditor; }
    public void setCreditor(Party creditor) { this.creditor = creditor; }

    public Account getCreditorAccount() { return creditorAccount; }
    public void setCreditorAccount(Account creditorAccount) { this.creditorAccount = creditorAccount; }

    public Agent getCreditorAgent() { return creditorAgent; }
    public void setCreditorAgent(Agent creditorAgent) { this.creditorAgent = creditorAgent; }

    public Party getUltimateCreditor() { return ultimateCreditor; }
    public void setUltimateCreditor(Party ultimateCreditor) { this.ultimateCreditor = ultimateCreditor; }

    public Agent getInstructingAgent() { return instructingAgent; }
    public void setInstructingAgent(Agent instructingAgent) { this.instructingAgent = instructingAgent; }

    public Agent getInstructedAgent() { return instructedAgent; }
    public void setInstructedAgent(Agent instructedAgent) { this.instructedAgent = instructedAgent; }

    public List<Agent> getIntermediaryAgents() { return intermediaryAgents; }
    public void addIntermediaryAgent(Agent agent) { this.intermediaryAgents.add(agent); }

    public List<Charge> getCharges() { return charges; }
    public void addCharge(Charge charge) { this.charges.add(charge); }

    public ChargeBearerType getChargeBearer() { return chargeBearer; }
    public void setChargeBearer(ChargeBearerType chargeBearer) { this.chargeBearer = chargeBearer; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getLocalInstrument() { return localInstrument; }
    public void setLocalInstrument(String localInstrument) { this.localInstrument = localInstrument; }

    public String getServiceLevel() { return serviceLevel; }
    public void setServiceLevel(String serviceLevel) { this.serviceLevel = serviceLevel; }

    public RemittanceInfo getRemittanceInfo() { return remittanceInfo; }
    public void setRemittanceInfo(RemittanceInfo remittanceInfo) { this.remittanceInfo = remittanceInfo; }

    public StructuredRemittance getStructuredRemittance() { return structuredRemittance; }
    public void setStructuredRemittance(StructuredRemittance structuredRemittance) { this.structuredRemittance = structuredRemittance; }

    public TaxInfo getTaxInfo() { return taxInfo; }
    public void setTaxInfo(TaxInfo taxInfo) { this.taxInfo = taxInfo; }

    public Garnishment getGarnishment() { return garnishment; }
    public void setGarnishment(Garnishment garnishment) { this.garnishment = garnishment; }

    public List<String> getCreditorAgentInstructions() { return creditorAgentInstructions; }
    public void addCreditorAgentInstruction(String instruction) { this.creditorAgentInstructions.add(instruction); }

    public SimplifiedTransaction getUnderlyingTransaction() { return underlyingTransaction; }
    public void setUnderlyingTransaction(SimplifiedTransaction underlyingTransaction) { this.underlyingTransaction = underlyingTransaction; }

    public ProcessingStatus getProcessingStatus() { return processingStatus; }
    public void setProcessingStatus(ProcessingStatus processingStatus) { this.processingStatus = processingStatus; }

    public List<String> getParsingNotes() { return parsingNotes; }
    public void addParsingNote(String note) { this.parsingNotes.add(note); }

    @Override
    public String toString() {
        return "SimplifiedTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", debtor=" + debtor +
                ", creditor=" + creditor +
                '}';
    }

    // ═══════════════════════════════════════════════════════════
    // Enums & Inner Classes
    // ═══════════════════════════════════════════════════════════

    public enum PriorityType {
        HIGH, NORMAL, URGENT
    }

    public enum ChargeBearerType {
        DEBT,  // Debtor bears charges
        CRED,  // Creditor bears charges
        SHAR,  // Shared between debtor and creditor
        SLEV   // Service level charges
    }
}
