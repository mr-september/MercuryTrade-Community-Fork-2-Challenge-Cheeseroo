@echo off
echo MercuryChat Debug Launcher
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
echo Checking if MercuryChat.jar exists...
if not exist "MercuryChat.jar" (
    echo ERROR: MercuryChat.jar not found in current directory
    echo Please ensure MercuryChat.jar is in the same folder as this script
    pause
    exit /b 1
)

echo.
echo Attempting to run MercuryChat.jar directly...
echo Command: java -jar MercuryChat.jar
echo.

java -jar MercuryChat.jar

echo.
echo MercuryChat exited with code: %errorlevel%
pause
