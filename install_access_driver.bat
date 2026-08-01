@echo off
title Access ODBC Driver Setup
echo ===================================
echo Microsoft Access ODBC Driver Setup
echo ===================================
echo.

echo This script will help you set up the Access ODBC driver required
echo for the Electricity Billing System to connect to the database.
echo.

:MENU
echo What would you like to do?
echo 1. Download Microsoft Access Database Engine (required for ODBC connection)
echo 2. Create a new empty Access database file (ebs.mdb)
echo 3. Test database connection
echo 4. Exit
echo.

set /p choice=Enter your choice (1-4): 

if "%choice%"=="1" goto DOWNLOAD
if "%choice%"=="2" goto CREATE_DB
if "%choice%"=="3" goto TEST_CONN
if "%choice%"=="4" goto END

echo Invalid choice. Please try again.
goto MENU

:DOWNLOAD
echo.
echo Which version do you need?
echo NOTE: You need to match your Java architecture!
echo 1. 32-bit Access Database Engine
echo 2. 64-bit Access Database Engine
echo 3. Back to main menu
echo.

set /p arch=Enter your choice (1-3): 

if "%arch%"=="1" (
    start https://www.microsoft.com/en-us/download/details.aspx?id=13255
    echo Browser opened to download 32-bit Microsoft Access Database Engine.
)
if "%arch%"=="2" (
    start https://www.microsoft.com/en-us/download/details.aspx?id=54920
    echo Browser opened to download 64-bit Microsoft Access Database Engine.
)
if "%arch%"=="3" goto MENU

echo.
echo After downloading, please run the installer.
echo Then return to this script and select option 3 to test the connection.
echo.
pause
goto MENU

:CREATE_DB
echo.
echo To create a new Access database file:
echo 1. You need Microsoft Access installed, or
echo 2. You can download Microsoft Access Database Engine and use it
echo.
echo Would you like to:
echo 1. Open instructions for manually creating an Access database
echo 2. Back to main menu
echo.

set /p db_choice=Enter your choice (1-2): 

if "%db_choice%"=="1" (
    echo.
    echo ========= CREATING AN ACCESS DATABASE FILE =========
    echo.
    echo If you have Microsoft Access:
    echo  1. Open Microsoft Access
    echo  2. Create a new blank database
    echo  3. Save it as "ebs.mdb" in this folder:
    echo     %CD%
    echo.
    echo If you don't have Microsoft Access:
    echo  1. Complete the Access Database Engine installation first
    echo  2. The application will attempt to create tables when run
    echo  3. But you may need Microsoft Access to create the initial file
    echo.
)
if "%db_choice%"=="2" goto MENU

pause
goto MENU

:TEST_CONN
echo.
echo Testing database connection...
echo.

REM Check if ebs.mdb exists
if not exist ebs.mdb (
    echo ERROR: Database file ebs.mdb not found!
    echo Please create an Access database file first.
    echo.
    pause
    goto MENU
)

REM Check if we have Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo Java not found. Please install Java 7.
    pause
    goto MENU
)

REM Try to compile and run the TestConnectionStrings class if it exists
if exist src\TestConnectionStrings.java (
    cd src
    javac TestConnectionStrings.java
    cd ..
    java -cp src TestConnectionStrings
) else (
    echo Test file not found. Trying a basic run...
    java -cp src login
)

echo.
pause
goto MENU

:END
echo.
echo Setup completed.
echo.
pause 