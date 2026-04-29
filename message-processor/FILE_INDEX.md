# Complete File Index - pacs.009 Payment Message Parser

## Project Root

```
pacs009-parser/
├── .gitignore                               # Git ignore file
├── pom.xml                                  # Maven build configuration
├── README.md                                # Full documentation (800+ lines)
├── QUICKSTART.md                            # Getting started guide (500+ lines)
└── src/                                     # Source code directory
```

---

## Java Source Code

### Main Application
```
src/main/java/com/payments/parser/
└── Pacs009ParserApplication.java           # CLI entry point (150 lines)
    - main(String[] args)
    - Command-line interface for batch conversion
    - Reads input file → converts → writes output
    - Displays batch summary statistics
    - Error handling and exit codes
```

### Converter Package
```
src/main/java/com/payments/parser/converter/
├── PaymentConverter.java                    # Generic converter interface (31 lines)
│   - convert(S source): T
│   - convertBatch(List<S> sources): List<T>
│   - validate(S source): ConversionValidationResult
│
├── ConversionException.java                 # Custom exception (60 lines)
│   - ConversionException(String message, String sourceFormat, String targetFormat)
│   - getSourceFormat(), getTargetFormat()
│   - getDetails(), addDetail(String)
│
├── ConversionValidationResult.java          # Validation result (73 lines)
│   - isValid(): boolean
│   - getErrors(), getWarnings()
│   - addError(), addWarning()
│
└── Pacs009ToSimplifiedConverter.java         # Main converter (450+ lines)
    - convert(String sourceXml): SimplifiedPaymentBatch
    - convertBatch(List<String> sources): List<SimplifiedPaymentBatch>
    - validate(String source): ConversionValidationResult
    - Private methods for extraction:
      * parseAndConvert()
      * extractBatchHeader()
      * extractTransaction()
      * extractRemittanceInfo()
      * extractPartyFromAgent()
      * extractAccount()
      * extractPostalAddress()
      * Helper methods (getElementText, parseDate, mapPriority, etc.)
```

### Model Package - Core Models
```
src/main/java/com/payments/parser/model/
├── SimplifiedPaymentBatch.java               # Batch container (220 lines)
│   - BatchHeader (inner class)
│   - BatchFooter (inner class)
│   - Properties: batchHeader, transactions, batchFooter
│   - Methods: getTransactionCount(), getTotalAmount()
│
├── SimplifiedTransaction.java                # Transaction model (290 lines)
│   - Transaction identifiers (transactionId, endToEndId, uetr)
│   - Amount & currency fields
│   - Dates & times (transactionDate, settlementDate, priority)
│   - Parties (debtor, debtorAccount, debtorAgent, creditor, creditorAccount)
│   - Intermediaries (instructingAgent, instructedAgent, intermediaryAgents)
│   - Payment details (purpose, paymentType, localInstrument, serviceLevel)
│   - Remittance & tax (remittanceInfo, structuredRemittance, taxInfo)
│   - Processing metadata (processingStatus, parsingNotes)
│   - Enums: PriorityType, ChargeBearerType
│
├── PaymentModels.java                       # Support models (450+ lines)
│   - Party (represents debtor, creditor, ultimate parties)
│   - PostalAddress (street, city, country, etc.)
│   - ContactInfo (phone, email, job title, etc.)
│   - OrganisationId (BIC, LEI, TaxId, etc.)
│   - PersonalId (dateOfBirth, nationality, idNumber, etc.)
│   - Agent (financial institution in payment chain)
│   - Account (bank account with IBAN/BBAN support)
│   - Charge (amount, currency, type, agent)
│
└── AdvancedModels.java                      # Complex models (550+ lines)
    - RemittanceInfo (unstructured & structured info)
    - StructuredRemittance (invoices, credits, references)
    - ReferredDocument (invoice, credit note, debit note)
    - LineDetail (line items in documents)
    - CreditorReference (remittance reference)
    - TaxInfo (tax information and records)
    - TaxParty (debtor/creditor tax details)
    - TaxRecord (tax type, rate, amount, period)
    - Garnishment (legal hold, wage garnishment)
    - ProcessingStatus (with StatusCode enum)
```

### Utility Package
```
src/main/java/com/payments/parser/util/
└── SimplifiedPaymentXmlSerializer.java       # XML output (380+ lines)
    - toXml(SimplifiedPaymentBatch): String
    - Private methods for XML generation:
      * writePaymentBatch()
      * writeBatchHeader(), writeBatchFooter()
      * writeTransaction()
      * writeParty(), writeAccount(), writeAgent()
      * writeCharge(), writeRemittanceInfo()
      * writePostalAddress(), writeProcessingStatus()
      * writeElement() (generic element writer)
```

---

## Test Code

```
src/test/java/com/payments/parser/converter/
└── Pacs009ToSimplifiedConverterTest.java     # Unit tests (280+ lines)
    @Test methods:
    - testSuccessfulConversion()
    - testBatchHeaderExtraction()
    - testFirstTransactionExtraction()
    - testDebtorExtraction()
    - testCreditorExtraction()
    - testRemittanceExtraction()
    - testValidateCorrectMessage()
    - testValidateNullInput()
    - testValidateEmptyInput()
    - testValidateNonXmlInput()
    - testBatchConversion()
    - testSecondTransactionPriority()
    - testProcessingStatusAndNotes()
    - testBatchTotals()
```

---

## XSD Schema Files

### Source Format Schema
```
src/main/resources/xsd/
└── pacs.009.001.08.xsd                      # 1,400+ lines
    ISO 20022 Financial Institution Credit Transfer Schema
    
    Main Elements:
    - Document (root)
      └── FIToFIFrdLnsTrf (main message)
          ├── GrpHdr (group header)
          ├── CdtTrfTxInf[] (transactions)
          └── SplmtryData (supplementary data)
    
    Key Complex Types:
    - GroupHeader93
    - CreditTransferTransaction36
    - PaymentIdentification7
    - BranchAndFinancialInstitutionIdentification6
    - CashAccount38
    - RemittanceInformation2
    - StructuredRemittanceInformation16
    - PaymentTypeInformation28
    - SettlementInstruction7
    - And 60+ supporting types...
    
    Simple Types:
    - ActiveCurrencyCode
    - ISODate, ISODateTime
    - Max35Text, Max140Text, etc.
    - Priority codes, charge bearer codes
    - External code sets (ExternalXxxCode)
```

### Simplified Output Schema
```
src/main/resources/xsd/
└── simplified-payment-v1.xsd                # 900+ lines
    Internal Simplified Payment Format Schema
    
    Main Elements:
    - PaymentBatch (root)
      ├── BatchHeader
      ├── Transaction[]
      └── BatchFooter
    
    Key Complex Types:
    - PaymentBatch (version attribute)
    - BatchHeader
    - Transaction (sequenceNumber attribute)
    - Party (partyType attribute)
    - Account
    - Agent
    - Charge
    - RemittanceInfo
    - StructuredRemittance
    - ReferredDocument
    - LineDetail
    - TaxInfo, TaxRecord
    - Garnishment
    - ProcessingStatus (StatusCode enum)
    - PostalAddress
    - ContactInfo
    
    Simple Types:
    - PriorityType (HIGH, NORMAL, URGENT)
    - ChargeBearerType (DEBT, CRED, SHAR, SLEV)
    - SettlementMethodType (INDA, INGA, COVE, CLRG)
    - StatusCode (PARSED, PARSE_ERROR, etc.)
    - PartyTypeCode (PERSON, ORGANISATION, UNKNOWN)
    - Custom date/time/amount types
```

---

## Configuration Files

```
src/main/resources/
├── logback.xml                              # 60 lines
│   Logging configuration:
│   - Console appender (stdout)
│   - Rolling file appender
│   - Error file appender
│   - Logger levels and patterns
│   - Max file size and retention
│
└── xsd/
    └── [see XSD Schema Files section above]
```

---

## Test Resources

```
src/test/resources/
└── samples/
    └── sample-pacs009.xml                   # 85 lines
        Sample pacs.009.001.08 message with 2 transactions:
        - Transaction 1: USD 1,000,000 (NORMAL priority)
          Debtor: Deutsche Bank
          Creditor: JP Morgan Chase
          Account: IBAN
        - Transaction 2: EUR 500,000 (HIGH priority)
          Debtor: BNP Paribas
          Creditor: Societe Generale
          Account: IBAN
```

---

## Documentation Files

### Root Level
```
pacs009-parser/
├── README.md                                # 800+ lines
│   - Project overview
│   - Features list
│   - Quick start
│   - Architecture & components
│   - Input format (pacs.009.001.08)
│   - Output format (Simplified v1)
│   - API reference
│   - Error handling
│   - Testing
│   - Configuration
│   - Performance benchmarks
│   - Troubleshooting
│   - References
│
├── QUICKSTART.md                            # 500+ lines
│   - 5-minute setup
│   - Usage examples
│   - Programmatic usage patterns
│   - Batch processing
│   - Error handling
│   - Serialization
│   - Sample input/output
│   - Running tests
│   - Debugging tips
│   - Performance tips
│   - Troubleshooting table
│
└── .gitignore                               # Standard patterns
    - Maven target/
    - IDE files (.idea, .vscode, etc.)
    - Build artifacts
    - Logs
    - OS files
```

---

## Build Configuration

```
pacs009-parser/
└── pom.xml                                  # 143 lines
    - Group ID: com.payments
    - Artifact ID: pacs009-parser
    - Version: 1.0.0-SNAPSHOT
    - Java: 17
    
    Dependencies:
    - Jakarta XML Bind 4.0.0
    - JAXB Implementation 4.0.2
    - Woodstox 6.5.1
    - SLF4J 2.0.9
    - Logback 1.4.14
    - JUnit 5.10.1
    - AssertJ 3.24.2
    
    Plugins:
    - Maven Compiler (Java 17)
    - Maven Surefire (JUnit 5)
    - Maven Shade (fat JAR)
```

---

## File Statistics

| Category | Count | Lines |
|----------|-------|-------|
| Java Classes | 13 | 2,500+ |
| Test Classes | 1 | 280+ |
| XSD Schemas | 2 | 2,300+ |
| Configuration | 2 | 200+ |
| Documentation | 3 | 1,800+ |
| Test Data | 1 | 85 |
| **Total** | **22** | **6,165+** |

---

## Key File Sizes

| File | Type | Lines | Size |
|------|------|-------|------|
| Pacs009ToSimplifiedConverter.java | Main converter | 450+ | 18 KB |
| pacs.009.001.08.xsd | Source schema | 1,400+ | 50 KB |
| simplified-payment-v1.xsd | Output schema | 900+ | 32 KB |
| README.md | Documentation | 800+ | 28 KB |
| QUICKSTART.md | Getting started | 500+ | 18 KB |
| SimplifiedTransaction.java | Model | 290+ | 12 KB |
| PaymentModels.java | Models | 450+ | 16 KB |
| AdvancedModels.java | Models | 550+ | 20 KB |

---

## How to Navigate

1. **Start with**: README.md → QUICKSTART.md
2. **Build the project**: pom.xml (Maven configuration)
3. **View sample data**: src/test/resources/samples/sample-pacs009.xml
4. **Understand schema**: src/main/resources/xsd/*.xsd
5. **Run tests**: src/test/java/.../*Test.java
6. **Explore models**: src/main/java/.../model/
7. **See converter**: src/main/java/.../converter/Pacs009ToSimplifiedConverter.java
8. **Learn patterns**: src/test/java/.../*Test.java (examples)

---

## File Dependencies

```
pacs009-parser (entry point)
├── Pacs009ToSimplifiedConverter
│   ├── SimplifiedPaymentBatch
│   ├── SimplifiedTransaction
│   ├── PaymentModels (Party, Account, Agent, etc.)
│   └── AdvancedModels (Tax, Garnishment, etc.)
│
├── SimplifiedPaymentXmlSerializer
│   └── SimplifiedPaymentBatch (output generation)
│
└── logback.xml (logging config)
```

---

## Building & Running

```bash
# Build
mvn clean package

# Run CLI
java -jar target/pacs009-parser-1.0.0-SNAPSHOT-shaded.jar input.xml output.xml

# Run tests
mvn test

# View logs
tail -f logs/pacs009-parser.log
```

---

End of File Index
