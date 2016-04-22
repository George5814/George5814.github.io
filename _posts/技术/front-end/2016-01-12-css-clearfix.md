---
layout: post
title: clearfix清除浮动进化史
category: 前端
tags:  CSS
keywords: 
description: 
---

{:toc}



## clearfix清除浮动
　　首先在很多很多年以前我们常用的清除浮动是这样的。

```css
.clear{
    clear:both;
    line-height:0;
}
```

	现在可能还可以在很多老的站点上可以看到这样的代码，相当暴力有效的解决浮动的问题。但是这个用法有一个致命伤，就是每次清除浮动的时候都需要增加一个空标签来使用。

　　这种做法如果在页面复杂的布局要经常清楚浮动的时候就会产生很多的空标签，增加了页面无用标签，不利于页面优化。但是我发现大型网站中 居然还在使用这种清楚浮动的方法。有兴趣的同学可以上他们首页搜索一下他们的.blank0这个样式名称。

　　因此有很多大神就研究出了 clearfix 清除浮动的方法，直接解决了上面的缺陷，不需要增加空标签，直接在有浮动的外层加上这个样式就可以了，这也是我们今天要讨论的clearfix进化史。

   起源：

```css
.clearfix:after { 
    visibility: hidden; 
    display: block; 
    font-size: 0; 
    content: " "; 
    clear: both; 
    height: 0; 
} 
* html  .clearfix { display: inline-table; } 
.clearfix { height: 1%; }//Hides from IE-mac 
.clearfix { display: block; }//End hide from IE-mac
```

解释一下以上的代码：

对大多数符合标准的浏览器应用第一个声明块，目的是创建一个隐形的内容为空的块来为目标元素清除浮动。

第二条为clearfix应用 inline-table 显示属性，仅仅针对IE/Mac。利用 * 对 IE/Mac 隐藏一些规则：

- height:1% 用来触发 IE6 下的haslayout。

- 重新对 IE/Mac 外的IE应用 block 显示属性。

最后一行用于结束针对 IE/Mac 的hack。（是不是觉得很坑爹，Mac下还有IE）

 起源代码可能也是很早期的时候了，再往后Mac下的IE5也发展到IE6了，各种浏览器开始向W3C这条标准慢慢靠齐了。所以就有了下面这个写法出现了。

```css
.clearfix:after { 
    visibility: hidden; 
    display: block; 
    font-size: 0; 
    content: " "; 
    clear: both; 
    height: 0; 
} 
* html .clearfix { zoom: 1; } /* IE6 */
*:first-child+html .clearfix { zoom: 1; } /* IE7 */
```


   IE6 和 IE7 都不支持 :after 这个伪类，因此需要后面两条来触发IE6/7的haslayout，以清除浮动。幸运的是IE8支持 :after 伪类。因此只需要针对IE6/7的hack了。

　　在一个有float 属性元素的外层增加一个拥有clearfix属性的div包裹，可以保证外部div的height，即清除"浮动元素脱离了文档流，包围图片和文本的 div 不占据空间"的问题。

　　Jeff Starr 在这里针对IE6/7用了两条语句来触发haslayout。我在想作者为什么不直接用 * 来直接对 IE6/7 同时应用 zoom:1 或者直接就写成：

但是对于很多同学这种优化程度代码还是不够给力，clearfix 发展到现在的两个终极版：

## 重构clearfix浮动

    

构成Block Formatting Context的方法有下面几种： 

- float的值不为none。

- overflow的值不为visible。

- display的值为table-cell, table-caption, inline-block中的任何一个。 

- position的值不为relative和static。 

　很明显，float和position不合适我们的需求。那只能从overflow或者display中选取一个。因为是应用了.clearfix和.menu的菜单极有可能是多级的，所以overflow: hidden或overflow: auto也不满足需求,（会把下拉的菜单隐藏掉或者出滚动条），那么只能从display下手。 
   
   我们可以将.clearfix的display值设为table-cell, table-caption, inline-block中的任何一个。但是display: inline-block会产生多余空白，所以也排除掉。剩下的只有table-cell, table-caption，为了保证兼容可以用display: table来使.clearfix形成一个Block Formatting Context。因为display: table会产生一些匿名盒子，这些匿名盒子的其中一个（display值为table-cell）会形成Block Formatting Context。
   
   这样我们新的.clearfix就会闭合内部元素的浮动。



后面又有人对此进行了改良：

### 终极版一：

```css
.clearfix:after { 
    content:"\200B"; 
    display:block; 
    height:0; 
    clear:both; 
} 
.clearfix {*zoom:1;}/*IE/7/6*/
```

解释下:`content:"\200B";`这个参数，Unicode字符里有一个“零宽度空格”，即 U+200B，代替原来的“.”，可以缩减代码量。而且不再使用visibility:hidden。

### 终极版二：

```css
.clearfix:before,.clearfix:after{ 
    content:""; 
    display:table; 
} 
.clearfix:after{clear:both;} 
.clearfix{ 
    *zoom:1;/*IE/7/6*/
}
```
