@echo off
if not exist "%~dp0..\..\server\eula.txt" (echo Minecraft EULA has not been accepted. Create server\eula.txt with eula=true after reviewing https://aka.ms/MinecraftEULA& exit /b 1)
pushd "%~dp0..\..\server"
java -Xms3G -Xmx3G -jar paper.jar --nogui
popd
