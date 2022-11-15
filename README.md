# kale-rpc
一款支持java的简易版rpc框架，采用netty作为通信框架，支持zookeeper作为注册中心和配置中心，以及记录一些监控信息作为监控中心，支持rocketmq作为消息队列，适配spring boot

## Features

- [x] **适配spring boot**：适配spring boot，导入starter依赖即可快速使用框架
- [x] **使用简单**：框架封装了调用细节，使用非常简单
- [x] **高性能通信**：使用netty作为通信框架，设计自定义协议进行通信
- [x] **支持心跳机制**：维持client和server之间的长连接
- [x] **支持多种序列化方式**：支持json以及kryo作为序列化方式
- [x] **支持多种消息队列**：支持本地阻塞队列作为消息队列以及rocketmq作为消息队列
- [x] **支持请求超时**：超时后会触发失败重传机制，并避免使用该次server地址
- [x] **支持失败重传**：支持rpc调用失败时触发失败策略，支持`fail-over`，`fail-safe`，`fail-fast`三种机制
- [x] **支持多种负载均衡**：rpc调用支持负载均衡，支持`random`，`min-conn`，`min-call`三种机制
- [x] **支持监控**：支持监控中心，server和client可以上报相关信息
- [x] **可扩展性**：使用SPI机制动态配置实现类，预留了大量接口，可以供实现不同的策略以及不同的实现方式

## Usage

- 见[kale-rpc-example](https://github.com/KaleW515/kale-rpc-example)

## Change Log

- 2022-11-15：发布0.1.0版本

## Architecture

### 架构图

![Architecture.png](https://s2.loli.net/2022/11/15/RbfIWAt5K2xg8rS.png)

### 扩展接口设计

#### proxy层

- `FailStrategy`：在rpc失败时提供下一步策略

#### config层

- `ConfigCenter`：对外暴露配置中心接口，提供配置设置和配置获取能力
- `ConfigContainer`：配置中心中持有的真正配置中心实现接口，如果需要扩展，实现类需要实现该接口

#### registry层

- `RegistryCenter`：对外暴露注册中心接口，提供一系列服务注册和服务发现能力

- `ServiceDiscovery`：注册中心中持有的真正服务发现实现接口，如果需要扩展，实现类需要实现该接口
- `ServiceRegistry`：注册中心中持有的真正服务注册实现接口，如果需要扩展，实现类需要实现该接口

#### monitor层

- `MonitorCenter`：对外暴露监控中心接口，提供一系列监控上报和获取监控信息能力
- `MonitorReport`：监控中心中持有的真正监控上报实现接口，扩展实现类需要实现该接口
- `MonitorObtain`：监控中心中持有的真正监控获取实现接口，扩展实现类需要实现该接口

#### transporter层

- `RpcServer`：实现server的主要接口，提供启动服务，获取服务状态等功能
- `RpcClient`：实现client的主要接口，提供发送请求，获取服务地址等功能

#### loadbalance层

- `LoadBalance`：提供负载均衡功能

#### serializer层

- `Serializer`：提供序列化和反序列化功能

#### compress层

- `Compress`：提供压缩功能，进一步压缩传输消息大小
