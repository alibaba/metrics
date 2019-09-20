# Metrics简介

[![Build Status](https://travis-ci.org/alibaba/metrics.svg?branch=master)](https://travis-ci.org/alibaba/metrics)
[![codecov](https://codecov.io/gh/alibaba/metrics/branch/master/graph/badge.svg)](https://codecov.io/gh/alibaba/metrics)
![license](https://img.shields.io/github/license/alibaba/metrics.svg)

随着微服务的兴起，如何对微服务进行监控，了解微服务当前的运行指标和健康状态，已经成为必备的能力。Metrics作为微服务中的重要的组件，为微服务的监控提供了数据基础。Metrics是一套标准度量库， 用于提供对从操作系统， 虚拟机， 容器，到应用的全方位, 多维度, 实时, 准确的度量服务。

Metrics（原Alibaba Metrics）是阿里巴巴集团内部广泛使用的度量埋点基础类库，内部有Java和Node.js两个版本，目前开源的是Java版本。内部版本诞生于2016年，经历过近三年的发展，经历三年双十一考验，已经成为阿里巴巴集团内部微服务监控度量的事实标准，覆盖了从系统，JVM，中间件，应用各层的度量指标，并且从命名规则，数据格式，埋点方式，计算规则等方便形成了一套统一的规范。

## 全面的指标统计

metrics全面支持了从操作系统，JVM，中间件，再到应用层面的各级指标，并且对统一了各种命名指标，可以做到开箱即用，并支持通过配置随时开启和关闭某类指标的收集。

目前支持的指标主要包括  

### 操作系统

支持Linux/Windows/Mac，包含cpu, load, disk, nettraffic, tcp。

### JVM

支持classload, gc次数和时间, 文件句柄，young/old区占用，线程状态, 堆外内存，编译时间，部分指标支持自动差值计算。

### 中间件

- Tomcat: 请求数，失败次数，处理时间，发送接收字节数，线程池活跃线程数等
- Druid: sql执行次数，错误数，执行时间，影响行数等
- Nginx: 接受，活跃连接数，读，写请求数，排队数，请求qps，平均rt等

更详细的指标可以参考[这里](https://github.com/alibaba/metrics/wiki/supported-metrics-list),  后续会陆续添加对Dubbo, Nacos, Sentinel, Fescar等的支持。



## REST支持

metrics提供了基于JAX-RS的REST接口暴露，可以轻松查询内部的各种指标，既可以独立启动HTTP Server提供服务（默认提供了一个基于Jersey+ sun Http server的简单实现)，也可以嵌入已有的HTTP Server进行暴露指标。具体的接口可以参考[这里](https://github.com/alibaba/metrics/wiki/query-from-http)   

### 如何使用

使用方式很简单，和日志框架的Logger获取方式一致。

```
Counter hello = MetricManager.getCounter("test", MetricName.build("test.my.counter"));
hello.inc();
```

支持的度量器包括：

- Counter（计数器）
- Meter（吞吐率度量器）
- Histogram（直方分布度量器）
- Gauge(瞬态值度量器)
- Timer（吞吐率和响应时间分布度量器）
- Compass(吞吐率， 响应时间分布， 成功率和错误码度量器)
- FastCompass(一种快速高效统计吞吐率，平均响应时间，成功率和错误码的度量器)
- ClusterHistogram(集群分位数度量器)     

具体各个度量器的使用方式可以参考[这里](https://github.com/alibaba/metrics/wiki/quick-start)

默认收集的指标，以及使用http接口进行查看，可以参考[metrics-demo](https://github.com/alibaba/metrics/wiki/demo)
