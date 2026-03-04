@echo off
chcp 65001
echo ========================================
echo 启动前端用户端项目
echo ========================================
echo.

echo [提示] 确保已切换到 Node.js 16
echo 当前Node版本:
node -v
echo.

echo [1/2] 检查依赖...
if not exist node_modules (
    echo node_modules不存在,正在安装依赖...
    npm install --legacy-peer-deps
)

echo [2/2] 启动开发服务器...
npm run serve

pause

