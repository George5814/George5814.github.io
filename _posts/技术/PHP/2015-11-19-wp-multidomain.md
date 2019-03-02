---
layout: post
title: 如何为 WordPress 绑定多个域名的方法s
category: 技术	
tags:  PHP
keywords: 
description: 
---

{:toc} 

   通常情况下，我们都知道 ，按照wordpress的默认设置,wordpress只能绑定一个域名..这些数据存在数据库里,但是,自定义常量的优先级要高于数据库,所以,对于有多个镜像的blog,可以这样做..
社区里，有通过插件实现的方法 ，在下面我们会提到插件使用方法，但这里先说到的是如何手动去操作实现这个效果。

打开并修改wp-config.php文件,在<?php后添加代码. 我的代码如下:

```php
 if ($_SERVER['HTTP_HOST']=='www.lamp99.com'):
 	define("WP_SITEURL", "http://www.lamp99.com/wordpress");
	define("WP_HOME", "http://www.lamp99.com");
 elseif ($_SERVER['HTTP_HOST']=='lamp99.com'):
 	define("WP_SITEURL", "http://lamp99.com/wordpress");
   	define("WP_HOME", "http://lamp99.com");
 else :
  	define("WP_SITEURL", "http://cnc.lamp99.com/wordpress");
  	define("WP_HOME", "http://cnc.lamp99.com");
 endif;
```

原理其实很简单：判断当前域名,然后根据域名设置wordpress的目录和显示目录..

    提醒：为防止域名改变而造成图片不可用，必须在控制面板的“设置 (Options) – 杂项 (Misc)”里将“文件的完整 URL 地址”设为 “wp-content/uploads”（与“默认上传路径”参数相同）。

WordPress 多域名绑定插件Domain Theme

WordPress默认只能绑定一个域名，如果想要把多个域名绑定到同一个博客而且不是以跳转的方式，除了可以通过修改相关文件代码实现外，还有更简单的方法，就是用WordPress相关插件。Domain Theme就是其中优秀的一个。

WordPress Domain Theme多域名绑定插件特色：
1、实现多个域名绑定到一个WordPress博客;
2、每个域名可以设置不同的博客名、博客简介、博客主题;
3、可以能过绑定的域名查看文章、登陆后台，而且与主域名无任何区别.

使用方法：
- 1、下载Domain Theme插件，上传插件至wp-content/plugins/;
- 2、登陆博客后台，在已安装插件中启用Domain Theme，之后会在后台设置下面多出一个Domain Theme选项，进入设置;
- 3、在Add Domain下的Domain添加要绑定的域名，在Theme选择该域名下的主题风格，在Blog Title填写博客的标题，在Tagline处填写博客的介绍简介。

至此，插件安装完毕。记得把域名解析到相关服务器和在服务器绑定相关域名。
提醒：绑定多个域名可能会被搜索引擎当成作弊行为，不利于网站的收录，所以考虑清楚。
