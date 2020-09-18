
set temp=%~dp0
set temp = %temp:~0,-1%
cd %temp%bin

java IsoEngine

