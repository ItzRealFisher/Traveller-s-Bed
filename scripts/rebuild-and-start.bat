@echo off
call "%~dp0build-plugin.bat" || exit /b 1
call "%~dp0deploy-plugin.bat" || exit /b 1
call "%~dp0start-server.bat"
