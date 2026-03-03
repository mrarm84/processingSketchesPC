@echo off
setlocal
set "BASE_DIR=%~dp0"
set "FLUXUS_DIR=%BASE_DIR%fluxus_bin\Fluxus"
set "PLTCOLLECTS=%FLUXUS_DIR%\lib"

pushd "%FLUXUS_DIR%\bin"
echo Starting Fluxus with local library paths...
.\fluxus.exe -s "%BASE_DIR%main.scm"
if %ERRORLEVEL% neq 0 (
    echo.
    echo Fluxus exited with error code %ERRORLEVEL%
    pause
)
popd
endlocal
