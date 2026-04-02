@echo off
cls
echo MercuryChat simple build starting (no packaging)
echo mvn clean package
call mvn clean package
if %errorlevel% neq 0 (
    echo Maven build failed. Checking for existing JAR file...
    if exist "release_files\MercuryChat-jar-fixed\MercuryChat.jar" (
        echo Using JAR from MercuryChat-jar-fixed\
        copy "release_files\MercuryChat-jar-fixed\MercuryChat.jar" "release_files\"
        set JAR_AVAILABLE=true
    ) else if exist "app\target\MercuryChat.jar" (
        echo Using existing JAR from app\target\
        copy "app\target\MercuryChat.jar" "release_files\"
        set JAR_AVAILABLE=true
    ) else (
        echo ERROR: No JAR file available. Cannot proceed with EXE creation.
        exit /b 1
    )
) else (
    echo Maven build successful
    echo Copying MercuryChat.jar from app/target to release_files
    cd app\target
    copy MercuryChat.jar "..\..\release_files"
    cd ..\..
    set JAR_AVAILABLE=true
)

if "%JAR_AVAILABLE%"=="true" (
    echo Launching launch4j.exe to generate MercuryChat.exe from .jar file
    cd launch4j
    launch4jc.exe ../release_files/release_config.xml
    cd ..
    echo Simple build completed - JAR and EXE files available in release_files/
    echo Note: Use build_mercury.bat for full packaging with zip files
) else (
    echo Build failed. Cannot create EXE without JAR file.
    exit /b 1
)
