# imageCreater

imageCreater 是一个 AI 图像生成平台，包含门户首页、用户创作台、用户资产管理和管理员后台。前端使用 Vue 3 + Vite + Pinia + Tailwind CSS，后端使用 Spring Boot + MyBatis-Plus + MySQL，并通过 OpenAI 兼容图像接口完成生图。

## 功能概览

- 门户首页：白色极简风格，Three.js 3D 交互动效，AI 作品展示。
- 用户认证：注册、登录、邮箱验证码、忘记密码邮箱找回。
- 用户创作：提示词生图，1K / 2K / 4K 规格选择，余额扣费，生成历史，图片预览、下载与删除。
- 生成反馈：生成时显示读秒和预计完成时间，预计时间基于全站成功生成耗时平均值计算。
- 用户中心：余额充值模拟、生成记录、资料管理、修改密码。
- 管理后台：仪表盘、用户管理、余额管理、密码重置、系统日志、生图结果列表。
- 生图定价：按规格配置模型、API Key、服务商 API 地址、图像路径、尺寸、质量、价格和启用状态。

## 技术栈

前端：

- Vue 3
- TypeScript
- Vite
- Pinia
- Vue Router
- Tailwind CSS
- Three.js
- Axios

后端：

- Spring Boot 3.3.5
- Spring Security + JWT
- MyBatis-Plus
- MySQL
- Spring AI OpenAI
- Spring Mail

## 目录结构

```text
.
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/       # 后端源码
│   └── src/main/resources/
│       ├── application.yml  # 后端配置
│       └── db/schema.sql    # 数据库初始化脚本
├── src/                     # Vue 前端源码
│   ├── api/                 # 前端接口封装
│   ├── components/          # 公共组件
│   ├── router/              # 路由
│   ├── store/               # Pinia 状态
│   └── views/               # 页面
├── package.json
└── README.md
```

## 环境要求

- Node.js 20.19+ 或 22.12+
- JDK 17+
- MySQL 8+
- Maven Wrapper 已包含在 `backend/` 目录中

## 数据库初始化

先创建数据库和表：

```sql
source backend/src/main/resources/db/schema.sql;
```

或手动在 MySQL 客户端中执行 [schema.sql](backend/src/main/resources/db/schema.sql)。

默认数据库名：

```text
image_creator
```

如果你是从旧版本升级，确认 `user` 表包含邮箱字段：

```sql
ALTER TABLE `user`
    ADD COLUMN email VARCHAR(120) UNIQUE AFTER username;
```

确认生图配置表包含以下字段：

```sql
ALTER TABLE image_generation_config
    ADD COLUMN api_base_url VARCHAR(255) NOT NULL DEFAULT 'https://api.openai.com' AFTER api_key;

ALTER TABLE image_generation_config
    ADD COLUMN endpoint_path VARCHAR(80) NOT NULL DEFAULT '/v1/images/generations' AFTER api_key;
```

如果字段已存在，不要重复执行对应 `ALTER TABLE`。

如果需要显示生图预计耗时，还需要创建成功耗时统计表：

```sql
CREATE TABLE IF NOT EXISTS image_generation_metric (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    image_record_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    quality_code VARCHAR(20),
    duration_ms BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_metric_created (created_at),
    INDEX idx_metric_user_created (user_id, created_at)
);
```

## 后端配置

配置文件：

```text
backend/src/main/resources/application.yml
```

默认 MySQL 配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/image_creator?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: root
```

可以按本机环境修改数据库账号密码。

邮件验证码相关环境变量：

```text
MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
MAIL_FROM
```

开发环境默认 `app.mail.dev-return-code: true`，如果没有配置 SMTP，验证码接口会返回开发验证码，方便本地测试。

JWT 密钥建议生产环境修改：

```text
JWT_SECRET
```

## 启动后端

```sh
cd backend
cmd /c mvnw.cmd spring-boot:run
```

后端默认端口：

```text
http://localhost:8080
```

## 启动前端

```sh
npm install
npm run dev
```

前端默认端口：

```text
http://localhost:5173
```

Vite 已配置 `/api` 代理到后端 `http://localhost:8080`。

## 默认管理员

后端启动时会自动创建默认管理员：

```text
账号：admin
密码：admin123
```

登录后进入：

```text
/admin/dashboard
```

## OpenAI / 服务商生图配置

进入管理员后台：

```text
/admin/pricing
```

每个生图规格可配置：

- 显示名称
- API Key
- 服务商 API 地址，例如 `https://api.openai.com` 或中转服务商地址
- 图像路径，只支持：
  - `/v1/images/generations`
  - `/v1/images/edits`
- 模型名
- 尺寸，例如 `1024x1024`
- 质量参数，例如 `standard`、`hd`
- 用户扣费价格
- 启用状态
- 排序

默认规格：

```text
1k：1K 标准图像
2k：2K 高清图像
4k：4K 超清图像
```

注意：服务商 API 地址只填写基础地址，不要带 `/v1/images/generations`；图像路径在后台单独选择。

## 常见问题

### 登录接口 404

确认前端开发服务器使用 Vite 代理，并且后端已经启动在 `8080` 端口。

### 邮箱验证码没有收到

本地开发如果没有配置 SMTP，接口会返回开发验证码。生产环境需要正确配置 `MAIL_HOST`、`MAIL_USERNAME`、`MAIL_PASSWORD`。

### OpenAI 429 TOO_MANY_REQUESTS

服务商限流或额度不足。检查 API Key 余额、请求频率、中转服务商额度池。

### OpenAI 503 SERVICE_UNAVAILABLE

服务商临时不可用、节点拥堵或模型不支持。稍后重试，或更换服务商 API 地址。

### I/O error on POST request

后端无法连接服务商 API。检查服务器网络、代理地址、防火墙、证书或中转服务商地址。

### Lock wait timeout exceeded

旧版本可能因生图长事务锁住用户余额。当前版本已将生图外部请求移出长事务；请重新启动后端并重试。

## 构建

前端构建：

```sh
npm run build
```

后端打包：

```sh
cd backend
cmd /c mvnw.cmd -q -DskipTests package
```

## 生产部署提示

- 修改数据库账号密码。
- 修改 `JWT_SECRET`。
- 关闭开发验证码返回：`app.mail.dev-return-code: false`。
- 配置真实 SMTP。
- 在后台配置真实可用的服务商 API 地址和 API Key。
- 确认 `app.upload.image-path` 指向持久化目录。
