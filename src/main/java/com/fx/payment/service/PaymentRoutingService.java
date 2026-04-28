package com.fx.payment.service;

import com.fx.payment.config.JmsConfig;
import com.fx.payment.model.domain.DomainPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * Routes processed payment messages to the appropriate JMS queues.
 *
 * <ul>
 *   <li>{@code fx.payment.valid}   – valid domain payment XML</li>
 *   <li>{@code fx.payment.invalid} – rejected raw XML with error detail</li>
 * </ul>
 *
 * <p>The {@link JmsTemplate} has {@code sessionTransacted=true}, so sends
 * participate in any active JMS-local transaction.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentRoutingService {

    private final JmsTemplate jmsTemplate;
    private final PaymentTransformationService transformationService;

    /**
     * Serialises the domain payment to XML and publishes it to the valid queue.
     *
     * @param domain the transformed domain payment object
     */
    public void routeValid(DomainPayment domain) {
        String xml = transformationService.toXml(domain);
        jmsTemplate.convertAndSend(JmsConfig.VALID_QUEUE, xml);
        log.info("Routed VALID payment to '{}'. paymentId={}",
                JmsConfig.VALID_QUEUE, domain.getPaymentId());
    }

    /**
     * Publishes an invalid / rejected message to the dead-letter queue,
     * including the validation error as a JMS message property.
     *
     * @param rawXml         the original XML that failed validation
     * @param validationError human-readable error description
     */
    public void routeInvalid(String rawXml, String validationError) {
        jmsTemplate.send(JmsConfig.INVALID_QUEUE, session -> {
            jakarta.jms.TextMessage msg = session.createTextMessage(rawXml);
            msg.setStringProperty("ValidationError", truncate(validationError, 500));
            msg.setStringProperty("MessageType", "pacs.009.001.08");
            return msg;
        });
        log.warn("Routed INVALID message to '{}'. reason={}",
                JmsConfig.INVALID_QUEUE, truncate(validationError, 200));
    }

    private String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) + "…" : s;
    }
}
