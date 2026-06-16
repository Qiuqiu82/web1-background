# 裁次元-动漫服装定制平台

本项目是基于 Spring Boot + MyBatis Plus + Vue 的服装定制系统后端项目，支持普通用户、管理员、设计相关能力，并联动 AI RAG 服务和虚拟试穿服务。

# 虚拟试穿展示
https://github.com/user-attachments/assets/13acd06c-27fa-4eac-84a2-8fa11212bd18
# RAG生成展示
https://github.com/user-attachments/assets/335afe6a-407c-4e39-9d50-7291288c2d99
## 项目组成

- 后端：Spring Boot 2.2.2
- 前端：Vue2
- RAG 服务：FastAPI + LlamaIndex、React+TypeScript
- 虚拟试穿服务：Stable Diffusion Inpainting、PyTorch、FastAPI

## 技术栈

- Spring Boot 2.2.2
- MyBatis Plus
- MySQL
- Apache Shiro
- Vue 2
- AI RAG
- PyTorch

## 功能概览

- 用户登录、注册与权限控制
- 服装浏览、收藏、评论、预约/订单管理
- 管理员后台管理
- AI 智能问答 / RAG 能力
- 虚拟试穿（图片生成）能力

## 运行环境

- JDK 1.8
- Maven
- Node.js 12.x / 14.x
- npm 6.x+
- MySQL 5.7+（推荐）

## 快速启动

### 1. 启动数据库

先打开 `Mysql`，并导入项目中的数据库脚本。

推荐导入：

- `db/springboot0le12.sql`

### 2. 启动后端

用 IntelliJ IDEA 打开后端java项目，运行主类：

- `src/main/java/com/SpringbootSchemaApplication.java`

### 3. 启动前端

用 Visual Studio Code 打开前端项目 `web01`，执行：

```bash
npm install
npm run serve
```

### 4. 启动 RAG 服务

用 Visual Studio Code 打开 `DeerTCM-AI---`，双击执行：

```bash
start_all.bat
```

### 5. 启动虚拟试穿服务

用 Visual Studio Code 打开 `CatVTON`，执行：

```bash
pip install -r requirements.txt
python api_server.py --host 0.0.0.0 --port 6006
```

## 端口说明

### 后端

- `server.port`: `8080`

访问示例：

```text
http://localhost:8080
```

> 如果你本地已经把后端改成 `8081`，请以你自己的 `application.yml` 为准。

### 前端

- `8080`

### RAG 服务

- `http://127.0.0.1:8000`

### 虚拟试穿服务

- `http://127.0.0.1:6006`
- 后端配置里当前使用的目标地址是 `http://127.0.0.1:16006`

## 数据库配置

当前后端配置位于：

- `src/main/resources/application.yml`

主要配置如下：

- 数据库地址：`jdbc:mysql://127.0.0.1:3307`
- 用户名：`root`
- 密码：`root`

如你的本地数据库端口、账号或密码不同，请同步修改 `application.yml`。

## 默认账号

- 管理员：`admin` / `admin`
- 设计账号：`teacher` / `123456`
- 学生账号：`test1` / `test1`

## 注意事项

- 后端会依赖 RAG 和 CatVTON 接口，相关服务未启动时，对应功能可能不可用。
- 如果页面无法正常访问，优先检查前后端端口、数据库连接和外部服务地址是否一致。

