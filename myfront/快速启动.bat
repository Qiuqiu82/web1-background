@echo off
chcp 65001
echo ========================================
echo Vue 2 前端项目 - 快速启动
echo ========================================
echo.

cd /d "%~dp0"

echo 检查依赖是否已安装...
if not exist node_modules (
    echo.
    echo [警告] 未检测到 node_modules 目录
    echo 需要先安装依赖，是否现在安装？
    echo.
    choice /C YN /M "是否安装依赖"
    if errorlevel 2 goto :end
    if errorlevel 1 goto :install
) else (
    echo ✓ 依赖已安装
    goto :serve
)

:install
echo.
echo ========================================
echo 开始安装依赖...
echo ========================================
call npm install --legacy-peer-deps
if %errorlevel% neq 0 (
    echo.
    echo [错误] 依赖安装失败！
    pause
    exit /b 1
)

:serve
echo.
echo ========================================
echo 启动开发服务器...
echo ========================================
echo.
echo 项目将在浏览器中自动打开
echo 访问地址: http://localhost:8080
echo.
echo 按 Ctrl+C 停止服务器
echo ========================================
echo.
call npm run serve

:end
pause

