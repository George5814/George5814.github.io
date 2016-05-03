**注意：任何方式的调度配置都需要首先引入spring-context-4.1.7.RELEASE.jar和spring-task-4.1.7.RELEASE.jar**

**注意：spring主配置文件中应该有配置context:component-scan base-package="cn.jingzztech.prac" />,以便spring扫描注解**
## 1. 注解+配置方式任务调度
在springContext.xml(spring主配置文件)配置
```xml
 	<!-- 任务注解驱动 -->
    <task:annotation-driven/>
```    
java代码处调用
```java
	@Scheduled(cron="*/1 * * * * ?")
	public void springTaskSchedule(){
		System.out.println("springTaskSchedule--->date:"+DateFormat.getInstance().format(new Date()));
	}
```
**这种方式只能提供两种可能，即要么带@Scheduled注解的方法全部按约定执行，要么全部不执行(注释掉task:annotation-driven/>)**

## 2. 纯注解方式任务调度
纯注解方式是只在java代码中添加注解，而不再配置<task:annotation-driven/>
java代码处调用
```java
@EnableScheduling
public class QuartzTest {
	@Scheduled(cron="*/1 * * * * ?")
	public void springTaskSchedule(){
		System.out.println("springTaskSchedule--->date:"+DateFormat.getInstance().format(new Date()));
	}
}
```
**这种方式不需手动改动配置，但当调度任务的需求更改时，需要修改源码**

## 3. 纯配置方式任务调度

在spring主配置文件中添加
```xml
	<task:scheduled-tasks> 
        <task:scheduled ref="quartzTest" method="springTaskSchedule2" cron="*/1 * * * * ?"/> 
	</task:scheduled-tasks>
	<bean id="quartzTest" class="cn.jingzztech.prac.quartz.QuartzTest" />
```
这样就会将cn.jingzztech.prac.quartz.QuartzTest.springTaskSchedule2设置为调度方法，按照指定cron调度
**这种方式可以通过配置动态的决定开启哪些调度和关闭哪些调度，灵活度大**  











