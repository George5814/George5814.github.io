---
layout: post
title:  go执行命令列表
category: 技术
tags: go
keywords: go
description: go 命令 入门
date: 2022-02-16
modified_date: 2022-02-16
author: followtry
published: true
istop: false
---

### 命令

```shell
#执行运行go文件
go run hello.go 

#编译成二级制文件并执行
go build hello.go & ./hello

# go获取包的命令
go get abc.com/dep
# go获取包的指定版本
go get abc.com/dep@v0.1.0

#整理依赖
go mod tidy

#固化vendor到本地目录，不依赖网络下载
go mod vendor 
或者 go build -mod=vendor

```

### 基础语法

```go


//获取包

//字符串连接用加号
fmt.Println("hello" + ",,,world")

//变量声明，中间必须空格分开
var age int = 12

//格式化字符串，%s:字符串，%d:数字
var age int = 12
var name = "zhagnsan"
var targetS = fmt.Sprintf("this is %s,his age is %d",name,age)
fmt.Println(targetS)

//多变量声明
var id1, id2 = 111, 222

//因式分解方式申明变量，适合声明全局变量
var (
    name2 string
    age2 int
)

//只允许在函数体内出现
name3, age3 := "zhangsan3", 23

//常量命名
const name4 string = "zhagnsan4"

//for循环
for init; condition; post { }
for condition { }
for { }

// foreach 循环，第一个数是index，第二个数是实际值
var ss = []string{"123","234","345","456"}
for a,b := range ss {
    fmt.Println(a,b)
}

// 函数使用
func test01(a int,b string) string {
	return b + "," + strconv.Itoa(a)
}

var a = test01(12345,"hello")
fmt.Println(a)

//初始化数组，不存在的设置默认值。后5个数值都显示0
var intArr = [10] int{1,2,3,4,5}
//不确定长度的数组，可以在[]中加上...
var intArr2 = [...]int{2,3,5,6,7}
//在数组的指定位置上设置数值
var intArr3 = [10] int {1:3,9:6,5:3}

//定义指针类型
var p = 13  //普通变量
var p1  *int //指针变量
p1 = &p //将普通变量p的内存地址复制给指针变量p1
fmt.Println(p1)  //打印出p的内存地址
fmt.Println(*p1) //打印出p的内存地址上的值。 指针指向的指针变量表示当前指针变量的值

//切片
var sliceList = make([]string,20)
//在原有切面大小后面追加元素，原有切面会扩容
sliceList = append(sliceList, "zhangsan","lisi","wangwu","zhaoliu")

```

### 结构体

可以将结构体比作java类使用

```go
// Person 人员信息
type Person struct {
	name string
	age int
	address string
	id string
	remark string
}

// Employee 员工信息
type Employee struct {
	person Person
	salary      float64
	companyName string
	companyAddr string
}

var p1 = Person{name: "张三",age: 24}
var e1 = Employee{companyName: "高德",companyAddr: "首开广场",person: p1}
fmt.Println(e1)
fmt.Println("公司名称:"+e1.companyName)
fmt.Println("公司地址:"+e1.companyAddr)
fmt.Println("员工名称:"+e1.person.name)
fmt.Println("员工年龄:"+ strconv.Itoa(e1.person.age))
```

### Map 

```go
var mapKv = make(map[int]string)
mapKv[100] = "zhangsan"
mapKv[101] = "zhangsan2"
mapKv[104] = "zhangsan3"
fmt.Println(mapKv)
//通过range遍历输出
for key,value := range mapKv {
    fmt.Println(fmt.Sprintf("key: %d,value:%s",key,value))
}
```