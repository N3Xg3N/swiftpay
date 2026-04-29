package com.payments.processor.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simplified payment batch model for internal use.
 * Abstracts away ISO 20022 complexity.
 */
public class SimplifiedPaymentBatch implements Serializable {
    private static final long serialVersionUID = 1L;

    private BatchHeader batchHeader;
    private final List<SimplifiedTransaction> transactions;
    private BatchFooter batchFooter;

    public SimplifiedPaymentBatch() {
        this.transactions = new ArrayList<>();
    }

    // ═══════════════════════════════════════════════════════════
    // Getters & Setters
    // ═══════════════════════════════════════════════════════════

    public BatchHeader getBatchHeader() {
        return batchHeader;
    }

    public void setBatchHeader(BatchHeader batchHeader) {
        this.batchHeader = batchHeader;
    }

    public List<SimplifiedTransaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(SimplifiedTransaction transaction) {
        this.transactions.add(transaction);
    }

    public BatchFooter getBatchFooter() {
        return batchFooter;
    }

    public void setBatchFooter(BatchFooter batchFooter) {
        this.batchFooter = batchFooter;
    }

    // ═══════════════════════════════════════════════════════════
    // Utilities
    // ═══════════════════════════════════════════════════════════

    public int getTransactionCount() {
        return transactions.size();
    }

    public BigDecimal getTotalAmount() {
        return transactions.stream()
            .map(SimplifiedTransaction::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return "SimplifiedPaymentBatch{" +
                "batchHeader=" + batchHeader +
                ", transactionCount=" + transactions.size() +
                ", batchFooter=" + batchFooter +
                '}';
    }

    // ═══════════════════════════════════════════════════════════
    // BATCH HEADER
    // ═══════════════════════════════════════════════════════════

    public static class BatchHeader implements Serializable {
        private static final long serialVersionUID = 1L;

        private String batchId;
        private LocalDateTime creationDateTime;
        private Integer transactionCount;
        private String batchCurrency;
        private BigDecimal batchAmount;
        private String settlementMethod;
        private LocalDate settlementDate;

        public BatchHeader() {}

        public BatchHeader(String batchId, LocalDateTime creationDateTime) {
            this.batchId = batchId;
            this.creationDateTime = creationDateTime;
        }

        public String getBatchId() { return batchId; }
        public void setBatchId(String batchId) { this.batchId = batchId; }

        public LocalDateTime getCreationDateTime() { return creationDateTime; }
        public void setCreationDateTime(LocalDateTime creationDateTime) { this.creationDateTime = creationDateTime; }

        public Integer getTransactionCount() { return transactionCount; }
        public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

        public String getBatchCurrency() { return batchCurrency; }
        public void setBatchCurrency(String batchCurrency) { this.batchCurrency = batchCurrency; }

        public BigDecimal getBatchAmount() { return batchAmount; }
        public void setBatchAmount(BigDecimal batchAmount) { this.batchAmount = batchAmount; }

        public String getSettlementMethod() { return settlementMethod; }
        public void setSettlementMethod(String settlementMethod) { this.settlementMethod = settlementMethod; }

        public LocalDate getSettlementDate() { return settlementDate; }
        public void setSettlementDate(LocalDate settlementDate) { this.settlementDate = settlementDate; }

        @Override
        public String toString() {
            return "BatchHeader{" +
                    "batchId='" + batchId + '\'' +
                    ", creationDateTime=" + creationDateTime +
                    ", transactionCount=" + transactionCount +
                    ", batchCurrency='" + batchCurrency + '\'' +
                    ", batchAmount=" + batchAmount +
                    '}';
        }
    }

    // ═══════════════════════════════════════════════════════════
    // BATCH FOOTER
    // ═══════════════════════════════════════════════════════════

    public static class BatchFooter implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer totalCount;
        private Integer successCount;
        private Integer failureCount;
        private BigDecimal totalAmount;
        private LocalDateTime processingEndTime;
        private Long processingDurationMs;

        public BatchFooter() {}

        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

        public Integer getSuccessCount() { return successCount; }
        public void setSuccessCount(Integer successCount) { this.successCount = successCount; }

        public Integer getFailureCount() { return failureCount; }
        public void setFailureCount(Integer failureCount) { this.failureCount = failureCount; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public LocalDateTime getProcessingEndTime() { return processingEndTime; }
        public void setProcessingEndTime(LocalDateTime processingEndTime) { this.processingEndTime = processingEndTime; }

        public Long getProcessingDurationMs() { return processingDurationMs; }
        public void setProcessingDurationMs(Long processingDurationMs) { this.processingDurationMs = processingDurationMs; }

        @Override
        public String toString() {
            return "BatchFooter{" +
                    "totalCount=" + totalCount +
                    ", successCount=" + successCount +
                    ", failureCount=" + failureCount +
                    ", totalAmount=" + totalAmount +
                    '}';
        }
    }
}
