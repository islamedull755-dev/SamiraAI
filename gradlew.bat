@echo off
set ROOT=%~dp0
set VERSION=8.9
set DIST=%USERPROFILE%\.gradle\samira-wrapper\gradle-%VERSION%
if not exist "%DIST%\bin\gradle.bat" (
  if not exist "%USERPROFILE%\.gradle\samira-wrapper" mkdir "%USERPROFILE%\.gradle\samira-wrapper"
  powershell -NoProfile -Command "Invoke-WebRequest https://services.gradle.org/distributions/gradle-%VERSION%-bin.zip -OutFile $env:TEMP\gradle-%VERSION%-bin.zip; Expand-Archive -Force $env:TEMP\gradle-%VERSION%-bin.zip '%USERPROFILE%\.gradle\samira-wrapper'"
)
call "%DIST%\bin\gradle.bat" -p "%ROOT%" %*
