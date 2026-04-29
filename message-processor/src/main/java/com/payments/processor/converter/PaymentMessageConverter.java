package com.payments.processor.converter;

import com.payments.processor.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Converter from ISO 20022 pacs.009.001.08 to internal simplified format.
 *
 * This converter:
 * 1. Parses pacs.009 XML into JAXB objects
 * 2. Extracts core payment information
 * 3. Maps to simplified internal model
 * 4. Validates and logs conversion details
 */
public class PaymentMessageConverter implements PaymentConverter<String, SimplifiedPaymentBatch> {

    private static final Logger log = LoggerFactory.getLogger(PaymentMessageConverter.class);

    private final JAXBContext jaxbContext;
    private final Unmarshaller unmarshaller;

    public PaymentMessageConverter() throws ConversionException {
        try {
            // Note: In production, you'd generate JAXB classes from the pacs.009 XSD
            // For this example, we'll parse as generic XML and extract via DOM/XPath
            this.jaxbContext = null;
            this.unmarshaller = null;
            log.info("Initialized PaymentMessageConverter");
        } catch (Exception e) {
            throw new ConversionException(
                "Failed to initialize JAXBContext",
                "pacs.009.001.08",
                "simplified-v1",
                e
            );
        }
    }

    @Override
    public SimplifiedPaymentBatch convert(String sourceXml) throws ConversionException {
        try {
            long startTime = System.currentTimeMillis();

            // Parse the source XML
            SimplifiedPaymentBatch batch = parseAndConvert(sourceXml);

            // Record processing metadata
            batch.setBatchFooter(createBatchFooter(
                batch.getTransactionCount(),
                System.currentTimeMillis() - startTime
            ));

            log.info("Successfully converted {} transactions in {} ms",
                batch.getTransactionCount(),
                System.currentTimeMillis() - startTime);

            return batch;

        } catch (ConversionException e) {
            throw e;
        } catch (Exception e) {
            ConversionException ce = new ConversionException(
                "Unexpected error during conversion: " + e.getMessage(),
                "pacs.009.001.08",
                "simplified-v1",
                e
            );
            ce.addDetail("Root cause: " + e.getClass().getSimpleName());
            throw ce;
        }
    }

    @Override
    public List<SimplifiedPaymentBatch> convertBatch(List<String> sources) throws ConversionException {
        List<SimplifiedPaymentBatch> results = new ArrayList<>();
        int processed = 0;
        int failed = 0;

        for (String source : sources) {
            try {
                results.add(convert(source));
                processed++;
            } catch (ConversionException e) {
                failed++;
                log.warn("Failed to convert message {}/{}: {}", processed + failed, sources.size(), e.getMessage());
            }
        }

        if (failed > 0) {
            log.warn("Batch conversion: {} processed, {} failed", processed, failed);
        }

        return results;
    }

    @Override
    public ConversionValidationResult validate(String source) {
        ConversionValidationResult result = ConversionValidationResult.success();

        try {
            if (source == null || source.trim().isEmpty()) {
                return ConversionValidationResult.failure()
                    .addError("Source XML is empty or null");
            }

            // Check XML structure
            if (!source.trim().startsWith("<")) {
                return ConversionValidationResult.failure()
                    .addError("Source does not appear to be XML");
            }

            // Check for required pacs.009 root element
            if (!source.contains("FIToFIFrdLnsTrf") && !source.contains("pacs.009")) {
                result.addWarning("Document doesn't appear to be pacs.009 format");
            }

            // Try to parse (basic validation)
            try {
                javax.xml.parsers.DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(source.getBytes()));
            } catch (Exception e) {
                return ConversionValidationResult.failure()
                    .addError("XML parsing failed: " + e.getMessage());
            }

        } catch (Exception e) {
            return ConversionValidationResult.failure()
                .addError("Validation error: " + e.getMessage());
        }

        return result;
    }

    // ═══════════════════════════════════════════════════════════
    // Private Methods
    // ═══════════════════════════════════════════════════════════

    private SimplifiedPaymentBatch parseAndConvert(String sourceXml) throws Exception {
        SimplifiedPaymentBatch batch = new SimplifiedPaymentBatch();

        // For this implementation, we'll parse using DOM
        // In production, use JAXB with generated classes
        javax.xml.parsers.DocumentBuilder builder =
            javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(sourceXml.getBytes()));

        // Extract batch header
        batch.setBatchHeader(extractBatchHeader(doc));

        // Extract transactions
        List<org.w3c.dom.Element> txnElements = extractElements(doc, "CdtTrfTxInf");
        for (int i = 0; i < txnElements.size(); i++) {
            SimplifiedTransaction txn = extractTransaction(txnElements.get(i), i + 1);
            batch.addTransaction(txn);
        }

        return batch;
    }

    private SimplifiedPaymentBatch.BatchHeader extractBatchHeader(org.w3c.dom.Document doc) throws Exception {
        SimplifiedPaymentBatch.BatchHeader header = new SimplifiedPaymentBatch.BatchHeader();

        // Extract from GrpHdr
        org.w3c.dom.Element grpHdr = (org.w3c.dom.Element) doc.getElementsByTagName("GrpHdr").item(0);
        if (grpHdr != null) {
            String msgId = getElementText(grpHdr, "MsgId");
            String creTime = getElementText(grpHdr, "CreDtTm");
            String nbOfTxs = getElementText(grpHdr, "NbOfTxs");

            header.setBatchId(msgId != null ? msgId : "UNKNOWN");
            if (creTime != null) {
                header.setCreationDateTime(parseDateTime(creTime));
            }
            if (nbOfTxs != null) {
                try {
                    header.setTransactionCount(Integer.parseInt(nbOfTxs));
                } catch (NumberFormatException ignored) {}
            }

            // Settlement info
            org.w3c.dom.Element sttlmInf = (org.w3c.dom.Element) grpHdr.getElementsByTagName("SttlmInf").item(0);
            if (sttlmInf != null) {
                String method = getElementText(sttlmInf, "SttlmMtd");
                if (method != null) {
                    header.setSettlementMethod(method);
                }
            }
        }

        return header;
    }

    private SimplifiedTransaction extractTransaction(org.w3c.dom.Element txnElem, int sequence) throws Exception {
        SimplifiedTransaction txn = new SimplifiedTransaction();
        txn.setSequenceNumber(sequence);
        txn.setProcessingStatus(new ProcessingStatus(ProcessingStatus.StatusCode.PARSED));

        try {
            // Payment IDs
            org.w3c.dom.Element pmtId = (org.w3c.dom.Element) txnElem.getElementsByTagName("PmtId").item(0);
            if (pmtId != null) {
                String endToEndId = getElementText(pmtId, "EndToEndId");
                String txId = getElementText(pmtId, "TxId");
                txn.setEndToEndId(endToEndId);
                txn.setTransactionId(txId != null ? txId : endToEndId);
                txn.setInstructionId(getElementText(pmtId, "InstrId"));
                txn.setUetr(getElementText(pmtId, "UETR"));
            }

            // Amount
            String amtText = getElementText(txnElem, "IntrBkSttlmAmt");
            if (amtText != null) {
                try {
                    txn.setAmount(new BigDecimal(amtText));
                    String ccy = getAttributeValue(
                        (org.w3c.dom.Element) txnElem.getElementsByTagName("IntrBkSttlmAmt").item(0),
                        "Ccy"
                    );
                    txn.setCurrency(ccy);
                } catch (Exception e) {
                    txn.getParsingNotes().add("Failed to parse amount: " + e.getMessage());
                }
            }

            // Dates
            String sttlmDt = getElementText(txnElem, "IntrBkSttlmDt");
            if (sttlmDt != null) {
                txn.setSettlementDate(parseDate(sttlmDt));
            }

            // Priority
            String priority = getElementText(txnElem, "SttlmPrty");
            if (priority != null) {
                txn.setPriority(mapPriority(priority));
            }

            // Debtor
            org.w3c.dom.Element dbtr = (org.w3c.dom.Element) txnElem.getElementsByTagName("Dbtr").item(0);
            if (dbtr != null) {
                txn.setDebtor(extractPartyFromAgent(dbtr));
            }

            // Debtor Account
            org.w3c.dom.Element dbtrAcct = (org.w3c.dom.Element) txnElem.getElementsByTagName("DbtrAcct").item(0);
            if (dbtrAcct != null) {
                txn.setDebtorAccount(extractAccount(dbtrAcct));
            }

            // Creditor
            org.w3c.dom.Element cdtr = (org.w3c.dom.Element) txnElem.getElementsByTagName("Cdtr").item(0);
            if (cdtr != null) {
                txn.setCreditor(extractPartyFromAgent(cdtr));
            }

            // Creditor Account
            org.w3c.dom.Element cdtrAcct = (org.w3c.dom.Element) txnElem.getElementsByTagName("CdtrAcct").item(0);
            if (cdtrAcct != null) {
                txn.setCreditorAccount(extractAccount(cdtrAcct));
            }

            // Remittance
            org.w3c.dom.Element rmtInf = (org.w3c.dom.Element) txnElem.getElementsByTagName("RmtInf").item(0);
            if (rmtInf != null) {
                txn.setRemittanceInfo(extractRemittanceInfo(rmtInf));
            }

            txn.getParsingNotes().add("Successfully extracted core transaction fields");

        } catch (Exception e) {
            txn.getProcessingStatus().setStatus(ProcessingStatus.StatusCode.PARTIAL_PARSE);
            txn.getProcessingStatus().addValidationError("Extraction error: " + e.getMessage());
            txn.getParsingNotes().add("Partial extraction completed: " + e.getMessage());
            log.warn("Partial transaction extraction for sequence {}: {}", sequence, e.getMessage());
        }

        return txn;
    }

    private RemittanceInfo extractRemittanceInfo(org.w3c.dom.Element rmtInf) {
        RemittanceInfo info = new RemittanceInfo();

        // Unstructured
        List<org.w3c.dom.Element> ustrdList = extractElements(rmtInf, "Ustrd");
        for (org.w3c.dom.Element ustrd : ustrdList) {
            String text = ustrd.getTextContent();
            if (text != null && !text.trim().isEmpty()) {
                info.addUnstructuredInfo(text.trim());
            }
        }

        // Structured
        org.w3c.dom.Element strd = (org.w3c.dom.Element) rmtInf.getElementsByTagName("Strd").item(0);
        if (strd != null) {
            StructuredRemittance structured = new StructuredRemittance();

            // Referred documents
            List<org.w3c.dom.Element> docs = extractElements(strd, "RfrdDocInf");
            for (org.w3c.dom.Element doc : docs) {
                ReferredDocument refDoc = new ReferredDocument();
                refDoc.setDocumentNumber(getElementText(doc, "Nb"));
                org.w3c.dom.Element tp = (org.w3c.dom.Element) doc.getElementsByTagName("Tp").item(0);
                if (tp != null) {
                    refDoc.setDocumentType(getElementText(tp, "Cd"));
                }
                structured.addReferredDocument(refDoc);
            }

            // Creditor reference
            org.w3c.dom.Element cdtrRef = (org.w3c.dom.Element) strd.getElementsByTagName("CdtrRefInf").item(0);
            if (cdtrRef != null) {
                CreditorReference cref = new CreditorReference();
                cref.setReferenceValue(getElementText(cdtrRef, "Ref"));
                structured.setCreditorReference(cref);
            }

            info.setStructuredInfo(structured);
        }

        return info;
    }

    private Party extractPartyFromAgent(org.w3c.dom.Element element) {
        Party party = new Party();

        String name = getElementText(element, "Nm");
        if (name != null) {
            party.setName(name);
        }

        String country = getElementText(element, "CtryOfRes");
        if (country != null) {
            party.setCountryOfResidence(country);
        }

        // Extract address
        org.w3c.dom.Element addr = (org.w3c.dom.Element) element.getElementsByTagName("PstlAdr").item(0);
        if (addr != null) {
            party.setPostalAddress(extractPostalAddress(addr));
        }

        return party;
    }

    private Account extractAccount(org.w3c.dom.Element acctElem) {
        Account account = new Account();

        org.w3c.dom.Element id = (org.w3c.dom.Element) acctElem.getElementsByTagName("Id").item(0);
        if (id != null) {
            String iban = getElementText(id, "IBAN");
            if (iban != null) {
                account.setAccountNumber(iban);
                account.setAccountType("IBAN");
            } else {
                org.w3c.dom.Element othr = (org.w3c.dom.Element) id.getElementsByTagName("Othr").item(0);
                if (othr != null) {
                    String acctNum = getElementText(othr, "Id");
                    if (acctNum != null) {
                        account.setAccountNumber(acctNum);
                    }
                }
            }
        }

        String ccy = getElementText(acctElem, "Ccy");
        if (ccy != null) {
            account.setCurrency(ccy);
        }

        String nm = getElementText(acctElem, "Nm");
        if (nm != null) {
            account.setAccountName(nm);
        }

        return account;
    }

    private PostalAddress extractPostalAddress(org.w3c.dom.Element addrElem) {
        PostalAddress addr = new PostalAddress();

        addr.setStreet(getElementText(addrElem, "StrtNm"));
        addr.setBuildingNumber(getElementText(addrElem, "BldgNb"));
        addr.setPostalCode(getElementText(addrElem, "PstCd"));
        addr.setCity(getElementText(addrElem, "TwnNm"));
        addr.setRegion(getElementText(addrElem, "CtrySubDvsn"));
        addr.setCountry(getElementText(addrElem, "Ctry"));

        // Address lines
        List<org.w3c.dom.Element> lines = extractElements(addrElem, "AdrLine");
        for (org.w3c.dom.Element line : lines) {
            String text = line.getTextContent();
            if (text != null && !text.trim().isEmpty()) {
                addr.addAddressLine(text.trim());
            }
        }

        return addr;
    }

    private SimplifiedPaymentBatch.BatchFooter createBatchFooter(int txnCount, long durationMs) {
        SimplifiedPaymentBatch.BatchFooter footer = new SimplifiedPaymentBatch.BatchFooter();
        footer.setTotalCount(txnCount);
        footer.setSuccessCount(txnCount);
        footer.setFailureCount(0);
        footer.setProcessingEndTime(LocalDateTime.now());
        footer.setProcessingDurationMs(durationMs);
        return footer;
    }

    // ═══════════════════════════════════════════════════════════
    // Utility Methods
    // ═══════════════════════════════════════════════════════════

    private String getElementText(org.w3c.dom.Element element, String tagName) {
        org.w3c.dom.Element child = (org.w3c.dom.Element) element.getElementsByTagName(tagName).item(0);
        return child != null ? child.getTextContent() : null;
    }

    private String getAttributeValue(org.w3c.dom.Element element, String attrName) {
        return element.hasAttribute(attrName) ? element.getAttribute(attrName) : null;
    }

    private List<org.w3c.dom.Element> extractElements(org.w3c.dom.Element parent, String tagName) {
        List<org.w3c.dom.Element> results = new ArrayList<>();
        org.w3c.dom.NodeList nodeList = parent.getElementsByTagName(tagName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            results.add((org.w3c.dom.Element) nodeList.item(i));
        }
        return results;
    }

    private List<org.w3c.dom.Element> extractElements(org.w3c.dom.Document doc, String tagName) {
        List<org.w3c.dom.Element> results = new ArrayList<>();
        org.w3c.dom.NodeList nodeList = doc.getElementsByTagName(tagName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            results.add((org.w3c.dom.Element) nodeList.item(i));
        }
        return results;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            log.warn("Failed to parse date: {}", dateStr);
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return null;
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", dateTimeStr);
            return null;
        }
    }

    private SimplifiedTransaction.PriorityType mapPriority(String priority) {
        if (priority == null) return SimplifiedTransaction.PriorityType.NORMAL;
        return switch (priority) {
            case "HIGH", "URGT" -> SimplifiedTransaction.PriorityType.HIGH;
            case "URGENT" -> SimplifiedTransaction.PriorityType.URGENT;
            default -> SimplifiedTransaction.PriorityType.NORMAL;
        };
    }
}
