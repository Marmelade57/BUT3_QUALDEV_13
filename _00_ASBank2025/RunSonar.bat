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

rem ============================================
rem ÉTAPE 1 : Nettoyage
rem ============================================
echo.
echo [========================================]
echo [ ÉTAPE 1/3 : Nettoyage des anciens rapports ]
echo [========================================]
if exist "target\site\jacoco" rmdir /s /q "target\site\jacoco"
if exist "target\coverage-reports" rmdir /s /q "target\coverage-reports"
echo [INFO] Nettoyage terminé.

rem ============================================
rem ÉTAPE 2 : Build + Tests + Génération du rapport JaCoCo
rem ============================================
echo.
echo [========================================]
echo [ ÉTAPE 2/3 : Build, Tests et Génération du rapport JaCoCo ]
echo [========================================]
echo [INFO] Building project and running tests...
call mvn -B -Pcoverage clean verify -Dmaven.test.failure.ignore=true -DskipTests=false
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Build failed. Check Maven logs for details.
    exit /b 1
)

echo [INFO] Build and tests completed successfully.

rem Vérification et génération du rapport JaCoCo
set "JACOCO_XML=target\site\jacoco\jacoco.xml"
echo [INFO] Checking for JaCoCo report at: %JACOCO_XML%

if not exist "%JACOCO_XML%" (
    echo [WARN] JaCoCo XML report not found. Attempting to generate it...
    echo [INFO] Checking for JaCoCo execution data...
    if exist "target\jacoco.exec" (
        echo [INFO] Found jacoco.exec file. Generating XML report...
        call mvn jacoco:report -Djacoco.dataFile=target/jacoco.exec
        if not exist "%JACOCO_XML%" (
            echo [ERROR] Failed to generate JaCoCo XML report.
            echo [ERROR] Cannot proceed to SonarCloud analysis without coverage report.
            exit /b 1
        )
        echo [INFO] JaCoCo XML report generated successfully.
    ) else (
        echo [ERROR] No JaCoCo execution data found (target\jacoco.exec).
        echo [ERROR] Make sure tests are executed with coverage enabled.
        exit /b 1
    )
) else (
    echo [INFO] JaCoCo XML report already exists.
)

rem Vérification finale que le rapport existe bien
if not exist "%JACOCO_XML%" (
    echo [ERROR] JaCoCo report still not found after generation attempt: %JACOCO_XML%
    echo [ERROR] Cannot proceed to SonarCloud analysis.
    exit /b 1
)

echo [INFO] JaCoCo report verified at: %JACOCO_XML%
echo [INFO] File size: 
for %%A in ("%JACOCO_XML%") do echo [INFO]   %%~zA bytes

rem ============================================
rem ÉTAPE 3 : Envoi à SonarCloud
rem ============================================
echo.
echo [========================================]
echo [ ÉTAPE 3/3 : Envoi à SonarCloud ]
echo [========================================]
echo [INFO] Running SonarCloud analysis...
echo [INFO] Using JaCoCo report: %JACOCO_XML%
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

echo.
echo [========================================]
echo [ SUCCÈS : Analyse SonarCloud terminée ]
echo [========================================]
echo [INFO] SonarCloud analysis completed successfully.
echo [INFO] Check your results at: https://sonarcloud.io/project/overview?id=%SONAR_PROJECT_KEY%
echo.
endlocal