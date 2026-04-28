package com.fx.payment.service;

import com.fx.payment.config.JaxbConfig;
import com.fx.payment.entity.PaymentMessage;
import com.fx.payment.entity.PaymentStatus;
import com.fx.payment.model.domain.DomainPayment;
import com.fx.payment.model.pacs009.Pacs009Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = {PaymentTransformationService.class, Pacs009ValidationService.class, JaxbConfig.class})
@ActiveProfiles("test")
class PaymentTransformationServiceTest {

    @Autowired private PaymentTransformationService transformationService;
    @Autowired private Pacs009ValidationService validationService;

    // ── toDomainPayment ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should map UUID from persisted entity to domain payment ID")
    void shouldMapPaymentIdFromEntity() throws Exception {
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009.xml"));
        PaymentMessage entity = buildEntity();

        DomainPayment domain = transformationService.toDomainPayment(doc, entity);

        assertThat(domain.getPaymentId()).isEqualTo(entity.getId().toString());
    }

    @Test
    @DisplayName("Should map settlement fields correctly")
    void shouldMapSettlementFields() throws Exception {
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009.xml"));

        DomainPayment domain = transformationService.toDomainPayment(doc, buildEntity());

        assertThat(domain.getSettlementAmount()).isEqualByComparingTo("1250000.00");
        assertThat(domain.getSettlementCurrency()).isEqualTo("USD");
        assertThat(domain.getSettlementDate()).isEqualTo("2024-04-17");
        assertThat(domain.getSettlementMethod()).isEqualTo("GROS");
    }

    @Test
    @DisplayName("Should map exchange rate for FX transaction")
    void shouldMapExchangeRate() throws Exception {
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009-eurjpy.xml"));

        DomainPayment domain = transformationService.toDomainPayment(doc, buildEntity());

        assertThat(domain.getExchangeRate()).isEqualByComparingTo("161.8900000000");
    }

    @Test
    @DisplayName("Should map debtor BIC and name")
    void shouldMapDebtorInfo() throws Exception {
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009.xml"));

        DomainPayment domain = transformationService.toDomainPayment(doc, buildEntity());

        assertThat(domain.getDebtorBic()).isEqualTo("BARCGB22");
        assertThat(domain.getDebtorName()).isEqualTo("Barclays Bank PLC");
        assertThat(domain.getDebtorIban()).isEqualTo("GB29BARC20000055779911");
    }

    @Test
    @DisplayName("Should map creditor BIC and name")
    void shouldMapCreditorInfo() throws Exception {
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009.xml"));

        DomainPayment domain = transformationService.toDomainPayment(doc, buildEntity());

        assertThat(domain.getCreditorBic()).isEqualTo("JPMSGB2L");
        assertThat(domain.getCreditorName()).isEqualTo("JP Morgan Chase Bank NA London");
        assertThat(domain.getCreditorIban()).isEqualTo("GB94JPMC50100012345678");
    }

    @Test
    @DisplayName("Should map UETR from pacs.009 to domain payment")
    void shouldMapUetr() throws Exception {
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009.xml"));

        DomainPayment domain = transformationService.toDomainPayment(doc, buildEntity());

        assertThat(domain.getUetr()).isEqualTo("a1b2c3d4-e5f6-4789-ab01-cd2345ef6789");
    }

    @Test
    @DisplayName("Should set payment status to PROCESSED")
    void shouldSetPaymentStatusToProcessed() throws Exception {
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009.xml"));

        DomainPayment domain = transformationService.toDomainPayment(doc, buildEntity());

        assertThat(domain.getPaymentStatus()).isEqualTo("PROCESSED");
    }

    @Test
    @DisplayName("Should populate processing timestamp")
    void shouldPopulateProcessingTimestamp() throws Exception {
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009.xml"));

        DomainPayment domain = transformationService.toDomainPayment(doc, buildEntity());

        assertThat(domain.getProcessingTimestamp()).isNotBlank();
    }

    @Test
    @DisplayName("Should map purpose code and remittance info")
    void shouldMapPurposeAndRemittance() throws Exception {
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009.xml"));

        DomainPayment domain = transformationService.toDomainPayment(doc, buildEntity());

        assertThat(domain.getPurposeCode()).isEqualTo("CORT");
        assertThat(domain.getRemittanceInfo()).isEqualTo("FX Trade Settlement REF-2024-XYZ-1001");
    }

    // ── toXml ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should serialise DomainPayment to well-formed XML")
    void shouldSerialiseToXml() throws Exception {
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009.xml"));
        DomainPayment domain = transformationService.toDomainPayment(doc, buildEntity());

        String xml = transformationService.toXml(domain);

        assertThat(xml).startsWith("<?xml");
        assertThat(xml).contains("DomainPayment");
        assertThat(xml).contains("urn:com:fx:payment:domain:v1");
    }

    @Test
    @DisplayName("Serialised XML should contain payment UUID")
    void serialisedXmlShouldContainPaymentId() throws Exception {
        PaymentMessage entity = buildEntity();
        Pacs009Document doc = validationService.validateAndUnmarshal(loadXml("messages/valid-pacs009.xml"));
        DomainPayment domain = transformationService.toDomainPayment(doc, entity);

        String xml = transformationService.toXml(domain);

        assertThat(xml).contains(entity.getId().toString());
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private PaymentMessage buildEntity() {
        return PaymentMessage.builder()
                .id(UUID.randomUUID())
                .status(PaymentStatus.VALIDATED)
                .build();
    }

    private String loadXml(String path) throws Exception {
        try (var is = getClass().getClassLoader().getResourceAsStream(path)) {
            assertThat(is).as("Resource not found: " + path).isNotNull();
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
