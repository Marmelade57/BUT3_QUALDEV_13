@echo off
echo Mise à jour des chemins des ressources statiques...

REM Mise à jour des références à style.css
for /r "WebContent\JSP" %%f in (*.jsp) do (
    powershell -Command "(Get-Content '%%f') -replace '/_00_ASBank2025/WebContent/style/style.css', 'style/style.css' | Set-Content '%%f' -Encoding UTF8"
)

REM Mise à jour des références à favicon.ico
for /r "WebContent\JSP" %%f in (*.jsp) do (
    powershell -Command "(Get-Content '%%f') -replace '/_00_ASBank2025/WebContent/style/favicon.ico', 'style/favicon.ico' | Set-Content '%%f' -Encoding UTF8"
)

echo Mise à jour terminée. N'oubliez de redéployer votre application.
pause
