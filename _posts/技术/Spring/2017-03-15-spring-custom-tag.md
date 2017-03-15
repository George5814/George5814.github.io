---
layout: post
title: 自定义Spring tag标签
category: 技术
tags:  Spring
keywords: 
description: 深入学习定制Spring标签
---

{:toc}

## 思路及实战

以自定义`java.text.SimpleDateFormat`的标签为例。

1. 编写schema(xsd文件)

该文件是用于声明可用的xml命名空间及元素的地方，文件暂时命名为`myns.xsd`。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsd:schema xmlns="http://followtry.cn/schema/myns"
            xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            xmlns:beans="http://www.springframework.org/schema/beans"
            targetNamespace="http://followtry.cn/schema/myns"
            elementFormDefault="qualified"
            attributeFormDefault="unqualified">
    <xsd:import namespace="http://www.springframework.org/schema/beans"/>
    <!---->
    <xsd:element name="dateformat">
        <xsd:complexType>
            <xsd:complexContent>
                <xsd:annotation></xsd:annotation>
                <xsd:extension base="beans:identifiedType">
                    <xsd:attribute name="lenient" type="xsd:string"/>
                    <xsd:attribute name="pattern" type="xsd:string" use="required"/>
                </xsd:extension>
            </xsd:complexContent>
        </xsd:complexType>
    </xsd:element>
</xsd:schema>
```

该文件存放在`src/main/resources`的`META-INF`目录下。



2. 编写NameSpaceHandler

该类是在Spring启动后，调用前注册指定的XML元素解析器。

```java
package cn.followtry.custom.spring.tag;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by followtry on 2017/3/15.
 */
public class SimpleDateFormatNameSpaceHander extends NamespaceHandlerSupport{
	@Override
	public void init() {
		//此处可以注册多个Bean的解析器，在需要解析命名空间中的元素时将会代理的解析器
		/*
			这样清晰的关注点的分离可以使得一个Namespacehandler可以处理解析命名空间内所有自定义元素的编排。
			它会将繁重的xml解析工作代理给指定的BeanDefinitionParsers，
			这意味着每个BeanDefinitionParserker将会包含只解析单个自定义元素的逻辑。
		 */
		registerBeanDefinitionParser("dateformat",new SimpleDateFormatBeanDefinitionParser());
	}
}

```

`NamespaceHandler`相当简单，只有三个方法`init`、`parse`、`decorate`;

- `init`:是在handler使用前被Spring调用。

- `parse`:在Spring遇到顶层元素时被调用，可以将注册BeanDefinition本身或者返回一个BeanDefinition。

- `decorate`:在Spring遇到不同命名空间的属性或者内部元素时调用。用来装饰一个或多个BeanDefinition。

示例代码中的`SimpleDateFormatBeanDefinitionParser`类是自定义的BeanDefinitionParser，将其注册进Spring中。


3. 编写BeanDefinitionParser(实际解析自定义xml元素的位置)

在Handler遇到xml元素匹配到指定的解析器后，用来处理自定义xml元素的地方，该处拥有主要的解析逻辑。

```java
package cn.followtry.custom.spring.tag;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;

/**
 * 当NameSpaceHander遇到xml元素类型映射到了指定的bean定义解析器时，对应的BeanDefinitionParser就会被使用。
 * 在解析时，我们可以访问到XMl元素，因此我们可以解析到自定义的xml的内容
 * Created by followtry on 2017/3/15.
 */
public class SimpleDateFormatBeanDefinitionParser extends AbstractSingleBeanDefinitionParser{

	//指定需要实例化的bean的name
	@Override
	protected Class<?> getBeanClass(Element element) {
		return SimpleDateFormat.class;
	}

	//解析命名空间下的自定义xml元素
	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		String pattern=element.getAttribute("pattern");
		builder.addConstructorArgValue(pattern);

		String lenient=element.getAttribute("lenient");
		if (StringUtils.hasText(lenient)){
			builder.addPropertyValue("lenient",lenient);
		}
	}
}
```

4. 配置spring.schema

该文件配置在`src/main/resources`下的`META-INF`文件夹下。

示例：

```
http\://followtry.cn/schema/myns.xsd=META-INF/myns.xsd
```

指定命名空间的xsd文件和jar包中文件所在位置，其中key为在引用时的xsd文件位置的声明(`http\://followtry.cn/schema/myns.xsd`)，value为xsd文件在本jar包中的位置（`META-INF/myns.xsd`）。

因为":"在java属性格式化中是分隔符，因此需要将其使用`\`转义。

5. 配置spring.handlers

该文件配置在`src/main/resources`下的`META-INF`文件夹下

示例：

```
http\://followtry.cn/schema/myns=cn.followtry.custom.spring.tag.SimpleDateFormatNameSpaceHander
```

该键值对中的键(`http\://followtry.cn/schema/myns`)xsd文件中的`targetNameSpace`相同。
值(`cn.followtry.custom.spring.tag.SimpleDateFormatNameSpaceHander`)为该命名空间的处理器，在遇到匹配的xml元素会调用相应的元素解析器。


6. 配置pom中build resources文件夹

在maven打包时将resources中的配置文件打包进jar包中。	

为了将`src/main/resources`下的内容打包进jar包中，需要在`pom.xml`中相应位置添加如下配置：

```xml
<project>
	.......
    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
            </resource>
        </resources>
    </build>
</project>
```


7. 应用该自定义spring的tag

如果在另一个项目中测试，则将自定义元素所在jar包引入。

建立测试配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:myns="http://followtry.cn/schema/myns"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd
	http://followtry.cn/schema/myns
	http://followtry.cn/schema/myns.xsd">

    <myns:dateformat id="dateformat" pattern="yyyy-MM-dd HH:mm" lenient="true"/>
</beans>
```

建立测试类

```java
package cn.followtry.custom.springtag;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;

/**
 * Created by followtry on 2017/3/13.
 */
public class test {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("spring-text.xml");
		SimpleDateFormat sdf = (SimpleDateFormat) context.getBean("dateformat");
		System.out.println(sdf.getTimeZone());
	}
}
```

如果测试结果返回`timeZone`的信息，则说明自定义元素已经初步成功。


