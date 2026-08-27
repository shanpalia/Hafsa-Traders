@echo off
if defined GRADLE_HOME (
  "%GRADLE_HOME%\bin\gradle.bat" %*
) else (
  gradle %*
)
