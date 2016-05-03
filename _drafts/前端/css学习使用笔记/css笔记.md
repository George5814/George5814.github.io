### 1. transition：从一个属性值平滑的过渡到另一个属性值
```CSS
	transition：/*<过渡属性名称> <过渡时间> <过渡模式[ease,liner]>*/
	-webkit-transition
	-ms-transition
	-moz-transition
	-o-transition
```
示例：
```HTML 
	<div id="block"></div>

	<style type="text/css">
		#block{
			width: 10px;
			height: 10px;
			background-color: #69c;
			margin: 0 auto;
			transition:background-color 2s;
			transition:width 2s ease-in,height 2s ease-in-out;
		}
		#block:hover{
			background-color: red;
			height: 400px;
			width: 400px;
		}
		
	</style>
```
关键点：animation：通过关键帧技术实现更加复杂的技术


### 2.em,px,rem的区别
	px：像素是相对于显示器的分辨率而言的。
	
	em：是相对于当前对象内文本的尺寸而言的,如果没有被设置字体尺寸，则按照浏览器默认尺寸，字高默认值为16px。
	未经调整的字体1em=16px，但为了简化font-size的计算，在body选择器中声明font-size=62.5%;那么px的值除以10就是em的值了。
	如果有中文，则需要让font-size=63%；
	在不同屏幕分辨率的浏览中，使用em比px更好一些。
	em特点：em值不是固定的；em会继承父元素字体的大小，如果父元素已经设置了合适的em，如1.2em，在子元素中设置字体为1.2em时只需要设置为1em即可。
	
	rem:
	在css3中引入了rem的单位概念，从名称中可以看出rem与em相似，但不同是什么呢，是r也就是root的意思。就是说rem是相对于根元素的字体大小而言的。
	相对于em来说，rem只需要知道根元素的字体大小就可以设置自己的字体大小了，但em却需要知道父元素的字体大小，然后计算当前字体大小的设置。
	
### 3. 文字换行
```CSS
p {	
    word-break: break-word;/*按照单词切分换行*/ 
    word-wrap: normal;
    white-space: initial;
    text-overflow: ellipsis;
}

p { /*文字不换行*/
    word-break: break-word;
    word-wrap: break-word;
    white-space: nowrap;
    text-overflow: ellipsis;
}
```
### 4.设置页面自适应@media元素
设置最大宽为800px时的样式，适用于适配不同分辨率的设备
```css
@media screen and (max-width: 800px){
	.right-box p {
	    word-break: break-word;
	    word-wrap: break-word;
	    white-space: nowrap;
	    text-overflow: ellipsis;
	}
}
```
### 5.文字超出后使用省略号代替
设置文字超出元素即使用省略号代替，必须设置width不大于屏幕上可以显示的位置， 在text-overflow属性之前设置overflow:hidden;
```css
p{ 
	width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;	
}
```

