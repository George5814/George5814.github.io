---
layout: post
title: 前JS 和 后springmvc操作文件上传
category: 前端
tags:  Javascript
keywords: 
description: 记录web页面上传图片的过程，后台是spring及其组件springmvc接收处理，可以实现在chrome和Firefox的文件上传操作。
---

{:toc} 


### 准备条件

- [JQuery-2.2.4](https://jquery.com/){:target="_blank"}

- [fileapi.js(html5版本)](https://github.com/mailru/FileAPI){:target="_blank"}

- springmvc后端环境

### 操作示例

**前端：**

引入js

```html
<script src="//cdn.bootcss.com/jquery/2.2.4/jquery.js"></script>
<script src="${static_file_path}/js/FileAPI.html5.min.js" type="text/javascript" charset="utf-8"></script>
<script src="${static_file_path}/js/FileAPI.exif.js" type="text/javascript" charset="utf-8"></script>
<script src="${static_file_path}/js/jquery.fileapi.min.js" type="text/javascript" charset="utf-8"></script>
```

添加文件的DOM元素

```html
<div class="fileInputContainer" id="uploadDiv" style="background-image: url(${(suite.logo)!'${static_file_path}/imgs/logo.png'});">
	<form>
      <input type="hidden" name="file"  id="suite-logo-path" value="${(suite.logo)!''}"/>
      <input class="fileInput" type="file" name="filedata" id="uploadFile"  accept="image/*"/>
	</form>
</div>
```

操作文件上传的JS代码

```javascript
uploadSuiteLogo("uploadDiv");

 /**
  * 上传文件
  * @param {String} id
  */
function uploadSuiteLogo(id){
	$('#'+id).fileapi({
	   url: '${static_file_path}/console/file/upload',
	   multiple: false,
	   maxSize: 20 * FileAPI.MB,
	   autoUpload: true,
	   onComplete:function(e,data){
	   		var result = data.result;
			if (result.code == 0) {
				//在文件上传成功后，处理返回值
				$("#uploadDiv").css('background-image','url(' + result.data + ')')
				$("#suite-logo-path").val(result.data)
			}else{
				alert(result.msg);
			}
	   }
	   
	});
   }
```

JS操作文件的id是`<input type=file>`元素所在div的id。

`static_file_path`为后端自定义的Request的属性。

**后端：**

springmvc的配置

```xml

<!-- SpringMVC上传文件时，需要配置MultipartResolver处理器 -->
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
	<property name="defaultEncoding" value="UTF-8"/>
	<!-- 指定所上传文件的总大小不能超过10M。注意maxUploadSize属性的限制不是针对单个文件，而是所有文件的容量之和 -->
	<property name="maxUploadSize" value="10485760"/>
</bean>

<!-- SpringMVC在超出上传文件限制时，会抛出org.springframework.web.multipart.MaxUploadSizeExceededException -->
<!-- 该异常是SpringMVC在检查上传的文件信息时抛出来的，而且此时还没有进入到Controller方法中 -->
<bean id="exceptionResolver" class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
	<property name="exceptionMappings">
		<props>
			<!-- 遇到MaxUploadSizeExceededException异常时，自动跳转到/WEB-INF/jsp/error_fileupload.jsp页面 -->
			<prop key="org.springframework.web.multipart.MaxUploadSizeExceededException">uploadFileError</prop>
		</props>
	</property>
</bean>

```

处理上传的文件
```
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
```


```java
@RequestMapping(value = "/file/upload")
@ResponseBody
public ResponseBean loginPage(HttpServletRequest request,HttpServletResponse response) {
	MultipartFile file = ((MultipartHttpServletRequest)request).getFile("filedata");
	if(file == null || file.isEmpty()){
		return new ResponseBean(CodeEnum.C_21, "文件未获取到");
	}
	System.out.println("Process file: "+file.getOriginalFilename() );
    System.out.println("文件长度: " + file.getSize());  
    System.out.println("文件类型: " + file.getContentType());  
    System.out.println("文件名称: " + file.getName());  
    System.out.println("文件原名: " + file.getOriginalFilename());  
    System.out.println("========================================");  
    String fileName = UUID.randomUUID()+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
    String filePath = MessageFormat.format(ValueConstants.LOGO_PATH_FILE, fileName);
    System.out.println("保存位置: " + filePath);  
    String pathUrl = ValueConstants.LOGO_PATH_WEB +  MessageFormat.format(ValueConstants.LOGO_PATH_DB, fileName);
    File dest = new File(filePath);
    dest.mkdirs();
    try {
		file.transferTo(dest);
	} catch (IllegalStateException | IOException e) {
		LOG.error("logo上传出错："+e.getMessage(),e);
	}
	return new ResponseBean(CodeEnum.C_0, pathUrl);
}
```

其中`ResponseBean`是自封装的返回格式对象，`getFile("filedata")`中的`filedata`与`<input type=file name="fiiledata">`为相同值，且`<input>`需要在`<form>`标签内

**经过测试，在chrome和Firefox下正常使用**
