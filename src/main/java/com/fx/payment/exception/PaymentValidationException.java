package com.fx.payment.exception;

/**
 * Thrown when a pacs.009 message fails XSD validation.
 *
 * <p>The message is caught by the orchestration layer, the raw XML is stored
 * with status {@code INVALID}, and the message is routed to
 * {@code fx.payment.invalid}.
 */
public class PaymentValidationException extends RuntimeException {

    private final String rawXml;

    public PaymentValidationException(String message, String rawXml) {
        super(message);
        this.rawXml = rawXml;
    }

    public PaymentValidationException(String message, String rawXml, Throwable cause) {
        super(message, cause);
        this.rawXml = rawXml;
    }

    public String getRawXml() {
        return rawXml;
    }
}
