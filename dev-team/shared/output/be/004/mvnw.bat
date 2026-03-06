@REM Maven Wrapper startup script for Windows
@echo off
set JAVA_HOME=C:\Program Files\Java\openjdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

@REM Download Maven if not exists
if not exist "%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.5-bin.zip" (
    echo Downloading Maven...
    powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.zip' -OutFile '%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.5-bin.zip'"
)

echo Starting Maven...
java -version
