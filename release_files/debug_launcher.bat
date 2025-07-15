@echo off
echo MercuryTrade Debug Launcher
echo ===========================

echo.
echo Checking Java installation...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java not found or not in PATH
    echo Please install Java 8 or higher from https://java.com/download
    pause
    exit /b 1
)

echo.
echo Checking if MercuryTrade.jar exists...
if not exist "MercuryTrade.jar" (
    echo ERROR: MercuryTrade.jar not found in current directory
    echo Please ensure MercuryTrade.jar is in the same folder as this script
    pause
    exit /b 1
)

echo.
echo Attempting to run MercuryTrade.jar directly...
echo Command: java -jar MercuryTrade.jar
echo.

java -jar MercuryTrade.jar

echo.
echo MercuryTrade exited with code: %errorlevel%
pause
