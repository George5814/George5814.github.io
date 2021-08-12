---
layout: post
title:  Mybatis技术内幕--Mybatis的日志拦截器
category: 技术
tags: mybatis Java springboot
keywords: mybatis Java
description: 
date: 2021-08-12
author: followtry
published: true
---


## 背景

Mybatis在执行Sql查询和更新时，无法知道具体的sql执行时间，是否存在慢查询等问题。需要在执行Sql时能对Sql进行监控，并定位到慢查询的问题发生的位置

## 环境

1. Mybatis
2. Spring
3. SpringBoot
4. SpringMVC

## 方案

实现Interceptor接口来实现自己的业务逻辑。

技术实现总共分

1. 实现自定义的拦截器
2. 实现自定义拦截器的加载
3. 实现自定义拦截器的注入

### 实现自定义的拦截器

1. 实现Mybatis的拦截器需要自定义类实现`Interceptor`接口。并实现接口的`SqlLogInterceptor#intercept`方法。
2. 在实现类`SqlLogInterceptor`增加注解`Intercepts`,指定该拦截器生效的位置。通过`Signature`注解指定类方法的签名。符合签名的方法才会被拦截执行。 
3. 现在要拦截Sql监控执行时间，则需要指定`Signature`的type为`StatementHandler.class`,仅对`StatementHandler.class`生效。method为`query`和`update`,表示对query和update方法都生效。
4. 在拦截器上标注`Component`注解，使其可以被Spring注册进容器中。

```java

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author followtry
 * @since 2021/8/12 10:42 上午
 */
//添加Spring的注解，允许加载为Spring的
@Component
@Intercepts(
        value = {
                @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
                @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        }
)
public class SqlLogInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(SqlLogInterceptor.class);

    public static final Long SLOW_SQL = TimeUnit.MILLISECONDS.toMillis(100);

    private static Map<String,String> sqlSignMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        Object parameterObject = statementHandler.getBoundSql().getParameterObject();
        String sql = statementHandler.getBoundSql().getSql();
        //将所有的换行都
        sql = sql.replaceAll("\n", " ");
        String param = JSON.toJSONString(parameterObject);

        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        boolean resSuc = true;
        Object proceed;
        try {
            proceed = invocation.proceed();
            endTime = System.currentTimeMillis();
        } catch (Exception e) {
            resSuc = false;
            endTime = System.currentTimeMillis();
            throw e;
        } finally {
            long cost = endTime - startTime;
            boolean isSlowSql = false;
            String signature = null;
            if (SLOW_SQL < cost) {
                isSlowSql = true;
                signature = genSqlSignature(invocation, sql);
            }
            LogUtils.logSql(sql, param, cost, resSuc, isSlowSql, signature);
        }
        return proceed;
    }

    private String genSqlSignature(Invocation invocation, String sql) {
        Optional<String> signatureOpt = Optional.ofNullable(sqlSignMap.get(sql));

        if (!signatureOpt.isPresent()) {
            try {
                StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
                Field delegate = statementHandler.getClass().getDeclaredField("delegate");
                ReflectionUtils.makeAccessible(delegate);
                StatementHandler statementHandlerV2 = (StatementHandler) delegate.get(statementHandler);
                Field mappedStatementField = statementHandlerV2.getClass().getSuperclass().getDeclaredField("mappedStatement");
                ReflectionUtils.makeAccessible(mappedStatementField);
                MappedStatement mappedStatement = (MappedStatement) mappedStatementField.get(statementHandlerV2);
                sqlSignMap.put(sql,mappedStatement.getId());
                return mappedStatement.getId();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                //ignore
                return null;
            }
        }
        return signatureOpt.get();
    }
}
```

### 实现自定义拦截器的加载 

既然已经实现了Mybatis的Sql拦截监控的主要逻辑。而我们要将该拦截器加载进Mybatis。但是对于使用SpringBoot的应用来说，应用使用`MybatisAutoConfiguration`来初始化Mybatis，其使用`ObjectProvider`为拦截器的自定义加载提供入口，
而不支持常见的配置的方式。

实现`ObjectProvider`需要被Spring加载为Bean,并将所有的`Interceptor`的bean实例都注入到自定义的`ObjectProvider`(`SqlLogInterceptorProvider`)中，并在实例化过程中注入`ApplicationContext`，通过其可以获取到`Interceptor`的bean实例数组。

```java

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.plugin.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author followtry
 * @since 2021/8/12 11:17 上午
 */
@Component
public class SqlLogInterceptorProvider implements ObjectProvider<Interceptor[]>, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(SqlLogInterceptorProvider.class);

    private Interceptor[] interceptors;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        setInterceptors();
    }

    private void setInterceptors() {
        Map<String, Interceptor> beansOfType = this.applicationContext.getBeansOfType(Interceptor.class);
        this.interceptors = beansOfType.values().toArray(new Interceptor[0]);
        log.info("inject interceptors, {}", JSON.toJSONString(interceptors));
    }

    @Override
    public Interceptor[] getObject() throws BeansException {
        return this.interceptors;
    }

    @Override
    public Interceptor[] getObject(Object... args) throws BeansException {
        return this.interceptors;
    }

    @Override
    public Interceptor[] getIfAvailable() throws BeansException {
        return this.interceptors;
    }

    @Override
    public Interceptor[] getIfUnique() throws BeansException {
        return this.interceptors;
    }
}
```

通过如上的代码，就可以实现`ObjectProvider`机制获取实例时获取到的都是自定义的Interceptor的Bean实例。


### 实现自定义拦截器的注入

既然如上的自定义拦截器的逻辑已经实现，自定义拦截器的加载机制也已经打通。剩下的就是怎么将已经实例化后的`Interceptor`实例注入到Mybatis中了。次步骤是使用的Mybatis-Springboot提供的方式，使用`MybatisAutoConfiguration`。

在`MybatisAutoConfiguration`的构造方法中，有个参数`ObjectProvider<Interceptor[]>` 就是通过Spring的注入机制在`MybatisAutoConfiguration`实例化时将拦截器注入进去的。

```java
public class MybatisAutoConfiguration implements InitializingBean {
    public MybatisAutoConfiguration(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider,
                                    ObjectProvider<TypeHandler[]> typeHandlersProvider, ObjectProvider<LanguageDriver[]> languageDriversProvider,
                                    ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                    ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        this.properties = properties;
        //此处将自定义的拦截器注入进配置类中
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.typeHandlers = typeHandlersProvider.getIfAvailable();
        this.languageDrivers = languageDriversProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
    }
}
```

该配置类不仅只注入自定义的拦截器。如自定义的typehandler,DatabaseId等都可以通过`ObjectProvider`的机制注入进来。
因为其为`Configuration`类，对于其中的`Bean`注解的方法都会自动执行，所以会继续实现`SqlSessionFactory`的初始化和`SqlSessionTemplate`的初始化。


```java
public class MybatisAutoConfiguration implements InitializingBean {
    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        //SqlSessionFactoryBean为实现了FactoryBean接口的类，也是用的Spring的机制，通过调用其getObject方法获取SqlSessionFactory实例
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        applyConfiguration(factory);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        //此处判断interceptors不为空则将其作为插件参数设置进去，此处只是设置参数，还未执行解析等动作
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        //设置类型的别名的包，该包路径下的类都会设置别名
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (this.properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        //设置TypeHandler所在的包路径
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        //设置通过ObjectProvider方式注入进来的TypeHandler
        if (!ObjectUtils.isEmpty(this.typeHandlers)) {
            factory.setTypeHandlers(this.typeHandlers);
        }
        //设置mapper的映射地址
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            factory.setMapperLocations(this.properties.resolveMapperLocations());
        }
        Set<String> factoryPropertyNames = Stream
                .of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors()).map(PropertyDescriptor::getName)
                .collect(Collectors.toSet());
        Class<? extends LanguageDriver> defaultLanguageDriver = this.properties.getDefaultScriptingLanguageDriver();
        if (factoryPropertyNames.contains("scriptingLanguageDrivers") && !ObjectUtils.isEmpty(this.languageDrivers)) {
            // Need to mybatis-spring 2.0.2+
            factory.setScriptingLanguageDrivers(this.languageDrivers);
            if (defaultLanguageDriver == null && this.languageDrivers.length == 1) {
                defaultLanguageDriver = this.languageDrivers[0].getClass();
            }
        }
        if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
            // Need to mybatis-spring 2.0.2+
            factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
        }
        //该步骤是使用的FactoryBean机制，通过getObject获取具体对象的实例。
        return factory.getObject();
    }
}
```

在`getObject`方法时，会执行Mybatis的初始化，并最终生成`SqlSessionFactory`实例


```java
public class SqlSessionFactoryBean
        implements FactoryBean<SqlSessionFactory>, InitializingBean, ApplicationListener<ApplicationEvent> {
    public SqlSessionFactory getObject() throws Exception {
        if (this.sqlSessionFactory == null) {
            afterPropertiesSet();
        }

        return this.sqlSessionFactory;
    }

    public void afterPropertiesSet() throws Exception {
        notNull(dataSource, "Property 'dataSource' is required");
        notNull(sqlSessionFactoryBuilder, "Property 'sqlSessionFactoryBuilder' is required");
        state((configuration == null && configLocation == null) || !(configuration != null && configLocation != null),
                "Property 'configuration' and 'configLocation' can not specified with together");

        //具体执行SqlSessionFactory实例化的地方
        this.sqlSessionFactory = buildSqlSessionFactory();
    }
}
```

主要在`buildSqlSessionFactory`方法中将`Interceptor`的拦截器注入进`sqlSessionFactory`,在`Configuration.newStatementHandler`方法会通过拦截器代理目标方法实现对执行Sql的拦截。

```java
public class Configuration {
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        //生成代理对象
        statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
        return statementHandler;
    }
}
```





