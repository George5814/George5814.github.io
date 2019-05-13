---
layout: post
title: mybatis处理枚举类
category: 技术
tags:  Mybatis
keywords: 
description: 自定义实现mybatis中对枚举的处理逻辑，完善mybatis对自定义枚举支持的不足。
---

{:toc}

## mybatis自带对枚举的处理类

- `org.apache.ibatis.type.EnumOrdinalTypeHandler<E>` :该类实现了枚举类型和Integer类型的相互转换。
但是给转换仅仅是将对应的枚举转换为其索引位置，也就是"ordinal()"方法获取到的值。对应自定义的int值，该类无能为力。

- `org.apache.ibatis.type.EnumTypeHandler<E>`:该类实现了枚举类型和String类型的相互转换。
对于想将枚举在数据库中存储为对应的int值的情况，该类没办法实现。

基于以上mybatis提供的两个枚举处理类的能力有限，因此只能自己定义对枚举的转换了。

## 自定义mybatis的枚举处理类`EnumValueTypeHandler`

该类需要继承`org.apache.ibatis.type.BaseTypeHandler<E>`，然后在重定义的方法中实现自有逻辑。

```JAVA
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.MappedTypes;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * 处理实现了{@link EsnBaseEnum}接口的枚举类
 * @author followtry
 * @time 2016年8月16日 下午8:06:49
 * @since 2016年8月16日 下午8:06:49
 */
 //在 xml 中添加该 TypeHandler 时需要使用该注解
@MappedTypes(value = {
        QcListTypeEnum.class,
        SellingQcBizTypeEnum.class
})
public class EnumValueTypeHandler<E extends EsnBaseEnum> extends BaseTypeHandler<E> {

	private Class<E> type;

	private final E[] enums;

	public EnumValueTypeHandler(Class<E> type) {
		if (type == null) {
			throw new IllegalArgumentException("Type argument cannot be null");
		}
		this.type = type;
		this.enums = type.getEnumConstants();
		if (this.enums == null) {
			throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
		}
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		//获取非空的枚举的int值并设置到statement中
		ps.setInt(i, parameter.getValue());
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		int i = rs.getInt(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			try {
				return getEnumByValue(i);
			} catch (Exception ex) {
				throw new IllegalArgumentException(
						"Cannot convert " + i + " to " + type.getSimpleName() + " by ordinal value.", ex);
			}
		}
	}

	/**
	 * 通过枚举类型的int值，获取到对应的枚举类型
	 * @author jingzz
	 * @param i
	 */
	protected E getEnumByValue(int i) {
		for (E e : enums) {
			if (e.getValue() == i) {
				return e;
			}
		}
		return null;
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		int i = rs.getInt(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			try {
				return getEnumByValue(i);
			} catch (Exception ex) {
				throw new IllegalArgumentException(
						"Cannot convert " + i + " to " + type.getSimpleName() + " by ordinal value.", ex);
			}
		}
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		int i = cs.getInt(columnIndex);
		if (cs.wasNull()) {
			return null;
		} else {
			try {
				return getEnumByValue(i);
			} catch (Exception ex) {
				throw new IllegalArgumentException(
						"Cannot convert " + i + " to " + type.getSimpleName() + " by ordinal value.", ex);
			}
		}
	}

}
```

该处理器是处理继承了`EsnBaseEnum`接口的枚举类，因为该接口中定义了获取自定义int值的方法。

如果在 mybatis 的 xml 中配置 该 typehandler，则需要添加注解`@MappedTypes`。在添加 typeHandler 注册时使用具体的实现类注册。

配置文件如下：

```xml
<typeAliases>
<!-- 为自定义的 TypeHandler 指定别名，在 sql 的 xml 中即可使用别名访问了 -->
    <typeAlias type="cn.followtry.mybatis.EnumValueTypeHandler" alias="enumValueTypeHandler"/>
</typeAliases>
<typeHandlers>
    <!--处理枚举的值转换-->
    <typeHandler handler="cn.followtry.mybatis.EnumValueTypeHandler"/>
</typeHandlers>
```


自定义的 Enum 需要实现的接口

```java
/**
 * @author jingzz
 * @time 2016年8月17日 上午9:22:46
 * @since 2016年8月17日 上午9:22:46
 */
public interface EsnBaseEnum {
	
	public int getValue();
}
```

而实现了`EsnBaseEnum`的枚举示例如下：

```java

/**
 * 同步的类型
 * @author jingzz
 * @time 2016年8月3日 上午11:13:06
 */
public enum SyncType implements EsnBaseEnum {
	
	DEPT(3),//部门
	PERSON(1);//人员
	
	private int value;
	
	private SyncType(int value) {
		
		this.value = value;
	}
	
	@Override
	public int getValue(){
		return this.value;
	}
}
```

只有在实现了`EsnBaseEnum`的接口，`EnumValueTypeHandler`才能通过接口的getValue方法获取到对应枚举的值。

到此，对于枚举的简单处理逻辑已经实现完成了，接下来就是如何配置来使用该自定义枚举处理逻辑

## 配置对枚举的处理

首先，mybatis中对于处理逻辑的设置是在sql的映射文件中，如`EsnSyncLogMapper.xml`。

关键的设置枚举处理的位置如下：

```xml
<resultMap id="BaseResultMap" type="com.test.dao.model.EsnSyncLog" >
    <id column="id" property="id" jdbcType="INTEGER" />
    <result column="sync_time" property="syncTime" jdbcType="TIMESTAMP" />
    <result column="total_num" property="totalNum" jdbcType="INTEGER" />
    <result column="success_total_num" property="successTotalNum" jdbcType="INTEGER" />
    <result column="type" property="type" typeHandler="com.test.common.EnumValueTypeHandler" />
    <result column="msg" property="msg" jdbcType="VARCHAR" />
  </resultMap>
```

其中type列设置了属性**typeHandler**，其值为自定义的枚举处理逻辑。

而在具体sql中也需要使用**typeHandler**属性，如：

```xml
 <if test="type != null" >
    #{type, typeHandler=com.test.common.EnumValueTypeHandler},
  </if>
```

在mybatis处理到该位置时，就会调用typeHandler指定的处理类来处理枚举类型。
