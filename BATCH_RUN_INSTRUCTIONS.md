# Electricity Billing System - Quick Start Guide

This document provides instructions for running the Electricity Billing System application using the included batch files.

## Prerequisites

1. **Java JDK**: Make sure you have Java Development Kit installed (JDK 8 or higher recommended)
2. **Microsoft Access Database**: The application uses Microsoft Access database (.mdb file)

## Running the Application

### Method 1: Using the Batch File (Recommended)

1. Double-click on `run_project.bat` in Windows Explorer
   - This will automatically check for required libraries
   - If libraries are missing, it will run the setup process
   - It will compile the code if needed and run the application

### Method 2: Manual Setup

If the automatic method doesn't work:

1. Run `setup_libraries.bat` first to set up the required libraries
2. After libraries are set up, run `run_project.bat`

## Troubleshooting

If you encounter issues:

### Missing Libraries

The application requires the UCanAccess library to connect to the Microsoft Access database. If these libraries are missing:

1. Run `setup_libraries.bat` and follow the instructions
2. Download UCanAccess from: https://ucanaccess.sourceforge.net/site.html
3. Copy all JAR files to the `lib` folder

### Compilation Errors

If you see compilation errors:

1. Make sure Java JDK is installed and properly set in your PATH
2. Check that all required libraries are in the `lib` folder
3. Look at the specific error messages for further troubleshooting

### Runtime Errors

Common runtime errors include:

1. **Database not found**: Make sure `ebs.mdb` exists in the application directory
2. **Connection errors**: Ensure that UCanAccess libraries are properly installed
3. **Java errors**: Make sure you're using a compatible Java version (Java 8 or higher recommended)

## Manual Execution

If batch files don't work, you can run the application manually:

```
javac -cp "lib\*;." src\*.java -d out\classes
java -cp "out\classes;lib\*;." splash
```

## Support

If you need further assistance, please refer to the original documentation or raise an issue in the project repository. 