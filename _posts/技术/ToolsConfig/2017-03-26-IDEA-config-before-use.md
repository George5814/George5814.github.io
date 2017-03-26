---
layout: post
title: IDEA首次使用之前的配置
category: 技术
tags: 工具配置
keywords: 
description: 不定期更新
---

{:toc}

## 由来

最近刚刚从Eclipse转IDEA，总会有些许不适应，既因为对使用了四年多的Eclipse有感情习惯了，也因为对IDEA各种陌生。
因此为了以后再次新安装IDEA时重复的操作，现将对IDEA首次使用前的配置做个记录，主要是为了以后少走弯路，避免将时间浪费在重复的事情上。


## 行动

### 编码问题

编码包括**项目文件编码**、**控制台编码**、**IDEA Server编码**和**IDEA编码**。

任何一个编码没有设置都会有可能在后期的使用中出现乱码，因此最好还是在第一次真正使用前将其设置妥当。

说干就干，先设置控制台编码

```
#这样做是为了让IDEA软件的字体支持中文
CTRL+ALT+S | Apperence & Behavior | Apperence | 右侧面板点选override default fonts by(now recommended0) | name处将字体设置为MicrosoftYaHei 
```

然后设置文件编码,默认文件编码是UTF-8，但properties文件是系统默认编码

```
CTRL+ALT+S | Editor | File Encoding | 右侧面板三处位置设置为UTF-8编码，一般只是在最下面的Default encoding  forproperties files处将系统默认编码设置为UTF-8
```

设置IDEA编码：

在IDEA安装目录IDEA_HOME}/bin下`idea.exe.vmoptions`和`idea64.exe.vmoptions`文件分别添加`-Dfile.encoding=UTF-8`

设置IDEA Server编码：

在菜单栏找到"Run->EditConfigrations " 找到"Server"选项卡 设置 VM options 为 -Dfile.encoding=UTF-8

## 代码自动提示

因为习惯了Eclipse的输入一个字符就会弹出提示列表，而IDEA默认也不会弹出，只是在`ALT+/`下会自动填充，不方便，因此做如下设置

```
CTRL+ALT+S | Editor | General | Code Completion 
```
