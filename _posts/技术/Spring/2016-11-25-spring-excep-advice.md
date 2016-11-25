---
layout: post
title: Spring的统一异常处理机制
category: 技术
tags:  Spring
keywords: 
description: Spring的统一异常处理机制介绍
---

{:toc}

## 目标

主要为了避免将详细的异常信息抛到前端页面，导致用户体验不好；也避免爆出详细异常信息，会让安全方面的人获得详细的破解情报。

## 配置

主要有两种配置：

- 通过实现拦截器`HandlerInterceptorAdapter`，自己实现拦截逻辑;

- 通过使用spring的`ControllerAdvice`和`ExceptionHandler`方式；

### 实现拦截器方式

新建类继承`org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver`类，
也可以实现`org.springframework.web.servlet.HandlerExceptionResolver`接口。

该方式不止是拦截异常，也可以根据自己的需要拦截请求，权限验证，参数验证，登录验证等多种情景，灵活度高。

代码示例如下：

```java
public class SimpleExceptionHandler implements HandlerExceptionResolver{
	
	private final static Logger LOG = LoggerFactory.getLogger(SimpleExceptionHandler.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		
		if (ex instanceof BizException) {
			LOG.error(ex.getMessage(), ex);
			BizException bizException = (BizException)ex;
            MappingJackson2JsonView view = new MappingJackson2JsonView();
            Map<String,Object> map = new HashMap<>();
            map.put("code", bizException.getCodeEnum().getCode());
            map.put("msg",bizException.getCodeEnum().getContent());
            return new ModelAndView(view,map);
		}else{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOG.error(ex.getMessage(), ex);
		}
		return new ModelAndView();
	}

}
```

该方式比较灵活，可以根据handler属于不同的类型进行不同的操作。

### 通过Spring注解的方式

该方式是Spring通过`ControllerAdvice`增强了Controller的功能，不止是可以进行统一的异常处理，但常用来处理异常。

而且该`ControllerAdvice`注解支持扫描注入的方式，即与`Controller`注解类似，其声明的注解有`Component`。
其注解的类的书写方式也与Controller类相似。不过在处理异常的方法上要加上指定的注解`ExceptionHandler`。
以此来告诉Spring，要使用该方法处理指定的异常。

代码示例：

```java
@ControllerAdvice
public class ExceptionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(code=HttpStatus.INTERNAL_SERVER_ERROR)
	public void runtimeExceptionResolve(RuntimeException runtimeException, HttpServletRequest request,
			HttpServletResponse response) {
		/*
		 * 指定返回值的编码和内容类型，
		 并通过response的PrintWriter对象将自定义的返回值返回给调用方。
		 */
		response.setCharacterEncoding("utf-8");
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		LOGGER.error("操作出现运行时异常情况", runtimeException);
		JsonResult result = JsonResult.newResult(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				runtimeException.getMessage(), null);
		try {
			response.getWriter().write(JSON.toJSONString(result));
		} catch (IOException e) {
			LOGGER.error("返回值操作出现异常情况", e);
		}
	}
}
```
