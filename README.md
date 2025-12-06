# 智慧学堂

## 技术栈

微服务架构（SpringCloud Gateway+Nacos+OpenFeign），springcloud+MybatisPlus+Redis+Nginx

## 项目部署

### 前端

1. 先下**NodeJS**

2. 用NodeJS下**pnpm** 。`npm install -g pnpm`

3. 在前端代码文件夹跑  `pnpm install` 

4. 运行  `pnpm run dev`

### 后端

* 主要改改每个**application.yml**中配置的IP地址、mysql的密码等。（一个电脑上跑就把IP全改成localhost即可，端口号可以不动）。

* 先启动下面三个软件，再跑代码。

* 测试账号 lty666,密码 s123456

#### Redis

* 启动：`.\redis-server.exe redis.windows.conf`

* 端口：默认6379

* 无密码

#### Nacos

* 启动： bin文件夹下 `.\startup.cmd -m standalone`

* 端口：默认 8848

#### Nginx

* 用途：对客户端提供服务器的课程视频文件的访问

* 启动：`start nginx `

* 端口：10030（默认80）

* 配置文件已改（含文件路径等）

## 项目展示

![展示](./智慧学堂.png "效果图")
