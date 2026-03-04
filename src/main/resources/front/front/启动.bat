@echo off
chcp 65001
echo ========================================
echo 启动前端用户端项目 (已修复CSS语法)
echo ========================================
echo.

cd /d "%~dp0"

echo [提示] 当前Node版本:
node -v
echo.

echo [启动] 正在启动开发服务器...
npm run serve

pause

