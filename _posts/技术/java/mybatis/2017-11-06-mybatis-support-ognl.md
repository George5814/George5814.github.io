---
layout: post
title: MyBatis支持的OGNL语法
category: 技术
tags:  Mybatis
keywords: 
description: 介绍Mybatis中支持的OGNL语法
---

**个人新的博客地址:<https://segmentfault.com/blog/followtry>**

#### Mybatis支持OGNL的语法

在sql映射语句中可以支持引入以下几种方式

示例SQL

```XML
<select id="getUserById" resultMap="BaseResultMap">
    select * from user
    <if test="id != null">
    <where>
            name = #{name}
            and id =${id}
            and id = ${user.id}
            and id = ${@@abs(-12345678)}
            and id = ${@@parseInt("654")}
            and id='${@cn.followtry.mybatis.bean.User@name()}'
            and id='${new cn.followtry.mybatis.bean.User()}'
            and id=${@cn.followtry.mybatis.bean.User@haha}
            and id='${@cn.followtry.mybatis.bean.User@arr[1]}'
            and id='${@cn.followtry.mybatis.bean.User@list[1]}'
            and id='${@cn.followtry.mybatis.bean.User@map.get("123")}'
            and id='${@cn.followtry.mybatis.bean.CodeTypeEnum@THREE.ordinal()}'
        </where>
        </if>
    limit 100
</select>
```

- 变量
    `id =${id}`
- 属性
    `id = ${user.id}`
- 静态方法（public）
    `id='${@cn.followtry.mybatis.bean.User@name()}'`
- 静态属性（public）
    `id=${@cn.followtry.mybatis.bean.User@aaa}`
- 数组索引
    `id='${@cn.followtry.mybatis.bean.User@arr[1]}'`
- 集合
    `'${@cn.followtry.mybatis.bean.User@list[1]}'`
- Map
    `id='${@cn.followtry.mybatis.bean.User@map.get("123")}'`
    `id='${@cn.followtry.mybatis.bean.User@map}'`
- Enum

    `id=${@cn.followtry.mybatis.bean.CodeTypeEnum@THREE.ordinal()}`
    
- 构造方法
    `id='${new cn.followtry.mybatis.bean.User()}'`
   
- java.lang.Math方法
    `id = ${@@abs(-12345678)}` 可以省略class的编写，方法的默认class是java.lang.Math

`${}`语法中通过两个`@`字符，前者定位到Java类，后者定位到类中的方法或属性
这里只列出的其中一部分，对于Mybatis支持的`${}`语法，可以参见OGNL语法手册。



