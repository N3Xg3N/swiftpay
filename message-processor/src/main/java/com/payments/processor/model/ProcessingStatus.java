package com.payments.processor.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Processing status for a transaction.
 */
public class ProcessingStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum StatusCode {
        PARSED, PARTIAL_PARSE, PARSE_ERROR, VALIDATION_FAILED, READY_FOR_PROCESSING
    }

    private StatusCode status;
    private LocalDateTime statusTimestamp;
    private String processingSystem;
    private final List<String> validationErrors;
    private final List<String> warnings;

    public ProcessingStatus() {
        this.statusTimestamp = LocalDateTime.now();
        this.validationErrors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public ProcessingStatus(StatusCode status) {
        this();
        this.status = status;
    }

    public StatusCode getStatus() { return status; }
    public void setStatus(StatusCode status) { this.status = status; }

    public LocalDateTime getStatusTimestamp() { return statusTimestamp; }
    public void setStatusTimestamp(LocalDateTime statusTimestamp) { this.statusTimestamp = statusTimestamp; }

    public String getProcessingSystem() { return processingSystem; }
    public void setProcessingSystem(String processingSystem) { this.processingSystem = processingSystem; }

    public List<String> getValidationErrors() { return validationErrors; }
    public void addValidationError(String error) { this.validationErrors.add(error); }

    public List<String> getWarnings() { return warnings; }
    public void addWarning(String warning) { this.warnings.add(warning); }

    @Override
    public String toString() {
        return "ProcessingStatus{" +
                "status=" + status +
                ", errors=" + validationErrors.size() +
                ", warnings=" + warnings.size() +
                '}';
    }
}
