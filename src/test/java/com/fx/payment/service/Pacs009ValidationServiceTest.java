package com.fx.payment.service;

import com.fx.payment.config.JaxbConfig;
import com.fx.payment.exception.PaymentValidationException;
import com.fx.payment.model.pacs009.Pacs009Document;
import jakarta.xml.bind.JAXBContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link Pacs009ValidationService}.
 *
 * Uses a Spring context only for JAXB bean wiring; no JMS or DB is started.
 */
@SpringBootTest(classes = {Pacs009ValidationService.class, JaxbConfig.class})
@ActiveProfiles("test")
class Pacs009ValidationServiceTest {

    @Autowired
    private Pacs009ValidationService validationService;

    // ── Valid message ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should successfully validate and unmarshal a valid pacs.009 message")
    void shouldValidateAndUnmarshalValidMessage() throws IOException {
        String rawXml = loadTestMessage("messages/valid-pacs009.xml");

        Pacs009Document doc = validationService.validateAndUnmarshal(rawXml);

        assertThat(doc).isNotNull();
        assertThat(doc.getFiToFICstmrCdtTrf()).isNotNull();
        assertThat(doc.getFiToFICstmrCdtTrf().getGrpHdr().getMsgId())
                .isEqualTo("FX-MSG-20240415-001");
        assertThat(doc.getFiToFICstmrCdtTrf().getCdtTrfTxInf()).hasSize(1);
    }

    @Test
    @DisplayName("Should correctly map settlement amount and currency")
    void shouldMapSettlementAmountAndCurrency() throws IOException {
        String rawXml = loadTestMessage("messages/valid-pacs009.xml");

        Pacs009Document doc = validationService.validateAndUnmarshal(rawXml);

        var tx = doc.getFiToFICstmrCdtTrf().getCdtTrfTxInf().get(0);
        assertThat(tx.getIntrBkSttlmAmt().getCcy()).isEqualTo("USD");
        assertThat(tx.getIntrBkSttlmAmt().getValue()).isEqualByComparingTo("1250000.00");
        assertThat(tx.getIntrBkSttlmDt()).isEqualTo("2024-04-17");
    }

    @Test
    @DisplayName("Should map payment identification fields correctly")
    void shouldMapPaymentIdentification() throws IOException {
        String rawXml = loadTestMessage("messages/valid-pacs009.xml");

        Pacs009Document doc = validationService.validateAndUnmarshal(rawXml);

        var pmtId = doc.getFiToFICstmrCdtTrf().getCdtTrfTxInf().get(0).getPmtId();
        assertThat(pmtId.getTxId()).isEqualTo("TXN-20240415-001");
        assertThat(pmtId.getEndToEndId()).isEqualTo("E2E-20240415-001");
        assertThat(pmtId.getUetr()).isEqualTo("a1b2c3d4-e5f6-4789-ab01-cd2345ef6789");
    }

    @Test
    @DisplayName("Should map debtor and creditor BICs")
    void shouldMapDebtorAndCreditorBics() throws IOException {
        String rawXml = loadTestMessage("messages/valid-pacs009.xml");

        Pacs009Document doc = validationService.validateAndUnmarshal(rawXml);

        var tx = doc.getFiToFICstmrCdtTrf().getCdtTrfTxInf().get(0);
        assertThat(tx.getDbtr().getFinInstnId().getBicfi()).isEqualTo("BARCGB22");
        assertThat(tx.getCdtr().getFinInstnId().getBicfi()).isEqualTo("JPMSGB2L");
    }

    @Test
    @DisplayName("Should map exchange rate for cross-currency transaction")
    void shouldMapExchangeRate() throws IOException {
        String rawXml = loadTestMessage("messages/valid-pacs009-eurjpy.xml");

        Pacs009Document doc = validationService.validateAndUnmarshal(rawXml);

        var tx = doc.getFiToFICstmrCdtTrf().getCdtTrfTxInf().get(0);
        assertThat(tx.getXchgRate()).isEqualByComparingTo("161.8900000000");
        assertThat(tx.getIntrBkSttlmAmt().getCcy()).isEqualTo("JPY");
    }

    // ── Invalid messages ──────────────────────────────────────────────────

    @Test
    @DisplayName("Should throw PaymentValidationException when TxId is missing")
    void shouldThrowExceptionWhenTxIdMissing() throws IOException {
        String rawXml = loadTestMessage("messages/invalid-pacs009-missing-txid.xml");

        assertThatThrownBy(() -> validationService.validateAndUnmarshal(rawXml))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("XSD validation failed");
    }

    @Test
    @DisplayName("Should include the raw XML in the exception")
    void shouldIncludeRawXmlInException() throws IOException {
        String rawXml = loadTestMessage("messages/invalid-pacs009-missing-txid.xml");

        assertThatThrownBy(() -> validationService.validateAndUnmarshal(rawXml))
                .isInstanceOf(PaymentValidationException.class)
                .satisfies(ex -> {
                    PaymentValidationException pve = (PaymentValidationException) ex;
                    assertThat(pve.getRawXml()).isEqualTo(rawXml);
                });
    }

    @Test
    @DisplayName("Should throw PaymentValidationException for completely malformed XML")
    void shouldThrowForMalformedXml() {
        String garbage = "<not-valid-xml>";

        assertThatThrownBy(() -> validationService.validateAndUnmarshal(garbage))
                .isInstanceOf(PaymentValidationException.class);
    }

    @Test
    @DisplayName("Should throw PaymentValidationException for empty string")
    void shouldThrowForEmptyInput() {
        assertThatThrownBy(() -> validationService.validateAndUnmarshal(""))
                .isInstanceOf(PaymentValidationException.class);
    }

    @Test
    @DisplayName("Should throw PaymentValidationException for wrong namespace")
    void shouldThrowForWrongNamespace() {
        String wrongNs = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Document xmlns="urn:wrong:namespace">
                    <FIToFICstmrCdtTrf/>
                </Document>
                """;

        assertThatThrownBy(() -> validationService.validateAndUnmarshal(wrongNs))
                .isInstanceOf(PaymentValidationException.class);
    }

    @Test
    @DisplayName("Should throw PaymentValidationException for invalid currency code (4-letter)")
    void shouldThrowForInvalidCurrencyCode() throws IOException {
        String rawXml = loadTestMessage("messages/invalid-pacs009-bad-currency.xml");

        assertThatThrownBy(() -> validationService.validateAndUnmarshal(rawXml))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("XSD validation failed");
    }

    @Test
    @DisplayName("Should throw PaymentValidationException for invalid settlement method code")
    void shouldThrowForInvalidSettlementMethod() throws IOException {
        String rawXml = loadTestMessage("messages/invalid-pacs009-bad-sttlm-method.xml");

        assertThatThrownBy(() -> validationService.validateAndUnmarshal(rawXml))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessageContaining("XSD validation failed");
    }

    // ── Helper ────────────────────────────────────────────────────────────

    private String loadTestMessage(String classpathPath) throws IOException {
        var resource = getClass().getClassLoader().getResourceAsStream(classpathPath);
        assertThat(resource).as("Test resource not found: " + classpathPath).isNotNull();
        return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
    }
}
