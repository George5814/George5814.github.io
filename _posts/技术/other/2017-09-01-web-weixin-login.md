---
layout: post
title: 简述网页版微信扫码登录的过程
category: 技巧	
tags:  Other
keywords: 
description: 
---

## 网页版温馨扫码登录流程

1. 先打开<https://wx.qq.com/>显示出页面，这时候会加载一堆的html，js等资源。

2. 获取会话UUID
 
微信Web版本不使用用户名和密码登录，而是采用扫描二维码登录，所以服务器需要首先分配一个唯一的会话ID，用来标识当前的一次登录。
 
使用get方法，通过请求地址：https://login.weixin.qq.com/jslogin?appid=wx782c26e4c19acffb&fun=new&lang=zh_CN&_=时间戳
 
其中，时间戳这个值是当前距离林威治标准时间的毫秒。
 
get成功，则返回：window.QRLogin.code = 200; window.QRLogin.uuid = "AAAAAAAA"
其中的AAAAAAAA就是我们需要的uuid

3. 获取登录二维码
 
访问网址：https://login.weixin.qq.com/qrcode/XXXXXX
这里的XXXXXXX就是我们刚才获取的uuid，这个网址直接显示的就是二维码,该二维码是有有效期的，有效期时长由微信服务端决定。

4. 查询是否扫描二维码登录
 
显示了二维码以后，用户必须用手机微信扫描这个二维码才能登录。（微信为啥要这么设计？很奇怪的思维。。。我用电脑很多情况不就是因为手机没在旁边吗。。。）
 
使用get方法，查询地址：https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login?loginicon=true&uuid=AAAAAAAA&tip=1&r=-921646107&_=时间戳
 
这里的AAAAAAAA是我们刚才获取的uuid，时间戳同上。tip在第一次获取时应为1，其他次时为0.
 
如果服务器返回：

```
window.code=201，
window.userAvatar = base64:img/bbb
```

则说明此时用户在手机端已经完成扫描，并在网页端显示扫描人的头像。但还没有点击登录，继续使用上面的地址查询，但tip要变成0；

点击登录后，如果服务器返回：

```
window.code=200;
window.redirect_uri="https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?ticket=AZBAkjuOKc-2GAHcRBsKNuOt@qrticket_0&uuid=YbHBoVi8_w==&lang=zh_CN&scan=1504160545";
```

则说明此时用户在手机端已经确认登录，`window.redirect_uri=`后面的这个网址（暂称为BB）要记下来，接着要访问这个地址。

如果服务器返回：window.code=408，则说明等待超时，继续使用上面的地址查询。

5. 访问登录地址BB，获得uin、sid、pass_ticket、skey
 
用get方法，访问在上一步骤获得访问地址，并在参数后面加上：&fun=new，会返回一个xml格式的文本，类似这样：

```xml
    <error>
    <ret>0</ret>
    <message></message>
    <skey>@crypt_d6549c5a_37242e82c0913b75e5d3ad5ef2c7bdba</skey>
    <wxsid>n/5yTn844+kHkU66</wxsid>
    <wxuin>2799942121</wxuin>
    <pass_ticket>JOkd35AoEoi8MVx34qKT6xJVeaS8tT7mo8BFdGlKRfDGRCWKkKLW9DsrA%2BEJ34WM</pass_ticket>
    <isgrayscale>1</isgrayscale>
    </error>
````

把这里的wxuin，wxsid，skey，pass_ticket都记下来，这是重要数据。

该结果中包含的信息就是用来在接下来的请求中校验用户用的。

**到这里已经表明当前用户已经扫码登录成功了，而且可以看到只是通过ticket方式确认web端登录成功，而不经过用户名密码方式的登录，其实个人感觉就是移动端微信扫码给web端发送了一个授权而已。**

6. 微信初始化
 
这个是很重要的一步，我在这个步骤折腾了很久。。。
 
要使用POST方法，访问地址：`https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit?r=-918790895&pass_ticket=JOkd35AoEoi8MVx34qKT6xJVeaS8tT7mo8BFdGlKRfDGRCWKkKLW9DsrA%252BEJ34WM`
 
其中，时间戳不用解释，pass_ticket是我们在上面获取的一长串字符。
 
POST的内容是个json串，{"BaseRequest":{"Uin":"XXXXXXXX","Sid":"XXXXXXXX","Skey":XXXXXXXXXXXXX","DeviceID":"e123456789012345"}}
 
uin、sid、skey分别对应上面步骤4获取的字符串，DeviceID是e后面跟着一个15字节的随机数。
 
程序里面要注意使用UTF8编码方式。
POST成功，则服务器返回一个很长的JSON串，格式是这样：


```json
{
    "BaseResponse":{
        "Ret":0,
        "ErrMsg":""
    },
    "Count":11,
    "ContactList":[
        {//公众号或文件助手信息
            "Uin":0,
            "UserName":"filehelper",
            "NickName":"æ–‡ä»¶ä¼ è¾“åŠ©æ‰‹",
            "HeadImgUrl":"/cgi-bin/mmwebwx-bin/webwxgeticon?seq=660589401&username=filehelper&skey=@crypt_d6549c5a_37242e82c0913b75e5d3ad5ef2c7bdba",
            "ContactFlag":1,
            "MemberCount":0,
            "MemberList":[],
            "RemarkName":"",
            "HideInputBarFlag":0,
            "Sex":0,
            "Signature":"",
            "VerifyFlag":0,
            "OwnerUin":0,
            "PYInitial":"WJCSZS",
            "PYQuanPin":"wenjianchuanshuzhushou",
            "RemarkPYInitial":"",
            "RemarkPYQuanPin":"",
            "StarFriend":0,
            "AppAccountFlag":0,
            "Statues":0,
            "AttrStatus":0,
            "Province":"",
            "City":"",
            "Alias":"",
            "SnsFlag":0,
            "UniFriend":0,
            "DisplayName":"",
            "ChatRoomId":0,
            "KeyWord":"fil",
            "EncryChatRoomId":"",
            "IsOwner":0
        },
        {
            "Uin":0,
            "UserName":"@@ed82e31e61783c765723eb8574831ae0be272bbb6b02f7280e442b0d274e8cb9",
            "NickName":"è¯—é…’è¶å¹´åŽ",
            "HeadImgUrl":"/cgi-bin/mmwebwx-bin/webwxgetheadimg?seq=660624044&username=@@ed82e31e61783c765723eb8574831ae0be272bbb6b02f7280e442b0d274e8cb9&skey=@crypt_d6549c5a_37242e82c0913b75e5d3ad5ef2c7bdba",
            "ContactFlag":2051,
            "MemberCount":13,
            "MemberList":[//用户信息
                {
                    "Uin":0,
                    "UserName":"@4dae6aa0756bd9899b68d0b2e60f109d",
                    "NickName":"",
                    "AttrStatus":0,
                    "PYInitial":"",
                    "PYQuanPin":"",
                    "RemarkPYInitial":"",
                    "RemarkPYQuanPin":"",
                    "MemberStatus":0,
                    "DisplayName":"",
                    "KeyWord":"cha"
                }
            ],
            "RemarkName":"",
            "HideInputBarFlag":0,
            "Sex":0,
            "Signature":"",
            "VerifyFlag":0,
            "OwnerUin":0,
            "PYInitial":"",
            "PYQuanPin":"",
            "RemarkPYInitial":"",
            "RemarkPYQuanPin":"",
            "StarFriend":0,
            "AppAccountFlag":0,
            "Statues":1,
            "AttrStatus":0,
            "Province":"",
            "City":"",
            "Alias":"",
            "SnsFlag":0,
            "UniFriend":0,
            "DisplayName":"",
            "ChatRoomId":0,
            "KeyWord":"",
            "EncryChatRoomId":"",
            "IsOwner":0
        }
    ],
    "SyncKey":{
        "Count":4,
        "List":[
            {
                "Key":1,
                "Val":660633313
            },
            {
                "Key":2,
                "Val":660633314
            },
            {
                "Key":3,
                "Val":660633176
            },
            {
                "Key":1000,
                "Val":1504141382
            }
        ]
    },
    "User":{//当前用户信息
        "Uin":2799942121,
        "UserName":"@cee283ef70f5473680a467d23da14918888aa5771f1b08c7410b2ca3d685dd6a",
        "NickName":"followtry",
        "HeadImgUrl":"/cgi-bin/mmwebwx-bin/webwxgeticon?seq=842975299&username=@cee283ef70f5473680a467d23da14918888aa5771f1b08c7410b2ca3d685dd6a&skey=@crypt_d6549c5a_37242e82c0913b75e5d3ad5ef2c7bdba",
        "RemarkName":"",
        "PYInitial":"",
        "PYQuanPin":"",
        "RemarkPYInitial":"",
        "RemarkPYQuanPin":"",
        "HideInputBarFlag":0,
        "StarFriend":0,
        "Sex":1,
        "Signature":"",
        "AppAccountFlag":0,
        "VerifyFlag":0,
        "ContactFlag":0,
        "WebWxPluginSwitch":0,
        "HeadImgFlag":1,
        "SnsFlag":17
    },
    "ChatSet":"filehelper,weixin,@@ed82e31e61783c765723eb8574831ae0be272bbb6b02f7280e442b0d274e8cb9,",
    "SKey":"@crypt_d6549c5a_37242e82c0913b75e5d3ad5ef2c7bdba",//我的skey，也就是个人标识
    "ClientVersion":637865269,
    "SystemTime":1504157627,
    "GrayScale":1,
    "InviteStartCount":40,
    "MPSubscribeMsgCount":22,
    "MPSubscribeMsgList":[//订阅信息列表
        {
            "UserName":"@d532ab26f3404fdc3a95bc7a7d3ae368",
            "MPArticleCount":4,//拉取四篇文章
            "MPArticleList":[//文章列表信息
                {
                    "Title":"Ubuntu Linux",
                    "Cover":"http://mmbiz.qpic.cn/mmbiz_jpg/W9DqKgFsc6icYW9UsESlGZ2r3SEOjIwqeToauxczCXYPLW63YtYmCcK2mxTaRjH7XibemZZOcVXknLFsPnhSLyeg/640?wxtype=jpeg&wxfrom=0",
                    "Url":"http://mp.weixin.qq.com/s?__biz=MjM5NjQ4MjYwMQ==&mid=2664609333&idx=1&sn=9eec4c182757edc5d2e98b2dfa5752b1&chksm=bdce8d738ab904653e64f18e23a8ffe8634fc1547edc05e2dad547de69894d5df572cc4f2b12&scene=0#rd"
                }
            ],
            "Time":1504153992,
            "NickName":"Linuxä¸­å›½"
        }
    ],
    "ClickReportInterval":600000
}
```

 拿到该结果后，浏览器会渲染最近联系人，各个订阅的公众号的最近几篇文章。

7. 获取好友列表
 
使用POST方法，访问：https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact?r=时间戳
 
POST的内容为空。成功则以JSON格式返回所有联系人的信息。格式类似：

```json
{
    "BaseResponse": {
        "Ret": 0,
        "ErrMsg": ""
    },
    "MemberCount": 21,
    "MemberList": [
        {
            "Uin": 0,
            "UserName": xxx,
            "NickName": "Urinx",
            "HeadImgUrl": xxx,
            "ContactFlag": 3,
            "MemberCount": 0,
            "MemberList": [],
            "RemarkName": "",
            "HideInputBarFlag": 0,
            "Sex": 0,
            "Signature": "xxxx",
            "VerifyFlag": 8,
            "OwnerUin": 0,
            "PYInitial": "URINX",
            "PYQuanPin": "Urinx",
            "RemarkPYInitial": "",
            "RemarkPYQuanPin": "",
            "StarFriend": 0,
            "AppAccountFlag": 0,
            "Statues": 0,
            "AttrStatus": 0,
            "Province": "",
            "City": "",
            "Alias": "Urinxs",
            "SnsFlag": 0,
            "UniFriend": 0,
            "DisplayName": "",
            "ChatRoomId": 0,
            "KeyWord": "gh_",
            "EncryChatRoomId": ""
        },
        ...
    ],
    "Seq": 0
}
其中，MemberCount表示总共有多少联系人，里面的内容都比较清晰。
```


8. 开启微信状态通知
 
用POST方法，访问：https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxstatusnotify
 
POST的内容是JSON串，格式：
 
{ 
     BaseRequest: { Uin: xxx, Sid: xxx, Skey: xxx, DeviceID: xxx }, 
     Code: 3, 
     FromUserName: 自己ID, 
     ToUserName: 自己ID, 
     ClientMsgId: 时间戳 
}
9. 心跳包，与服务器同步并获取状态
 
以上步骤完成以后，就可以进入收发微信的循环了，可以用线程方式发送心跳包。
 
使用get方法，设置超时为60秒，访问：https://webpush.wx2.qq.com/cgi-bin/mmwebwx-bin/synccheck?sid=XXXXXX&uin=XXXXXX&synckey=XXXXXX&r=时间戳&skey=XXXXXX&deviceid=XXXXXX&_=时间戳
 
其他几个参数不用解释，这里的synckey需要说一下，前面的步骤获取的json串中有多个key信息，需要把这些信息拼起来，key_val，中间用|分割，类似这样：
 
1_652651920|2_652651939|3_652651904|1000_0
服务器返回：window.synccheck={retcode:”0”,selector:”0”}
 
retcode为0表示成功，selector为2和6表示有新信息。4表示公众号新信息。
 
10. 读取新信息
 
检测到有信息以后，用POST方法，访问：https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsync?sid=XXXXXX&skey=XXXXXX
 
POST的内容：
 
 ```json
{
    "BaseResponse":{
        "Ret":0,
        "ErrMsg":""
    },
    "AddMsgCount":0,
    "AddMsgList":[

    ],
    "ModContactCount":0,
    "ModContactList":[

    ],
    "DelContactCount":0,
    "DelContactList":[

    ],
    "ModChatRoomMemberCount":0,
    "ModChatRoomMemberList":[

    ],
    "Profile":{
        "BitFlag":0,
        "UserName":{
            "Buff":""
        },
        "NickName":{
            "Buff":""
        },
        "BindUin":0,
        "BindEmail":{
            "Buff":""
        },
        "BindMobile":{
            "Buff":""
        },
        "Status":0,
        "Sex":0,
        "PersonalCard":0,
        "Alias":"",
        "HeadImgUpdateFlag":0,
        "HeadImgUrl":"",
        "Signature":""
    },
    "ContinueFlag":0,
    "SyncKey":{
        "Count":7,
        "List":[
            {
                "Key":1,
                "Val":660633324
            }
        ]
    },
    "SKey":"",
    "SyncCheckKey":{
        "Count":7,
        "List":[
            {
                "Key":1,
                "Val":660633324
            }
        ]
    }
}
 ```

注意这里的SyncKey格式，参考前面的说明。
 
请求成功之后服务器会返回一个JSON串，其中AddMsgCount表示有多少信息，AddMsgList中是一个数组，包含了所有新消息，里面的MsgType表示信息类型，Content就是信息内容。
注意again，返回的信息中，会有新的synckey，要更新这个内容，下次获取信息访问要用这个新的key。
 
10、发送信息
 
这个比较简单，用POST方法，访问：https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg
 
POST的还是json格式，类似这样：


 ```json
 {
    "Msg":{
        "Type":1,
        "Content":"测试信息",
        "FromUserName":"XXXXXX",
        "ToUserName":"XXXXXX",
        "LocalID":"时间戳",
        "ClientMsgId":"时间戳"
    },
    "BaseRequest":{
        "Uin":"XXXXXX",
        "Sid":"XXXXXX",
        "Skey":"XXXXXX",
        "DeviceID":"XXXXXX"
    }
}
 ```


这里的Content是信息内容，LocalID和ClientMsgId都用当前时间戳。
 
以上就是基本的微信收发流程了。参考这个，可以自己去开发其他相关内容，比如群发消息之类的。欢迎讨论。


> 参考文章  
> [微信网页web版通信协议分析 实现微信登录发送接收消息](http://www.oicqzone.com/qqjiqiao/2017032723731.html)


![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin2.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin3.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin4.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin5.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin6.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin7.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin8.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin9.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin10.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin11.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin12.png)

![web端微信登录](http://omsz9j1wp.bkt.clouddn.com/image/weixin/wxjslogin13.png)






