package com.payments.processor.util;

import com.payments.processor.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;

/**
 * Serializes simplified payment batches to XML format.
 * Produces a flat, minimal XML structure optimized for readability and simplicity.
 */
public class SimplifiedPaymentXmlSerializer {

    private static final Logger log = LoggerFactory.getLogger(SimplifiedPaymentXmlSerializer.class);

    /**
     * Serialize a payment batch to simplified XML string.
     */
    public String toXml(SimplifiedPaymentBatch batch) throws Exception {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);

        writer.writeStartDocument("UTF-8", "1.0");
        writePaymentBatch(writer, batch);
        writer.writeEndDocument();
        writer.flush();
        writer.close();

        return sw.toString();
    }

    private void writePaymentBatch(XMLStreamWriter writer, SimplifiedPaymentBatch batch) throws Exception {
        writer.writeStartElement("Batch");
        writer.writeAttribute("id", batch.getBatchHeader() != null ? batch.getBatchHeader().getBatchId() : "");
        writer.writeAttribute("count", String.valueOf(batch.getTransactionCount()));

        // Write each transaction
        for (SimplifiedTransaction txn : batch.getTransactions()) {
            writeTransaction(writer, txn);
        }

        writer.writeEndElement();
    }

    private void writeTransaction(XMLStreamWriter writer, SimplifiedTransaction txn) throws Exception {
        writer.writeStartElement("Txn");
        writer.writeAttribute("id", txn.getTransactionId());
        writer.writeAttribute("amount", txn.getAmount() != null ? txn.getAmount().toPlainString() : "0");
        writer.writeAttribute("ccy", txn.getCurrency() != null ? txn.getCurrency() : "");
        writer.writeAttribute("status", txn.getProcessingStatus() != null ?
            txn.getProcessingStatus().getStatus().toString() : "");

        // Debtor
        if (txn.getDebtor() != null) {
            writer.writeStartElement("Dbtr");
            writeParty(writer, txn.getDebtor());
            writer.writeEndElement();
        }

        // Creditor
        if (txn.getCreditor() != null) {
            writer.writeStartElement("Cdtr");
            writeParty(writer, txn.getCreditor());
            writer.writeEndElement();
        }

        // Remittance Info (simplified)
        if (txn.getRemittanceInfo() != null && !txn.getRemittanceInfo().getUnstructuredInfo().isEmpty()) {
            writer.writeStartElement("Ref");
            for (String info : txn.getRemittanceInfo().getUnstructuredInfo()) {
                writeElement(writer, "Text", info);
            }
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }

    private void writeParty(XMLStreamWriter writer, Party party) throws Exception {
        if (party == null) return;

        writeElement(writer, "Name", party.getName());

        if (party.getPersonalId() != null) {
            writeElement(writer, "ID", party.getPersonalId().getPersonalId());
        } else if (party.getOrganisationId() != null) {
            writeElement(writer, "ID", party.getOrganisationId().getOrganisationId());
        }

        if (party.getPostalAddress() != null) {
            PostalAddress addr = party.getPostalAddress();
            writer.writeStartElement("Addr");
            if (addr.getStreetName() != null) {
                writeElement(writer, "Street", addr.getStreetName());
            }
            if (addr.getTownName() != null) {
                writeElement(writer, "City", addr.getTownName());
            }
            if (addr.getCountry() != null) {
                writeElement(writer, "Country", addr.getCountry());
            }
            writer.writeEndElement();
        }
    }

    private void writeElement(XMLStreamWriter writer, String name, String value) throws Exception {
        if (value == null || value.isEmpty()) {
            return;
        }
        writer.writeStartElement(name);
        writer.writeCharacters(value);
        writer.writeEndElement();
    }
}
