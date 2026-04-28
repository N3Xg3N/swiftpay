package com.fx.payment.config;

import com.fx.payment.model.domain.DomainPayment;
import com.fx.payment.model.pacs009.Pacs009Document;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates singleton {@link JAXBContext} beans for:
 * <ul>
 *   <li>pacs.009.001.08  – ISO 20022 inbound messages</li>
 *   <li>domain-payment   – internal domain model sent to the valid queue</li>
 * </ul>
 *
 * JAXBContext is thread-safe and expensive to create, so it is created once
 * and shared via Spring's singleton scope.
 */
@Configuration
public class JaxbConfig {

    @Bean(name = "pacs009JaxbContext")
    public JAXBContext pacs009JaxbContext() throws JAXBException {
        return JAXBContext.newInstance(Pacs009Document.class);
    }

    @Bean(name = "domainJaxbContext")
    public JAXBContext domainJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(DomainPayment.class);
    }
}
