#!/bin/bash
# MercuryTrade Build Script - Comprehensive build with packaging
# This script builds the JAR, creates Windows EXE, and packages everything for release

echo "MercuryTrade build starting..."

# Clean and build with Maven
echo "Running: mvn clean package"
if mvn clean package; then
    echo "Maven build successful"
    # Copy JAR to release_files
    echo "Copying MercuryTrade.jar from app/target to release_files"
    cp app/target/MercuryTrade.jar release_files/
    JAR_AVAILABLE=true
else
    echo "Maven build failed. Will try to use existing JAR file."
    JAR_AVAILABLE=false
    
    # Check if there's an existing JAR file we can use
    if [ -f "release_files/MercuryTrade-jar-fixed/MercuryTrade.jar" ]; then
        echo "Using JAR from MercuryTrade-jar-fixed/"
        cp release_files/MercuryTrade-jar-fixed/MercuryTrade.jar release_files/
        JAR_AVAILABLE=true
    elif [ -f "app/target/MercuryTrade.jar" ]; then
        echo "Using existing JAR from app/target/"
        cp app/target/MercuryTrade.jar release_files/
        JAR_AVAILABLE=true
    else
        echo "ERROR: No JAR file available. Cannot proceed with packaging."
        exit 1
    fi
fi

# Proceed with packaging only if JAR is available
if [ "$JAR_AVAILABLE" = true ]; then

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

echo "Creating release packages..."
cd release_files

# Clean up old files
rm -f MercuryTrade-*.zip

# Create JAR package with proper structure including resources
echo "Creating JAR package with resources..."
rm -rf MercuryTrade-jar
mkdir -p MercuryTrade-jar
cp MercuryTrade.jar MercuryTrade-jar/
cp HOW_TO_RUN_JAR.txt MercuryTrade-jar/
# Copy the entire MercuryTrade directory structure (including resources)
cp -r MercuryTrade/* MercuryTrade-jar/
zip -r MercuryTrade-jar.zip MercuryTrade-jar/
rm -rf MercuryTrade-jar/
echo "JAR package completed with resources"

# Create EXE package with proper structure including resources (if EXE exists)
if [ -f "MercuryTrade.exe" ]; then
    echo "Creating EXE package with resources..."
    rm -rf MercuryTrade-exe
    mkdir -p MercuryTrade-exe
    cp MercuryTrade.exe MercuryTrade-exe/
    # Copy the entire MercuryTrade directory structure (including resources)
    cp -r MercuryTrade/* MercuryTrade-exe/
    zip -r MercuryTrade-exe.zip MercuryTrade-exe/
    rm -rf MercuryTrade-exe/
    echo "EXE package completed with resources"
else
    echo "MercuryTrade.exe not found. Skipping EXE package."
fi

# Create language package
echo "Creating language package..."
zip -r lang.zip ../app-shared/src/main/resources/lang/*

# Clean up standalone files
echo "Cleaning up standalone files..."
rm -f MercuryTrade.jar
rm -f MercuryTrade.exe
echo "Standalone files removed"

cd ..

echo "Build and packaging completed!"
echo ""
echo "Files created in release_files/:"
echo "  - MercuryTrade-jar.zip (complete package with resources)"
if [ -f "release_files/MercuryTrade-exe.zip" ]; then
    echo "  - MercuryTrade-exe.zip (complete package with resources)"
fi
echo "  - lang.zip"
echo ""
echo "The zip files include the complete directory structure with:"
echo "  - MercuryTrade.jar or MercuryTrade.exe"
echo "  - HOW_TO_RUN_JAR.txt (for JAR package)"
echo "  - README.txt"
echo "  - MercuryTrade.l4j.ini"
echo "  - resources/app/helpIGImg.png"
echo ""
echo "Note: Standalone JAR and EXE files have been cleaned up."
echo "This matches the original Morph21 release format."
else
    echo "Build failed. Cannot create packages without JAR file."
    exit 1
fi
