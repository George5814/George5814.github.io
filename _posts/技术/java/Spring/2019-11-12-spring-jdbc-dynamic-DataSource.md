---
layout: post
title: Spring学习 - 自定义动态数据源
category: 技术
tags:  Spring Mysql Java
keywords: 
modify: 2019-11-12
description: 学习通过 Spring的 JDBC 封装抽象类实现动态数据源
published: true
---

# 动态数据源

需要实现类继承自`org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource`,来实现自定义的数据源路由。


## 自定义实现动态数据源

代码

```java
//自定义实现方法`determineCurrentLookupKey`来确定当前lookupKey，通过该 key 找到配置的多个数据源中的目标数据源。
public class DynamicDataSource extends AbstractRoutingDataSource {

    private String defaultDataSourceKey;

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSource = DynamicDataSourceHolder.get();
        String dataSourceKey = StringUtils.isNotEmpty(dataSource) ? dataSource : this.defaultDataSourceKey;
        return dataSourceKey;
    }

    public String getDefaultDataSourceKey() {
        return defaultDataSourceKey;
    }

    public void setDefaultDataSourceKey(String defaultDataSourceKey) {
        this.defaultDataSourceKey = defaultDataSourceKey;
    }
}
```

## lookupkey的动态传递

通过`ThreadLocal`来实现数据源 `lookupkey`的动态绑定。如果想要在多线程中使用，则将`ThreadLocal`换为`InheritableThreadLocal`

```java
public class DynamicDataSourceHolder {

    private static final ThreadLocal<String> targetDataSource = new ThreadLocal<>();

    public static void set(String target) {
        targetDataSource.set(target);
    }

    public static String get() {
        return targetDataSource.get();
    }

    public static void clear() {
        targetDataSource.remove();
    }
}
```


## lookupkey的设置

通过切面+注解的方式，可以自定义的动态指定不同的 Sql 来访问不同的数据源。



```java
//读主节点的注解
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ReadMaster {

}


//通过切面判断该方法是否读主节点，是的话设置 lookupKey为 masterDataSource
public class AnnotationReadMasterAdvice implements MethodBeforeAdvice, AfterReturningAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        ReadMaster readMaster = method.getAnnotation(ReadMaster.class);
        if (readMaster == null) {
            return;
        }
        DynamicDataSourceHolder.set("masterDataSource")
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        ReadMaster readMaster = method.getAnnotation(ReadMaster.class);
        if (readMaster == null) {
            return;
        }
        ZebraForceMasterHelper.clearLocalContext();
    }
}
```

## 配置使用

```xml
   <!-- xml方式切面配置 -->
   <aop:config>
        <aop:pointcut id="serviceMethods"
                      expression="execution(* cn.followtry.dao..*DAO.*(..)) || execution(* cn.followtry.db.dao..*DAO.*(..))"/>
        <aop:advisor advice-ref="annotationReadMasterAdvice" pointcut-ref="serviceMethods"/>
    </aop:config>

   <!-- 实例化自定义的 advice -->
    <bean id="annotationReadMasterAdvice" class="com.meituan.hotel.pegasus.common.db.readstrategy.AnnotationReadMasterAdvice"/>
```

## 多数据源设置

通过 Bean 方式实例化动态数据源`dynamicDataSource`，然后将该动态数据源作为参数配置到`jdbcTemplate`或者 mybatis 的`sqlSessionFactory`中，供上层使用。

```xml
<bean id="dynamicDataSource"
          class="cn.followtry.db.datasource.DynamicDataSource" primary="true">
   <property name="targetDataSources">
      <map key-type="java.lang.String">
            <entry key="defaultDataSource" value-ref="defaultDataSource"/>
            <entry key="default2DataSource" value-ref="default2DataSource"/>
      </map>
   </property>
   <property name="defaultTargetDataSource" ref="defaultDataSource"/>
   <property name="defaultDataSourceKey" value="defaultDataSource"/>
</bean>

<!-- jdbcTemplate -->
<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
   <property name="dataSource" ref="dynamicDataSource"/>
</bean>

<!-- 集成myBaits框架,配置sqlSessionFatory -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
   <property name="configLocation" value="classpath:dal/mybatis-config.xml"/>
   <property name="dataSource" ref="dynamicDataSource"/>
   <property name="typeAliasesPackage" value="cn.followtry.domain"/>
   <!-- 配置扫描Mapper XML的位置 -->
   <property name="mapperLocations">
      <array>
            <value>classpath*:dal/mappers/*.xml</value>
            <value>classpath*:mybatis/mapper/**/*.xml</value>
            <value>classpath*:mybatis/mapper/*.xml</value>
      </array>
   </property>

</bean>
```


# 原理过程

1. 如 `Mybatis`或`hibernate`等方式配置数据源时将动态数据源配置进去，
2. 然后在动态数据源`dynamicDataSource`中配置多个数据源,如`defaultDataSource`,`default2DataSource`。在`AbstractRoutingDataSource`中设置属性`resolvedDataSources`保存多个数据源，
3. 在每次实际请求中，通过判断`Method`上的`@ReadMaster`注解来将`lookupkey`设置在上下文`DynamicDataSourceHolder`中。
4. 然后每次访问数据源获取连接`getConnection`时，都通过`determineTargetDataSource`来检测目标数据源。而目标数据源 key 的指定是通过实现`determineCurrentLookupKey`方法来完成的。







