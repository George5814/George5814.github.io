---
layout: post
title: Spring学习 - 自定义Spring 的 bean扫描器
category: 技术
tags:  Spring Java SpringBoot
keywords: Spring Java genBeanName SpringBoot
description:  自定义扫描器的实践 Demo，可以通过 SpringBoot 的 starter 方式启动扫描器
modify: 2019-10-30
published: true
---

## 自定义扫描器

### 创建自定义的注解类型

如创建注解`MyAnno`

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MyAnno {

    @AliasFor(annotation = Component.class)
    String value() default "empty name";
}
```

此处注意`@MyAnno`必须被`@Component`标记，因为自定义的 scanner 是继承自`ClassPathBeanDefinitionScanner`,而其检测的目标注解就是`@Component`。

如果想使用自定义的 value，必须使用`@AliasFor`指定注解为`@Component`,原因还是因为`ClassPathBeanDefinitionScanner`只会取`@Component`的 value 值。

### 创建自定义的扫描器

```java
public class CustomScanner extends ClassPathBeanDefinitionScanner {

    private Class<? extends Annotation> annoType;

    public CustomScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        super(registry);
        this.annoType = annoType;

        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }

    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }
}
```

通过在构造方法中指定解析的注解来使得自定义的注解生效。通过`super.addIncludeFilter`方法将自定义注解添加进过滤器中。


### 引用自定义的注解

```java
package cn.followtry.boot.java.service;

@MyAnno(value = "my_first_test_scanner_service")
public class MyService {
}
```

创建一个测试类，并在该类上打上注解`@MyAnno`并为其 value 赋值。

### 创建测试类

```java
package cn.followtry.boot.java.test;

public class ScannerTest {

    /**  */
    private static final String BASE_PACKAGE = "cn.followtry.boot.java.service";

    /**
     * main.
     */
    public static void main(String[] args) {

        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        CustomScanner scanner = new CustomScanner(applicationContext, MyAnno.class);
        int scanCount = scanner.scan(BASE_PACKAGE);
        System.out.println("1扫描的数量"+scanCount);
        applicationContext.refresh();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println("bean name : " + beanDefinitionName);
        }

    }
}
```

1. 先创建Spring 的注解方式的上下文 `AnnotationConfigApplicationContext`。
2. 然后创建自定义扫描器实例 `CustomScanner`,将注解`@MyAnno`作为构造方法的参数传入，使其在该扫描器中生效。
3. 调用`scan`方法扫描 base 包，并生成注册 Bean。
4. 调用`applicationContext.refresh();`完成 Spring 的初始化。
5. 通过打印`applicationContext.getBeanDefinitionNames();`,判断当前自定义注解`@MyAnno`标记的 bean 是否成功注册进 Spring。


打印的日志

```logger
bean name : org.springframework.context.annotation.internalConfigurationAnnotationProcessor
bean name : org.springframework.context.annotation.internalAutowiredAnnotationProcessor
bean name : org.springframework.context.annotation.internalRequiredAnnotationProcessor
bean name : org.springframework.context.annotation.internalCommonAnnotationProcessor
bean name : org.springframework.context.event.internalEventListenerProcessor
bean name : org.springframework.context.event.internalEventListenerFactory
bean name : my_first_test_scanner_service
```

可以看到，有 bean name 为`my_first_test_scanner_service`被打印出，说明自定义的注解已经生效了。

## 创建自定义的 starter


###  依赖自定义的扫描器 `CustomScanner`

###  创建扫描器配置类

配置类`ScannerConfiguration`实现接口 `BeanDefinitionRegistryPostProcessor`, `InitializingBean`, `ApplicationContextAware`, `BeanNameAware`。

代码如下:

```java
public class ScannerConfiguration implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

    private static final Logger log = LoggerFactory.getLogger(ScannerConfiguration.class);

    private String basePackage;

    /**  */
    private String beanName;

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("======初始化自定义的配置:{},basePackage : {}", this.getClass().getCanonicalName(), this.basePackage);
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        log.info("=====开始启动自定义的扫描器扫描自定义的注解====");
        CustomScanner customScanner = new CustomScanner(registry, MyAnno.class);
        customScanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
        log.info("=====完成启动自定义的扫描器扫描自定义的注解====");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // left intentionally blank
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getBeanName() {
        return beanName;
    }
}
```

### 创建扫描器的注册处理器

处理器`ScannerPostProcessor`,实现接口 `BeanDefinitionRegistryPostProcessor`, `EnvironmentAware`, `BeanFactoryAware`

代码如下：

```java
public class ScannerPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, BeanFactoryAware {

    private static final Logger log = LoggerFactory.getLogger(ScannerPostProcessor.class);
    private ConfigurableEnvironment environment;
    private ConfigurableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory)beanFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //将配置的 kv 绑定到指定的对象上
        MyBootScanProperties scanProperties = Binder.get(this.environment).bind("my-scanner", Bindable.of(MyBootScanProperties.class)).orElse(new MyBootScanProperties());
        BeanDefinition beanDefinition = this.buildScannerConfigurerBeanDefinition(scanProperties);
        //注册对象
        registry.registerBeanDefinition("scannerConfiguration",beanDefinition);

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment)environment;
    }

    private BeanDefinition buildScannerConfigurerBeanDefinition(MyBootScanProperties myBootScanProperties) {
        BeanDefinitionBuilder scannerConfigurerBuilder = BeanDefinitionBuilder.genericBeanDefinition(ScannerConfiguration.class);
        scannerConfigurerBuilder.addPropertyValue("basePackage", myBootScanProperties.getBasePackage());
        return scannerConfigurerBuilder.getBeanDefinition();
    }
}
```

其中的`BeanDefinitionBuilder scannerConfigurerBuilder = BeanDefinitionBuilder.genericBeanDefinition(ScannerConfiguration.class);`就是在将`ScannerConfiguration`转为 BeanDefinition，并将`MyBootScanProperties`的值赋值给`ScannerConfiguration`相应的属性。

`MyBootScanProperties`为配置属性类，在下面会介绍。

### starter 的启动类

启动类为`ScanAutoConfiguration`,主要为了设置启动开关和实例化扫描器的注册处理器`ScannerPostProcessor`，一遍让`ScannerPostProcessor`在实例化的时候顺便把`ScannerConfiguration也实例化。

```java
@Configuration
@ConditionalOnProperty(
        name = {"mdp.zebra.enabled"},
        matchIfMissing = true
)
public class ScanAutoConfiguration {

    @Bean
    public ScannerPostProcessor myScannerPostProcessor(){
        return new ScannerPostProcessor();
    }
}
```

### 配置属性类

`MyBootScanProperties`配置属性类设置前缀为`my-scanner`,仅设置了`basePackage`一个属性用于配置自定义扫描器的 base 包路径。

```java
@ConfigurationProperties(
        prefix = "my-scanner"
)
public class MyBootScanProperties {
    public static final String MY_BOOT_PREFIX = "my-scanner";

    /**  */
    private String basePackage = ".";

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
```

### 配置启动类

在`src/main/resources`下建立文件`META-INF/spring.factories`,该文件出配置启动类。SpringBoot在启动时会扫描所有包下的该文件，收集启动类。

```
# 配置可以被自动化配置开启的类
org.springframework.boot.autoconfigure.EnableAutoConfiguration=cn.followtry.ScanAutoConfiguration
```

此处将`ScanAutoConfiguration`设置为自动启动类。

建立文件`META-INF/spring-autoconfigure-metadata.properties`,用来配置条件类

```
#Mon Jul 15 16:03:44 JST 2019
# 指定需要可以出发自动化配置条件的类
cn.followtry.UserAutoConfiguration.ConditionalOnClass=cn.followtry.MyBootScanProperties
```


建立文件`META-INF/spring-configuration-metadata.json`,用来设置属性配置项

json 格式，内容如下

```
{
  /** 指定属性标签的名称，类型和属性值及类型 **/
  "groups": [
    {
      "name": "my-scanner",
      "type": "cn.followtry.MyBootScanProperties",
      "sourceType": "cn.followtry.MyBootScanProperties"
    }
  ],
  "properties": [
    {
      "name": "my-scanner.base-package",
      "type": "java.lang.String",
      "description": "base package path",
      "sourceType": "cn.followtry.MyBootScanProperties",
      "defaultValue": ""
    }
  ],
  "hints": []
}
```

完成以上步骤后，在 Springboot 项目中，配置扫描 basePackage,并使用`@MyAnno`注解标记类，则项目启动后，该类就被注册进 Spring 的注册器了。

## 总结

总体流程如下：

1. SpringBoot项目配置 `basePackage` 属性，启动项目
2. springboot 会自动获取所有 jar 包下的`spring.factories`文件，该文件中定义了每个 starter 的启动器
3. 触发启动器`ScanAutoConfiguration`,该启动器会实例化扫描器的注册处理器`ScannerPostProcessor`。
4. `ScannerPostProcessor`会在实例化时通过`postProcessBeanDefinitionRegistry`方法将`ScannerConfiguration`给实例化，并会将配置的属性，设置给改该类。
5. 而`ScannerConfiguration`中在`postProcessBeanDefinitionRegistry`中配置了自定义的扫描器。会 new 一个新的扫描器，然后通过给定的`basePackage`,扫描并注册所有的 bean 的信息，包括`@MyAnno`标记的 Bean 的信息。
6. 最后完成 spring 的整个初始化，项目完成启动。

