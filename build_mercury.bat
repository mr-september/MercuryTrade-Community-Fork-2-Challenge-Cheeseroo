@echo off
cls
echo MercuryChat build starting
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
        echo ERROR: No JAR file available. Cannot proceed with packaging.
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
    echo build_mercury_bat completed

    echo preparing zip files for release starting...
echo zipping with jar file start
cd release_files
echo removing old zip files
del MercuryChat-jar.zip
del MercuryChat-exe.zip

REM Create JAR package with proper structure
echo Creating JAR package with resources...
if exist MercuryChat-jar rd /s /q MercuryChat-jar
mkdir MercuryChat-jar
copy MercuryChat.jar MercuryChat-jar\
copy HOW_TO_RUN_JAR.txt MercuryChat-jar\
xcopy MercuryChat\* MercuryChat-jar\ /e /i
call powershell Compress-Archive -Force MercuryChat-jar MercuryChat-jar.zip
rd /s /q MercuryChat-jar
echo JAR package completed with resources

REM Create EXE package with proper structure
echo Creating EXE package with resources...
if exist MercuryChat-exe rd /s /q MercuryChat-exe
mkdir MercuryChat-exe
copy MercuryChat.exe MercuryChat-exe\
xcopy MercuryChat\* MercuryChat-exe\ /e /i
call powershell Compress-Archive -Force MercuryChat-exe MercuryChat-exe.zip
rd /s /q MercuryChat-exe
echo EXE package completed with resources

echo zipping with lang started
echo removing old lang files
del lang.zip
call powershell Compress-Archive ../app-shared/src/main/resources/lang/* lang.zip
echo zipping with lang ended

echo Cleaning up standalone files...
del MercuryChat.jar
del MercuryChat.exe
echo Standalone files removed

echo Build and packaging completed!
echo.
echo Files created:
echo  - MercuryChat-jar.zip (complete package with resources)
echo  - MercuryChat-exe.zip (complete package with resources)
echo  - lang.zip
echo.
cd ..
) else (
    echo Build failed. Cannot create packages without JAR file.
    exit /b 1
)
