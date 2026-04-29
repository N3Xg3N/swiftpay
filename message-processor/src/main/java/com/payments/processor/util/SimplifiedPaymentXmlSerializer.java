package com.payments.processor.util;

import com.payments.processor.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Serializes simplified payment batches to XML format.
 */
public class SimplifiedPaymentXmlSerializer {

    private static final Logger log = LoggerFactory.getLogger(SimplifiedPaymentXmlSerializer.class);
    private static final String NS = "urn:com:payments:simplified:v1";

    /**
     * Serialize a payment batch to XML string.
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
        writer.writeStartElement("PaymentBatch");
        writer.writeAttribute("version", "1.0");
        writer.writeDefaultNamespace(NS);

        // Batch Header
        if (batch.getBatchHeader() != null) {
            writeBatchHeader(writer, batch.getBatchHeader());
        }

        // Transactions
        for (SimplifiedTransaction txn : batch.getTransactions()) {
            writeTransaction(writer, txn);
        }

        // Batch Footer
        if (batch.getBatchFooter() != null) {
            writeBatchFooter(writer, batch.getBatchFooter());
        }

        writer.writeEndElement();
    }

    private void writeBatchHeader(XMLStreamWriter writer, SimplifiedPaymentBatch.BatchHeader header) throws Exception {
        writer.writeStartElement("BatchHeader");

        writeElement(writer, "BatchId", header.getBatchId());
        if (header.getCreationDateTime() != null) {
            writeElement(writer, "CreationDateTime", header.getCreationDateTime().toString());
        }
        writeElement(writer, "TransactionCount", String.valueOf(header.getTransactionCount()));
        writeElement(writer, "BatchCurrency", header.getBatchCurrency());
        if (header.getBatchAmount() != null) {
            writeElement(writer, "BatchAmount", header.getBatchAmount().toPlainString());
        }
        writeElement(writer, "SettlementMethod", header.getSettlementMethod());
        if (header.getSettlementDate() != null) {
            writeElement(writer, "SettlementDate", header.getSettlementDate().toString());
        }

        writer.writeEndElement();
    }

    private void writeTransaction(XMLStreamWriter writer, SimplifiedTransaction txn) throws Exception {
        writer.writeStartElement("Transaction");
        if (txn.getSequenceNumber() != null) {
            writer.writeAttribute("sequenceNumber", String.valueOf(txn.getSequenceNumber()));
        }

        // IDs
        writeElement(writer, "TransactionId", txn.getTransactionId());
        writeElement(writer, "EndToEndId", txn.getEndToEndId());
        writeElement(writer, "InstructionId", txn.getInstructionId());
        writeElement(writer, "UETR", txn.getUetr());

        // Amount & Currency
        if (txn.getAmount() != null) {
            writeElement(writer, "Amount", txn.getAmount().toPlainString());
        }
        writeElement(writer, "Currency", txn.getCurrency());
        if (txn.getInstructedAmount() != null) {
            writeElement(writer, "InstructedAmount", txn.getInstructedAmount().toPlainString());
        }
        if (txn.getExchangeRate() != null) {
            writeElement(writer, "ExchangeRate", txn.getExchangeRate().toPlainString());
        }

        // Dates & Times
        if (txn.getTransactionDate() != null) {
            writeElement(writer, "TransactionDate", txn.getTransactionDate().toString());
        }
        if (txn.getSettlementDate() != null) {
            writeElement(writer, "SettlementDate", txn.getSettlementDate().toString());
        }
        if (txn.getPriority() != null) {
            writeElement(writer, "Priority", txn.getPriority().toString());
        }
        if (txn.getDebitDateTime() != null) {
            writeElement(writer, "DebitDateTime", txn.getDebitDateTime().toString());
        }
        if (txn.getCreditDateTime() != null) {
            writeElement(writer, "CreditDateTime", txn.getCreditDateTime().toString());
        }

        // Debtor
        if (txn.getDebtor() != null) {
            writeParty(writer, "Debtor", txn.getDebtor());
        }
        if (txn.getDebtorAccount() != null) {
            writeAccount(writer, "DebtorAccount", txn.getDebtorAccount());
        }
        if (txn.getDebtorAgent() != null) {
            writeAgent(writer, "DebtorAgent", txn.getDebtorAgent());
        }
        if (txn.getUltimateDebtor() != null) {
            writeParty(writer, "UltimateDebtor", txn.getUltimateDebtor());
        }

        // Creditor
        if (txn.getCreditor() != null) {
            writeParty(writer, "Creditor", txn.getCreditor());
        }
        if (txn.getCreditorAccount() != null) {
            writeAccount(writer, "CreditorAccount", txn.getCreditorAccount());
        }
        if (txn.getCreditorAgent() != null) {
            writeAgent(writer, "CreditorAgent", txn.getCreditorAgent());
        }
        if (txn.getUltimateCreditor() != null) {
            writeParty(writer, "UltimateCreditor", txn.getUltimateCreditor());
        }

        // Intermediaries
        if (txn.getInstructingAgent() != null) {
            writeAgent(writer, "InstructingAgent", txn.getInstructingAgent());
        }
        if (txn.getInstructedAgent() != null) {
            writeAgent(writer, "InstructedAgent", txn.getInstructedAgent());
        }
        for (Agent agent : txn.getIntermediaryAgents()) {
            writeAgent(writer, "IntermediaryAgent", agent);
        }

        // Charges
        for (Charge charge : txn.getCharges()) {
            writeCharge(writer, charge);
        }
        if (txn.getChargeBearer() != null) {
            writeElement(writer, "ChargeBearer", txn.getChargeBearer().toString());
        }

        // Payment Details
        writeElement(writer, "Purpose", txn.getPurpose());
        writeElement(writer, "PaymentType", txn.getPaymentType());
        writeElement(writer, "LocalInstrument", txn.getLocalInstrument());
        writeElement(writer, "ServiceLevel", txn.getServiceLevel());

        // Remittance
        if (txn.getRemittanceInfo() != null) {
            writeRemittanceInfo(writer, txn.getRemittanceInfo());
        }

        // Processing
        if (txn.getProcessingStatus() != null) {
            writeProcessingStatus(writer, txn.getProcessingStatus());
        }

        // Notes
        for (String note : txn.getParsingNotes()) {
            writeElement(writer, "ParsingNotes", note);
        }

        writer.writeEndElement();
    }

    private void writeParty(XMLStreamWriter writer, String elementName, Party party) throws Exception {
        writer.writeStartElement(elementName);
        writeElement(writer, "Name", party.getName());
        writeElement(writer, "LEI", party.getLei());
        writeElement(writer, "IdentificationType", party.getIdentificationType());
        writeElement(writer, "IdentificationValue", party.getIdentificationValue());
        writeElement(writer, "CountryOfResidence", party.getCountryOfResidence());
        if (party.getPostalAddress() != null) {
            writePostalAddress(writer, party.getPostalAddress());
        }
        writer.writeEndElement();
    }

    private void writeAccount(XMLStreamWriter writer, String elementName, Account account) throws Exception {
        writer.writeStartElement(elementName);
        writeElement(writer, "AccountNumber", account.getAccountNumber());
        writeElement(writer, "AccountType", account.getAccountType());
        writeElement(writer, "Currency", account.getCurrency());
        writeElement(writer, "AccountName", account.getAccountName());
        writer.writeEndElement();
    }

    private void writeAgent(XMLStreamWriter writer, String elementName, Agent agent) throws Exception {
        writer.writeStartElement(elementName);
        writeElement(writer, "BIC", agent.getBic());
        writeElement(writer, "Name", agent.getName());
        writeElement(writer, "LEI", agent.getLei());
        writeElement(writer, "ClearingSystemId", agent.getClearingSystemId());
        writer.writeEndElement();
    }

    private void writeCharge(XMLStreamWriter writer, Charge charge) throws Exception {
        writer.writeStartElement("Charge");
        if (charge.getAmount() != null) {
            writeElement(writer, "Amount", charge.getAmount().toPlainString());
        }
        writeElement(writer, "Currency", charge.getCurrency());
        writeElement(writer, "ChargeType", charge.getChargeType());
        if (charge.getChargingAgent() != null) {
            writeAgent(writer, "ChargingAgent", charge.getChargingAgent());
        }
        writer.writeEndElement();
    }

    private void writeRemittanceInfo(XMLStreamWriter writer, RemittanceInfo remittance) throws Exception {
        writer.writeStartElement("RemittanceInfo");
        for (String info : remittance.getUnstructuredInfo()) {
            writeElement(writer, "UnstructuredInfo", info);
        }
        writer.writeEndElement();
    }

    private void writePostalAddress(XMLStreamWriter writer, PostalAddress address) throws Exception {
        writer.writeStartElement("PostalAddress");
        writeElement(writer, "Street", address.getStreet());
        writeElement(writer, "BuildingNumber", address.getBuildingNumber());
        writeElement(writer, "PostalCode", address.getPostalCode());
        writeElement(writer, "City", address.getCity());
        writeElement(writer, "Country", address.getCountry());
        for (String line : address.getAddressLines()) {
            writeElement(writer, "AddressLine", line);
        }
        writer.writeEndElement();
    }

    private void writeProcessingStatus(XMLStreamWriter writer, ProcessingStatus status) throws Exception {
        writer.writeStartElement("ProcessingStatus");
        writeElement(writer, "Status", status.getStatus().toString());
        if (status.getStatusTimestamp() != null) {
            writeElement(writer, "StatusTimestamp", status.getStatusTimestamp().toString());
        }
        for (String error : status.getValidationErrors()) {
            writeElement(writer, "ValidationErrors", error);
        }
        for (String warning : status.getWarnings()) {
            writeElement(writer, "Warnings", warning);
        }
        writer.writeEndElement();
    }

    private void writeBatchFooter(XMLStreamWriter writer, SimplifiedPaymentBatch.BatchFooter footer) throws Exception {
        writer.writeStartElement("BatchFooter");
        writeElement(writer, "TotalCount", String.valueOf(footer.getTotalCount()));
        writeElement(writer, "SuccessCount", String.valueOf(footer.getSuccessCount()));
        writeElement(writer, "FailureCount", String.valueOf(footer.getFailureCount()));
        if (footer.getTotalAmount() != null) {
            writeElement(writer, "TotalAmount", footer.getTotalAmount().toPlainString());
        }
        if (footer.getProcessingEndTime() != null) {
            writeElement(writer, "ProcessingEndTime", footer.getProcessingEndTime().toString());
        }
        if (footer.getProcessingDurationMs() != null) {
            writeElement(writer, "ProcessingDurationMs", String.valueOf(footer.getProcessingDurationMs()));
        }
        writer.writeEndElement();
    }

    private void writeElement(XMLStreamWriter writer, String name, String value) throws Exception {
        if (value != null && !value.isEmpty()) {
            writer.writeStartElement(name);
            writer.writeCharacters(value);
            writer.writeEndElement();
        }
    }
}
