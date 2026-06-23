#!/bin/bash
# UQS - Online Queue Management System Starter

echo ""
echo "  ============================================"
echo "   UQS - Online Queue Management System"
echo "   Starting on http://localhost:8080"
echo "  ============================================"
echo ""

# Check Java
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java not found! Install Java 17 from https://adoptium.net"
    exit 1
fi

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VER" -lt 17 ] 2>/dev/null; then
    echo "[WARN] Java $JAVA_VER detected. Java 17+ recommended."
fi

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "[ERROR] Maven not found! Install from https://maven.apache.org"
    exit 1
fi

echo "[INFO] Starting UQS application..."
echo "[INFO] Open browser at: http://localhost:8080"
echo "[INFO] Admin login:     admin@uqs.com / admin123"
echo "[INFO] Press Ctrl+C to stop"
echo ""

mvn spring-boot:run
