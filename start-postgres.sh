#!/usr/bin/env bash
# ══════════════════════════════════════════════════════════════════════════
#  FX Payment Processor – Production-like Start
#  Starts docker-compose (Artemis + PostgreSQL) then the Spring Boot app.
#  Requirements: Java 21+, Maven 3.9+, Docker with Compose v2
# ══════════════════════════════════════════════════════════════════════════
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

GREEN='\033[0;32m'; CYAN='\033[0;36m'; YELLOW='\033[1;33m'; NC='\033[0m'

echo -e "${CYAN}Starting infrastructure (Artemis + PostgreSQL)...${NC}"
docker compose up -d

echo -e "${CYAN}Waiting for services to be healthy...${NC}"
for service in artemis postgres; do
    until [ "$(docker inspect -f '{{.State.Health.Status}}' fx-$service 2>/dev/null)" = "healthy" ]; do
        echo -n "."
        sleep 2
    done
    echo -e " ${GREEN}$service ready${NC}"
done

echo -e "${GREEN}Starting FX Payment Processor with postgres profile...${NC}"
echo -e "${CYAN}  Artemis Console: http://localhost:8161 (artemis/artemis)${NC}"
echo ""
mvn spring-boot:run -Dspring-boot.run.profiles=postgres -q
