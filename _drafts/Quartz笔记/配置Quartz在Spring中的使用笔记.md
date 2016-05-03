## 1. quartz 配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
     http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
    <!-- 启动触发器的配置开始 -->
    <bean name="startQuertz" lazy-init="false" autowire="no"
        class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
        <property name="triggers">
            <list>
                <ref bean="myJobTrigger" />
            </list>
        </property>
    </bean>
    <!-- 启动触发器的配置结束 -->
 
    <!-- quartz-2.x的配置 -->
    <bean id="myJobTrigger"  class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
        <property name="jobDetail" ref="myJobDetail"/>
           <!--每秒中执行一次 -->
        <property name="cronExpression" value="*/1 * * * * ?"/>
        <!-- 延迟10秒开始执行 -->
        <property name="startDelay" value="10"></property>
    </bean>
    <!-- 调度的配置结束 -->
 
    <!-- job的配置开始 -->
    <bean id="myJobDetail"
        class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
        <property name="targetObject" ref="quartzTest">
        </property>
        <property name="targetMethod" value="work">
        </property>
    </bean>
    <!-- job的配置结束 -->
 
    <!-- 工作的bean -->
    <bean id="quartzTest" class="cn.jingzztech.prac.quartz.QuartzTest" />
</beans>
```

```java
package cn.jingzztech.prac.quartz;

import java.text.DateFormat;
import java.util.Date;

/**
 * 测试使用Quartz库
 * @author jingzz
 * @time 2016年2月23日 上午8:56:28
 * @func 
 * @name QuartzTest
 */
public class QuartzTest {
	
	public void work(){
		System.out.println("date:"+DateFormat.getInstance().format(new Date()));
	}

}

```

## 2. quartz cron表达式
**[cron工具地址](http://www.jeasyuicn.com/cron/)**
