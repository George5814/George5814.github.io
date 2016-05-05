---
layout: post
title: Spring MVC实现跳转的几种方式
category: 技术
tags:  Spring
keywords: 
description: 
---

{:toc}


## 1.通过controller 跳转到前端页面

 例：
 
```java
    @RequestMapping("/login")
    public String login(PersonModel p){
       personService.login(p);
       return "/index";
    }
```

## 2.通过controller跳转的另一个controller

例：

```java
    @RequestMapping("/add")
    public String add(Person P,HttpSession session){
         String userid = (String)session.getAttribute("userid");
         if(userid == null){
          return "redirect:/person/login"; //跳转到login方法
         }else{
             personService.add(P);
         }
         return "/index";//跳转到页面
    }
```

## 3．通过ModelAndView实现跳转到另一个controller

例：

```java
    @RequestMapping("/add")
    public ModelAndView add(Person P,HttpSession session){
        
          return new  ModelAndView("redirect:/person/login");
    }
```

## 4.通过ModelAndView实现跳转到前端页面

```java
    @RequestMapping("/add")
    public ModelAndView add(Person P,HttpSession session){
        
          return new  ModelAndView("person/login");
    }
```










