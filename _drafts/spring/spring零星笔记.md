# Spring 零星笔记
### 1. 注入三种类型
	构造方法注入、属性注入和接口注入    
	
### 2. Spring 资源加载
	Spring提供了强大的加载资源的机制，不但能够通过“classpath:”、“file:”等资源地质前缀识别不同的资源类型，还支持Ant风格带有通配符的资源地址。
	
资源类型的地址前缀：

|地址前缀|示例|对应资源类型|
|------|---|---------|
|classpath:|classpath:com/test/bao/bean.xml|从类路径中加载资源，classpath:和classpath:/是等价的。都是相对于类的根路径。资源文件可以在标准的FS中，也可以在jar或zip的类包中|
|file:|file:/conf/com/test/bao/bean.xml|使用URLResource从FS中加载资源，可采用绝对和相对路径|
|http://|http://www.bao.com/resource/bean.xml|使用URLResource从WEB服务器加载资源|
|ftp://|ftp://www.bao.com/resource.bean.xml|使用URLResource从ftp服务器加载资源|
|没有前缀|com/test/bao/bean.xml|根据applicationContext具体实现类采用对应类型的Resource|

**PS：**

**classpath\*:和classpath:两者比较**:

	"classpath:"只会在第一个加载的com.test.bao包下查找；
	"classpath*:"会扫描到所有的jar包及类路径下出现的com.test.bao包路径
	

**Ant风格资源地址支持3中匹配符**：
	"?":匹配文件中的一个字符
	"\*"：匹配文件名中的任意个字符
	"\*\*":匹配多层路径
	
### 3.加载配置
如果使用web监听器，必须将Log4jConfiglistener放置在ContextLoaderListener前面

如果使用@Configuration的java类提供配置信息，则web.xml配置如下：
```XML
<!-- 通过指定context参数，让spring使用指定的启动器-->
<context-param>
	<param-name>contextClass</param-name>
	<param-value>org.springframework.web.context.support.AnnotatinConfigWebApplicationContext</param-value>
</context-param>
<!-- 标注@Configuration的配置类，多个可以使用逗号或者空格分隔 -->
<context-param>
	<param-name>contextConfigClass</param-name>
	<param-value>com.test.bao.config</param-value>
</context-param>
```

### 4. Spring MVC默认加载配置
```xml
  <servlet>
    <servlet-name>springMVC</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <!-- 指定mvc配置文件 -->
    <init-param>
    	<param-name>contextConfigLocation</param-name>
    	<param-value>classpath*:springmvc-config.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
  </servlet>
```

默认情况下，springmvc根据servlet的名字查找WEB-INF下的<servlet-name>-servlet.xml作为springmvc的配置文件。
通过参数contextConfigLocation可以指定springmvc配置文件的位置


### 5. spring日志自动加载
	log4j.properties放置在类路径下
### 6. spring url后缀
	struts中以".do"结尾，struts2中以".action"结尾,也可以使用".html"
	使用".html"的好处：
	(1).用户不能通过URL直接指导我们采用的那种服务端技术；
	(2).".html"是静态网页的后缀，也可以骗过搜索引擎。
	(3).对于真正的无需动态处理的静态网页则可以使用.htm后缀加以区分避免被框架拦截。
	

### 7. SpringMVC @RequestBody接收Json对象字符串
#### 7.1 配置jackson为解析器
配置springMVC配置文件,需要的jar包spring-web-4.xx.jar 
```xml
	<bean id="jacksonMessageConverter" class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter"/>
			
	<bean class="org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter">
	   <property name="messageConverters">
	      <list>
	         <ref bean="jacksonMessageConverter" />
	      </list>
	   </property>
	</bean>
```
maven中引入依赖：
```xml
	<jackson.version>2.6.1</jackson.version>

	<dependency>
		<groupId>com.fasterxml.jackson.core</groupId>
		<artifactId>jackson-core</artifactId>
		<name>Jackson-core</name>
		<version>${jackson.version}</version>
	</dependency>
	<dependency>
		<groupId>com.fasterxml.jackson.core</groupId>
		<artifactId>jackson-databind</artifactId>
		<version>${jackson.version}</version>
		<name>jackson-databind</name>
	</dependency>
	<dependency>
		<groupId>com.fasterxml.jackson.core</groupId>
		<artifactId>jackson-annotations</artifactId>
		<name>Jackson-annotations</name>
		<version>${jackson.version}</version>
	</dependency>
	<dependency>
	<groupId>org.codehaus.jackson</groupId>
		<artifactId>jackson-lgpl</artifactId>
		<packaging>jar</packaging>
		<name>Jackson</name>
		<version>0.9.5</version>
	</dependency>

	<dependency>
		<groupId>org.codehaus.jackson</groupId>
		<artifactId>jackson-asl</artifactId>
		<packaging>jar</packaging>
		<name>Jackson</name>
		<version>0.9.5</version>
	</dependency>
```
#### 7.2 配置fastjson为解析器
配置依赖：
```xml
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>fastjson</artifactId>
	<version>1.2.7</version>
</dependency>
```
配置文件：
```xml
	<bean id="stringConverter"
		class="org.springframework.http.converter.StringHttpMessageConverter">
		<property name="supportedMediaTypes">
			<list>
				<value>text/plain;charset=UTF-8</value>
			</list>
		</property>
	</bean>

	<!-- 输出对象转JSON支持 -->
	<bean id="jsonConverter"
		class="com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter">
		<property name="supportedMediaTypes">
			<list>
				<value>application/json;charset=UTF-8</value>  
                <value>text/html;charset=UTF-8</value><!-- 避免IE出现下载JSON文件的情况 -->  
                <value>text/plain;charset=UTF-8</value>
			</list>
		</property>
	</bean>

	<bean
		class="org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter">
		<property name="messageConverters">
			<list>
				<ref bean="stringConverter" />
				<ref bean="jsonConverter" />
			</list>
		</property>
	</bean>
```

**jackson和fastjson公用的java和js代码**


java代码：

```java
	//produces = "plain/text; charset=UTF-8"
	@RequestMapping(value="/add",produces={MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public int  createProject(@RequestBody Project project,HttpServletRequest request) {//
		project.setStartdate(new Date());
		project.setEnddate(new Date());
		project.setEntryDatetime(new Date());
		int num = projectService.createProject(project);
		 return num;
	}
```

js代码：
```javascript
$("#createProject").on('click',function(){
		var param = {
				"id":"22",
				"entryId":"33",
				"entryDatetime":null,
				"updateId":"334",
				"updateDatetime":null,
				"name":"eeeee",
				"master":"222",
				"description":"werwer",
				"startdate":null,
				"enddate":null,
		}
		var data = JSON.stringify(param);
		alert(data);
		var url = "http://localhost:8080/web-server/service/project/add";
		alert("当前url:"+url);
		$.ajax({
			type:"post",
			dataType: "json",
			contentType:"application/json",
			url:url,
			data:data,
			success:function(data,e){
				alert("创建项目成功"+JSON.stringify(data)+e);
			},
			error:function(data,e){
				console.log(data);
				alert("创建项目失败"+JSON.stringify(data)+e);
			}
		});
		
	});
```

### 8.任务调度
**注意：任何方式的调度配置都需要首先引入spring-context-4.1.7.RELEASE.jar和spring-task-4.1.7.RELEASE.jar**

**注意：spring主配置文件中应该有配置context:component-scan base-package="cn.jingzztech.prac" />,以便spring扫描注解**
#### 8.1 注解+配置方式任务调度
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

#### 8.2 纯注解方式任务调度
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

#### 8.3 纯配置方式任务调度

在spring主配置文件中添加
```xml
	<task:scheduled-tasks> 
        <task:scheduled ref="quartzTest" method="springTaskSchedule2" cron="*/1 * * * * ?"/> 
	</task:scheduled-tasks>
	<bean id="quartzTest" class="cn.jingzztech.prac.quartz.QuartzTest" />
```
这样就会将cn.jingzztech.prac.quartz.QuartzTest.springTaskSchedule2设置为调度方法，按照指定cron调度


**这种方式可以通过配置动态的决定开启哪些调度和关闭哪些调度，灵活度大**  




      
















































