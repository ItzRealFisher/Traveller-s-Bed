@echo off
set "JAR=%~dp0..\plugin\build\libs\TravellersBed-1.0.0.jar"
if not exist "%JAR%" (echo Build the plugin first.& exit /b 1)
copy /Y "%JAR%" "%~dp0..\..\server\plugins\TravellersBed.jar" >nul
echo Deployed TravellersBed.jar. Ensure the server is stopped before deploying.
