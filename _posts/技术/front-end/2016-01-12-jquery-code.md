---
layout: post
title: jQuery 部分知识点片段
category: 前端
tags:  Javascript
keywords: 
description: 
---

{:toc}


## 1.jquery 设置当前元素样式和同级其他元素样式

```js
    //设置当前元素的背景色
    $(this).css('background','#FFFFFF');
	//设置当前元素的同级元素背景色
	$(this).siblings().css('background','#F5F7FA');
```	

## 2.获取当前页面url

```js
    window.location.href
```

## 3.获取页面路径（url除去协议，host和port的部分）

```js
    window.location.pathName
```

## 4.获取当前URL的协议

```js
    window.location.protocol
```

## 5.js回车键事件

```js
        $("body").on('keydown',function(){
            //13是回车键的keycode
	    if (event.keyCode == 13) {
	        //自定义方法
	        startLogin();
            }
	});
```	

## 6.绑定事件

```html
<a id="mytest" href="javascript:;">解绑事件</a>
```

```js
    $("#mytest").bind('click',function(){
	    alert()
	    $(this).unbind('click');
	});
```

  结果：

    第一次单击弹出提示框，其他时候单击无反应

## 7.设置元素属性

```js
$("textarea").attr('readonly',false);
$("textarea").attr('readonly',true);
```

## 8.切换属性

```js
$(this).toggleClass("hidden");
```


## 9.解析Unicode编码的字符

```js
window.decodeURIComponent("\u65e0\u6570\u636e")
```

结果为：`无数据`


## 禁止animate的动画效果积累多次执行

在执行前先用stop停止掉正在执行的动画效果，然后重新开始执行动画。

```
$(".partner-mid a").hover(function(e){
	$(this).find("span").stop().animate({
		height:"86px",
		"font-size":"18px",
		"line-height":"86px",
	},500)
}
,function(){
	$(this).find("span").stop().animate({
		height:"0px"
	})
},10)
```

## 禁用和启动input[type=file]的文件选择功能

对`<input type="file" id="myfile"/>`

禁用

```
$("#myfile").attr('disabled','disabled');
```

启用

```
$("#myfile").attr('disabled',true);

```
