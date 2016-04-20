---
layout: post
title: mybatis异常 ：元素内容必须由格式正确的字符数据或标记组成
category: 技术
tags:  Mybatis
keywords: 
description: 
---

{:toc}


## 错误事例：

 
``` xml
<select id="findTenantActiveInfo" resultType="com.hehe.sns.imworkbench.bean.mongo.TenantActiveBean">    
    select 
	    uc_user.etp_id etpId, uc_login_history.login_time loginTime,uc_login_history.login_ip loginIp
	from 
	    uc_login_history 
	join uc_user 
	on uc_user.uid = uc_login_history.user_id
	and
	 uc_login_history.login_time >= ${startDate}
	and uc_login_history.login_time < ${endDate}
</select>
``` 
 
## 解决办法：

```xml
    <select id="findTenantActiveInfo" resultType="com.hehe.sns.imworkbench.bean.mongo.TenantActiveBean">    
        select 
    	    uc_user.etp_id etpId, uc_login_history.login_time loginTime,uc_login_history.login_ip loginIp
    	from 
    	    uc_login_history 
    	join uc_user 
    	on uc_user.uid = uc_login_history.user_id
    	and
    	<![CDATA[ 
    	 uc_login_history.login_time >= ${startDate}
    	and uc_login_history.login_time < ${endDate}
    	]]>
    </select>
```

## 错误原因：

    mybatis查询的时候，需要用到运算符 小于号< 和  大于号 >,在mybatis配置文件里面，这种会被认为是标签，所以解析错误。在用到>和<的语句外围加上

`<![CDATA[`和 `]]>` 即可。
