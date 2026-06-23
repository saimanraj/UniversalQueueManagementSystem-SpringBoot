@echo off
TITLE UQS - Online Queue Management System
echo.
echo  ============================================
echo   UQS - Online Queue Management System
echo   Starting on http://localhost:8080
echo  ============================================
echo.

REM Check Java
java -version >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java not found! Install Java 17 from https://adoptium.net
    pause
    exit /b 1
)

REM Check Maven
mvn -version >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven not found! Install from https://maven.apache.org
    pause
    exit /b 1
)

echo [INFO] Starting application...
echo [INFO] Open browser at: http://localhost:8080
echo [INFO] Admin login: admin@uqs.com / admin123
echo [INFO] Press Ctrl+C to stop
echo.
mvn spring-boot:run
pause
