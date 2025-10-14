@echo off
title Team Project 2 Launcher
color 0B

echo.
echo Checking Dependencies plz wait 
echo you are currently running group 25 TP2 Assignment for cse360

REM just making sure our folders exist so stuff doesn’t error out
if not exist lib mkdir lib
if not exist bin mkdir bin

REM checking if the h2 database jar is already there
REM if not, we just download it straight from maven
if not exist "lib\h2-2.2.224.jar" (
    echo [Downloading H2 Database...]
    powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar -OutFile 'lib\h2-2.2.224.jar'"
)

REM next up, we check if javafx is already set up
REM if not, we grab the sdk zip from gluonhq and extract it automatically
if not exist "lib\javafx-sdk-25\lib\javafx.base.jar" (
    echo [Downloading JavaFX SDK... this may take a moment]
    powershell -Command "Invoke-WebRequest -Uri https://download2.gluonhq.com/openjfx/25/openjfx-25_windows-x64_bin-sdk.zip -OutFile 'lib\javafx-sdk.zip'"
    echo [Extracting JavaFX...]
    powershell -Command "Expand-Archive -Path 'lib\javafx-sdk.zip' -DestinationPath 'lib' -Force"
    ren "lib\javafx-sdk-25_windows-x64_bin-sdk" "javafx-sdk-25"
    del "lib\javafx-sdk.zip"
)

echo.
echo === Compiling Java Files ===

REM we’re gonna grab every single .java file under src and put them all into one list
REM this way we compile everything together so nothing is missing
del sources.txt 2>nul
for /r src %%f in (*.java) do echo %%f >> sources.txt

REM now actually compile everything and toss the .class files into bin
javac -d bin -cp "lib\h2-2.2.224.jar;lib\javafx-sdk-25\lib\*;." @sources.txt
if %errorlevel% neq 0 (
    echo.
    echo [!] Compilation failed. Check your code or imports.
    pause
    exit /b
)
del sources.txt

echo.
echo === Launching Application ===

REM finally, launch the main program using javafx and h2 paths we just set up
REM this should open the StartCSE360 window if everything compiled right
java --module-path "lib\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml ^
    -Djava.library.path="lib\javafx-sdk-25\bin" ^
    -cp "bin;lib\h2-2.2.224.jar;." application.StartCSE360

echo.
echo [Application Closed]
pause
