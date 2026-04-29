package com.payments.processor.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Result object for validation operations.
 * Tracks errors, warnings, and validation status.
 */
public class ConversionValidationResult {

    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;

    public ConversionValidationResult(boolean valid) {
        this.valid = valid;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public ConversionValidationResult addError(String error) {
        this.errors.add(error);
        return this;
    }

    public ConversionValidationResult addWarning(String warning) {
        this.warnings.add(warning);
        return this;
    }

    public ConversionValidationResult addErrors(List<String> errors) {
        this.errors.addAll(errors);
        return this;
    }

    public ConversionValidationResult addWarnings(List<String> warnings) {
        this.warnings.addAll(warnings);
        return this;
    }

    public boolean isValid() {
        return valid && errors.isEmpty();
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }

    public int getErrorCount() {
        return errors.size();
    }

    public int getWarningCount() {
        return warnings.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationResult{")
          .append("valid=").append(isValid());
        if (!errors.isEmpty()) {
            sb.append(", errors=").append(errors);
        }
        if (!warnings.isEmpty()) {
            sb.append(", warnings=").append(warnings);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Factory method for successful validation.
     */
    public static ConversionValidationResult success() {
        return new ConversionValidationResult(true);
    }

    /**
     * Factory method for failed validation.
     */
    public static ConversionValidationResult failure() {
        return new ConversionValidationResult(false);
    }
}
