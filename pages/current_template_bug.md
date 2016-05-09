---
layout: post
title: 当前模板的BUG
---


## 1.目录长度过多时，目录不滚动

**总共目录到达7.3之多，但当前图中只显示了4.8，再向下就无法滚动**
	
![目录不滚动](/public/img/catalog-not-scroll.png)

**已经解决**

![目录滚动](/public/img/catalog-scroll.png)

解决办法：
	/_layout/post.html内的`<div class="panel-body" id="nav" style="overflow: scroll;width: 100%;height: 600px"></div>`将style改为当前样式

