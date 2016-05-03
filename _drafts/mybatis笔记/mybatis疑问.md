## mybatis疑问
### 1.namespace的作用

### 2.resultMap  的使用

### 3. 参数为对象时，可以直接指定属性，mybatis会自动寻找

### 4. mybatis映射文件可以直接省略

### 


### 6.mysql 根据时间按日分组查询
```sql
	select count(id) ,login_time ,DATE_FORMAT(from_unixtime(login_time),'%Y-%m-%d'),DAY(from_unixtime(login_time))
	from uc_login_history
	where DATE_FORMAT(from_unixtime(login_time),'%Y-%m') = '2015-09'
	GROUP BY MONTH(from_unixtime(login_time)),DAY(from_unixtime(login_time))
```
### 7.mysql不允许远程连接

	将名为mysql数据库的users表中对应用户的host改为%

### 8.解决mysql > 和 < 问题
	解决办法：加上 <![CDATA[ 和 ]]>
```sql
	<![CDATA[ 
		 uc_login_history.login_time >= ${startDate}
		and uc_login_history.login_time < ${endDate}
	]]>
```

### 14. mybatis 配置文件 错误：3 字节的 UTF-8 序列的字节 3 无效 
	删除配置文件中的中文

### 21.mybatis批量插入
```xml
	<insert id="batchInsertB2B" parameterType="ArrayList">
		insert into xxxxtable(hkgs,hkgsjsda,office,asdf,ddd,ffff,supfullName,classtype,agent_type,remark)
		<foreach collection="list" item="item" index="index" separator="union all">
			select  #{item.hkgs,jdbcType=VARCHAR},
					#{item.hkgsjsda,jdbcType=VARCHAR},
					#{item.office,jdbcType=VARCHAR},
					#{item.asdf,jdbcType=VARCHAR},
					#{item.ddd,jdbcType=VARCHAR},
					#{item.ffff,jdbcType=VARCHAR},
					#{item.supfullName,jdbcType=VARCHAR},0,0,
					#{item.remark,jdbcType=VARCHAR} 
			from dual
		</foreach>
	</insert>
```
批量插入（mysql）：
```xml
	<insert id="batchInsertData" parameterType="java.util.Map"><!-- java.util.List -->
		insert into app_login_day(etpid,appid,count,date)
		SELECT  
				etpid ,
				appid ,
				COUNT(userid) count,
				DATE_FORMAT(logintime,'%Y-%m-%d') date 
			FROM user_login 
			<![CDATA[
			WHERE logintime >= #{startDate}
			and logintime < #{endDate}
			]]>
			GROUP BY etpid,appid
	</insert>
```
```xml
	INSERT INTO TStudent(name,age) 
	  <foreach collection="list" item="item" index="index" open="(" close=")" separator="union all">
	    SELECT #{item.name} as a, #{item.age} as b
	  </foreach>
```
### 22. Mybatis中模糊查询的各种写法
	1. sql中字符串拼接
	   SELECT * FROM tableName WHERE name LIKE CONCAT(CONCAT('%', #{text}), '%');
	 
	2. 使用 ${...} 代替 #{...}
	   SELECT * FROM tableName WHERE name LIKE '%${text}%';
	 
	3. 程序中拼接
	   Java
	   // String searchText = "%" + text + "%";
	   String searchText = new StringBuilder("%").append(text).append("%").toString();
	   parameterMap.put("text", searchText);
	 
	   SqlMap.xml
	   SELECT * FROM tableName WHERE name LIKE #{text};
	 
	4. 大小写匹配查询
	   SELECT *  FROM TABLENAME  WHERE UPPER(SUBSYSTEM) LIKE '%' || UPPER('jz') || '%'
	   或者 
	   SELECT *   FROM TABLENAME  WHERE LOWER(SUBSYSTEM) LIKE '%' || LOWER('jz') || '%'
	   


### 23.mybatis 插入时返回记录主键
```xml
<insert id="insert" parameterType="com.yonyou.worktime.data.project.model.MemberRelation" useGeneratedKeys="true" keyProperty="id">
```

`useGeneratedKeys="true" keyProperty="id"`

这两句是关键配置，指定是否使用生成的主键，主键属性名是什么


### 24.解决错误 Value '0000-00-00 00:00:00' can not be represented as java.sql.Timestamp

	mysql的datetime类型支持的范围为'1000-01-01'到'9999-12-31'，当你输入不合法的日期或者0自动转为0000-00-00 00:00:00，
 	而这个日期 明显不在我上面提到的范围，所以报错了










