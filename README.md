# 🎵 悦享音乐

## 📖 项目简介

“悦享音乐”是一个基于 **Spring Boot + Vue 3** 构建的全栈音乐网站系统。可用于充作课设/课程设计。
项目分为 **前台移动端网页** 与 **后台管理系统** 两部分，提供音乐播放、收藏、歌单管理、AI 分析等功能。
<img width="816" height="935" alt="image" src="https://github.com/user-attachments/assets/336ccb23-7bef-4815-b2b7-d7797d0fadba" />
<img width="790" height="956" alt="image" src="https://github.com/user-attachments/assets/8d743154-8610-450e-bbbb-4355aca1a317" />
<img width="954" height="937" alt="image" src="https://github.com/user-attachments/assets/6efc9608-3fa4-4805-85f2-1dde643d8841" />
<img width="973" height="924" alt="image" src="https://github.com/user-attachments/assets/7b8939d2-de77-4693-b6e6-7f3c411d5fe0" />
<img width="818" height="920" alt="image" src="https://github.com/user-attachments/assets/82cac1eb-f0c3-4659-86fd-55b94f216169" />
<img width="741" height="843" alt="image" src="https://github.com/user-attachments/assets/b9843363-c3e2-43ea-b0dd-ff354b1469d3" />
<img width="1250" height="612" alt="image" src="https://github.com/user-attachments/assets/98d2da18-3e44-446c-bac5-1de24dffb524" />

---
## 注意：
华为云配置文件位置写死到代码里了，请根据实际情况修改代码
更多信息查看ppt


## 🧱 技术栈

### 💻 开发环境

| 工具          | 版本                              |
| ----------- | ------------------------------- |
| **JDK**     | 21                              |
| **IDEA**    | IntelliJ IDEA 2023.3.4 Ultimate |
| **Maven**   | 3.9.5                           |
| **Node.js** | 14.8.0                          |
| **VSCode**  | 1.101.2                         |

### 🌐 部署环境

| 服务        | 版本       |
| --------- | -------- |
| **Nginx** | 1.20.1   |
| **Nacos** | 2.5.1    |
| **Redis** | 5.0.14.1 |
| **MySQL** | 8.0.18   |

### ⚙️ 项目框架

* **Spring Boot 3.2.2**
* **Spring Cloud Alibaba 2022.0.0.0-RC1**
* **MyBatis-Plus 3.5.6**
* **Vue 3.2.13**
* **Element Plus 2.10.3**

---

## 🏗️ 系统架构

**技术架构说明：**
用户通过浏览器访问系统 → 请求由 **Nginx** 转发 → 进入前台或后台前端 →
经由 **Gateway 网关** 调用注册在 **Nacos** 的各功能模块服务 →
数据存储于 **MySQL**，使用 **Redis** 做缓存加速。

---

## 🔄 业务流程

* 歌手上传/发布歌曲至歌曲库；
* 管理员审核、管理歌手及歌曲，发布公告；
* 听众可收听、收藏、创建歌单并进行互动；
* 围绕“歌曲库”形成上传 → 管理 → 推荐 → 播放的闭环业务流程。

---

## 🧩 后端设计

### 📁 多模块结构

根目录为 `music` 主项目，通过 `<modules>` 定义多个子模块：

* **domain**：公共实体类
* **Mod_admin**：后台管理模块
* **music_gateway**：网关模块
  每个模块遵循 MVC 分层：

```
controller/   处理 HTTP 请求
mapper/       MyBatis-Plus 映射数据库
service/impl/ 封装业务逻辑
resources/    配置文件（数据库、端口、服务名等）
```

### ☁️ 华为云对象存储集成

* 从 JSON 配置文件解析 `client_secret`、`client_id`
* 获取 `access_token` 后调用上传接口
* 支持分段下载（Range 请求头）
* 优化文件上传、下载、清理、Token 缓存机制

### 🚀 性能优化

* 使用 **连接池（PoolingHttpClientConnectionManager）** 减少 TCP 连接开销
* 并发提升、自动释放空闲连接、超时控制灵活
* 利用 **StopWatch** 监测请求各阶段耗时，精确定位性能瓶颈

---

## 📱 前端 - 移动端网页

基于 **Vue 3 + Vuex + Vue Router + Element Plus** 构建的音乐播放器。

### 页面与功能

* **登录注册**：用户身份认证
* **推荐 & 歌手**：个性化推荐，榜单展示
* **我的 & 收藏**：收藏歌单、我的歌曲
* **歌单管理**：创建、编辑、删除个人歌单
* **设置页面**：个性化设置与账号信息

### 功能优化

* 🎵 **播放模式**：顺序、单曲循环、随机播放
* 🕐 **歌词同步**：解决歌词与音频不同步问题
* 🎚️ **进度条交互**：歌词与进度条动态同步
* 🧩 **推荐算法重写**：前后端协作实现多元推荐
* 📦 **收藏修复**：修复收藏歌单显示 bug

---

## 🖥️ 前端 - 后台管理系统

提供管理员对系统的管理功能：

* 歌手与歌曲管理、发布
* 日志查看与公告发布
* 数据可视化分析（AI 统计图表）
* 图片裁剪上传（限制为 1:1 比例）

---

## 🔍 项目总结与反思

### 小组成员

* 前台开发与文档：负责移动端页面与交互
* 后台开发与 PPT 制作：负责后端逻辑与云服务集成
* 后台前端开发与文档：负责管理系统与统计可视化

### 存在问题

1. 未实现歌手关注、点赞与评论等交互功能
2. 模块划分尚不够清晰
3. 未实现完整云部署与公网访问

---

## ❤️ 致谢

感谢团队成员的共同努力


