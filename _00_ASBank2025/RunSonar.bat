@echo on
setlocal
cd /d "%~dp0"

rem Force le JDK 17 pour Maven (adapter si ton chemin diffère)
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17+10"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Using JAVA_HOME=%JAVA_HOME%
set "SONAR_PROJECT_KEY=Marmelade57_BUT3_QUALDEV_13"
set "SONAR_ORG=marmelade57"
set "SONAR_HOST=https://sonarcloud.io"
set "SONAR_TOKEN=%SONAR_TOKEN%"
if "%SONAR_TOKEN%"=="" set "SONAR_TOKEN=ada0520817d2a5383da445e8922b4af80029b032"

echo Sonar params: projectKey=%SONAR_PROJECT_KEY% organization=%SONAR_ORG% host=%SONAR_HOST%
call mvn -version

rem 1) Build + tests + génération du rapport JaCoCo (ne pas échouer sur tests KO)
call mvn -B -Pcoverage clean verify -Dmaven.test.failure.ignore=true -DskipTests=false
if errorlevel 1 goto :eof

rem Vérification présence du rapport
if not exist target\site\jacoco\jacoco.xml (
  echo [ERROR] Rapport JaCoCo introuvable: target\site\jacoco\jacoco.xml
  goto :eof
)

rem 2) Analyse SonarCloud
call mvn -B sonar:sonar -Dsonar.projectKey=%SONAR_PROJECT_KEY% -Dsonar.organization=%SONAR_ORG% -Dsonar.host.url=%SONAR_HOST% -Dsonar.token=%SONAR_TOKEN% -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

endlocal
@echo off