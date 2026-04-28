package com.fx.payment.service;

import com.fx.payment.entity.PaymentMessage;
import com.fx.payment.entity.PaymentStatus;
import com.fx.payment.model.pacs009.CreditTransferTransaction;
import com.fx.payment.model.pacs009.Pacs009Document;
import com.fx.payment.repository.PaymentMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handles all database persistence for payment messages.
 *
 * <p>Each public method is wrapped in a Spring {@code @Transactional} boundary.
 * For the happy-path flow, {@link #persistValidMessage} stores the raw XML plus
 * key attributes in one atomic write; the returned {@link PaymentMessage} carries
 * the generated UUID that is subsequently embedded in the domain payment object.
 *
 * <p><strong>Note on XA transactions:</strong> In production, persisting to the
 * database and publishing to the JMS broker should be atomic.  The recommended
 * approach is the <em>Transactional Outbox Pattern</em> (persist an outbox row
 * in the same JDBC transaction, then relay via a scheduled poller).  For
 * simplicity this implementation uses separate JDBC and JMS transactions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentPersistenceService {

    private final PaymentMessageRepository repository;

    /**
     * Persists a successfully validated pacs.009 message and extracts key fields.
     *
     * @param doc    the unmarshalled pacs.009 document
     * @param rawXml the original XML string (stored verbatim for audit)
     * @return the persisted {@link PaymentMessage} with its generated UUID
     */
    @Transactional
    public PaymentMessage persistValidMessage(Pacs009Document doc, String rawXml) {
        CreditTransferTransaction tx = doc.getFiToFICstmrCdtTrf()
                .getCdtTrfTxInf().get(0);

        PaymentMessage entity = PaymentMessage.builder()
                .messageId(doc.getFiToFICstmrCdtTrf().getGrpHdr().getMsgId())
                .transactionId(tx.getPmtId().getTxId())
                .endToEndId(tx.getPmtId().getEndToEndId())
                .uetr(tx.getPmtId().getUetr())
                .settlementAmount(tx.getIntrBkSttlmAmt().getValue())
                .settlementCurrency(tx.getIntrBkSttlmAmt().getCcy())
                .settlementDate(tx.getIntrBkSttlmDt() != null
                        ? java.time.LocalDate.parse(tx.getIntrBkSttlmDt()) : null)
                .exchangeRate(tx.getXchgRate())
                .debtorBic(safeGetBic(tx, true))
                .creditorBic(safeGetBic(tx, false))
                .status(PaymentStatus.VALIDATED)
                .rawXml(rawXml)
                .build();

        PaymentMessage saved = repository.save(entity);
        log.info("Persisted valid payment. id={} msgId={} txId={}",
                saved.getId(), saved.getMessageId(), saved.getTransactionId());
        return saved;
    }

    /**
     * Persists a rejected (XSD-invalid) message for audit purposes.
     *
     * @param rawXml         the raw XML that failed validation
     * @param validationError human-readable description of the failure
     * @return the persisted {@link PaymentMessage}
     */
    @Transactional
    public PaymentMessage persistInvalidMessage(String rawXml, String validationError) {
        PaymentMessage entity = PaymentMessage.builder()
                .status(PaymentStatus.INVALID)
                .rawXml(rawXml)
                .validationErrors(truncate(validationError, 2000))
                .build();

        PaymentMessage saved = repository.save(entity);
        log.warn("Persisted INVALID payment message. id={}", saved.getId());
        return saved;
    }

    /**
     * Updates the status of an existing record (e.g. VALIDATED → PROCESSED).
     */
    @Transactional
    public void updateStatus(UUID id, PaymentStatus newStatus) {
        repository.findById(id).ifPresentOrElse(
                msg -> {
                    msg.setStatus(newStatus);
                    repository.save(msg);
                    log.debug("Updated payment {} status → {}", id, newStatus);
                },
                () -> log.error("Cannot update status – payment not found: {}", id)
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private String safeGetBic(CreditTransferTransaction tx, boolean debtor) {
        try {
            return debtor
                    ? tx.getDbtr().getFinInstnId().getBicfi()
                    : tx.getCdtr().getFinInstnId().getBicfi();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) : s;
    }
}
