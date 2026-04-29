package com.payments.processor;

import com.payments.processor.converter.ConversionException;
import com.payments.processor.converter.ConversionValidationResult;
import com.payments.processor.converter.PaymentMessageConverter;
import com.payments.processor.model.SimplifiedPaymentBatch;
import com.payments.processor.model.SimplifiedTransaction;
import com.payments.processor.util.SimplifiedPaymentXmlSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main application for parsing pacs.009.001.08 payment messages
 * and converting to internal simplified XML format.
 *
 * Usage:
 *   java -jar pacs009-parser.jar input.xml output.xml
 */
public class MessageProcessorApplication {

    private static final Logger log = LoggerFactory.getLogger(MessageProcessorApplication.class);

    public static void main(String[] args) {
        try {
            // Validate arguments
            if (args.length < 1) {
                printUsage();
                System.exit(1);
            }

            String inputFile = args[0];
            String outputFile = args.length > 1 ? args[1] : null;

            log.info("Starting pacs.009 parser");
            log.info("Input file: {}", inputFile);
            if (outputFile != null) {
                log.info("Output file: {}", outputFile);
            }

            // Read input
            String inputXml = readFile(inputFile);
            if (inputXml == null || inputXml.trim().isEmpty()) {
                log.error("Input file is empty");
                System.exit(1);
            }

            // Initialize converter
            PaymentMessageConverter converter = new PaymentMessageConverter();

            // Validate input
            log.info("Validating input...");
            ConversionValidationResult validationResult = converter.validate(inputXml);
            if (!validationResult.isValid()) {
                log.error("Validation failed:");
                validationResult.getErrors().forEach(e -> log.error("  - {}", e));
                validationResult.getWarnings().forEach(w -> log.warn("  - {}", w));
            } else {
                log.info("Validation passed");
                validationResult.getWarnings().forEach(w -> log.warn("  Warning: {}", w));
            }

            // Convert
            log.info("Converting...");
            SimplifiedPaymentBatch batch = converter.convert(inputXml);
            log.info("Conversion successful: {} transactions", batch.getTransactionCount());

            // Print summary
            printBatchSummary(batch);

            // Serialize output
            SimplifiedPaymentXmlSerializer serializer = new SimplifiedPaymentXmlSerializer();
            String outputXml = serializer.toXml(batch);

            // Write output
            if (outputFile != null) {
                writeFile(outputFile, outputXml);
                log.info("Output written to: {}", outputFile);
            } else {
                System.out.println("\n═══════════════════════════════════════════════════════════");
                System.out.println("CONVERTED OUTPUT XML:");
                System.out.println("═══════════════════════════════════════════════════════════");
                System.out.println(outputXml);
            }

            log.info("Processing completed successfully");
            System.exit(0);

        } catch (ConversionException e) {
            log.error("Conversion error: {}", e.getMessage());
            e.getDetails().forEach(d -> log.error("  {}", d));
            System.exit(2);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            System.exit(3);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Helper Methods
    // ═══════════════════════════════════════════════════════════

    private static String readFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            log.error("File not found: {}", filename);
            return null;
        }
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static void writeFile(String filename, String content) throws IOException {
        Path path = Paths.get(filename);
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    private static void printBatchSummary(SimplifiedPaymentBatch batch) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              BATCH CONVERSION SUMMARY                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        if (batch.getBatchHeader() != null) {
            SimplifiedPaymentBatch.BatchHeader header = batch.getBatchHeader();
            System.out.println("Batch ID:               " + header.getBatchId());
            System.out.println("Creation Time:          " + header.getCreationDateTime());
            System.out.println("Settlement Method:      " + header.getSettlementMethod());
        }

        System.out.println("\nTransactions:           " + batch.getTransactionCount());
        System.out.println("Total Amount:           " + batch.getTotalAmount());

        if (!batch.getTransactions().isEmpty()) {
            System.out.println("\nFirst Transaction:");
            SimplifiedTransaction first = batch.getTransactions().get(0);
            System.out.println("  ID:         " + first.getTransactionId());
            System.out.println("  Amount:     " + first.getAmount() + " " + first.getCurrency());
            if (first.getDebtor() != null) {
                System.out.println("  Debtor:     " + first.getDebtor().getName());
            }
            if (first.getCreditor() != null) {
                System.out.println("  Creditor:   " + first.getCreditor().getName());
            }
        }

        if (batch.getBatchFooter() != null) {
            SimplifiedPaymentBatch.BatchFooter footer = batch.getBatchFooter();
            System.out.println("\nProcessing:");
            System.out.println("  Duration:   " + footer.getProcessingDurationMs() + " ms");
            System.out.println("  Success:    " + footer.getSuccessCount());
            System.out.println("  Failed:     " + footer.getFailureCount());
        }

        System.out.println("═══════════════════════════════════════════════════════════");
    }

    private static void printUsage() {
        System.out.println("\nUsage: java -jar pacs009-parser.jar <input-file> [output-file]");
        System.out.println("\nArguments:");
        System.out.println("  input-file   Path to pacs.009.001.08 XML file (required)");
        System.out.println("  output-file  Path to write simplified XML (optional)");
        System.out.println("\nExamples:");
        System.out.println("  java -jar pacs009-parser.jar input.xml output.xml");
        System.out.println("  java -jar pacs009-parser.jar input.xml  (output to console)");
        System.out.println();
    }
}
