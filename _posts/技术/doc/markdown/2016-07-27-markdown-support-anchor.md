---
layout: post
title: markdown在jekyll中支持的一些操作
category: 技术
tags:  Markdown
keywords: 
description: 该文档描述了一些在jekyll中使用markdown的用法，不完全标准的符合markdown规范。
---

{:toc}

## md支持锚操作

```markdown
[fetchdt]({ % post_url 2016-07-20-18-hadoop-doc-translate %}#title13){:target="_blank"}
```

以上语法是在jekyll中写markdown文档时用到的，`{ % %}`("{"和"%"间没有空格)内为jekyll的语法，`post_url`后面可以跟所要连接的本站的markdown文档地址。
而个人突发奇想在其后根据html语法添加上了锚，经过编译后居然成功了。

而标准的markdown使用锚的方式如下：
[fetchdt](/2016/07/20/18-hadoop-doc-translate.html#title13){:target="_blank"}{:name="mytest"}

```
[fetchdt](/2016/07/20/18-hadoop-doc-translate.html#title13){:target="_blank"}{:name="mytest"}
```

## md指定连接跳转方式

如上代码片段所示，最后的`{:target="_blank"}`在jekyll编译时会成为元素的属性，然后与html中的意义相同。
其中语法`{:}`可以同时有多个并列。`target="_blank"`为html的属性写法。



