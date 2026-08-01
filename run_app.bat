@echo off
echo Electricity Billing System
echo =========================
echo.

REM Check for Java 7
java -version 2>&1 | findstr "1.7" > nul
if %errorlevel% neq 0 (
    echo WARNING: You may not be using Java 7!
    echo This application requires Java 7 for JDBC-ODBC Bridge support.
    echo.
    pause
)

REM Check for ebs.mdb
if not exist ebs.mdb (
    echo ERROR: Database file ebs.mdb not found!
    echo Please create an empty Access database file named ebs.mdb in this folder.
    echo.
    pause
    exit /b
)

REM Compile Java files
echo Compiling Java files...
cd src
javac *.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    cd ..
    pause
    exit /b
)
cd ..
echo.

REM Run the application
echo Starting Electricity Billing System...
echo.
echo Please log in with:
echo - Username: admin
echo - Password: admin
echo.

java -cp src login

echo.
pause 