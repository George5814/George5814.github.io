---
layout: post
title: [转]由ES规范知：深入理解this
category: 技术
tags: MongoDB
keywords: 
description: 
---

{:toc}



# 一. this的来源

	this是JavaScript的关键字，它最初应该是从Java、C++等面向对象的语言中借鉴来的。
	
	比如，在Java中没有函数只有方法，this只能用在类的成员方法或构造方法中，表示当前实例对象。所以在Java中this的含义很明确，在其他语言中也类似。
	
	然而到了JavaScript中，this变得复杂了起来：不仅函数内可以用，在所有函数外(全局上下文中)也可以用；函数中的this的含义在函数声明时无法确定，要到运行期才能确定，而且与调用函数的方式有关；代码是否是严格模式也会影响this的取值。

# 二. this到底是什么

	在ES5.1中，有一个所谓执行上下文(Execution Context，EC)的概念，简单的说就是JS引擎的执行进入到某块代码区域时，为该代码区域建立的上下文对象，主要用来记录该区域中声明的变量、函数等。

EC有三个重要组成部分：VE、LE和ThisBinding。前两个是词法环境，暂且不管。第三个 ThisBinding就是指在该代码区域的this的值。

可见，this是跟某块代码区域关联的。而在JS中，代码区域有三种：

- global代码
- function代码
- eval代码。


此文中主要讨论前两种。

# 三. 全局代码区域中的this

全局代码区域是所有函数之外的区域。在此区域中的this就是指全局对象window(在Node.js中是global)。

示例：
```js
var num = 123;
console.log(this.num); // 输出123
```
参考：[http://es5.github.io/#x10.4.1.1](http://es5.github.io/#x10.4.1.1)

# 四. 函数代码区域中的this

函数代码区域是指某个函数内的代码，但是不包括它所嵌套的函数内的代码。从我们可以看出：

this是与包裹它的且离它最近的函数相关的，this既不能穿透到外部的函数，也不能穿透进内部的函数。
示例：
```js
btn.addListener('click', function() {    
    var that = this;
    dosth(function() {        
        console.log(that.name);
    });
});
```
通常每个函数中的this是不同的，内部函数可以引用外部函数的局部变量，但是不能直接引用外部函数的this。通过将外部函数的this赋值给一个局部变量可以解决这个问题。

函数内的this的具体函数比较复杂，主要与调用这个函数的方式有关。主要包括以下情况：

## 1. 直接调用时

示例：
```js
var num = 123;
function fn() {    
    console.log(this.num);
}
function fn2() {    
    "use strict"//严格模式
    console.log(this.num);
}

fn(); // 输出123
fn2(); // 报错
```
直接调用函数时，如果是在严格模式下，this会被设为undefined；如果是在非严格模式下，this会被设为全局对象window。

## 2. 作为方法调用时

示例：
```js
var student = {
    name: 'Tom',
    sayName: function() {       
         console.log(this.name);
    };
};

student.sayName(); // 输出Tom
```
作为方法调用时，this指方法所属的对象。

参考：[http://es5.github.io/#x10.4.3和 http://es5.github.io/#x11.2.3](http://es5.github.io/#x10.4.3和 http://es5.github.io/#x11.2.3)

## 3. call和apply方法：调用时指定this

除了上述两种固定的情况外，Javascript提供了一种可以随心所欲地根据需要更改函数中this方法。即使用函数对象的call或apply方法来调用函数，显然这种方式给编程带来了极大的灵活性。

示例：
```js
function fn() {    
    var args = Array. prototype. slice.call(arguments, 1);    
    console.log(args);
}
fn(1, 2, 3); // 输出[2, 3]
```
这种方法常用的场景就是：把一个对象的方法"借"给另一个具有类似结构的对象使用。

## 4. bind方法：重新绑定函数的this

与call和apply不同，bind方法是在调用前就把函数内的this绑定了，而且一旦绑定就不能再改变。实际上bind方法返回了一个原函数的新版本。

示例：
```js
function fn() {    
    console.log(this.age);
}

var fn2 = fn.bind({age: 18}); 
fn2() // 输出18
fn2.call({age: 25}) // 输出18
```
通过bind得到的函数，不论用哪种方式调用，它的this都是相同的。

**参考：[http://es5.github.io/#x15.3.4.5](http://es5.github.io/#x15.3.4.5)**

## 5. 构造函数中的this

当构造函数通过new操作符来调用时，this表示正在创建的对象。

**参考：[http://es5.github.io/#x11.2.2](http://es5.github.io/#x11.2.2)**

## 6. 回调函数的this

回调函数也只不过是函数的一种，实际上这种情况已经包含在了前面提到的情况中。但是由于回调函数的调用者往往不是我们自己，而是回调函数的接收者，即某个库或框架、甚至是JS运行时环境。这样一来，回调函数在中的this是什么就与对方的调用方式有关了，因此变得比较复杂，所以单独拿出来讨论一下。

- 情况1：没有明确作用对象的情况下，通常this为全局对象

例如setTimeout函数的回调函数，它的this就是全局对象。你如果希望自己指定this，可以通过bind函数等方法。

- 情况2：某个事件的监听器回调函数，通常this就是事件源对象

例如：

`button.addEventListener('click', fn)`fn的中的this就是事件源button对象。

- 情况3：某些API会专门提供一个参数，用来指定回调函数中的this

例如，我们可以重新设计一个可以指定this的setTimeout：

```javascript

function setTimeoutExt(cb, period, thisArg) {
    setTimeout(function() {
        cb.call(thisArg);
    }, period);
}
```

另外，在ExtJS中也大量使用了可以指定this的接口。

# 五. 重新审视

	this，除了面向对象语言中通用的那两种情况(方法和构造函数)外，在JavaScript 中还提供了更多的使用方式，虽然这让JS中的this变得相对难以掌握，但是它使得JS更加丰富更加灵活。我们可以把this看成函数的一个特殊的隐含的参数，这个参数代表函数正在操作的主体。