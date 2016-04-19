---
layout: post
title: 由ES规范知undefined可以被赋值，而null不可以
category: 前端
tags:  javascript
keywords: 
description: 
---

{:toc}


## 提出问题:

如下代码：

```js
undefined = 123;
null = 123; // 报错：Uncaught Reference Error
```

- 第一条语句可以执行，尽管赋值并没有成功；
- 第二条语句报错。

这是为什么呢？undefined和null有什么区别？

## 解决问题:

读过ES规范后，发现原来是这样的：

	undefined、NaN和Infinity都是全局对象window的属性。既然是属性，当然可以赋值。然而这三个属性又是不可写的属性，即它们的的内部特性[[writable]]为false，所以赋值无效。
	null是一个字面量(literal)，准确地说叫做Null字面量。与true和false类似。它们都属于JavaScript的保留字。换句话说它们都是值，与数字值123、字符串值"foobar"一样，当然不能被赋值了。
### 相关知识:

####  1.既然undefined只是一个属性，并不是语言的保留字，那么它是否可以用来作为变量的名字？当然可以，你完全可以自定义一个叫做undefined的变量或者函数，但是注意不要把它放到全局作用域。例如：

```js
function foo() {     
    var undefined = 10;
    console.log(undefined);
}
foo(); // 打印10
```

#### 2.通过ES5新增的方法Object.getOwnPropertyDescriptor方法，可以证明undefined是window对象的只读属性：

```js
/* 输出：Object {value: undefined, writable: false, 
	enumerable: false, configurable: false} 
*/
Object.getOwnPropertyDescriptor(window, 'undefined');
```

**在严格模式下，给undefined赋值会报错。因为严格模式下，禁止给对象的只读属性赋值。**
**null虽然号称是Null这种原始类型可以取的唯一值，然而：`typeof null; // 输出"object"`**