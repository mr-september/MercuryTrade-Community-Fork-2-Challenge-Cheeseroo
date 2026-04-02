#!/bin/bash
# MercuryChat Build Script - Comprehensive build with packaging
# This script builds the JAR, creates Windows EXE, and packages everything for release

echo "MercuryChat build starting..."

# Clean and build with Maven
echo "Running: mvn clean package"
if mvn clean package; then
    echo "Maven build successful"
    # Copy JAR to release_files
    echo "Copying MercuryChat.jar from app/target to release_files"
    cp app/target/MercuryChat.jar release_files/
    JAR_AVAILABLE=true
else
    echo "Maven build failed. Will try to use existing JAR file."
    JAR_AVAILABLE=false
    
    # Check if there's an existing JAR file we can use
    if [ -f "release_files/MercuryChat-jar-fixed/MercuryChat.jar" ]; then
        echo "Using JAR from MercuryChat-jar-fixed/"
        cp release_files/MercuryChat-jar-fixed/MercuryChat.jar release_files/
        JAR_AVAILABLE=true
    elif [ -f "app/target/MercuryChat.jar" ]; then
        echo "Using existing JAR from app/target/"
        cp app/target/MercuryChat.jar release_files/
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
rm -f MercuryChat-*.zip MercuryTrade-*.zip

# Create JAR package with proper structure including resources
echo "Creating JAR package with resources..."
rm -rf MercuryChat-jar
mkdir -p MercuryChat-jar
cp MercuryChat.jar MercuryChat-jar/
cp HOW_TO_RUN_JAR.txt MercuryChat-jar/
# Copy the entire MercuryChat directory structure (including resources)
cp -r MercuryChat/* MercuryChat-jar/
zip -r MercuryChat-jar.zip MercuryChat-jar/
rm -rf MercuryChat-jar/
echo "JAR package completed with resources"

# Create EXE package with proper structure including resources (if EXE exists)
if [ -f "MercuryChat.exe" ]; then
    echo "Creating EXE package with resources..."
    rm -rf MercuryChat-exe
    mkdir -p MercuryChat-exe
    cp MercuryChat.exe MercuryChat-exe/
    # Copy the entire MercuryChat directory structure (including resources)
    cp -r MercuryChat/* MercuryChat-exe/
    zip -r MercuryChat-exe.zip MercuryChat-exe/
    rm -rf MercuryChat-exe/
    echo "EXE package completed with resources"
else
    echo "MercuryChat.exe not found. Skipping EXE package."
fi

# Create language package
echo "Creating language package..."
zip -r lang.zip ../app-shared/src/main/resources/lang/*

# Clean up standalone files
echo "Cleaning up standalone files..."
rm -f MercuryChat.jar
rm -f MercuryChat.exe
echo "Standalone files removed"

cd ..

echo "Build and packaging completed!"
echo ""
echo "Files created in release_files/:"
echo "  - MercuryChat-jar.zip (complete package with resources)"
if [ -f "release_files/MercuryChat-exe.zip" ]; then
    echo "  - MercuryChat-exe.zip (complete package with resources)"
fi
echo "  - lang.zip"
echo ""
echo "The zip files include the complete directory structure with:"
echo "  - MercuryChat.jar or MercuryChat.exe"
echo "  - HOW_TO_RUN_JAR.txt (for JAR package)"
echo "  - README.txt"
echo "  - MercuryChat.l4j.ini"
echo "  - resources/app/helpIGImg.png"
echo ""
echo "Note: Standalone JAR and EXE files have been cleaned up."
echo "This matches the original Morph21 release format."
else
    echo "Build failed. Cannot create packages without JAR file."
    exit 1
fi
