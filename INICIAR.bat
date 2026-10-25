@echo off
cd /d "%~dp0"
echo Abriendo Gimnasio en http://localhost:8081
echo Verifique que MySQL de XAMPP este iniciado en el puerto 3307.
where mvn >nul 2>nul
if errorlevel 1 (
 echo Maven no esta en PATH. Use IntelliJ: Maven, Execute Maven Goal, compile exec:java
 pause
 exit /b 1
)
call mvn compile exec:java
pause
