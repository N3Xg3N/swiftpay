package com.fx.payment.entity;

/**
 * Lifecycle status of a pacs.009 message received by the processor.
 */
public enum PaymentStatus {
    /** Message received, XSD validation pending. */
    RECEIVED,
    /** Message passed XSD validation and has been persisted. */
    VALIDATED,
    /** Domain payment object created and sent to the valid queue. */
    PROCESSED,
    /** Message failed XSD validation; sent to the invalid queue. */
    INVALID,
    /** Unexpected processing error. */
    ERROR
}
