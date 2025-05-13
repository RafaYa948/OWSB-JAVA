@echo off
setlocal

rem === Configuration ===
set SRC=src
set BIN=bin
set MAIN=LoginPage

rem === Compile Java files ===
echo Compiling Java files...
if not exist %BIN% (
    mkdir %BIN%
)

rem Collect all .java files under src recursively
for /r %SRC% %%f in (*.java) do (
    echo Compiling: %%f
)

javac -d %BIN% %SRC%\**\*.java
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)

rem === Run main class ===
echo Running %MAIN%...
java -cp %BIN% %MAIN%

endlocal
