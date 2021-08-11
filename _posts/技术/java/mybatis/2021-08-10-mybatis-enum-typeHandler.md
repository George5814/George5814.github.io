---
layout: post
title:  Mybatis技术内幕--SpringBoot下自定义枚举的TypeHandler及原理
category: 技术
tags: mybatis Java springboot
keywords: mybatis Java
description: 
date: 2021-08-10
author: followtry
published: true
---

## 原比较简单的方式自定义TypeHandler

<http://followtry.cn/2016-08-17/mybatis-handler-enum.html>


## 背景

因Mybatis默认的Enum的TypeHandler仅支持`org.apache.ibatis.type.EnumTypeHandler`或者`org.apache.ibatis.type.EnumOrdinalTypeHandler`。但因为很多业务中定义类型使用的是枚举，而数据库中存储的字段是int或varchar类型。一般不使用枚举默认的name或者ordinal作为数据库内的值存储。因此在很多使用使用mybatis存储枚举时都需要手动取出枚举的int值（以取出int类型自定义code属性为例），在后续维护起来不容易。因此想通过自定义的枚举类型来实现对int和映射枚举之间的双向转换。


## 自定义EnumTypeHandler实践方案

如果使用自定义的枚举处理器，需要枚举都实现一个固定的接口，通过该接口方法来获取int值

### 自定义枚举需要实现的接口

接口名为BaseBizEnum

```java
/**
 * @author followtry
 * @since 2021/8/9 3:30 下午
 */
public interface BaseBizEnum {

    Integer getCode();
}
```

### 实现接口的自定义枚举

自定义枚举为AgreementType，实现了BaseBizEnum，其`getCode`方法被标记上`@Override`注解

```java
import com.google.common.collect.Maps;
import lombok.Getter;
import java.util.Map;
import java.util.Optional;

public enum AgreementType implements BaseBizEnum{
    /***/
    QUICK_PAY(1,"免密支付"),
    ;

    private final Integer code;
    @Getter
    private final String desc;
    private static Map<Integer,AgreementType> itemMap = Maps.newHashMap();
    static {
        for (AgreementType typeEnum : AgreementType.values()) {
            itemMap.put(typeEnum.getCode(),typeEnum);
        }
    }
    AgreementType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    //重写了接口BaseBizEnum的方法
    @Override
    public Integer getCode() {
        return code;
    }
    public static AgreementType ofNullable(Integer code) {
        return itemMap.get(code);
    }
}
```

有了自定义枚举后，就需要有自定义枚举的类型来解析该枚举

### 定义枚举类处理器

对于同类型的枚举，可以定义基类的处理类实现通用的逻辑。

```java
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author followtry
 * @since 2021/8/9 3:38 下午
 */
public class BizEnumTypeHandler<E extends BaseBizEnum> extends BaseTypeHandler<E> {

    private Class<E> type;

    //初始化时定义枚举和code的映射关系
    private final Map<Integer,E> enumsMap = new HashMap<>();

    public BizEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        for (E enumConstant : type.getEnumConstants()) {
            enumsMap.put(enumConstant.getCode(),enumConstant);
        }
        if (this.enumsMap.size() == 0) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
    }

    //在请求Sql执行时转换参数
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E e, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i,e.getCode());
    }

    //处理返回结果
    @Override
    public E getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        if (resultSet.wasNull()) {
            return null;
        }
        int code = resultSet.getInt(columnName);
        return getEnum(code);
    }

    private E getEnum(Integer code) {
        try {
            return getEnumByValue(code);
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Cannot convert " + code + " to " + type.getSimpleName() + " by ordinal value.", ex);
        }
    }

    protected E getEnumByValue(Integer code) {
        return enumsMap.get(code);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        if (resultSet.wasNull()) {
            return null;
        }
        int code = resultSet.getInt(columnIndex);
        return getEnum(code);
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        if (callableStatement.wasNull()) {
            return null;
        }
        int code = callableStatement.getInt(columnIndex);
        return getEnum(code);
    }
}
```

现在枚举处理的基类有了，就需要通过继承该基类实现自定义枚举的处理类

### 创建AgreementTypeEnumTypeHandler类

```java
import com.autonavi.aos.tmp.api.enums.AgreementType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

 //指定处理的映射枚举类的class
@MappedTypes(value = {AgreementType.class})
//指定返回结果时哪些jdbc类型的值需要转换
@MappedJdbcTypes(value = {JdbcType.INTEGER,JdbcType.TINYINT,JdbcType.SMALLINT})
public class AgreementTypeEnumTypeHandler extends BizEnumTypeHandler<AgreementType>{
    //在当前类实例化时即给父类的构造方法指定枚举类
    public AgreementTypeEnumTypeHandler() {
        super(AgreementType.class);
    }
}
```

定义完如上的代码后，还需要Mybatis在初始化时能将其扫描到并注册进Mybatis的TypeHandler注册器才能实现解析。

### 扫描自定义的TypeHandler类

因为使用的是SpringBoot和Mybatis集成的方式，所以在application.properties文件中需要指定扫描的目录，以便能识别到`AgreementTypeEnumTypeHandler`.


```xml
mybatis.type-handlers-package=cn.followtry.typehandler
mybatis.type-aliases-package=cn.followtry.typehandler
mybatis.configuration.map-underscore-to-camel-case=true
```

如上步骤完成后，就可以实现枚举字段在存储进DB前，自动转换为int类型。从db中查询出的对应字段，自动转为枚举类型。


### 示例Sql配置

#### 建表语句

```sql
CREATE TABLE `test_agreement_info` (
	`id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
	`agreement_type` tinyint NULL COMMENT '协议类型',
	`name` varchar(100) NULL COMMENT '协议名称',
	PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET=utf8mb4 COMMENT='协议信息';
```

#### Mybatis的Mapper接口

```java
import cn.followtry.AgreementType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface TestAgreementInfoMapper {

    @Insert("insert into test_agreement_info(name,agreement_type) value(#{name},#{agreementType})")
    boolean insert(@Param("name") String name, @Param("agreementType") AgreementType agreementType);

    @Select("select * from test_agreement_info where name = #{name}")
    List<TestAgreementModel> selectByName(@Param("name") String name);
}
```

#### 测试Controller代码

```java
@RestController
@RequestMapping(value = "/ws/tc/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TestController2 {
    @Autowired
    private TestAgreementInfoMapper testAgreementInfoMapper;
    @GetMapping(value = "insertTest")
    public Object insertTest(String name, AgreementType agreementType) {
        System.out.println("name="+name+",agreementType="+agreementType.name());
        return testAgreementInfoMapper.insert(name,agreementType);
    }
    @GetMapping(value = "getTest")
    public Object getTest(String name) {
        return testAgreementInfoMapper.selectByName(name);
    }
}
```

服务启动起来后通过接口调用分别插入和查询数据

```http
http://localhost:8080/ws/tc/test/insertTest?name=zhangsan&agreementType=QUICK_PAY
http://localhost:8080/ws/tc/test/getTest?name=zhangsan
```

以上代码会将插入时的QUICK_PAY转为1存储在db中，查询时会将1转为枚举AgreementType的实例QUICK_PAY

**下面的内容默认读者已了解SpringBoot的AutoConfiguration机制，了解Mybatis的加载机制，了解Mybatis的Configuration和SQLSessionFactory的初始化过程。**

## 原理解析


在应用启动时，SpringBoot会驱动Mybatis进行初始化，并将扩展的枚举类加载进Mybatis的内核机制中。

在Sql执行时，Mybatis会动态将枚举参数替换为int类型，并将返回结果中的对应int类型转换为对应的枚举

### 首先，初始化阶段

#### 加载`MybatisAutoConfiguration`

SpringBoot会自动加载`MybatisAutoConfiguration`类，按照Spring的规则会将该类进行Bean的转换和注册。

Spring通过`MybatisAutoConfiguration#sqlSessionFactory`方法初始化`SqlSessionFactoryBean`的参数设置，此时会将配置的参数解析设置给其属性。

参数如下

```xml
mybatis.type-handlers-package=cn.followtry.typehandler
mybatis.type-aliases-package=cn.followtry.typehandler
mybatis.configuration.map-underscore-to-camel-case=true
```

在方法的最后`SqlSessionFactoryBean`会调用`getObject`方法(Spring的FactoryBean机制)执行`SessionFactory`的实例化，此时会初始化默认配置和扫描已经配置的TypeHandler包`cn.followtry.typehandler`

```java
    if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
      factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
    }
```

在`SqlSessionFactoryBean#buildSqlSessionFactory`方法内，有如下代码片段将自定义的`AgreementTypeEnumTypeHandler`解析并实例化注册到注册器中。

```java
    if (hasLength(this.typeHandlersPackage)) {
      scanClasses(this.typeHandlersPackage, TypeHandler.class).stream().filter(clazz -> !clazz.isAnonymousClass())
          .filter(clazz -> !clazz.isInterface()).filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
          .filter(clazz -> ClassUtils.getConstructorIfAvailable(clazz) != null)
          .forEach(targetConfiguration.getTypeHandlerRegistry()::register);
    }
```

`scanClasses`方法是针对将指定包下的指定类型的子类都收集到候选的集合中。

`targetConfiguration.getTypeHandlerRegistry()::register`会将过滤后的`TypeHandler`子类即`AgreementTypeEnumTypeHandler`注册进容器。

注册代码如下

```java

  /**
  *  先检查`AgreementTypeEnumTypeHandler`上的MappedTypes注解。有注解的以注解注入。
  *  仅仅支持当前类上的注解，不支持父类上的。
     如果没有指定MappedTypes注解，则无法判断该处理器处理哪个枚举，只能在xml配置Sql时指定TypeHandler
  */
  public void register(Class<?> typeHandlerClass) {
    boolean mappedTypeFound = false;
    MappedTypes mappedTypes = typeHandlerClass.getAnnotation(MappedTypes.class);
    if (mappedTypes != null) {
      for (Class<?> javaTypeClass : mappedTypes.value()) {
        register(javaTypeClass, typeHandlerClass);
        mappedTypeFound = true;
      }
    }
    if (!mappedTypeFound) {
      register(getInstance(null, typeHandlerClass));
    }
  }
  public void register(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
    register(javaTypeClass, getInstance(javaTypeClass, typeHandlerClass));
  }
  
  //register方法参数上调用了本方法，在本方法内通过构造方法反射生成TypeHandler的实例
  public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
    if (javaTypeClass != null) {
      try {
          //如果存在带有class参数的构造方法，则使用其生成实例，否则使用无参构造方法生成实例
        Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
        return (TypeHandler<T>) c.newInstance(javaTypeClass);
      } catch (NoSuchMethodException ignored) {
        // ignored
      } catch (Exception e) {
        throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
      }
    }
    try {
        //因为本示例中的AgreementTypeEnumTypeHandler只有无参构造方法，因此只能通过此处代码生成实例
      Constructor<?> c = typeHandlerClass.getConstructor();
      return (TypeHandler<T>) c.newInstance();
    } catch (Exception e) {
      throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
    }
  }


  
  public <T> void register(Class<T> type, JdbcType jdbcType, TypeHandler<? extends T> handler) {
    register((Type) type, jdbcType, handler);
  }

  //注册最终会调用该方法将AgreementTypeEnumTypeHandler注册进typeHandlerMap中，typeHandlerMap本质是个Map，用来作为typeHandler的容器
  //而对于没有指定类型的TypeHandler，则注册进allTypeHandlersMap中，在sql配置中指定后才能使用。
  //typeHandlerMap容器的key为MappedTypes注解指定的枚举类，value为MappedJdbcTypes指定的jdbc类型和AgreementTypeEnumTypeHandler实例的映射
  private void register(Type javaType, JdbcType jdbcType, TypeHandler<?> handler) {
    if (javaType != null) {
      Map<JdbcType, TypeHandler<?>> map = typeHandlerMap.get(javaType);
      if (map == null || map == NULL_TYPE_HANDLER_MAP) {
        map = new HashMap<>();
        typeHandlerMap.put(javaType, map);
      }
      map.put(jdbcType, handler);
    }
    allTypeHandlersMap.put(handler.getClass(), handler);
  }
```

额外看下Mybatis默认加载的处理器（非全部）

```java
public TypeHandlerRegistry() {
    register(Boolean.class, new BooleanTypeHandler());
    register(boolean.class, new BooleanTypeHandler());
    register(JdbcType.BOOLEAN, new BooleanTypeHandler());
    register(JdbcType.BIT, new BooleanTypeHandler());

    register(Byte.class, new ByteTypeHandler());
    register(byte.class, new ByteTypeHandler());
    register(JdbcType.TINYINT, new ByteTypeHandler());

    register(Short.class, new ShortTypeHandler());
    register(short.class, new ShortTypeHandler());
    register(JdbcType.SMALLINT, new ShortTypeHandler());

    register(Integer.class, new IntegerTypeHandler());
    register(int.class, new IntegerTypeHandler());
    register(JdbcType.INTEGER, new IntegerTypeHandler());

    register(Long.class, new LongTypeHandler());
    register(long.class, new LongTypeHandler());

    register(Float.class, new FloatTypeHandler());
    register(float.class, new FloatTypeHandler());
    register(JdbcType.FLOAT, new FloatTypeHandler());

    register(Double.class, new DoubleTypeHandler());
    register(double.class, new DoubleTypeHandler());
    register(JdbcType.DOUBLE, new DoubleTypeHandler());
    ....
}

```

那初始化加载完后，什么时候设置参数将枚举转为int呢，接着往下看。

#### 执行Sql，将枚举参数转为int

重新将我们的Sql代码搬过来,根据Mybatis的机制，**如果参数使用`#{}`而非`${}`则会使用`PreparedStatement`，如果使用`${}`,则Mybatis中的类型处理器是不生效的，此处不注意的话可能会踩坑。**

```java
@Mapper
public interface TestAgreementInfoMapper {

    @Insert("insert into test_agreement_info(name,agreement_type) value(#{name},#{agreementType})")
    boolean insert(@Param("name") String name, @Param("agreementType") AgreementType agreementType);

    @Select("select * from test_agreement_info where name = #{name}")
    List<TestAgreementModel> selectByName(@Param("name") String name);
}

@Data
public class TestAgreementModel {
    private String name;

    private AgreementType agreementType;
}
```

对于Mybatis的参数处理接口`ParameterHandler`,其有默认实现`DefaultParameterHandler`,而参数的转换就是在`DefaultParameterHandler#setParameters`中完成的。

代码如下

```java
  @Override
  public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    //Mybatis通过Param注解解析到的参数映射，因为我们没在xml配置中指定jdbc类型和TypeHandler类型，因此在此方法内部获取TypeHandler是UnknownTypeHandler
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
      for (int i = 0; i < parameterMappings.size(); i++) {
        ParameterMapping parameterMapping = parameterMappings.get(i);
        if (parameterMapping.getMode() != ParameterMode.OUT) {
          Object value;
          String propertyName = parameterMapping.getProperty();
          if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
            value = boundSql.getAdditionalParameter(propertyName);
          } else if (parameterObject == null) {
            value = null;
          } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            value = parameterObject;
          } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            value = metaObject.getValue(propertyName);
          }
          //此处parameterMapping没有解析到设置的TypeHandler和JdbcType，因为压根就没设置，但是不妨碍Mybatis推断出使用的TypeHanler，对于没有配置TypeHandler的，Mybatis有默认实现UnknownTypeHandler
          TypeHandler typeHandler = parameterMapping.getTypeHandler();
          JdbcType jdbcType = parameterMapping.getJdbcType();
          if (value == null && jdbcType == null) {
            jdbcType = configuration.getJdbcTypeForNull();
          }
          try {
              //使用的UnknownTypeHandler来设置未获取到相关配置的参数
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
          } catch (TypeException | SQLException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
          }
        }
      }
    }
  }
```

在使用`UnknownTypeHandler`设置参数内部，还会根据java类型和jdbc类型再找一次当前枚举类型的处理器。代码如下

```java
public class UnknownTypeHandler extends BaseTypeHandler<Object> {
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
      throws SQLException {
    TypeHandler handler = resolveTypeHandler(parameter, jdbcType);
    //AgreementTypeEnumTypeHandler实例调用setParameter方法，就进入了我们自定义的AgreementTypeEnumTypeHandler中，按照我们的逻辑将枚举通过getCode方法转为了int类型
    handler.setParameter(ps, i, parameter, jdbcType);
  }

  //获取的具体枚举的类型处理器
  private TypeHandler<?> resolveTypeHandler(Object parameter, JdbcType jdbcType) {
    TypeHandler<?> handler;
    if (parameter == null) {
      handler = OBJECT_TYPE_HANDLER;
    } else {
        //如果jdbcType还是为null，则取其唯一的一个类型处理器实例。其实在Mybatis加载时多个jdbcType对应的TypeHandler实例是相同的
        //因此就能获取到AgreementTypeEnumTypeHandler实例
      handler = typeHandlerRegistry.getTypeHandler(parameter.getClass(), jdbcType);
      // check if handler is null (issue #270)
      if (handler == null || handler instanceof UnknownTypeHandler) {
        handler = OBJECT_TYPE_HANDLER;
      }
    }
    //此处返回的是AgreementTypeEnumTypeHandler实例
    return handler;
  }
}
```

对于Insert的枚举参数，通过上面的一系列代码的执行，已经实现了枚举和int类型的转换了。接下来再通过查询的方法，看下返回结果是怎么将int转换为枚举的。

### 执行查询Sql，将int转为枚举

在执行查询时，同样会先调用`SimpleExecutor#prepareStatement`方法，其内部调用`ParameterHandler#setParameters`实现参数的转换。
参数转换完后，执行sql并获取到了jdbc返回的结果ResultSet，然后将ResultSet转为指定的类型。

处理ResultSet结果有个`ResultSetHandler`接口，在默认实现`DefaultResultSetHandler#handleResultSets`接口中将ResultSet转换为实际的类型实例。

`DefaultResultSetHandler#getRowValue`方法是实际将一行Sql数据映射为结果类型的入口。

在`DefaultResultSetHandler#getRowValue`方法内部，会调用`DefaultResultSetHandler#createResultObject`方法获取映射类型的值（如果结果类型有TypeHandler，使用已定义的TypeHandler来获取）或者会使用默认的构造方法生成对象实例(对于自定义的对象)。对于本示例中的`TestAgreementInfoMapper.selectByName`方法的返回类型为`List<TestAgreementModel>`,因此每行数据都会新建一个`TestAgreementModel`对象实例，此时实例的属性都还是null。

```java
//创建行数据对象的核心方法如下
  private Object createResultObject(ResultSetWrapper rsw, ResultMap resultMap, List<Class<?>> constructorArgTypes, List<Object> constructorArgs, String columnPrefix)
      throws SQLException {
    final Class<?> resultType = resultMap.getType();
    final MetaClass metaType = MetaClass.forClass(resultType, reflectorFactory);
    final List<ResultMapping> constructorMappings = resultMap.getConstructorResultMappings();
    if (hasTypeHandlerForResultObject(rsw, resultType)) {
        //如果返回一列数据，如直接返回AgreementType，则会通过createPrimitiveResultObject方法直接获取到值
      return createPrimitiveResultObject(rsw, resultMap, columnPrefix);
    } else if (!constructorMappings.isEmpty()) {
      return createParameterizedResultObject(rsw, resultType, constructorMappings, constructorArgTypes, constructorArgs, columnPrefix);
    } else if (resultType.isInterface() || metaType.hasDefaultConstructor()) {
        //创建自定义的对象
      return objectFactory.create(resultType);
    } else if (shouldApplyAutomaticMappings(resultMap, false)) {
      return createByConstructorSignature(rsw, resultType, constructorArgTypes, constructorArgs);
    }
    throw new ExecutorException("Do not know how to create an instance of " + resultType);
  }
```

对于新生成的`TestAgreementModel`对象实例，mybatis会生成新的`MetaObject`实例，`MetaObject`中存有原对象以及对原对象类的属性和setter，getter方法以及构造方法的解析信息。解析信息存储在类`org.apache.ibatis.reflection.Reflector`中，访问路径比较深(`MetaObject.BeanWrapper.MetaClass.Reflector`)

通过`DefaultResultSetHandler#applyAutomaticMappings`实现自动映射，自动映射的机制使用了缓存，key为驼峰格式的属性名都转为大写，value为属性名。通过将列名的下划线去掉并都转为大写字母，就可以从缓存中查找对应的java对象属性名了。对于没有明确指定映射关系的属性，mybatis会将其映射关系都封装在`UnMappedColumnAutoMapping`中,`UnMappedColumnAutoMapping`的属性包括`column`,`property`,`typeHandler`,`primitive`。对于字段的映射关系，mybatis做了一级缓存，以避免在下次调用时再次解析，提高性能。

获取到以上的`UnMappedColumnAutoMapping`集合后，会循环执行该集合，并调用每个映射`UnMappedColumnAutoMapping`的`TypeHandler#getResult`获取到实际的值。如本示例中调用`BizEnumTypeHandler#getNullableResult`方法来执行我们的自定义的逻辑，获取到转换后的值。通过`metaObject`（其中包含有当前行java实例）的setValue方法，通过method的反射机制，将值赋值给原始的java实例。如此循环，直到`UnMappedColumnAutoMapping`集合循环完毕，完成了一行数据的赋值。然后对于下一行继续进行如上操作，直到数据都赋值完成。

在赋值完一行数据（`TestAgreementModel(name=zhangsan1, agreementType=QUICK_PAY)`）后需要将该结果暂存起来，通过调用`ResultHandler#handleResult`方法，将结果对象存储在ResultHandler中。而默认实现类`DefaultResultHandler`中使用`List<Object>`用来存储所有的行数据。每个ResultSet都有一个`DefaultResultHandler`实例，可以保证并发安全性。 

通过`DefaultResultHandler`的引用可以将数据带到外层赋值给返回结果接收者`List<Object> multipleResults`，通过`multipleResults`将数据带到了`SimpleExecutor`中，而`SimpleExecutor`中的结果会返回给`DefaultSqlSession`。SqlSession有`selectOne`和`selectList`,可以判断给应用的mapper接口方法返回一个对象结果还是List结果或者条件不符合而报错。


如上描述就完成了Sql查询的ResultSet结果转为JavaBean实例的过程。



