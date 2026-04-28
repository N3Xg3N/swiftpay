# ══════════════════════════════════════════════════════════════════════════
#  FX Payment Processor – Makefile
#  Usage: make <target>
# ══════════════════════════════════════════════════════════════════════════

.DEFAULT_GOAL := start
.PHONY: start start-postgres stop test clean send-valid send-invalid help

## One-click start (embedded Artemis + H2, no Docker required)
start:
	@chmod +x start.sh && ./start.sh

## Start with Docker (standalone Artemis + PostgreSQL)
start-postgres:
	@chmod +x start-postgres.sh && ./start-postgres.sh

## Stop docker-compose infrastructure
stop:
	docker compose down

## Stop and remove persistent volumes
stop-clean:
	docker compose down -v

## Run all tests
test:
	mvn test

## Run only unit tests (faster)
test-unit:
	mvn test -pl . -Dtest="*Test" -DexcludedTests="*IntegrationTest"

## Run only integration tests
test-integration:
	mvn test -Dtest="*IntegrationTest"

## Clean build artifacts
clean:
	mvn clean

## Build fat JAR
build:
	mvn clean package -DskipTests

## Send a valid pacs.009 test message to the inbound queue
## Requires: app running + artemis-cli on PATH  OR  use the helper script below
send-valid:
	@echo "Sending valid pacs.009 message to fx.pacs009.inbound..."
	@mvn -q exec:java \
	  -Dexec.mainClass="com.fx.payment.util.TestMessageSender" \
	  -Dexec.args="src/test/resources/messages/valid-pacs009.xml" 2>/dev/null || \
	  echo "See README.md §Testing for manual send instructions"

## Send an invalid pacs.009 test message
send-invalid:
	@echo "Sending invalid pacs.009 message to fx.pacs009.inbound..."
	@mvn -q exec:java \
	  -Dexec.mainClass="com.fx.payment.util.TestMessageSender" \
	  -Dexec.args="src/test/resources/messages/invalid-pacs009-missing-txid.xml" 2>/dev/null || \
	  echo "See README.md §Testing for manual send instructions"

## Show help
help:
	@echo ""
	@echo "  make start           – start (embedded mode, no Docker)"
	@echo "  make start-postgres  – start with Docker infra"
	@echo "  make stop            – stop Docker infra"
	@echo "  make test            – run all tests"
	@echo "  make build           – build fat JAR"
	@echo "  make send-valid      – send valid test message"
	@echo "  make send-invalid    – send invalid test message"
	@echo ""
