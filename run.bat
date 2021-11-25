
set temp=%~dp0
set temp = %temp:~0,-1%
cd %temp%bin

java -classpath "jar\gluegen-rt.jar;jar\jogl-all.jar;." IsoEngine

// @pause