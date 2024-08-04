@echo off
setlocal

REM Check if an argument is provided
if "%1"=="" (
    cls
    mvn exec:java -e -DLOG4J_SKIP_JANSI=false
)

REM Handle the --version argument
if "%1"=="--invite" (
    mvn exec:java -e -DLOG4J_SKIP_JANSI=false -Dexec.args="--invite"
)

REM Handle other arguments here
echo Unknown argument: %1


:end
endlocal


cls
mvn exec:java -e -DLOG4J_SKIP_JANSI=false