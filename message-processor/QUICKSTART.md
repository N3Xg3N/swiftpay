# Quick Start Guide: pacs.009 Parser

## 5-Minute Setup

### 1. Build the Project

```bash
cd pacs009-parser
mvn clean package -DskipTests
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX s
```

### 2. Convert a Payment Message

```bash
java -jar target/pacs009-parser-1.0.0-SNAPSHOT-shaded.jar \
     src/test/resources/samples/sample-pacs009.xml \
     output.xml
```

This will:
- Parse the input pacs.009 XML
- Extract transaction details
- Create a simplified output.xml file
- Print a summary to console

### 3. View the Output

```bash
cat output.xml
```

## Usage Examples

### Example 1: CLI Conversion

**Command:**
```bash
java -jar target/pacs009-parser-1.0.0-SNAPSHOT-shaded.jar input.xml output.xml
```

**Output:**
```
╔═══════════════════════════════════════════════════════════╗
║              BATCH CONVERSION SUMMARY                     ║
╚═══════════════════════════════════════════════════════════╝
Batch ID:               MSG-20250101-001
Creation Time:          2025-01-01T09:30:00Z
Settlement Method:      INGA

Transactions:           2
Total Amount:           1500000

First Transaction:
  ID:         TXN-001
  Amount:     1000000.00 USD
  Debtor:     Deutsche Bank
  Creditor:   JP Morgan Chase

Processing:
  Duration:   45 ms
  Success:    2
  Failed:     0
═══════════════════════════════════════════════════════════
```

### Example 2: Programmatic Usage

```java
import com.payments.parser.converter.*;
import com.payments.parser.model.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PaymentProcessing {
    public static void main(String[] args) throws Exception {
        // Read pacs.009 file
        String xmlContent = new String(
            Files.readAllBytes(Paths.get("input.xml"))
        );
        
        // Create converter
        Pacs009ToSimplifiedConverter converter = 
            new Pacs009ToSimplifiedConverter();
        
        // Validate
        ConversionValidationResult validation = 
            converter.validate(xmlContent);
        if (!validation.isValid()) {
            System.out.println("Validation failed!");
            validation.getErrors().forEach(System.out::println);
            System.exit(1);
        }
        
        // Convert
        SimplifiedPaymentBatch batch = converter.convert(xmlContent);
        
        // Process transactions
        batch.getTransactions().forEach(txn -> {
            System.out.println("Transaction: " + txn.getTransactionId());
            System.out.println("Amount: " + txn.getAmount());
            System.out.println("From: " + txn.getDebtor().getName());
            System.out.println("To: " + txn.getCreditor().getName());
            
            // Access remittance
            if (txn.getRemittanceInfo() != null) {
                txn.getRemittanceInfo().getUnstructuredInfo()
                    .forEach(info -> System.out.println("Ref: " + info));
            }
        });
    }
}
```

### Example 3: Batch Processing

```java
import com.payments.parser.converter.*;
import java.nio.file.*;
import java.util.*;

public class BatchProcessor {
    public static void main(String[] args) throws Exception {
        Pacs009ToSimplifiedConverter converter = 
            new Pacs009ToSimplifiedConverter();
        
        // Load multiple files
        List<String> xmlFiles = new ArrayList<>();
        Files.list(Paths.get("input-dir"))
            .filter(p -> p.toString().endsWith(".xml"))
            .forEach(p -> {
                try {
                    xmlFiles.add(new String(Files.readAllBytes(p)));
                } catch (Exception e) {
                    System.err.println("Error reading: " + p);
                }
            });
        
        // Convert batch
        List<SimplifiedPaymentBatch> results = 
            converter.convertBatch(xmlFiles);
        
        // Statistics
        int totalTxns = results.stream()
            .mapToInt(SimplifiedPaymentBatch::getTransactionCount)
            .sum();
        System.out.println("Total transactions: " + totalTxns);
    }
}
```

### Example 4: Error Handling

```java
import com.payments.parser.converter.*;

public class ErrorHandling {
    public static void main(String[] args) {
        Pacs009ToSimplifiedConverter converter;
        try {
            converter = new Pacs009ToSimplifiedConverter();
        } catch (ConversionException e) {
            System.err.println("Failed to initialize: " + e.getMessage());
            System.exit(1);
        }
        
        String xmlString = "...pacs.009 XML...";
        
        // Validate first
        ConversionValidationResult result = converter.validate(xmlString);
        if (!result.isValid()) {
            System.out.println("Errors:");
            result.getErrors().forEach(e -> 
                System.out.println("  [ERROR] " + e)
            );
            System.out.println("Warnings:");
            result.getWarnings().forEach(w -> 
                System.out.println("  [WARN] " + w)
            );
            return;
        }
        
        // Convert with error handling
        try {
            var batch = converter.convert(xmlString);
            
            // Check transaction processing status
            batch.getTransactions().forEach(txn -> {
                if (txn.getProcessingStatus() != null) {
                    var status = txn.getProcessingStatus();
                    if (!status.getValidationErrors().isEmpty()) {
                        System.out.println("Transaction " + txn.getTransactionId());
                        status.getValidationErrors().forEach(e -> 
                            System.out.println("  Error: " + e)
                        );
                    }
                }
            });
            
        } catch (ConversionException e) {
            System.err.println("Conversion failed: " + e.getMessage());
            System.err.println("Source: " + e.getSourceFormat());
            System.err.println("Target: " + e.getTargetFormat());
            e.getDetails().forEach(d -> 
                System.err.println("  - " + d)
            );
        }
    }
}
```

### Example 5: Serialization

```java
import com.payments.parser.converter.*;
import com.payments.parser.model.*;
import com.payments.parser.util.*;
import java.nio.file.*;

public class SerializationExample {
    public static void main(String[] args) throws Exception {
        // Convert
        String input = new String(
            Files.readAllBytes(Paths.get("input.xml"))
        );
        
        Pacs009ToSimplifiedConverter converter = 
            new Pacs009ToSimplifiedConverter();
        SimplifiedPaymentBatch batch = converter.convert(input);
        
        // Serialize to XML
        SimplifiedPaymentXmlSerializer serializer = 
            new SimplifiedPaymentXmlSerializer();
        String outputXml = serializer.toXml(batch);
        
        // Write to file
        Files.write(Paths.get("output.xml"), 
                   outputXml.getBytes());
        
        // Or use for further processing
        System.out.println(outputXml);
    }
}
```

## Sample Input/Output

### Input (pacs.009.001.08)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.009.001.08">
    <FIToFIFrdLnsTrf>
        <GrpHdr>
            <MsgId>MSG-001</MsgId>
            <CreDtTm>2025-01-01T10:00:00Z</CreDtTm>
            <NbOfTxs>1</NbOfTxs>
            <SttlmInf>
                <SttlmMtd>INGA</SttlmMtd>
            </SttlmInf>
        </GrpHdr>
        <CdtTrfTxInf>
            <PmtId>
                <EndToEndId>E2E-001</EndToEndId>
                <TxId>TXN-001</TxId>
            </PmtId>
            <IntrBkSttlmAmt Ccy="USD">1000000.00</IntrBkSttlmAmt>
            <Dbtr>
                <FinInstnId>
                    <BICFI>DEUTDEDD</BICFI>
                </FinInstnId>
            </Dbtr>
            <DbtrAcct>
                <Id><IBAN>DE75512108001234567890</IBAN></Id>
            </DbtrAcct>
            <Cdtr>
                <FinInstnId>
                    <BICFI>PBNKUS33</BICFI>
                </FinInstnId>
            </Cdtr>
            <CdtrAcct>
                <Id><IBAN>US90010000000000000001</IBAN></Id>
            </CdtrAcct>
            <RmtInf>
                <Ustrd>Payment for goods</Ustrd>
            </RmtInf>
        </CdtTrfTxInf>
    </FIToFIFrdLnsTrf>
</Document>
```

### Output (Simplified Payment v1)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<PaymentBatch version="1.0">
    <BatchHeader>
        <BatchId>MSG-001</BatchId>
        <CreationDateTime>2025-01-01T10:00:00Z</CreationDateTime>
        <TransactionCount>1</TransactionCount>
        <SettlementMethod>INGA</SettlementMethod>
    </BatchHeader>
    
    <Transaction sequenceNumber="1">
        <TransactionId>TXN-001</TransactionId>
        <EndToEndId>E2E-001</EndToEndId>
        <Amount>1000000.00</Amount>
        <Currency>USD</Currency>
        
        <Debtor partyType="ORGANISATION">
            <Name>Deutsche Bank</Name>
        </Debtor>
        <DebtorAccount>
            <AccountNumber>DE75512108001234567890</AccountNumber>
            <AccountType>IBAN</AccountType>
        </DebtorAccount>
        
        <Creditor partyType="ORGANISATION">
            <Name>Bank of America</Name>
        </Creditor>
        <CreditorAccount>
            <AccountNumber>US90010000000000000001</AccountNumber>
            <AccountType>IBAN</AccountType>
        </CreditorAccount>
        
        <RemittanceInfo>
            <UnstructuredInfo>Payment for goods</UnstructuredInfo>
        </RemittanceInfo>
        
        <ProcessingStatus>
            <Status>PARSED</Status>
            <StatusTimestamp>2025-01-01T10:00:05Z</StatusTimestamp>
        </ProcessingStatus>
    </Transaction>
    
    <BatchFooter>
        <TotalCount>1</TotalCount>
        <SuccessCount>1</SuccessCount>
        <FailureCount>0</FailureCount>
        <ProcessingDurationMs>45</ProcessingDurationMs>
    </BatchFooter>
</PaymentBatch>
```

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=Pacs009ToSimplifiedConverterTest

# Run with detailed output
mvn test -X

# Skip tests during build
mvn package -DskipTests
```

## Debugging

### Enable Debug Logging

Edit `src/main/resources/logback.xml`:
```xml
<logger name="com.payments.parser" level="DEBUG"/>
```

### Check Log Files

```bash
tail -f logs/pacs009-parser.log
tail -f logs/pacs009-parser-error.log
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| JAR not found | Run `mvn package` first |
| XML parsing error | Validate XML structure, check encoding |
| NullPointerException | Check for optional fields before access |
| Memory issues | Process in batches, increase heap: `-Xmx2g` |
| Encoding issues | Ensure UTF-8: `file -i input.xml` |

## Performance Tips

1. **Reuse converter instances**: Don't create new ones for each conversion
2. **Use batch processing**: Process multiple messages together
3. **Validate before converting**: Catch errors early
4. **Monitor logs**: Check for warnings about skipped fields
5. **Tune heap**: `-Xmx4g` for large batch processing

## Next Steps

1. ✅ Review the `README.md` for full documentation
2. ✅ Check `src/test/java/` for more examples
3. ✅ Explore the model classes for available fields
4. ✅ Customize the converter for your needs
5. ✅ Integrate into your payment processing pipeline

---

For more information, see the full README.md documentation.
