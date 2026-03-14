@echo off
setlocal

set ODRIVE_DIR=.
set ODRIVE_ARTIFACT=${project.artifactId}-${project.version}

java -jar %ODRIVE_DIR%\lib\%ODRIVE_ARTIFACT%-spring-boot.jar %*

endlocal
@echo on
