@echo off
chcp 65001
echo 开始构建前端项目...
call npm run build
if %errorlevel% neq 0 (
    echo 构建失败！
    pause
    exit /b 1
)
echo 构建成功！
pause