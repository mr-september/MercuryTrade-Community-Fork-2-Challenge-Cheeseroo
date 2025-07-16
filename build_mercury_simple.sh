#!/bin/bash
# MercuryTrade Simple Build Script - Build without packaging
# This script builds the JAR and creates Windows EXE without creating zip packages

set -e  # Exit on any error

echo "MercuryTrade simple build starting (no packaging)..."

# Clean and build with Maven
echo "Running: mvn clean package"
mvn clean package

# Copy JAR to release_files
echo "Copying MercuryTrade.jar from app/target to release_files"
cp app/target/MercuryTrade.jar release_files/

# Check if Launch4j is available (local or system)
if [ -x "launch4j/launch4j" ]; then
    echo "Creating Windows executable with Launch4j..."
    cd release_files
    ../launch4j/launch4j release_config.xml
    cd ..
    echo "Launch4j EXE creation completed"
elif [ -f "launch4j/launch4j.jar" ]; then
    echo "Creating Windows executable with Launch4j (using JAR)..."
    cd release_files
    java -jar ../launch4j/launch4j.jar release_config.xml
    cd ..
    echo "Launch4j EXE creation completed"
elif command -v launch4j &> /dev/null; then
    echo "Creating Windows executable with Launch4j (system)..."
    cd release_files
    launch4j release_config.xml
    cd ..
    echo "Launch4j EXE creation completed"
else
    echo "Launch4j not found. Skipping EXE creation."
    echo "Install Launch4j to create Windows executables."
fi

echo "Simple build completed - JAR and EXE files available in release_files/"
echo "Note: Use build_mercury.sh for full packaging with zip files"
