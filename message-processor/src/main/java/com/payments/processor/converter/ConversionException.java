package com.payments.processor.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when conversion between message formats fails.
 */
public class ConversionException extends Exception {

    private final String sourceFormat;
    private final String targetFormat;
    private final List<String> details;

    public ConversionException(String message, String sourceFormat, String targetFormat) {
        super(message);
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
        this.details = new ArrayList<>();
    }

    public ConversionException(String message, String sourceFormat, String targetFormat, Throwable cause) {
        super(message, cause);
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
        this.details = new ArrayList<>();
    }

    public String getSourceFormat() {
        return sourceFormat;
    }

    public String getTargetFormat() {
        return targetFormat;
    }

    public List<String> getDetails() {
        return details;
    }

    public void addDetail(String detail) {
        this.details.add(detail);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConversionException: ").append(getMessage())
          .append("\n  Source: ").append(sourceFormat)
          .append("\n  Target: ").append(targetFormat);
        if (!details.isEmpty()) {
            sb.append("\n  Details:");
            for (String detail : details) {
                sb.append("\n    - ").append(detail);
            }
        }
        return sb.toString();
    }
}
