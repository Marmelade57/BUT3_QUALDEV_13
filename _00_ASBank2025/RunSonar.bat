@echo off
setlocal enabledelayedexpansion

rem Définition des chemins
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

rem Configuration Java
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17+10"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [INFO] Using JAVA_HOME=%JAVA_HOME%

rem Configuration SonarCloud
set "SONAR_PROJECT_KEY=Marmelade57_BUT3_QUALDEV_13"
set "SONAR_ORG=marmelade57"
set "SONAR_HOST=https://sonarcloud.io"
set "SONAR_TOKEN=%SONAR_TOKEN%"
if "%SONAR_TOKEN%"=="" set "SONAR_TOKEN=ada0520817d2a5383da445e8922b4af80029b032"

echo [INFO] SonarCloud parameters: projectKey=%SONAR_PROJECT_KEY% organization=%SONAR_ORG% host=%SONAR_HOST%
call mvn -version

rem Nettoyage des anciens rapports
if exist "target\site\jacoco" rmdir /s /q "target\site\jacoco"
if exist "target\coverage-reports" rmdir /s /q "target\coverage-reports"

rem 1) Build + tests + génération du rapport JaCoCo
echo [INFO] Building project and generating JaCoCo report...
call mvn -B -Pcoverage clean verify -Dmaven.test.failure.ignore=true -DskipTests=false
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Build failed. Check Maven logs for details.
    exit /b 1
)

rem Vérification de la présence du rapport JaCoCo
set "JACOCO_XML=target\site\jacoco\jacoco.xml"
if not exist "%JACOCO_XML%" (
    echo [ERROR] JaCoCo report not found: %JACOCO_XML%
    echo [INFO] Checking for JaCoCo execution data...
    if exist "target\jacoco.exec" (
        echo [INFO] Found jacoco.exec file. Generating report...
        call mvn jacoco:report -Djacoco.dataFile=target/jacoco.exec
        if not exist "%JACOCO_XML%" (
            echo [ERROR] Failed to generate JaCoCo report.
            exit /b 1
        )
    ) else (
        echo [ERROR] No JaCoCo execution data found. Make sure tests are executed.
        exit /b 1
    )
)

echo [INFO] JaCoCo report found at: %JACOCO_XML%

rem 2) Analyse SonarCloud
echo [INFO] Running SonarCloud analysis...
call mvn -B sonar:sonar ^
    -Dsonar.projectKey=%SONAR_PROJECT_KEY% ^
    -Dsonar.organization=%SONAR_ORG% ^
    -Dsonar.host.url=%SONAR_HOST% ^
    -Dsonar.token=%SONAR_TOKEN% ^
    -Dsonar.java.coveragePlugin=jacoco ^
    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml ^
    -Dsonar.java.binaries=target/classes ^
    -Dsonar.sources=src/main/java ^
    -Dsonar.tests=src/test/java ^
    -Dsonar.test.inclusions=src/test/**/*

if %ERRORLEVEL% neq 0 (
    echo [ERROR] SonarCloud analysis failed.
    exit /b 1
)

echo [INFO] SonarCloud analysis completed successfully.
endlocal