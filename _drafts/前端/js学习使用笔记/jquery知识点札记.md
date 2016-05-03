## JQuery 使用笔记
### 1.jquery 设置当前元素样式和同级其他元素样式
	$(this).css('background','#FFFFFF');
	//设置当前元素的同级元素样式
	$(this).siblings().css('background','#F5F7FA');
### 2.删除元素
	$("elem").remove();
### 3.显示和隐藏元素
	$("elem").hide();
	$("elem").show();
### 4.清空子元素
	$("elem").empty();//子元素置为空
### 5. div 赋值
```js
	$("#elem").text("hello")//赋值成功
	$("#elem").html("hello")//赋值成功
	$("#elem").val("hello")//赋值失败
```
### 6. 动态创建元素
```html
	var elem = $("<div id='value"+i+"'>"+ percent +"%</div>");
    $("#id").append(elem);
```

### 7. js数学操作
#### 7.1 js对小数取整操作
	Math.round(value);
	
#### 7.2 js保留指定位数的小数
```js
		var num = value[i].value / totleNum * 100;
		//n是要保留的小数的个数
    	var percent = num.toFixed(n);
```
### 9.value的n次方
	Math.pow(value,n);