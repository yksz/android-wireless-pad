@echo off
cd /d %~dp0
if not exist build (
    mkdir build
)
cd build

cmake -DLWS_WITH_SSL:BOOL=OFF -G "Visual Studio 12" ..
pause
