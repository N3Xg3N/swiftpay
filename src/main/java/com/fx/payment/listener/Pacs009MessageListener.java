package com.fx.payment.listener;

import com.fx.payment.config.JmsConfig;
import com.fx.payment.orchestration.PaymentOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * JMS listener that consumes raw pacs.009 XML messages from
 * {@value JmsConfig#INBOUND_QUEUE} and delegates to the orchestration service.
 *
 * <p>The listener container is configured with {@code sessionTransacted=true}
 * (see {@link com.fx.payment.config.JmsConfig}), so the message is
 * acknowledged only after this method returns successfully.  Any uncaught
 * exception causes a redelivery up to the broker's configured retry limit,
 * after which the message is dead-lettered.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Pacs009MessageListener {

    private final PaymentOrchestrationService orchestrationService;

    @JmsListener(
            destination = JmsConfig.INBOUND_QUEUE,
            containerFactory = "jmsListenerContainerFactory"
    )
    public void onMessage(String rawXml) {
        log.info("Received message on '{}' (len={})", JmsConfig.INBOUND_QUEUE, rawXml.length());
        orchestrationService.process(rawXml);
    }
}
