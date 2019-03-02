---
layout: post
title: 从文件中读取zk配置——ZooKeeper编程技能（1）
category: 技术
tags: ZooKeeper
keywords: zookeeper,分布式技术,协调服务
description: 在文件中配置zookeeper的初始化配置属性，本文以CuratorFramewor客户端为例进行说明。
---
 
 
 ### 背景

 为了在项目中更加便捷的使用zookeeper，对于zookeeper客户端的初始化参数最好是抽离到可视的配置文件中，避免程序中硬编码，而且写入到配置文件也便于zookeeper的灵活扩展。


### 期望目标

通过本文，能够对CuratorFramewor客户端的配置进行配置化设置。

### 填充配置文件

暂定配置文件名为:zk.properties。

内容如下：

```
connectionString=test.host.cn:2181,test.host.cn:2281,test.host.cn:2381
sessionTimeoutMs=1800000
retryPolicy=oneTime
retryTimes=3
retryInteval=10000
canBeReadOnly=false
```

- connectionString
    
    zookeeper集群的连接地址，每个节点包括<host:port>,多个节点用逗号分隔。

- sessionTimeoutMs

    定义session超时时间，以毫秒为单位，例文中为30分钟。

- retryPolicy

    为重试策略。选项包括，`oneTime`（只重试一次），`NtTime`（重试指定次数），`forever`（一直重试直到连接到服务），默认只重试一次。例文中使用的是`oneTime`（只重试一次）。

- retryTimes
    
    重试次数，只有在`retryPolicy=NtTime`时有效。

- retryInteval

    重试时间间隔，单位为毫秒，默认10秒间隔。

- canBeReadOnly

    有效值为true和false。如果设置为true，意味着允许Zookeeper客户端在网络隔离情况下可以只读模式访问。即如果该节点与集群无法连接，可以提供服务，但是不能获取最新的更改。

**如果使用maven管理java项目，则该文件放置在src/main/resources目录下。**

### 读取配置文件

使用jdk自带的`Properties`类存储读取的属性配置。

暂定解析配置类为：`ConfigHelper`

内容如下：

```java
package cn.followtry.zk.utils;

import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从配置文件zk.properties中载入zookeeper的配置属性
 * Created by followtry on 2017/5/31.
 */
public class ConfigHelper {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigHelper.class);
  
  private static Properties properties = new Properties();
  
  static {
      URL resource =    .getResource("zk.properties");
    try {
      properties.load(resource.openStream());
    } catch (IOException e) {
      LOGGER.error("载入配置异常",e);
    }
  }
  
  private ConfigHelper(){
    // 不允许外部实例化
  }
  
  public static Properties getProp() {
    return properties;
  }
}
```

`ConfigHelper`中使用guava包中的Resources获取到配置文件`zk.properties`的位置，并使用`Properties`载入配置资源。该解析类为单例模式。

### ZK配置的工具类

暂定ZK配置工具类为：`ZkProp`

内容如下：

```java
package cn.followtry.zk.utils;

import java.util.Properties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by followtry on 2017/5/31.
 */
public class ZkProp {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ZkProp.class);
  
  private static Properties prop = ConfigHelper.getProp();
  
  //session默认过期值，30分钟
  private static final int DEFAULT_SESSION_TIMEOUT_MS = 30 * 60 * 1000;
  
  //连接超时时间，默认为30秒
  private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 30 * 1000;
  
  //重试时间间隔，默认为10秒
  private static final int DEFAULT_RETRY_INTEVAL_MS = 10 * 1000;
  //重试次数，默认为3
  private static final int DEFAULT_RETRY_TIMES = 3;
  
  //默认zookeeper节点地址
  private static final String DEFAULT_ZK_NODE = "127.0.0.1:2181";
  
  private static final String DEFAULT_RETRY_POLICY = "oneTime";
  
  private static final String DEFAULT_CAN_BE_READONLY = "false";
  public static String connectionString() {
    return prop.getProperty("connectionString",DEFAULT_ZK_NODE);
  }
  
  public static int sessionTimeoutMs() {
    return Integer.valueOf(prop.getProperty("sessionTimeoutMs",String.valueOf(DEFAULT_SESSION_TIMEOUT_MS)));
  }
  
  public static int connectionTimeoutMs() {
    return Integer.valueOf(prop.getProperty("connectionTimeoutMs",String.valueOf(DEFAULT_CONNECTION_TIMEOUT_MS)));
  }
  
  public static RetryPolicy retryPolicy() {
    String retryPolicy = prop.getProperty("retryPolicy",DEFAULT_RETRY_POLICY);
    RetryPolicy rp;
    switch (retryPolicy) {
      case "oneTime":
        rp = new RetryOneTime(retryInteval());
        break;
      case "NtTime":
        rp = new RetryNTimes(retryTimes(),retryInteval());
        break;
      case "forever":
        rp = new RetryForever(retryInteval());
        break;
      default:
        LOGGER.warn("retryPolicy not set,now setting default retryPolicy is retryOneTime,retryInteval is {}ms",retryInteval());
        rp = new RetryOneTime(retryInteval());
    }
    return rp;
  }
  
  public static Boolean canBeReadOnly() {
    String canBeReadOnly1 = prop.getProperty("canBeReadOnly",DEFAULT_CAN_BE_READONLY);
    Boolean canBeReadOnly;
    switch (canBeReadOnly1) {
      case "true":
        canBeReadOnly = Boolean.parseBoolean(canBeReadOnly1);
        break;
      case "false":
        canBeReadOnly = Boolean.parseBoolean(canBeReadOnly1);
        break;
      default:
        canBeReadOnly = Boolean.parseBoolean("false");
    }
    return canBeReadOnly;
  }
  
  private static int retryInteval() {
    return Integer.valueOf(prop.getProperty("retryInteval",String.valueOf(DEFAULT_RETRY_INTEVAL_MS)));
  }
  
  private static int retryTimes() {
    return Integer.valueOf(prop.getProperty("retryTimes",String.valueOf(DEFAULT_RETRY_TIMES)));
  }
  
}
```

该类将读取到的配置封装为每一个静态方法并为其制定了默认值。


### 代码中获取ZK的客户端

暂定客户端获取类为：`ZkClientFactory`。

内容如下：

```java
package cn.followtry.zk;

import cn.followtry.zk.utils.ZkProp;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

public class ZkClientFactory {
    
    private static CuratorFramework client;
    
    public static CuratorFramework getZkClient() {
        //从配置中读取对客户端的设置
        client = CuratorFrameworkFactory.builder()
                .canBeReadOnly(ZkProp.canBeReadOnly())
                .connectionTimeoutMs(ZkProp.connectionTimeoutMs())
                .sessionTimeoutMs(ZkProp.sessionTimeoutMs())
                .retryPolicy(ZkProp.retryPolicy())
                .connectString(ZkProp.connectionString())
                .build();
        return client;
    }
}
```

将建造者模式的`CuratorFrameworkFactory`客户端创建封装在静态方法中，方便调用。
默认设置的参数有`canBeReadOnly`、`connectionTimeoutMs`、`sessionTimeoutMs`、`retryPolicy`和`connectString`。


### 测试代码

暂定测试类：`CfzkClientTest`

内容如下：

```java
package cn.followtry.zk;

import org.apache.curator.framework.CuratorFramework;

public class CfzkClientTest {
    /** main. */
    public static void main(String[] args) throws Exception {
        CuratorFramework zkClient = ZkClientFactory.getZkClient();
        zkClient.start();
        String forPath = zkClient.create().creatingParentsIfNeeded().forPath("/testzk/myzk",null);
        System.out.println(forPath);
    }
}
```

输出结果:

![输出结果](//raw.githubusercontent.com/George5814/blog-pic/master/image/zk/zk-config-read-1.png)  









