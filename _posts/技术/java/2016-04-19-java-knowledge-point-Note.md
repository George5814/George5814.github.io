---
layout: post
title: java知识点札记
category: 技术
tags: java
keywords: 
description: 
---

{:toc}

### 1. 密码存储

	密码加密存储并只保存密码的md5摘要

### 2. int和Integer默认值

	int的默认值为0，而Integer的默认值为null
	
### 3.java 中添加跨域

	HttpServletResponse response
	response.addHeader("Access-Control-Allow-Origin","*");

### 4.只有非private方法才可以被覆盖

	虽然编译器不会报错，但也不会按照我们期望的来执行。因此在导出类中，对于基类中的private方法，最好采用 不同的名字。
	

### 5.当sub对象转型为Super引用时，任何域访问操作都将由编译器解析，因此不是多态的。

sub对象默认的域并不是super的域，而是自己的域。因此为了得到super域，需要显式的指明super.field

**注意:**

	虽然super和sub中定义相同的field没有问题，但相同的field名称还是容易产生混淆，因此不推荐使用。
	
### 6.除了少许的限制，enum就是普通的类

- 枚举的自定义方法必须放在枚举变量之后，否则会报错；

- 枚举不能继承其他的类和枚举（因为已经默认隐含继承了java.lang.Enum），但可以实现接口；

```java
public interface IntEnum {
	
	int hello();
	
	enum IntEnum2 implements IntEnum{
		;//一定要有
		@Override
		public int hello() {
			return 0;
		}
	}
	
	enum IntEnum3 implements IntEnum{
		;//一定要有
		@Override
		public int hello() {
			return 0;
		}
	}
}
``` 
	
**创建接口是使得enum子类化的唯一办法**






