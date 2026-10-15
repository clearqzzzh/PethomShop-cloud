# 智宠云商 — 基于微服务的智能宠物电商与 AI 客服平台

面向 B/C 端用户的宠物用品电商平台，采用 Spring Cloud 微服务架构，覆盖"浏览—下单—支付—物流—售后"完整闭环。独立设计并实现 AI 智能客服系统，支持多 Agent 协同对话与 RAG 知识检索。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.0 + Spring Cloud Alibaba 2022.0.0 |
| 服务治理 | Nacos（服务注册发现 + 配置管理） |
| API 网关 | Spring Cloud Gateway |
| 服务调用 | OpenFeign |
| 数据库 | MySQL 8.0 + MyBatis-Plus |
| 缓存 | Redis（Lua 脚本原子操作） |
| 消息队列 | RocketMQ（异步消息 + 死信队列） |
| 支付 | 支付宝沙箱（RSA2 签名） |
| AI 框架 | LangGraph + DeepSeek + ChromaDB + BGE |
| 认证鉴权 | JWT + RSA + RBAC |
| API 文档 | SpringDoc OpenAPI 3 |
| 对象存储 | 阿里云 OSS |
| 开发工具 | Git + Docker + Nginx |

## 项目架构

```
pethome-cloud/
├── pethome-basic/          # 基础层：BaseMapper、BaseService、通用 POJO
├── pethome-common/         # 公共工具：JWT、拦截器、OSS、Swagger
├── pethome-pojo/           # 共享实体：User、Product、Order 等
├── pethome-api/            # Feign 接口契约
│   ├── pethome-api-user/
│   ├── pethome-api-product/
│   ├── pethome-api-order/
│   ├── pethome-api-pay/
│   └── pethome-api-coupon/
├── pethome-gateway/        # API 网关（端口 8080）
├── user-service/           # 用户服务（端口 8083）
├── product-service/        # 商品服务（端口 8082）
├── order-service/          # 订单服务（端口 8084）
├── pay-service/            # 支付服务（端口 8085）
├── coupon-service/         # 优惠券秒杀服务（端口 8087）
└── admin-service/          # 后台管理服务（端口 8086）
```

## 核心模块

### 微服务架构搭建

基于 Spring Cloud Alibaba 搭建微服务体系，使用 Nacos 实现服务注册发现与配置管理，Spring Cloud Gateway 统一网关路由转发 6 个下游服务，OpenFeign 完成服务间同步调用。

### 优惠券秒杀与缓存方案

负责优惠券秒杀模块开发，基于 **Redis + Lua** 脚本将查库存、判限购、扣库存三步封装为原子操作，利用 Redis 单线程特性天然防超卖；设计优惠券预约锁模式（下单锁定、支付核销、取消释放），配合 Redisson 分布式锁控制多节点并发粒度，QPS 较直接操作 MySQL 提升约 5 倍。

### 消息中间件集成

负责支付回调模块开发，采用 RocketMQ 实现支付成功后的异步消息通知，下游服务消费消息完成库存扣减，降低服务间同步调用耦合；配置 3 次自动重试 + 死信队列 + DB 持久化兜底的三级消息保障机制，确保关键业务消息零丢失。

### 多 Agent 编排

基于 LangGraph StateGraph 搭建 **Supervisor + 6 执行 Agent** 协作架构，通过 Function Calling 封装 22 个业务工具供 LLM 自主调用，实现售前咨询、下单操作、售后处理、宠物健康等全场景智能对话与业务代办。

### RAG 检索引擎

采用 ChromaDB 向量检索 + BM25 关键词检索双路召回，经 **RRF 融合排序 + Reranker 精排**后交由 LLM 生成回答，自建 60 条人工标注评测集，来源准确率 95%、关键词召回率 95.1%；医疗类问题采用严格溯源模式，无检索来源一律拒绝回答，抑制模型幻觉。

## 业务流程

```
用户浏览 → 加入购物车 → 创建订单 → 领取优惠券 → 支付宝支付
    → 支付回调(RocketMQ) → 库存扣减 → 物流跟踪 → 确认收货
    → 退货退款 → 售后处理
```

## 数据库设计

| 数据库 | 所属服务 |
|--------|----------|
| pethome_member | user-service |
| pethome_product | product-service / coupon-service |
| pethome_order | order-service |
| pethome_pay | pay-service |
| pethome_admin | admin-service |

## API 网关路由

| 路径前缀 | 目标服务 |
|----------|----------|
| /api/user/**, /api/pet/** | user-service |
| /api/product/**, /api/favorite/**, /api/review/** | product-service |
| /api/order/**, /api/cart/**, /api/address/** | order-service |
| /api/pay/** | pay-service |
| /api/coupon/** | coupon-service |
| /api/admin/** | admin-service |

## 快速启动

### 前置依赖

- JDK 17+
- MySQL 8.0
- Redis 6.0+
- Nacos 2.x（端口 8858）
- RocketMQ 5.x（端口 9876）

### 启动顺序

1. 启动 Nacos Server
2. 启动 RocketMQ NameServer 和 Broker
3. 依次启动各微服务（建议顺序：gateway → user → product → order → pay → coupon → admin）
4. 访问 Swagger 文档：`http://localhost:{port}/swagger-ui.html`

### 配置说明

数据库账号密码通过环境变量注入：

```bash
export MYSQL_USER=root
export MYSQL_PASSWORD=root
```

支付宝沙箱配置位于 `pay-service` 的 `application.yml`，需替换为真实的 AppID 和密钥。

## 项目截图

> （可在此处添加系统截图或架构图）

## License

MIT
