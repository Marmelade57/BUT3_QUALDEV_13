@echo off
setlocal enabledelayedexpansion

rem ============================================
rem Configuration des chemins et variables
rem ============================================
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
if "%SONAR_TOKEN%"=="" (
    set "SONAR_TOKEN=ada0520817d2a5383da445e8922b4af80029b032"
)
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
rem ÉTAPE 2 : Build + Tests + JaCoCo
rem ============================================
echo.
echo [========================================]
echo [ ÉTAPE 2/3 : Build, Tests et Génération du rapport JaCoCo ]
echo [========================================]
echo [INFO] Building project and running tests...
call mvn -B -Pcoverage clean verify ^
    -Dmaven.test.failure.ignore=true ^
    -DskipTests=false ^
    -Djacoco.destFile=target/jacoco.exec ^
    -Djacoco.append=true ^
    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml,target/jacoco-ut/jacoco.xml ^
    -Dsonar.jacoco.reportPaths=target/jacoco.exec ^
    -Dsonar.jacoco.reportPath=target/jacoco.exec ^
    -Dsonar.java.coveragePlugin=jacoco ^
    -Dsonar.coverage.exclusions=**/model/**,**/dto/**,**/exception/** ^
    -Dsonar.java.binaries=target/classes ^
    -Dsonar.java.libraries=target/dependency/*.jar
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Build failed. Check Maven logs for details.
    exit /b 1
)
echo [INFO] Build and tests completed successfully.

rem Vérification du rapport JaCoCo
echo [INFO] Vérification du rapport JaCoCo...
set "JACOCO_XML=target\site\jacoco\jacoco.xml"
set "JACOCO_EXEC=target\jacoco.exec"

if not exist "%JACOCO_EXEC%" (
    echo [ERROR] Fichier jacoco.exec introuvable dans target/
    exit /b 1
)

if not exist "%JACOCO_XML%" (
    echo [INFO] Génération du rapport JaCoCo XML...
    call mvn jacoco:report -Djacoco.dataFile=%JACOCO_EXEC%
    if not exist "%JACOCO_XML%" (
        echo [ERROR] Échec de la génération du rapport JaCoCo XML
        exit /b 1
    )
)

echo [INFO] Rapport JaCoCo prêt pour l'analyse SonarCloud

rem ============================================
rem ÉTAPE 3 : Envoi à SonarCloud
rem ============================================
echo.
echo [========================================]
echo [ ÉTAPE 3/3 : Envoi à SonarCloud ]
echo [========================================]
echo [INFO] Running SonarCloud analysis...
echo [INFO] Using JaCoCo report: %JACOCO_XML%

echo [INFO] Lancement de l'analyse SonarCloud...
rem Construire la commande SonarQube
set "SONAR_CMD=mvn -B sonar:sonar"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.projectKey=%SONAR_PROJECT_KEY%"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.organization=%SONAR_ORG%"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.host.url=%SONAR_HOST%"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.token=%SONAR_TOKEN%"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.java.coveragePlugin=jacoco"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.coverage.jacoco.xmlReportPaths=%JACOCO_XML%"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.java.binaries=target/classes"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.sources=src/main/java"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.tests=src/test/java"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.test.inclusions=src/test/**/*"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.sourceEncoding=UTF-8"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.java.source=17"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.coverage.exclusions=**/model/**,**/dto/**,**/exception/**"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.cpd.exclusions=**/model/**,**/dto/**"

set "SONAR_CMD=!SONAR_CMD! -Dsonar.jacoco.reportPaths=target/jacoco.exec"
set "SONAR_CMD=!SONAR_CMD! -Dsonar.verbose=true"

echo [INFO] Commande SonarQube: !SONAR_CMD!
call !SONAR_CMD!

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
