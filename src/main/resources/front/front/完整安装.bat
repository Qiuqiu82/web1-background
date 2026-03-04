@echo off
chcp 65001
echo ========================================
echo 完整重新安装前端依赖
echo ========================================
echo.

cd /d "%~dp0"

echo [1/5] 删除旧的依赖...
if exist node_modules (
    echo 正在删除 node_modules...
    rmdir /s /q node_modules
)
if exist package-lock.json (
    del /f package-lock.json
)

echo.
echo [2/5] 清理npm缓存...
call npm cache clean --force

echo.
echo [3/5] 验证Node版本...
node -v
npm -v

echo.
echo [4/5] 重新安装依赖 (这可能需要5-10分钟)...
call npm install --legacy-peer-deps

echo.
echo [5/5] 验证安装...
if exist node_modules\@vue\cli-service (
    echo ✓ @vue/cli-service 安装成功
) else (
    echo ✗ @vue/cli-service 安装失败
    pause
    exit /b 1
)

if exist node_modules\.bin\vue-cli-service.cmd (
    echo ✓ vue-cli-service 命令可用
) else (
    echo ✗ vue-cli-service 命令不可用
    pause
    exit /b 1
)

echo.
echo ========================================
echo 安装完成! 现在可以运行 npm run serve
echo ========================================
pause

