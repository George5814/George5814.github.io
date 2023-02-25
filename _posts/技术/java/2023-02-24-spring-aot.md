---
layout: post
title:  Spring AOT介绍
category: 技术
tags: Java
keywords: Java Spring Graalvm Native
description: Spring Graalvm
date: 2023-02-24
modified_date: 2023-02-25
author: followtry
published: true
istop: false
---

Spring对AOT优化的支持意味着将哪些通常在运行时才发生的事情提前到编译期做，包括在构建时检查ApplicationContext，支持决策和发现执行逻辑。这样做可以构建一个更直接的应用程序启动安排，并主要基于类路径和环境来关注一组固定的特性。

支持这样的优化意味着需要对原Spring应用做如下的限制：
1. `classpath`是固定的，并在在构建时就已经全部指定了。
2. bean的定义在运行时不能改变。
    1. `@Profile`,特别是需要在构建时选择特定于配置文件的配置
    1.  影响bean存在的环境属性配置`@Conditional`仅能在构建时考虑
3. 带有Supplier（包括`lambda`和方法引用）的Bean的定义不能被AOT转换。
4. `@Bean`注解的方法的返回类型得是具体的类，而不能是接口了，以便允许正确的提示推断。

当以上的限制都避免了，就可以在构建时执行AOT的处理并生成额外的资产。

经过Spring AOT处理过的应用，通过会生成如下资产：
1. Java源码
2. 字节码
3. `RuntimeHints`,用于反射，资源定位，序列化和Java反射

在当前情况下，Spring AOT专注于使用GraalVM将Spring的应用部署为原生的镜像，后续可能会支持更多的JVM。

## AOT引擎介绍

用于处理ApplicationContext排列的AOT引擎的入口点是`ApplicationContextAotGenerator`.它负责以下步骤,其基于的参数`GenericApplicationContext`表示要被优化的应用，和一个通用的上下文参数`GenerationContext`.
1. 刷新用于AOT处理的`ApplicationContext`。与传统的刷新不同，此版本只创建bean定义，而不是bean实例
1. 调用可用的`BeanFactoryInitializationAotProcessor`的具体实现，并对`GenerationContext`使用。例如，核心实现 迭代所有候选bean definition，并生成必要的代码以恢复BeanFactory的状态。

一旦该处理完成，`GenerationContext`将被那些应用运行所必须的已生成代码、资源和类更新。`RuntimeHints`实例可以用于生成与`GraalVM`相关的原生镜像配置文件。

`ApplicationContextAotGenerator#processAheadOfTime`返回`ApplicationContextInitializer`入口点的类名，该入口点允许使用AOT优化启动上下文。

## 刷新AOT的处理

所有`GenericApplicationContext`的实现都支持AOT处理的刷新。应用程序上下文由任意数量的入口点创建，通常以`@Configuration`注解类的形式。

通常的实现如下：

```java
@Configuration(proxyBeanMethods=false)
@ComponentScan
@Import({DataSourceConfiguration.class, ContainerConfiguration.class})
public class MyApplication {
}
```


使用常规运行时启动此应用程序涉及许多步骤，包括类路径扫描、配置类解析、bean实例化和生命周期回调处理。AOT处理的刷新仅应用常规刷新的子集。AOT处理可按如下方式触发：

```java
RuntimeHints hints = new RuntimeHints();
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
context.register(MyApplication.class);
context.refreshForAotProcessing(hints);
// ...
context.close();
```

在AOT模式下，`BeanFactoryPostProcessor`扩展点的实现和平时一样调用。包括`configuration`类的解析、`import selector`和类扫描等。这些步骤确保`BeanRegistry`包含应用程序的相关bean定义.如果Bean definition收到`conditions`(如 `@Profile`)的保护，则在该阶段会被抛弃。因为此模式实际上不创建Bean的实例，除了与AOT相关的变体实现之外，`BeanPostProcessor`将不会被调用。变体实现包括：
1. `MergedBeanDefinitionPostProcessor`的实现，后处理bean定义以提取其他设置，如`init`和`destroy`方法

2. `SmartInstantiationAwareBeanPostProcessor`的实现，如果需要，确定更精确的bean类型，这确保创建运行时需要的任何代理类。

一旦该步骤完成，`BeanFactory`就包含了应用运行所必须的bean definition 集合。它不触发bean实例化，但允许AOT引擎检查将在运行时创建的bean。

## Bean工厂初始化AOT贡献

希望参与此步骤的组件可以实现`BeanFactoryInitializationAotProcessor`接口。每个实现都可以根据bean工厂的状态返回AOT贡献。

AOT贡献是贡献生成的代码可以再现特定行为的组件。它还可以提供`RuntimeHints`来指示反射、资源加载、序列化或JDK代理的需要.

`BeanFactoryInitializationAotProcessor`的实现可以注册在`META-INF/spring/aot.factories`中，key为该接口的全限定名。

`BeanFactoryInitializationAotProcessor`也可以直接被一个bean实现。在这种模式下，bean提供的AOT贡献与它在常规运行时提供的特性相当。因此，这样的bean会自动从AOT优化上下文中排除。

**注意:**  如果bean实现了`BeanFactoryInitializationAotProcessor`接口，那么在AOT处理期间将初始化bean及其所有依赖项。我们通常建议此接口仅由基础结构bean（如`BeanFactoryPostProcessor`）实现，这些bean具有有限的依赖性，并且在bean工厂生命周期的早期就已经初始化。如果这样的bean是使用`@bean`工厂方法注册的，**请确保该方法是静态的**，以便其封闭的`@Configuration`类不必初始化。

### Bean注册AOT贡献

`BeanFactoryInitializationAotProcessor`实现的核心功能是负责为每个候选BeanDefinition收集必要的贡献。它使用专用的`BeanRegistryAotProcessor`来实现。

该接口的使用方式如下：

1. 由`BeanPostProcessor`bean实现，以替换其运行时行为。例如，AutowiredAnnotationBeanPostProcessor实现了这个接口，以生成注入用@Autowired注释的成员的代码。

1. 由`META-INF/spring/aot.factors`中注册的类型实现，其key等于接口的完全限定名称。通常在需要针对核心框架的特定特性进行调整的bean定义时使用。

**注意:**  如果一个bean实现了`BeanRegistryAotProcessor`接口，那么在AOT处理期间将初始化该bean及其所有依赖项。我们通常建议此接口仅由基础结构bean（如`BeanFactoryPostProcessor`）实现，这些bean具有有限的依赖性，并且在bean工厂生命周期的早期就已经初始化。如果这样的bean是使用`@bean`工厂方法注册的，**请确保该方法是静态的**，以便其封闭的`@Configuration`类不必初始化。

如果没有BeanRegisterationAotProcessor处理特定注册的bean，则默认实现会处理它。这是默认行为，因为为bean definition 调整生成的代码应该仅限于比较冷门的使用案例。

以前面的示例为例，我们假设DataSourceConfiguration如下：

```java
@Configuration(proxyBeanMethods = false)
public class DataSourceConfiguration {

    @Bean
    public SimpleDataSource dataSource() {
        return new SimpleDataSource();
    }

}
```

由于该类上没有任何特定条件，因此`dataSourceConfiguration`和`dataSource`被标识为候选项。AOT引擎会将上面的配置类转换为与以下类似的代码：

```java
/**
 * Bean definitions for {@link DataSourceConfiguration}
 */
public class DataSourceConfiguration__BeanDefinitions {
    /**
     * Get the bean definition for 'dataSourceConfiguration'
     */
    public static BeanDefinition getDataSourceConfigurationBeanDefinition() {
        Class<?> beanType = DataSourceConfiguration.class;
        RootBeanDefinition beanDefinition = new RootBeanDefinition(beanType);
        beanDefinition.setInstanceSupplier(DataSourceConfiguration::new);
        return beanDefinition;
    }

    /**
     * Get the bean instance supplier for 'dataSource'.
     */
    private static BeanInstanceSupplier<SimpleDataSource> getDataSourceInstanceSupplier() {
        return BeanInstanceSupplier.<SimpleDataSource>forFactoryMethod(DataSourceConfiguration.class, "dataSource")
                .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean(DataSourceConfiguration.class).dataSource());
    }

    /**
     * Get the bean definition for 'dataSource'
     */
    public static BeanDefinition getDataSourceBeanDefinition() {
        Class<?> beanType = SimpleDataSource.class;
        RootBeanDefinition beanDefinition = new RootBeanDefinition(beanType);
        beanDefinition.setInstanceSupplier(getDataSourceInstanceSupplier());
        return beanDefinition;
    }
}
```

**根据bean定义的确切性质，生成的确切代码可能有所不同。**

上面生成的代码创建了与`@Configuration`类等效的bean定义，但以直接的方式，如果可能的话，不使用反射。dataSourceConfiguration有一个bean定义，dataSourceBean有一个。当需要数据源实例时，将调用BeanInstance Supplier。此Supplier调用dataSourceConfiguration bean上的dataSource()方法。

## 运行时提示(Runtime Hints)

与常规JVM运行时相比，将应用程序作为native image 运行需要额外的信息。例如，GraalVM需要提前知道组件是否使用反射。类似地，除非明确指定，否则类路径资源不会在native image中提供。因此，如果应用程序需要加载资源，则必须从相应的GraalVM native image 配置文件中引用该资源。

`RuntimeHints`的API收集运行时对反射、资源加载、序列化和JDK代理的需求。以下示例确保`config/app.properties`可以在运行时从本机映像中的类路径加载。

```java
runtimeHints.resources().registerPattern("config/app.properties");
```

在AOT处理过程中，会自动处理许多合同。例如：检查`@Controller`方法的返回类型，如果Spring检测到类型应该序列化（通常为JSON），则添加相关的反射提示。

对于核心容器无法推断的情况，可以以编程方式注册此类提示。还为常见用例提供了许多方便的注释。

### @ImportRuntimeHints

`RuntimeHintsRegister`实现允许您获取对AOT引擎管理的`RuntimeHints`实例的回调。可以在任何Spring的bean实例或`@bean`工厂方法上使用`@ImportRuntimeHints`注册此接口的实现。在构建时检测并调用`RuntimeHintsRegister`实现。

```java
@Component
@ImportRuntimeHints(SpellCheckService.SpellCheckServiceRuntimeHints.class)
public class SpellCheckService {

    public void loadDictionary(Locale locale) {
        ClassPathResource resource = new ClassPathResource("dicts/" + locale.getLanguage() + ".txt");
        //...
    }

    static class SpellCheckServiceRuntimeHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("dicts/*");
        }
    }

}
```

如果可能，`@ImportRuntimeHints`应尽可能靠近需要提示的组件使用。这样，如果组件没有被贡献给`BeanFactory`，`hints`也不会被贡献。

### @Reflective

`@Reflective`提供了一种惯用的方法来标记对带注解元素的反射的需要。例如，`@EventListener`使用`@Reflective`进行元注释，因为底层实现使用反射调用注释方法.

默认情况下，只考虑Spring的bean，并为带注解的元素注册调用提示。这可以通过通过`@Reflective`注解指定自定义`ReflectiveProcessor`实现来调整。

库作者可以出于自己的目的重用此注释。如果需要处理Spring bean以外的组件，`BeanFactoryInitializationAotProcessor`可以检测相关类型并使用`ReflectiveRuntimeHintsRegister`来处理它们。

### @RegisterReflectionForBinding

`@RegisterReflectionForBinding` 是`@Reflective`的特例，它注册了序列化任意类型的需要。典型的用例是容器无法推断的DTO的使用，例如在方法体中使用web客户端。

`@RegisterReflectionForBinding`可以应用于类级别的任何Spring bean，但也可以直接应用于方法、字段或构造函数，以更好地指示实际需要提示的位置。以下示例 注册Account以进行序列化。

```java
@Component
public class OrderService {

    @RegisterReflectionForBinding(Account.class)
    public void process(Order order) {
        // ...
    }

}
```

### 测试 Runtime Hints

Spring Core还提供`RuntimeHintsPredices`，这是一个用于检查现有提示是否匹配特定用例的实用程序。这可以在您自己的测试中使用，以验证`RuntimeHintsRegister`是否包含预期结果。我们可以为我们的SpellCheckService编写测试，并确保我们能够在运行时加载字典：

```java
@Test
void shouldRegisterResourceHints() {
    RuntimeHints hints = new RuntimeHints();
    new SpellCheckServiceRuntimeHints().registerHints(hints, getClass().getClassLoader());
    assertThat(RuntimeHintsPredicates.resource().forResource("dicts/en.txt"))
            .accepts(hints);
}
```

使用`RuntimeHintsPredices`，我们可以检查反射、资源、序列化或代理生成提示。这种方法适用于单元测试，但意味着组件的运行时行为是众所周知的。通过使用GraalVM跟踪代理运行应用程序的测试套件（或应用程序本身），可以了解有关应用程序全局运行时行为的更多信息。该代理将在运行时记录所有需要GraalVM提示的相关调用，并将其作为JSON配置文件写入。

为了更具针对性的发现和测试，Spring Framework提供了一个带有核心AOT测试实用程序的专用模块，“org.springframework:Spring-core测试”。此模块包含RuntimeHints Agent，这是一个Java代理，它记录与运行时提示相关的所有方法调用，并帮助您断言给定的RuntimeHinds实例覆盖所有记录的调用。让我们考虑一个基础设施，我们希望测试在AOT处理阶段提供的提示。

```java
public class SampleReflection {

    private final Log logger = LogFactory.getLog(SampleReflection.class);

    public void performReflection() {
        try {
            Class<?> springVersion = ClassUtils.forName("org.springframework.core.SpringVersion", null);
            Method getVersion = ClassUtils.getMethod(springVersion, "getVersion");
            String version = (String) getVersion.invoke(null);
            logger.info("Spring version:" + version);
        }
        catch (Exception exc) {
            logger.error("reflection failed", exc);
        }
    }
}
```

然后，我们可以编写一个单元测试（不需要本机编译），检查我们提供的提示：

```java
@EnabledIfRuntimeHintsAgent
class SampleReflectionRuntimeHintsTests {

    @Test
    void shouldRegisterReflectionHints() {
        RuntimeHints runtimeHints = new RuntimeHints();
        // Call a RuntimeHintsRegistrar that contributes hints like:
        runtimeHints.reflection().registerType(SpringVersion.class, typeHint ->
                typeHint.withMethod("getVersion", List.of(), ExecutableMode.INVOKE));

        // Invoke the relevant piece of code we want to test within a recording lambda
        RuntimeHintsInvocations invocations = RuntimeHintsRecorder.record(() -> {
            SampleReflection sample = new SampleReflection();
            sample.performReflection();
        });
        // assert that the recorded invocations are covered by the contributed hints
        assertThat(invocations).match(runtimeHints);
    }
}
```

如果您忘记提供提示，测试将失败，并提供有关调用的一些详细信息：

```java
org.springframework.docs.core.aot.hints.testing.SampleReflection performReflection
INFO: Spring version:6.0.0-SNAPSHOT

Missing <"ReflectionHints"> for invocation <java.lang.Class#forName>
with arguments ["org.springframework.core.SpringVersion",
    false,
    jdk.internal.loader.ClassLoaders$AppClassLoader@251a69d7].
Stacktrace:
<"org.springframework.util.ClassUtils#forName, Line 284
io.spring.runtimehintstesting.SampleReflection#performReflection, Line 19
io.spring.runtimehintstesting.SampleReflectionRuntimeHintsTests#lambda$shouldRegisterReflectionHints$0, Line 25
```

该文章的内容来自于Spring官方手册，原文内容：<https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#core.aot>


