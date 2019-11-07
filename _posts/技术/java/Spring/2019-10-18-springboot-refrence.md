---
layout: post
title: Springboot 参考手册学习 - 1. SpringBoot 实现原理
category: 技术
tags:  Spring Springboot Java
keywords: 
modify: 2019-11-07
description: 深入学习定制Spring标签
published: false
---


## spring Boot 实现原理

获取 starter 的配置类的流程：

`@SpringBootApplication` =>  
`EnableAutoConfiguration` => 
`AutoConfigurationImportSelector.selectImports` => 
`AutoConfigurationImportSelector.getCandidateConfigurations` => 
`SpringFactoriesLoader.loadFactoryNames` =>  
`PropertiesLoaderUtils.loadProperties` =>
读取`FACTORIES_RESOURCE_LOCATION="META-INF/spring.factories`文件


将 starter 的`META-INF/spring.factories`中的Configuration类预先读取。该配置类一般会配置`@Configuration`和`@Bean`注解，通过这两个注解实例化 Bean。而 Bean 的实例化逻辑是在`@Bean`注解的方法内自定义实现的。如果使得该 Bean 可以被 Spring 管理，需要引入 Spring 的`BeanDefinitionRegistryPostProcessor`，将创建好的`BeanDefinition`注册进`BeanDefinitionRegistry`中。


## SpringBoot 启动过程

`SpringApplication#run` => 
`SpringApplication#getRunListeners` => 
`SpringApplication.prepareEnvironment -> StandardServletEnvironment` =>
`SpringApplication#getSpringFactoriesInstances` => 
`SpringFactoriesLoader#loadFactoryNames` => 
`SpringApplication#createSpringFactoriesInstances` => 
`SpringApplication#createApplicationContext -> AnnotationConfigServletWebServerApplicationContext`  => 
`SpringApplication#prepareContext` => 
`SpringApplication#load` => 
`SpringApplication#createBeanDefinitionLoader` => 
`BeanDefinitionLoader#load` => 
`BeanDefinitionLoader#isComponent` => 
`AnnotationUtils#findAnnotation` => 
`AnnotatedBeanDefinitionReader#register` => 
`SpringApplication#refreshContext` => 
`org.springframework.context.support.AbstractApplicationContext#refresh` =>
`AnnotationConfigServletWebServerApplicationContext#prepareRefresh` => 
`AbstractApplicationContext#obtainFreshBeanFactory` => 
`AbstractApplicationContext#getBeanFactory` => 
`AbstractApplicationContext#prepareBeanFactory` => 
`org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors` =>  **关键位置，scan 的入口处**
`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` => 
`PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors` => 
`BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry` => 
`ConfigurationClassPostProcessor#processConfigBeanDefinitions` => 
`ConfigurationClassParser#parse` => 
`ConfigurationClassParser#parse：202` => 
`ConfigurationClassParser#processConfigurationClass` => 
`ConfigurationClassParser#doProcessConfigurationClass` => 
`org.springframework.context.annotation.ComponentScanAnnotationParser#parse` =>  **解析 basePackage 并扫描类的位置**
`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan` => **扫描类**



1. `SpringApplication#createSpringFactoriesInstances` : 通过从`spring.factories`中获取到的`SpringApplicationRunListener`的实现类，将其实例化

2. `SpringApplication#createApplicationContext` 默认创建`AnnotationConfigApplicationContext`作为上下文。servlet 创建`AnnotationConfigServletWebServerApplicationContext`
3. `AnnotationUtils#findAnnotation` : **会递归查询类上声明的注解，直到找到指定注解`@Component`或者什么都没有找到**
4. `SpringApplication#refreshContext`:开始刷新上下文
5. `AbstractApplicationContext#refresh`: 核心加载方法
6. `ConfigurationClassPostProcessor`： 处理ConfigBeanDefinition的注册
7. `ConfigurationClassParser`： 解析`@Configuration`注解，会查询出`@ComponentScan`注解
   1. 关键方法：parse开头的方法
8. `ClassPathBeanDefinitionScanner`: 注解扫描类，将注解的类都扫描出并转为 BeanDefinition 注册进 Spring 中
9.  `AbstractApplicationContext#invokeBeanFactoryPostProcessors`: 扫描注解类并注册进 Spring 中





### SpringApplication 初始化准备的数据

1. primarySources 设置当前项目的启动类
2. webApplicationType
   1. 只要当前 classpath 下存在`javax.servlet.Servlet`或`ConfigurableWebApplicationContext`，就将其设置为`SERVLET`，如果仅存在`DispatcherHandler`,则将其设置为`REACTIVE`。
   2. 应用的类型就是通过如上的引用类推断的，只要有 Servlet 相关的包存在则认为其是 Servlet 环境
3. 从`spring.factories`中获取配置信息并加入缓存.
4. 设置初始化器，实现`ApplicationContextInitializer`接口的所有实例
   
   ```json
    [
        "org.springframework.boot.devtools.restart.RestartScopeInitializer",
        "org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer",
        "org.springframework.boot.context.ContextIdApplicationContextInitializer",
        "org.springframework.boot.context.config.DelegatingApplicationContextInitializer",
        "org.springframework.boot.rsocket.context.RSocketPortInfoApplicationContextInitializer",
        "org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer",
        "org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer",
        "org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener"
    ]
   ```

5. 设置监听器，实现`ApplicationListener`接口的所有实例
   
   ```json
    [
        "org.springframework.boot.devtools.restart.RestartApplicationListener",
        "org.springframework.boot.devtools.logger.DevToolsLogFactory.Listener",
        "org.springframework.boot.ClearCachesApplicationListener",
        "org.springframework.boot.builder.ParentContextCloserApplicationListener",
        "org.springframework.boot.context.FileEncodingApplicationListener",
        "org.springframework.boot.context.config.AnsiOutputApplicationListener",
        "org.springframework.boot.context.config.ConfigFileApplicationListener",
        "org.springframework.boot.context.config.DelegatingApplicationListener",
        "org.springframework.boot.context.logging.ClasspathLoggingApplicationListener",
        "org.springframework.boot.context.logging.LoggingApplicationListener",
        "org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener",
        "org.springframework.boot.autoconfigure.BackgroundPreinitializer"
    ]
   ```
6. 推断主类信息： mainApplicationClass
   获取当前堆栈中 main 方法所属类


### SpringApplication run方法的执行

1. 设置`SpringApplicationRunListener`监听器
   
   ```java
    org.springframework.boot.context.event.EventPublishingRunListener
   ```

2. 请求参数
   将参数封装在`ApplicationArguments`中
3. 获取环境
   根据 webApplicationType 获取环境信息。
   添加转换器的实例和格式化的实例
4. 创建 ApplicationContext
   1. 根据 webApplicationType 获取到Context 的类 `org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext`，并通过工具`BeanUtils.instantiateClass`将其实例化
   2. 在实例化`AnnotationConfigServletWebServerApplicationContext`时，会实例化`AnnotatedBeanDefinitionReader`和`ClassPathBeanDefinitionScanner`。
      1. 实例化`AnnotatedBeanDefinitionReader`时
         1. 设置默认的`beanNameGenerator`为`AnnotationBeanNameGenerator`
         2. 设置默认的`scopeMetadataResolver`为`AnnotationScopeMetadataResolver`
         3. 实例化`conditionEvaluator`为`ConditionEvaluator`
         4. 注册注解配合的处理器。
            1. 将`ConfigurationClassPostProcessor`(**解析注解配置的关键处理类**)初始化为 BeanDefinition，并注册 BeanName 为`org.springframework.context.annotation.internalConfigurationAnnotationProcessor`。
            2. 将`AutowiredAnnotationBeanPostProcessor`(**解析注解`@Autowired`时使用**)初始化为 BeanDefinition，并注册 beanName 为`org.springframework.context.annotation.internalAutowiredAnnotationProcessor`。
            3. 将`CommonAnnotationBeanPostProcessor`（**处理注解`@Resource`的类**）初始化为 BeanDefinition，并注册 beanName 为`org.springframework.context.annotation.internalCommonAnnotationProcessor`
            4. 将`EventListenerMethodProcessor`（**处理注解`@EventListener`的类**）初始化为 BeanDefinition，并注册 beanName 为`org.springframework.context.event.internalEventListenerProcessor`
            5. 将`DefaultEventListenerFactory`（**用来管理`EventListenerFactory`的类**）初始化为 BeanDefinition，并注册 beanName 为`org.springframework.context.event.internalEventListenerFactory`
      2. 实例化`ClassPathBeanDefinitionScanner`时
         1. 设置默认的`beanNameGenerator`为`AnnotationBeanNameGenerator`
         2. 设置默认的`beanDefinitionDefaults`为`BeanDefinitionDefaults`
         3. 设置默认的`scopeMetadataResolver`为`AnnotationScopeMetadataResolver`
         4. 设置默认支持注解配置`includeAnnotationConfig=true`
         5. 注册默认的过滤器
            1. 注解`@Component`、`@Named`和`@ManagedBean`
         6. 设置环境
         7. 设置资源加载器
5. 准备 ApplicationContext 的数据
   1. 设置环境信息
   2. 后处理
      1. 按需设置`beanNameGenerator`、`resourceLoader`、`conversionService`
   3. 支持初始化器，初始化器会做一下 context 的设置操作
      1. SharedMetadataReaderFactoryContextInitializer初始化器
         1. 在`BeanFactoryPostProcessor`中添加`CachingMetadataReaderFactoryPostProcessor`
      2. ContextIdApplicationContextInitializer初始化器
         1. 为 context 设置 ContextId 及将其注册进 Spring 的容器中。可通过`spring.application.name`属性配置，默认为`application`
      3. RestartScopeInitializer初始化器
         1. 将`RestartScope`注册到`restart` scope
      4. ConfigurationWarningsApplicationContextInitializer初始化器
         1. 在`BeanFactoryPostProcessor`中添加`ConfigurationWarningsPostProcessor`。将`org.springframework`和`org`设置为有问题的扫描包
      5. RSocketPortInfoApplicationContextInitializer 初始化器
         1. 为`ApplicationListener`添加新的监听器`RSocketPortInfoApplicationContextInitializer.Listener`
      6. ServerPortInfoApplicationContextInitializer 初始化器
         1. 将当前的applicationContext添加为`ApplicationListener`的监听器
      7. ConditionEvaluationReportLoggingListener
         1. 将`ConditionEvaluationReportListener`加入到`ApplicationListener`监听器中
      8. 
   4. 将参数类注册为单例。
      1. `springApplicationArguments` -> `ApplicationArguments`
   5. 将 banner 注册为单例
      1. `springBootBanner` -> `Banner`
   6. 默认不允许 BeanDefinition 覆盖
   7. 默认不允许延迟初始化
   8. 创建BeanDefinitionLoader，在 SpringBoot中
      1. 实例化注解读取器 `AnnotatedBeanDefinitionReader`
         1. 在实例化时，会通过工具注册几个 BeanFactoryPostProcessor,如`ConfigurationClassPostProcessor`和`AutowiredAnnotationBeanPostProcessor`。参考`AnnotationConfigUtils.registerAnnotationConfigProcessors`
         2. 还会实例化条件推断器`ConditionEvaluator`,用于`@Conditional`注解
      2. 实例化 xml 读取器 `XmlBeanDefinitionReader`
         1. 实例化常量
         2. 设置`beanNameGenerator`默认为`DefaultBeanNameGenerator`。
         3. xml 的校验模式默认设置为自动`VALIDATION_AUTO`
         4. doc 读取类`documentReaderClass`默认设置为`DefaultBeanDefinitionDocumentReader`
         5. doc 加载器`documentLoader`默认设置为`DefaultDocumentLoader`。
      3. 实例化扫描器 `ClassPathBeanDefinitionScanner`
         1. 注册默认的过滤器
         2. 扫描器将当前目标类过滤掉
      4. 通过 load 方法加载 bean 信息，并将当前类转换为 BeanDefinition 注册进容器中。还未实例化
   9.  刷新 Context，会调用`AbstractApplicationContext`中的`refresh`进行刷新操作
       1.  为刷新做准备
           1.  扫描器缓存清空
           2.  容器状态设置为激活
           3.  初始化属性源`PropertySources`
           4.  校验请求的属性
           5.  在刷新之前注册本地监听器
       2. 获取 BeanFactory
          1. 刷新 BeanFactory。此处注解方式会调用 `GenericApplicationContext`的`refreshBeanFactory`，而 xml 方式会调用`AbstractRefreshableApplicationContext`的`refreshBeanFactory`方法。
       3.  准备 BeanFactory
           1.  设置 Bean 表达式解析器 `StandardBeanExpressionResolver`
           2.  添加属性编辑器注册  `ResourceEditorRegistrar`
           3.  添加 `BeanPostProcessor`的后处理器`ApplicationContextAwareProcessor`,用于处理时限了`aware`的子接口的实例 bean。**在此时，`beanPostProcessors`属性内还是空的。**
               1.  还可以设置是否具有实例化和销毁 aware。通过实现`InstantiationAwareBeanPostProcessor`和`DestructionAwareBeanPostProcessor`接口
               2.  此处还有个顺序问题，将以前注册的相同的处理器删除，然后再在最后加上该处理器
           4.  忽略指定的依赖接口进行自动装配`autowiring`,包括`EnvironmentAware`,`EmbeddedValueResolverAware`,`ResourceLoaderAware`,`ApplicationEventPublisherAware`,`MessageSourceAware`,`ApplicationContextAware`。
           5.  注册可解析的依赖。`BeanFactory`,`ResourceLoader`,`ApplicationEventPublisher`,`ApplicationContext`。
           6.  添加 `BeanPostProcessor`的后处理器`ApplicationListenerDetector`。用于检测实现了`ApplicationListener`接口的 bean。
           7.  将`environment`,`systemProperties`,`systemEnvironment`注册为单例 Bean
       4. 后处理Bean工厂。实现类：`ServletWebServerApplicationContext`
          1. 添加 `BeanPostProcessor`的后处理器`WebApplicationContextServletContextAwareProcessor`,用来为实现了接口`ServletContextAware`,`ServletConfigAware`的类设置变量
          2. 忽略指定的依赖接口进行自动装配`autowiring`,`ServletContextAware`
          3. 注册 Scope。`request`、`session`
          4. 注册可解析的依赖。
             1. `ServletRequest` -> `RequestObjectFactory`
             2. `ServletResponse` -> `ResponseObjectFactory`
             3. `HttpSession` -> `SessionObjectFactory`
             4. `WebRequest` -> `WebRequestObjectFactory`
          5. 扫描器扫描和指定注解的bean 的注册。可指定
       5. 调用Bean 工厂后处理器
          1. 获取到`BeanDefinitionRegistryPostProcessor`的 bean 为`org.springframework.context.annotation.ConfigurationClassPostProcessor`。
          2. 将上一步获取到的处理器加入到`BeanDefinitionRegistryPostProcessor`集合中，并通过方法**PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors**调用BeanDefinition注册器的后处理器，即多个后处理器通过`postProcessBeanDefinitionRegistry`来执行。在`SharedMetadataReaderFactoryContextInitializer.CachingMetadataReaderFactoryPostProcessor`的方法`configureConfigurationClassPostProcessor`中获取已经注册为 BeanDefinition 的`org.springframework.context.annotation.internalConfigurationAnnotationProcessor`。
          3. 以`ConfigurationClassPostProcessor`为例，会扫描出所有的候选的 BeanDefinition 的名称集合。
             1. 检查是否是`ConfigurationClass`的候选类。通过查找元数据中是否与`@Configuration`注解。通过解析注解`@Order`，来获得顺序数字，通过 `list.sort()`使得候选类重新排序
             2. 将选出的候选类封装在`BeanDefinitionHolder`中，并存在集合里。
             3. 实例化解析类`ConfigurationClassParser`。并在该类中实例化了`ComponentScanAnnotationParser`和`ConditionEvaluator`分别用来扫描和条件判断。还实例化了`DeferredImportSelectorHandler`，即延时导入处理器
             4. 通过`ConfigurationClassParser.parse`方法开始解析候选类。
                1. 通过`conditionEvaluator`判断是否符合跳过的条件。
                2. 递归地处理`ConfigurationClass`类及其超类层次结构
                   1. 先判断`@Component`注解来递归处理其成员类
                   2. 判断`@PropertySources`注解，处理器属性源
                   3. 判断`@ComponentScans`注解，使用扫描器`ComponentScanAnnotationParser`扫描 Bean 信息并添加进容器中。
                   4. 判断 Bean 是否为`ConfigurationClass`的候选 bean，是的话递归解析。
                   5. 处理完`ConfigurationClass`类后，处理`@Import`注解的类，也是递归取找类上的`@Import` 注解。如果有的话，将其 value 收集到`Set<SourceClass>`的集合中。
                   6. 处理`@ImportResource`注解的资源信息
                   7. 处理`@Bean`注解的方法
                   8. 处理接口上的默认方法
                3. `DeferredImportSelectorHandler`用于处理`Import`
                   1.  扫描`META-INF/spring.factories`内所有的`org.springframework.boot.autoconfigure.EnableAutoConfiguration`的配置类。
                   2.  移除重复的和被指定排除的类
                   3.  获取所有的过滤器`@ConditionOn***`系列的执行类，查看是否匹配。如果不匹配则直接过滤掉。
                   4.  通过`ConditionEvaluationReportAutoConfigurationImportListener.onAutoConfigurationImportEvent`记录信息
                   5.  生成`AutoConfigurationEntry`包含待处理的自动化配置类和排除的类。
                   6.  将过滤后的 class 缓存在`AutoConfigurationImportSelector.AutoConfigurationGroup#entries`中
                   7.  通过执行`AutoConfigurationImportSelector.AutoConfigurationGroup#selectImports`,来筛选出哪些需要 import 的。
                   8.  通过`org.springframework.context.annotation.ConfigurationClassParser#processImports`方法来处理自动化配置类。
                   9. 因为所有的自动化配置类有用`@Configuration`标记了。因此这些类都是通过`ConfigurationClassParser#processConfigurationClass`来处理所有的配置类及其引入的配置类。
                4.  实例化读取器`ConfigurationClassBeanDefinitionReader`,并将刚刚扫描出的自动化配置类转为 BeanDefinition 并注册进 Spring 容器。 
                    1.  在`org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitions`将所有的自动化配置类注册为 BeanDefinition。
                5. 循环解析
                6. 将`org.springframework.context.annotation.ConfigurationClassPostProcessor.importRegistry`注册为单例,
             5. 通过`ConfigurationClassParser.validate`校验
             6. 初始化`ConfigurationClassBeanDefinitionReader`,通过方法`loadBeanDefinitions`加载配置的 Class，如 SpringBoot 中配置的自动启动配置类  **此处是加载自动化配置的 bean 的地方**
                1. `org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass`为实际加载自动配置类的位置
          4. 调用到目前为止处理的所有处理器的postProcessBeanFactory回调。
       6. 注册`BeanPostProcessors`
          1. 将`BeanPostProcessorChecker`、`ConfigurationPropertiesBindingPostProcessor`、`MethodValidationPostProcessor`、`webServerFactoryCustomizerBeanPostProcessor`、`errorPageRegistrarBeanPostProcessor`、`MethodValidationPostProcessor`、`MethodValidationPostProcessor`、`CommonAnnotationBeanPostProcessor`、`AutowiredAnnotationBeanPostProcessor`加入到`BeanPostProcessors`
          2. 将`ApplicationListenerDetector`重新加到`BeanPostProcessors`的结尾。

            ```java
            [
                "org.springframework.context.annotation.internalAutowiredAnnotationProcessor",
                "org.springframework.context.annotation.internalCommonAnnotationProcessor",
                "org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor",
                "webServerFactoryCustomizerBeanPostProcessor",
                "errorPageRegistrarBeanPostProcessor",
                "methodValidationPostProcessor"
            ]
            ```

       7. 初始化消息源，国际化
       8. 初始化时间多播器
       9. 刷新其他bean
       10. 注册监听器
       11. 实例化剩余的所有非懒加载的单例 bean `finishBeanFactoryInitialization`。
           1.  实际实例化的位置： `org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons`。通过调用`org.springframework.beans.factory.support.AbstractBeanFactory#getBean`完成实例化。
               1.  在`AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`方法中完成 Bean 的后处理设置，比如解析`@autowaired`、`@Value`和`@Inject` 注解的 bean 等。
               2.  在`AbstractAutowireCapableBeanFactory#initializeBean`处完成`aware`方法的设置，调用初始化方法
   10. 




