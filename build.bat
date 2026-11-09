@echo off
chcp 65001 > nul
set CP=Drivers\flatlaf-3.6.jar;Drivers\jcommon-1.0.24.jar;Drivers\jfreechart-1.5.3.jar;Drivers\mysql-connector-j-9.3.0\mysql-connector-j-9.3.0.jar
if not exist "out\production\SmartExpenseTracker" mkdir "out\production\SmartExpenseTracker"
del /q /s "out\production\SmartExpenseTracker\*" > nul 2>&1
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "%CP%" -d "out\production\SmartExpenseTracker" @sources.txt
if %ERRORLEVEL% EQU 0 (
    echo BUILD SUCCESSFUL
) else (
    echo BUILD FAILED
)

