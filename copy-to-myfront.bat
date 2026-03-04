@echo off
chcp 65001
echo ========================================
echo 复制前端项目到 myfront 目录
echo ========================================
echo.

cd /d "%~dp0"

echo [1/3] 删除旧的 myfront 目录（如果存在）...
if exist myfront (
    rmdir /s /q myfront
)

echo.
echo [2/3] 复制项目文件...
xcopy "src\main\resources\front\front" "myfront" /E /I /H /Y /EXCLUDE:copy-exclude.txt

echo.
echo [3/3] 清理不需要的文件...
if exist myfront\node_modules (
    echo 删除 node_modules...
    rmdir /s /q myfront\node_modules
)
if exist myfront\package-lock.json (
    del /f myfront\package-lock.json
)
if exist myfront\dist (
    rmdir /s /q myfront\dist
)

echo.
echo ========================================
echo 复制完成！
echo 项目位置: %~dp0myfront
echo.
echo 下一步操作:
echo 1. cd myfront
echo 2. npm install --legacy-peer-deps
echo 3. npm run serve
echo ========================================
pause

