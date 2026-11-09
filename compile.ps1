$ErrorActionPreference = "Stop"
$cp = "Drivers/flatlaf-3.6.jar;Drivers/jcommon-1.0.24.jar;Drivers/jfreechart-1.5.3.jar;Drivers/mysql-connector-j-9.3.0/mysql-connector-j-9.3.0.jar"
if (!(Test-Path "out/production/SmartExpenseTracker")) {
    New-Item -ItemType Directory -Force -Path "out/production/SmartExpenseTracker" | Out-Null
}
$files = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
Write-Host "Compiling $($files.Count) Java files..."
& javac -encoding UTF-8 -cp $cp -d "out/production/SmartExpenseTracker" $files
Write-Host "Compilation finished with exit code: $LASTEXITCODE"

