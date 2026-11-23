@echo off
call "%~dp0..\plugin\gradlew.bat" -p "%~dp0..\plugin" clean test jar
