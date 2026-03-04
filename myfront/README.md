# Vue 2 前端项目

## 技术栈

- Vue 2.6.11
- Vue Router 3.5.2
- Vuex 3.1.1
- Element UI 2.15.5
- Vue CLI 4.5.0
- Sass/SCSS

## 环境要求

- Node.js: 12.x 或 14.x
- npm: 6.x 或更高

## 安装依赖

```bash
npm install --legacy-peer-deps
```

或使用完整安装脚本：
```bash
完整安装.bat
```

## 运行项目

### 开发环境
```bash
npm run serve
```

或使用启动脚本：
```bash
run.bat
```

### 生产构建
```bash
npm run build
```

或使用构建脚本：
```bash
build.bat
```

## 项目结构

```
myfront/
├── public/          # 静态资源
├── src/
│   ├── assets/      # 图片、样式等资源
│   ├── common/      # 公共工具函数
│   ├── components/  # 公共组件
│   ├── config/      # 配置文件
│   ├── pages/       # 页面组件
│   ├── router/      # 路由配置
│   ├── store/       # Vuex 状态管理
│   ├── App.vue      # 根组件
│   └── main.js      # 入口文件
├── babel.config.js  # Babel 配置
├── vue.config.js    # Vue CLI 配置
└── package.json     # 项目依赖
```

## 常见问题

### 1. 安装依赖失败
- 清理缓存：`npm cache clean --force`
- 使用 cnpm：`cnpm install`
- 删除 node_modules 后重新安装

### 2. 构建失败
- 检查 Node.js 版本
- 确保依赖完整安装
- 增加内存限制（已在 package.json 中配置）

### 3. 端口被占用
修改 vue.config.js 中的 devServer.port

## 开发说明

- 默认开发服务器端口：8080
- API 代理配置：vue.config.js
- 后端接口地址：src/config/config.js

