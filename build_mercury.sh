#!/bin/bash
# MercuryTrade Build Script (Linux/macOS equivalent of build_mercury_with_zip.bat)

set -e  # Exit on any error

echo "MercuryTrade build starting..."

# Clean and build with Maven
echo "Running: mvn clean package"
mvn clean package

# Copy JAR to release_files
echo "Copying MercuryTrade.jar from app/target to release_files"
cp app/target/MercuryTrade.jar release_files/

# Check if Launch4j is available
if command -v launch4j &> /dev/null; then
    echo "Creating Windows executable with Launch4j..."
    cd release_files
    launch4j release_config.xml
    cd ..
    echo "Launch4j EXE creation completed"
else
    echo "Launch4j not found. Skipping EXE creation."
    echo "Install Launch4j to create Windows executables."
fi

echo "Creating release packages..."
cd release_files

# Clean up old files
rm -f MercuryTrade-*.zip

# Create JAR package
echo "Creating JAR package..."
mkdir -p MercuryTrade-jar
cp MercuryTrade.jar MercuryTrade-jar/
cp HOW_TO_RUN_JAR.txt MercuryTrade-jar/
zip -r MercuryTrade-jar.zip MercuryTrade-jar/
rm -rf MercuryTrade-jar/

# Create EXE package (if EXE exists)
if [ -f "MercuryTrade.exe" ]; then
    echo "Creating EXE package..."
    mkdir -p MercuryTrade-exe
    cp MercuryTrade.exe MercuryTrade-exe/
    zip -r MercuryTrade-exe.zip MercuryTrade-exe/
    rm -rf MercuryTrade-exe/
else
    echo "MercuryTrade.exe not found. Skipping EXE package."
fi

# Create language package
echo "Creating language package..."
zip -r lang.zip ../app-shared/src/main/resources/lang/*

cd ..

echo "Build and packaging completed!"
echo "Files created in release_files/:"
ls -la release_files/*.zip 2>/dev/null || echo "No zip files found"
ls -la release_files/*.jar 2>/dev/null || echo "No jar files found"
ls -la release_files/*.exe 2>/dev/null || echo "No exe files found"
