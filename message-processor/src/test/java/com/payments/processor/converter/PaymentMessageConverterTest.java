package com.payments.processor.converter;

import com.payments.processor.model.SimplifiedPaymentBatch;
import com.payments.processor.model.SimplifiedTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

@DisplayName("pacs.009 to Simplified Converter Tests")
class PaymentMessageConverterTest {

    private PaymentMessageConverter converter;
    private String samplePacs009Xml;

    @BeforeEach
    void setUp() throws ConversionException, IOException {
        converter = new PaymentMessageConverter();
        samplePacs009Xml = loadSampleFile();
    }

    @Test
    @DisplayName("Should successfully convert valid pacs.009 message")
    void testSuccessfulConversion() throws ConversionException {
        // Act
        SimplifiedPaymentBatch batch = converter.convert(samplePacs009Xml);

        // Assert
        assertThat(batch).isNotNull();
        assertThat(batch.getBatchHeader()).isNotNull();
        assertThat(batch.getBatchHeader().getBatchId()).isEqualTo("MSG-20250101-001");
        assertThat(batch.getTransactionCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should extract batch header correctly")
    void testBatchHeaderExtraction() throws ConversionException {
        // Act
        SimplifiedPaymentBatch batch = converter.convert(samplePacs009Xml);

        // Assert
        SimplifiedPaymentBatch.BatchHeader header = batch.getBatchHeader();
        assertThat(header).isNotNull();
        assertThat(header.getBatchId()).isEqualTo("MSG-20250101-001");
        assertThat(header.getTransactionCount()).isEqualTo(2);
        assertThat(header.getSettlementMethod()).isEqualTo("INGA");
    }

    @Test
    @DisplayName("Should extract first transaction correctly")
    void testFirstTransactionExtraction() throws ConversionException {
        // Act
        SimplifiedPaymentBatch batch = converter.convert(samplePacs009Xml);

        // Assert
        assertThat(batch.getTransactions()).hasSize(2);
        SimplifiedTransaction txn = batch.getTransactions().get(0);
        assertThat(txn.getTransactionId()).isEqualTo("TXN-001");
        assertThat(txn.getEndToEndId()).isEqualTo("E2E-001");
        assertThat(txn.getAmount().toPlainString()).isEqualTo("1000000.00");
        assertThat(txn.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should extract debtor information")
    void testDebtorExtraction() throws ConversionException {
        // Act
        SimplifiedPaymentBatch batch = converter.convert(samplePacs009Xml);
        SimplifiedTransaction txn = batch.getTransactions().get(0);

        // Assert
        assertThat(txn.getDebtor()).isNotNull();
        assertThat(txn.getDebtorAccount()).isNotNull();
        assertThat(txn.getDebtorAccount().getAccountNumber()).isEqualTo("DE75512108001234567890");
        assertThat(txn.getDebtorAccount().getAccountType()).isEqualTo("IBAN");
    }

    @Test
    @DisplayName("Should extract creditor information")
    void testCreditorExtraction() throws ConversionException {
        // Act
        SimplifiedPaymentBatch batch = converter.convert(samplePacs009Xml);
        SimplifiedTransaction txn = batch.getTransactions().get(0);

        // Assert
        assertThat(txn.getCreditor()).isNotNull();
        assertThat(txn.getCreditorAccount()).isNotNull();
        assertThat(txn.getCreditorAccount().getAccountNumber()).isEqualTo("US90010000000000000001");
        assertThat(txn.getCreditorAccount().getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should extract remittance information")
    void testRemittanceExtraction() throws ConversionException {
        // Act
        SimplifiedPaymentBatch batch = converter.convert(samplePacs009Xml);
        SimplifiedTransaction txn = batch.getTransactions().get(0);

        // Assert
        assertThat(txn.getRemittanceInfo()).isNotNull();
        assertThat(txn.getRemittanceInfo().getUnstructuredInfo())
            .contains("Payment for Invoice INV-2025-001 due 2025-01-15");
    }

    @Test
    @DisplayName("Should validate correct pacs.009 message")
    void testValidateCorrectMessage() {
        // Act
        ConversionValidationResult result = converter.validate(samplePacs009Xml);

        // Assert
        assertThat(result.isValid()).isTrue();
    }

    @Test
    @DisplayName("Should fail validation for null input")
    void testValidateNullInput() {
        // Act
        ConversionValidationResult result = converter.validate(null);

        // Assert
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation for empty input")
    void testValidateEmptyInput() {
        // Act
        ConversionValidationResult result = converter.validate("");

        // Assert
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation for non-XML input")
    void testValidateNonXmlInput() {
        // Act
        ConversionValidationResult result = converter.validate("This is not XML");

        // Assert
        assertThat(result.isValid()).isFalse();
    }

    @Test
    @DisplayName("Should process batch conversion")
    void testBatchConversion() throws ConversionException {
        // Act
        java.util.List<SimplifiedPaymentBatch> results = converter.convertBatch(
            java.util.Arrays.asList(samplePacs009Xml, samplePacs009Xml)
        );

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTransactionCount()).isEqualTo(2);
        assertThat(results.get(1).getTransactionCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle second transaction with high priority")
    void testSecondTransactionPriority() throws ConversionException {
        // Act
        SimplifiedPaymentBatch batch = converter.convert(samplePacs009Xml);
        SimplifiedTransaction txn = batch.getTransactions().get(1);

        // Assert
        assertThat(txn.getTransactionId()).isEqualTo("TXN-002");
        assertThat(txn.getPriority()).isEqualTo(SimplifiedTransaction.PriorityType.HIGH);
        assertThat(txn.getAmount().toPlainString()).isEqualTo("500000.00");
    }

    @Test
    @DisplayName("Should include processing status and notes")
    void testProcessingStatusAndNotes() throws ConversionException {
        // Act
        SimplifiedPaymentBatch batch = converter.convert(samplePacs009Xml);
        SimplifiedTransaction txn = batch.getTransactions().get(0);

        // Assert
        assertThat(txn.getProcessingStatus()).isNotNull();
        assertThat(txn.getParsingNotes()).isNotEmpty();
    }

    @Test
    @DisplayName("Should calculate batch totals")
    void testBatchTotals() throws ConversionException {
        // Act
        SimplifiedPaymentBatch batch = converter.convert(samplePacs009Xml);

        // Assert
        assertThat(batch.getTotalAmount().toPlainString()).isEqualTo("1500000.00");
        assertThat(batch.getBatchFooter().getTotalCount()).isEqualTo(2);
    }

    // ═══════════════════════════════════════════════════════════
    // Helper Methods
    // ═══════════════════════════════════════════════════════════

    private String loadSampleFile() throws IOException {
        return new String(
            Files.readAllBytes(Paths.get("src/test/resources/samples/sample-pacs009.xml")),
            StandardCharsets.UTF_8
        );
    }
}
