@echo off
cls
echo MercuryTrade build starting
echo mvn clean package
call mvn clean package
if %errorlevel% neq 0 (
    echo Maven build failed. Checking for existing JAR file...
    if exist "release_files\MercuryTrade-jar-fixed\MercuryTrade.jar" (
        echo Using JAR from MercuryTrade-jar-fixed\
        copy "release_files\MercuryTrade-jar-fixed\MercuryTrade.jar" "release_files\"
        set JAR_AVAILABLE=true
    ) else if exist "app\target\MercuryTrade.jar" (
        echo Using existing JAR from app\target\
        copy "app\target\MercuryTrade.jar" "release_files\"
        set JAR_AVAILABLE=true
    ) else (
        echo ERROR: No JAR file available. Cannot proceed with packaging.
        exit /b 1
    )
) else (
    echo Maven build successful
    echo Copying MercuryTrade.jar from app/target to release_files
    cd app\target
    copy MercuryTrade.jar "..\..\release_files"
    cd ..\..
    set JAR_AVAILABLE=true
)
if "%JAR_AVAILABLE%"=="true" (
    echo Launching launch4j.exe to generate MercuryTrade.exe from .jar file
    cd launch4j
    launch4jc.exe ../release_files/release_config.xml
    cd ..
    echo build_mercury_bat completed

    echo preparing zip files for release starting...
echo zipping with jar file start
cd release_files
echo removing old zip files
del MercuryTrade-jar.zip
del MercuryTrade-exe.zip

REM Create JAR package with proper structure
echo Creating JAR package with resources...
if exist MercuryTrade-jar rd /s /q MercuryTrade-jar
mkdir MercuryTrade-jar
copy MercuryTrade.jar MercuryTrade-jar\
copy HOW_TO_RUN_JAR.txt MercuryTrade-jar\
xcopy MercuryTrade\* MercuryTrade-jar\ /e /i
call powershell Compress-Archive -Force MercuryTrade-jar MercuryTrade-jar.zip
rd /s /q MercuryTrade-jar
echo JAR package completed with resources

REM Create EXE package with proper structure
echo Creating EXE package with resources...
if exist MercuryTrade-exe rd /s /q MercuryTrade-exe
mkdir MercuryTrade-exe
copy MercuryTrade.exe MercuryTrade-exe\
xcopy MercuryTrade\* MercuryTrade-exe\ /e /i
call powershell Compress-Archive -Force MercuryTrade-exe MercuryTrade-exe.zip
rd /s /q MercuryTrade-exe
echo EXE package completed with resources

echo zipping with lang started
echo removing old lang files
del lang.zip
call powershell Compress-Archive ../app-shared/src/main/resources/lang/* lang.zip
echo zipping with lang ended

echo Cleaning up standalone files...
del MercuryTrade.jar
del MercuryTrade.exe
echo Standalone files removed

echo Build and packaging completed!
echo.
echo Files created:
echo  - MercuryTrade-jar.zip (complete package with resources)
echo  - MercuryTrade-exe.zip (complete package with resources)
echo  - lang.zip
echo.
cd ..
) else (
    echo Build failed. Cannot create packages without JAR file.
    exit /b 1
)