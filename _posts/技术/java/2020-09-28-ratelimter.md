---
layout: post
title:  guava 限流技术原理分析
category: 技术
tags:  guava RateLimiter Java
keywords: guava RateLimiter Java
description: 
date: 2020-09-28
author: followtry
published: true
---


## 前言

互联网高并发、高流量业务特性使大家都关注可保障系统稳定的[漏桶算法和令牌桶算法](https://blog.csdn.net/m0_37477061/article/details/95313062)来解决。Google的Guava组件对**令牌桶算法**做了两个版本的实现，分别是`SmoothBursty`和`SmoothWarmingUp`。**需要对系统做预热处理的建议使用SmoothWarmingUp**，使用场景如：系统启动、耗时较长的资源初始化需要10秒，在这10秒内不希望承受流量冲击。SmoothWarmingUp称这10秒为warming up，在warming up阶段流量保能很缓慢的进来，当warming up结束后流控恢复到正常限速水平(stable interval 阶段，stable interval阶段实际效果和SmoothBursty没有区别)。

## 原理

业务场景：限制单个JVM线程10QPS 的下单能力，想想有什么方案呢？

### 方案一

对线程使用sleep使得线程，停顿指定时间，同时在指定时间时减去流程处理消耗的时间。简单粗暴。这个应该可以看成是最简版的**漏桶算法**——即处理速率是一定的，不能应对突发流量。


### 方案二

按令牌桶算法的思路实现，用一个单线程的线程池往池子里已特定的速率放入令牌，池子为链表的List。每次从链表后面放入令牌，请求从链表头拿令牌。看起来实现简单，严格按照令牌桶的算法描述实现。但缺点是单独线程池会造成CPU的消耗。如果 JVM 有多个 RateLimiter 的实例，那么对应线程池也要有多个。这对于性能来说是不可接受的，而且作为限流器也太重了。

### Guava RateLimiter 原理

RateLimiter 是基于时间轴的变化在每次请求的时候判断是否有可用的 permit（许可证）。主要通过`stableIntervalMicros`、`nextFreeTicketMicros`、`requiredPermits`(请求的许可证数量)来判断是否有可用的 permit。

**名词解释：**

1. `stableIntervalMicros`:是稳定后的每个 permit 的间隔秒数。
1. `nextFreeTicketMicros`:是下次最早可以获得 permit 的时间，相对于RateLimiter启动时的 startTicker的纳秒，如果加上startTick，则为绝对时间。
1. `requiredPermits`:为请求的permit数量。
1. `nowMicros`：是 Stopwatch计算的当前时间举例RateLimiter最近一次启动的时间差值，因为Stopwatch可以 stop，因此取最近一次启动的差值。

#### 获取 permit 的机制。

当`nowMicros` > `nextFreeTicketMicros`,可以通过两者的差值与`stableIntervalMicros`的比值计算出这段时间内新生成了多少个 permit。将`storedPermits`调整为这段时间内的总 permit，不超过`maxPermits`。并将`nextFreeTicketMicros`记为`nowMicros`,即将下次获取 permit 的时间调整为现在。

如果现在`storedPermits`内存储的数量仍然不足以需要的`requiredPermits`数量。则会使用`requiredPermits`和`storedPermits`计算需要等待的 permit 数量，乘以`stableIntervalMicros`就可以得到当前请求需要等待的微妙数`waitMicros`。此时再次将`nextFreeTicketMicros`往未来推动`waitMicros`,就可以得到下一次请求最早可以获得 permit 的时间了。

在获得了nextFreeTicketMicros后，将`nextFreeTicketMicros`与`nowMicros`做比较，如果结果大于0，就需要等待相应的时间，否则就立即获得所需的`requiredPermits`。

在线程等待时也没有什么高深的等待定时技术，使用的是 Stopwatch 实现类`SleepingStopwatch`内封装的`Thread.sleep`。但此处使用`sleep`与方案一不同处是，只有在需要等待的时候才会`sleep`。这样就可以实现线程的阻塞等待了。

**上面的描述主要涉及到核心方法:**

`reserveAndGetWaitLength`: 用来计算需要等待的微秒数，并将`nextFreeTicketMicros` 往未来推动，并调整`storedPermits`的数量。
`stopwatch.sleepMicrosUninterruptibly`:主要封装了对`Thread.sleep`的调用。

源码如下：
```java
  //获取 permit，并阻塞等待
  public double acquire(int permits) {
    long microsToWait = reserve(permits);
    stopwatch.sleepMicrosUninterruptibly(microsToWait);
    return 1.0 * microsToWait / SECONDS.toMicros(1L);
  }
  //计算需要等待的时间
  final long reserve(int permits) {
    checkPermits(permits);
    synchronized (mutex()) {
      return reserveAndGetWaitLength(permits, stopwatch.readMicros());
    }
  }
  //计算需要等待的时间
  final long reserveAndGetWaitLength(int permits, long nowMicros) {
    long momentAvailable = reserveEarliestAvailable(permits, nowMicros);
    return max(momentAvailable - nowMicros, 0);
  }
  //计算最早可获得 permit 的时间
  final long reserveEarliestAvailable(int requiredPermits, long nowMicros) {
    resync(nowMicros);
    long returnValue = nextFreeTicketMicros;
    double storedPermitsToSpend = min(requiredPermits, this.storedPermits);
    double freshPermits = requiredPermits - storedPermitsToSpend;
    long waitMicros =
        storedPermitsToWaitTime(this.storedPermits, storedPermitsToSpend)
            + (long) (freshPermits * stableIntervalMicros);

    this.nextFreeTicketMicros = LongMath.saturatedAdd(nextFreeTicketMicros, waitMicros);
    this.storedPermits -= storedPermitsToSpend;
    return returnValue;
  }
  //更新storedPermits和nextFreeTicketMicros
  void resync(long nowMicros) {
    // if nextFreeTicket is in the past, resync to now
    if (nowMicros > nextFreeTicketMicros) {
      //计算一段时间内生成了多少permit
      double newPermits = (nowMicros - nextFreeTicketMicros) / coolDownIntervalMicros();
      storedPermits = min(maxPermits, storedPermits + newPermits);
      nextFreeTicketMicros = nowMicros;
    }
  }
```

SleepingStopwatch的实现类如下

```java
  abstract static class SleepingStopwatch {
    /** Constructor for use by subclasses. */
    protected SleepingStopwatch() {}

    /*
     * We always hold the mutex when calling this. TODO(cpovirk): Is that important? Perhaps we need
     * to guarantee that each call to reserveEarliestAvailable, etc. sees a value >= the previous?
     * Also, is it OK that we don't hold the mutex when sleeping?
     */
    protected abstract long readMicros();

    protected abstract void sleepMicrosUninterruptibly(long micros);

    public static final SleepingStopwatch createFromSystemTimer() {
      return new SleepingStopwatch() {
        //启动Stopwatch，并记录启动时间 startTicket,该时间为计算nowMicros的时间
        final Stopwatch stopwatch = Stopwatch.createStarted();

        @Override
        protected long readMicros() {
          //通过该方法获得当前 Stopwatch 已经流逝了多长时间。
          return stopwatch.elapsed(MICROSECONDS);
        }

        @Override
        protected void sleepMicrosUninterruptibly(long micros) {
          if (micros > 0) {
            //通过该方法封装的Thread.sleep来实现线程的暂停
            Uninterruptibles.sleepUninterruptibly(micros, MICROSECONDS);
          }
        }
      };
    }
  }
```

#### 设置 Rate 的机制

设置 Rate 有两种方式，通过Create的时候创建和通过 setRate创建，两个底层实现都是一样的。

在 setRate 时，会先获取此刻的时间区间`nowMicros`，通过该时间更新`storedPermits`和`nextFreeTicketMicros`。在更新完后，会通过`SECONDS.toMicros(1L) / permitsPerSecond`来计算并设置`stableIntervalMicros`,即每个 permit 的稳定间隔。然后对于`SmoothBursty`会继续设置最大 permit 数量`maxPermits`和`storedPermits`。

核心源码如下:

```java
  public final void setRate(double permitsPerSecond) {
    checkArgument(
        permitsPerSecond > 0.0 && !Double.isNaN(permitsPerSecond), "rate must be positive");
    synchronized (mutex()) {
      doSetRate(permitsPerSecond, stopwatch.readMicros());
    }
  }
  final void doSetRate(double permitsPerSecond, long nowMicros) {
    resync(nowMicros);
    double stableIntervalMicros = SECONDS.toMicros(1L) / permitsPerSecond;
    this.stableIntervalMicros = stableIntervalMicros;
    doSetRate(permitsPerSecond, stableIntervalMicros);
  }
  void doSetRate(double permitsPerSecond, double stableIntervalMicros) {
      double oldMaxPermits = this.maxPermits;
      maxPermits = maxBurstSeconds * permitsPerSecond;
      if (oldMaxPermits == Double.POSITIVE_INFINITY) {
        // if we don't special-case this, we would get storedPermits == NaN, below
        storedPermits = maxPermits;
      } else {
        storedPermits =
            (oldMaxPermits == 0.0)
                ? 0.0 // initial state
                : storedPermits * maxPermits / oldMaxPermits;
      }
    }
```

对于`SmoothWarmingUp`怎么实现缓慢增长直到稳定，下次再分析。


## 参考文档

> [Guava RateLimiter算法原理及源码解读](https://blog.csdn.net/asd491310/article/details/103669049)
>
> Guava 23.0 版本源码





