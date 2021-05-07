---
layout: post
title:  重试小工具
category: 技术
tags: Java
keywords: Java
description: 封装guava的重试工具
date: 2021-05-07
author: followtry
published: true
---

将开源的重试工具进行简单封装为小工具，方便使用。

### 引入jar包

在pom中引入如下的包

```xml
<dependency>
    <groupId>com.github.rholder</groupId>
    <artifactId>guava-retrying</artifactId>
    <version>2.0.0</version>
</dependency>
```

### 编码

```java
package com.gongsi.aos.tmp.utils;

import com.github.rholder.retry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * @author jingzhongzhi
 * @since 2020/7/29
 */
public class RetryUtils<V> {

    private static final Logger log = LoggerFactory.getLogger(RetryUtils.class);

    /**
     * 重试等待时间
     */
    public static final int RETRY_SLEEP_TIME = 100;
    /**
     * 重试次数
     */
    public static final int RETRY_ATTEMPT_TIMES = 3;

    /**
     * 重试操作
     *
     * @param retryIfResult 如果该参数指定结果为 true，则会继续重试，否则结束重复返回值
     * @param callable  执行重试的业务逻辑处理
     * @return 重试后的结果
     */
    public static <V> V retry(Predicate<V> retryIfResult, Callable<V> callable) {
        return retry(retryIfResult, callable,TimeUnit.MILLISECONDS.toMillis(RETRY_SLEEP_TIME));
    }


    /**
     * 重试操作
     *
     * @param retryIfResult 如果该参数指定结果为 true，则会继续重试，否则结束重复返回值
     * @param callable  执行重试的业务逻辑处理
     * @param maxWaitTimeInMill 重试最大等待时间
     * @return 重试后的结果
     */
    public static <V> V retry(Predicate<V> retryIfResult, Callable<V> callable, long maxWaitTimeInMill ) {
        Retryer<V> retryer = RetryerBuilder.<V>newBuilder()
                .retryIfResult(retryIfResult::test)
                .retryIfException()
                .withWaitStrategy(WaitStrategies.randomWait(maxWaitTimeInMill, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(RETRY_ATTEMPT_TIMES))
                .build();
        try {
            return retryer.call(callable);
        } catch (ExecutionException | RetryException e) {
            log.info("重试执行异常", e);
        }
        return null;
    }
}
```


将常用的功能和重试策略等封装为工具，避免每次使用都重新new Retryer的builder代码，提高代码复用。

### 使用示例

```java
Object retry = RetryUtils.retry(Objects::nonNull, (Callable<Object>) () -> "执行重试任务", MAX_WAIT_TIME_IN_MILL);
```



