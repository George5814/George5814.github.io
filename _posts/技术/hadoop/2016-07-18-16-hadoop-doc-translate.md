---
layout: post
title: 16.hadoop-2.7.2官网文档翻译-Hadoop的KMS(key 管理服务器)-文档集
category: 技术
tags:  Hadoop
keywords: 
description: Hadoop的KMS(key 管理服务器)-文档集。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-kms/index.html
---

{:toc}

HadoopKMS是基于Hadoop的**KeyProvider**的API的加密的key管理服务器。

它提供了使用REST API进行通信的客户端和服务端组件。

客户端是使用KMS的Rest API与KMS沟通的**KeyProvider**。

KMS和其客户端内置安全，并且它们支持HTTP SPNEGO kerberos认证和HTTPS安全传输。

KMS是java的web应用，运行在附随着Hadoop分发中预配置的tomcat中。

## KMS客户端配置

KMS客户端**KeyProvider**使用**kms**的协议，并且嵌入的URL必须是KMS的URL。比如，运行在`http://localhost:16000/kms`的KMS，**KeyProvider**的URI是`kms://http@localhost:16000/kms`,运行在`https://localhost:16000/kms`上的KMS，**KeyProvider**的URI是`kms://https@localhost:16000/kms`。

## KMS

### KMS配置

在`etc/hadoop/kms-site.xml`配置文件中配置KMS支持KeyProvider属性。

```xml
  <property>
     <name>hadoop.kms.key.provider.uri</name>
     <value>jceks://file@/${user.home}/kms.keystore</value>
  </property>

  <property>
    <name>hadoop.security.keystore.java-keystore-provider.password-file</name>
    <value>kms.keystore.password</value>
  </property>
```

该password文件通过classpath在Hadoop的配置目录中查找。

**注意：**需要重启KMS使得修改生效。


### KMS缓存

KMS缓存很短时间避免对基础密钥提供程序的过多的点击。

缓存默认是开启的（可以通过设置`hadoop.kms.cache.enable`关闭，值为Boolean）。

缓存只在下面的三个方法中使用，`getCurrentKey()`,`getKeyVersion()`和`getMetadata()`。

对于`getCurrentKey()`方法，无论key被访问多少次，缓存记录最大保留30000毫秒(30秒)，为避免不可考虑的键被认为是当前的。

对于`getKeyVersion()`方法，缓存记录被保留默认固定时间600000毫秒(10分钟)。可以通过`etc/hadoop/kms-site.xml`文件配置:

```xml
 <property>
     <name>hadoop.kms.cache.enable</name>
     <value>true</value>
   </property>

   <property>
     <name>hadoop.kms.cache.timeout.ms</name>
     <value>600000</value>
   </property>

   <property>
     <name>hadoop.kms.current.key.cache.timeout.ms</name>
     <value>30000</value>
   </property>


```

### KMS汇总审计日志 

API访问`GET_KEY_VERSION`,`GET_CURRENT_KEY`,`DECRYPT_EEK`,`GENERATE_EEK`操作的审计日志会被汇总。

记录被使用组合键(用户，key和操作)分类为由给定的关键用户在指定终端的访问数量在一个可配置的聚集区间之后刷新到审计日志。

聚集区间可以通过下面的属性配置:

```xml
<property>
    <name>hadoop.kms.aggregation.delay.ms</name>
    <value>10000</value>
</property>
```

### 启动/停止KMS

使用KMS的bin/kms.sh脚本启动和停止KMS，比如：`hadoop-2.7.2 $ sbin/kms.sh start`。

**注意：**调用脚本没有任何参数列表，所有可能带参数(start,stop,run等等)。`kms.sh`脚本时对tomcat的`catalina.sh`脚本的包装，来设置运行KMS需要的环境变量和java系统属性。


### 内嵌tomcat的配置

在`share/hadoop/kms/tomcat/conf`中配置内嵌的tomcat。

KMS在tomcat的`server.xml`中预配置了HTTP和Admin的端口分别为16000和16001。

tomcat的日志也预配置在了hadoop的`logs/`目录。

下面的环境变量（可以在`etc/hadoop/kms-env.sh`脚本中设置）可以用于修改那些值：

- KMS_HTTP_PORT
- KMS_ADMIN_PORT
- KMS_MAX_THREADS
- KMS_LOG

**注意：**为了使得配置修改生效需要重启KMS。


### 载入原生库

下面的环境变量可以用于指定任何需要的原生库的位置。比如，tomcat的APR库：

- JAVA_LIBRARY_PATH


### KMS安全配置

**启用kerberos的HTTP SPNEGO认证**

使用你的KDC服务器信息，配置kerberos的文件`etc/krb5.conf`。

为KMS创建一个服务的负责人和他的keytab，必须是HTTP服务负责人。使用正确的安全值配置KMS的`etc/hadoop/kms-site.xml`。例如：

```xml
 <property>
     <name>hadoop.kms.authentication.type</name>
     <value>kerberos</value>
   </property>

   <property>
     <name>hadoop.kms.authentication.kerberos.keytab</name>
     <value>${user.home}/kms.keytab</value>
   </property>

   <property>
     <name>hadoop.kms.authentication.kerberos.principal</name>
     <value>HTTP/localhost</value>
   </property>

   <property>
     <name>hadoop.kms.authentication.kerberos.name.rules</name>
     <value>DEFAULT</value>
   </property>
```


**注意：**为了使得配置修改生效需要重启KMS。

#### KMS代理用户配置

每个代理用户必须使用下面的属性在`etc/hadoop/kms-site.xml`中配置。

```
 <property>
    <name>hadoop.kms.proxyuser.#USER#.users</name>
    <value>*</value>
  </property>

  <property>
    <name>hadoop.kms.proxyuser.#USER#.groups</name>
    <value>*</value>
  </property>

  <property>
    <name>hadoop.kms.proxyuser.#USER#.hosts</name>
    <value>*</value>
  </property>
```

`#USER#`是待配置代理用户名称

`users`属性表明用户可以被扮演。

`groups`属性表明被扮演的用户必须属于这些组。

`users`和`groups`属性至少有一个必须别配置。如果两个都指定了，那配置的代理用户只可以扮演既属于`users`列表的用户，又属于`groups`列表中某个组的用户

`host`属性表明来自哪一个主机的代理用户可以扮演。

如果`users`,`groups`或者`hosts`有一个是`*`,意味着对于用户，组或者主机对于代理用户没有限制。

#### HTTPS上的KMS

必须在`etc/hadoop/kms_env.sh`中设置一下两个属性才能将KMS配置在HTTPS上工作：

- KMS_SSL_KEYSTORE_FILE=$HOME/.keystore

- KMS_SSL_KEYSTORE_PASS=password

在kms`tomcat/conf`目录，使用`ssl-server.xml`替换`server.xml`文件。

你需要为KMS创建一个SSL证书。作为Unix用户的`kms`，使用java的`keytool`命令创建SSL证书：

```bash
$ keytool -genkey -alias tomcat -keyalg RSA
```

在一个互动的提示中你会被问一系列问题。然后它会创建keystore文件，被命名为`.keystore`并存储在kms用户的主目录汇总。

在“keystore password”阶段你输入的密码必须与配置目录中`kms-env.sh`脚本内设置的`KMS_SSL_KEYSTORE_PASS`环境变量值相匹配。

“What is your first and last name?”的回答必须是要运行KMS的机器的主机名。

#### KMS访问控制

在KMS的`etc/hadoop/kms-acls.xml`配置文件中定义KMS的ACL配置。在修改后，该文件会热加载。

KMS同时支持细粒度的访问控制和通过ACL配置属性的集合配置KMS操作的黑名单。

访问KMS的用户首先检查是否在请求操作的访问控制列表中，然后在假定允许访问的情况下检查是否在黑名单之外。

```xml
<configuration>
  <property>
    <name>hadoop.kms.acl.CREATE</name>
    <value>*</value>
    <description>
          ACL for create-key operations.
          If the user is not in the GET ACL, the key material is not returned
          as part of the response.
    </description>
  </property>

  <property>
    <name>hadoop.kms.blacklist.CREATE</name>
    <value>hdfs,foo</value>
    <description>
          Blacklist for create-key operations.
          If the user is in the Blacklist, the key material is not returned
          as part of the response.
    </description>
  </property>

  <property>
    <name>hadoop.kms.acl.DELETE</name>
    <value>*</value>
    <description>
          ACL for delete-key operations.
    </description>
  </property>

  <property>
    <name>hadoop.kms.blacklist.DELETE</name>
    <value>hdfs,foo</value>
    <description>
          Blacklist for delete-key operations.
    </description>
  </property>

  <property>
    <name>hadoop.kms.acl.ROLLOVER</name>
    <value>*</value>
    <description>
          ACL for rollover-key operations.
          If the user is not in the GET ACL, the key material is not returned
          as part of the response.
    </description>
  </property>

  <property>
    <name>hadoop.kms.blacklist.ROLLOVER</name>
    <value>hdfs,foo</value>
    <description>
          Blacklist for rollover-key operations.
    </description>
  </property>

  <property>
    <name>hadoop.kms.acl.GET</name>
    <value>*</value>
    <description>
          ACL for get-key-version and get-current-key operations.
    </description>
  </property>

  <property>
    <name>hadoop.kms.blacklist.GET</name>
    <value>hdfs,foo</value>
    <description>
          ACL for get-key-version and get-current-key operations.
    </description>
  </property>

  <property>
    <name>hadoop.kms.acl.GET_KEYS</name>
    <value>*</value>
    <description>
         ACL for get-keys operation.
    </description>
  </property>

  <property>
    <name>hadoop.kms.blacklist.GET_KEYS</name>
    <value>hdfs,foo</value>
    <description>
          Blacklist for get-keys operation.
    </description>
  </property>

  <property>
    <name>hadoop.kms.acl.GET_METADATA</name>
    <value>*</value>
    <description>
        ACL for get-key-metadata and get-keys-metadata operations.
    </description>
  </property>

  <property>
    <name>hadoop.kms.blacklist.GET_METADATA</name>
    <value>hdfs,foo</value>
    <description>
         Blacklist for get-key-metadata and get-keys-metadata operations.
    </description>
  </property>

  <property>
    <name>hadoop.kms.acl.SET_KEY_MATERIAL</name>
    <value>*</value>
    <description>
            Complimentary ACL for CREATE and ROLLOVER operation to allow the client
            to provide the key material when creating or rolling a key.
    </description>
  </property>

  <property>
    <name>hadoop.kms.blacklist.SET_KEY_MATERIAL</name>
    <value>hdfs,foo</value>
    <description>
            Complimentary Blacklist for CREATE and ROLLOVER operation to allow the client
            to provide the key material when creating or rolling a key.
    </description>
  </property>

  <property>
    <name>hadoop.kms.acl.GENERATE_EEK</name>
    <value>*</value>
    <description>
          ACL for generateEncryptedKey
          CryptoExtension operations
    </description>
  </property>

  <property>
    <name>hadoop.kms.blacklist.GENERATE_EEK</name>
    <value>hdfs,foo</value>
    <description>
          Blacklist for generateEncryptedKey
          CryptoExtension operations
    </description>
  </property>

  <property>
    <name>hadoop.kms.acl.DECRYPT_EEK</name>
    <value>*</value>
    <description>
          ACL for decrypt EncryptedKey
          CryptoExtension operations
    </description>
  </property>

  <property>
    <name>hadoop.kms.blacklist.DECRYPT_EEK</name>
    <value>hdfs,foo</value>
    <description>
          Blacklist for decrypt EncryptedKey
          CryptoExtension operations
    </description>
  </property>
</configuration>
```

#### Key的访问控制
在key级别，KMS支持对所有非读操作的访问控制。所有的key访问操作被列为：

- MANAGEMENT ：创建key，删除key和滚动升级到新版本。

- GENERATE_EEK ：生成加密key，warmUpEncryptedKeys

- DECRYPT_EEK ：解密加密的key

- READ ：获取单个key版本，获取key的多个版本，获取元数据，获取多个key的元数据，获取当前的key

- ALL ：上面的所有操作

可以在KMS的`etc/hadoop/kms-acls.xml`中定义，就想下面展示的。

对于没有显示配置密钥访问的所有密钥，会被操作类型的子集配置默认的密钥访问控制。

也可能会配置白名单核心访问控制的列表操作类型子集。白名单的核心ACL是除了显示知名和默认每个Key的ACL之外的白名单。如果没有key的ACL被显示知名。如果他们在默认的ACL或白名单ACL中，用户会被准许访问。如果per-key显示设置，，如果他们在per-key的ACL或者白名单的keyACL中，用户将会被允许访问。

对于请求操作，如果没有为指定的key配置ACL并且没有默认的ACL被配置，并且root的key ACL被配置，那么访问将会被拒绝。

**注意：**默认的和白名单的key ACL不支持`all`操作

```xml
  <property>
    <name>key.acl.testKey1.MANAGEMENT</name>
    <value>*</value>
    <description>
      ACL for create-key, deleteKey and rolloverNewVersion operations.
    </description>
  </property>

  <property>
    <name>key.acl.testKey2.GENERATE_EEK</name>
    <value>*</value>
    <description>
      ACL for generateEncryptedKey operations.
    </description>
  </property>

  <property>
    <name>key.acl.testKey3.DECRYPT_EEK</name>
    <value>admink3</value>
    <description>
      ACL for decryptEncryptedKey operations.
    </description>
  </property>

  <property>
    <name>key.acl.testKey4.READ</name>
    <value>*</value>
    <description>
      ACL for getKeyVersion, getKeyVersions, getMetadata, getKeysMetadata,
      getCurrentKey operations
    </description>
  </property>

  <property>
    <name>key.acl.testKey5.ALL</name>
    <value>*</value>
    <description>
      ACL for ALL operations.
    </description>
  </property>

  <property>
    <name>whitelist.key.acl.MANAGEMENT</name>
    <value>admin1</value>
    <description>
      whitelist ACL for MANAGEMENT operations for all keys.
    </description>
  </property>

  <!--
  'testKey3' key ACL is defined. Since a 'whitelist'
  key is also defined for DECRYPT_EEK, in addition to
  admink3, admin1 can also perform DECRYPT_EEK operations
  on 'testKey3'
-->
  <property>
    <name>whitelist.key.acl.DECRYPT_EEK</name>
    <value>admin1</value>
    <description>
      whitelist ACL for DECRYPT_EEK operations for all keys.
    </description>
  </property>

  <property>
    <name>default.key.acl.MANAGEMENT</name>
    <value>user1,user2</value>
    <description>
      default ACL for MANAGEMENT operations for all keys that are not
      explicitly defined.
    </description>
  </property>

  <property>
    <name>default.key.acl.GENERATE_EEK</name>
    <value>user1,user2</value>
    <description>
      default ACL for GENERATE_EEK operations for all keys that are not
      explicitly defined.
    </description>
  </property>

  <property>
    <name>default.key.acl.DECRYPT_EEK</name>
    <value>user1,user2</value>
    <description>
      default ACL for DECRYPT_EEK operations for all keys that are not
      explicitly defined.
    </description>
  </property>

  <property>
    <name>default.key.acl.READ</name>
    <value>user1,user2</value>
    <description>
      default ACL for READ operations for all keys that are not
      explicitly defined.
    </description>
  </property>
```


### KMS的委派Token配置

KMS的委派token密钥管理器可以使用下面的属性配置：

```xml
  <property>
    <name>hadoop.kms.authentication.delegation-token.update-interval.sec</name>
    <value>86400</value>
    <description>
      How often the master key is rotated, in seconds. Default value 1 day.
    </description>
  </property>

  <property>
    <name>hadoop.kms.authentication.delegation-token.max-lifetime.sec</name>
    <value>604800</value>
    <description>
      Maximum lifetime of a delagation token, in seconds. Default value 7 days.
    </description>
  </property>

  <property>
    <name>hadoop.kms.authentication.delegation-token.renew-interval.sec</name>
    <value>86400</value>
    <description>
      Renewal interval of a delagation token, in seconds. Default value 1 day.
    </description>
  </property>

  <property>
    <name>hadoop.kms.authentication.delegation-token.removal-scan-interval.sec</name>
    <value>3600</value>
    <description>
      Scan interval to remove expired delegation tokens.
    </description>
  </property>
```

### 在负载均衡器和VIP后面使用多个KMS实例

为了可伸缩性和HA（高可用）的目标，KMS支持在负载均衡器和VIP后面使用多个KMS实例。

在使用多个KMS实例时，来自同一用户的请求可能会被不同的KMS实例处理。

KMS实例必须专门配置作为单一的逻辑服务以便正确的工作。


#### HTTP的kerberos负责人配置

当KMS多个实例在负载均衡器或者VIP之后时，客户端会用VIP的主机名。对于kerberos SPNEGO认证，URL的主机名是用来构建服务器上kerberos服务的名称。`HTTP/#HOSTNAME#`。这意味着所有的KMS实例都必须有个kerberos服务的名称和负载均衡或VIP的主机名。

为了正确的访问指定的KMS实例，KMS实例必须有kerberos服务名称和它的主机名。这是为了监控和管理的目的。

kerberos服务负责人证书（负载均衡主机名或VIP的主机名和真是的KMS实例主机名）全都必须在认证的keytab文件中配置。并且在配置中负责人的名称必须指定为`*`。
比如：

```
  <property>
    <name>hadoop.kms.authentication.kerberos.principal</name>
    <value>*</value>
  </property>
```

**注意：**如果使用HTTPS，KMS实例使用的SSL证书必须配置支持多个主机名（请看java 7 keytool中SAN扩展支持）。

#### HTTP认证签名

KMS使用Hadoop认证来作为HTTP认证。Hadoop认证问题是一旦客户端认证成功，就签署了HTTP的cookie。该cookie有个过期时间，过期后会触发新的认证序列。这样做避免了一个客户端的每个HTTP请求都触发认证。

KMS实例必须合适cookie签名是被其他KMS实例签署的。为了这样做，所有的KMS实例必须共享签署密钥。

该密钥共享可以使用ZooKeeper服务器集群，在`kms-site.xml`中配置如下属性：

```
<property>
    <name>hadoop.kms.authentication.signer.secret.provider</name>
    <value>zookeeper</value>
    <description>
      Indicates how the secret to sign the authentication cookies will be
      stored. Options are 'random' (default), 'string' and 'zookeeper'.
      If using a setup with multiple KMS instances, 'zookeeper' should be used.
    </description>
  </property>
  <property>
    <name>hadoop.kms.authentication.signer.secret.provider.zookeeper.path</name>
    <value>/hadoop-kms/hadoop-auth-signature-secret</value>
    <description>
      The Zookeeper ZNode path where the KMS instances will store and retrieve
      the secret from.
    </description>
  </property>
  <property>
    <name>hadoop.kms.authentication.signer.secret.provider.zookeeper.connection.string</name>
    <value>#HOSTNAME#:#PORT#,...</value>
    <description>
      The Zookeeper connection string, a list of hostnames and port comma
      separated.
    </description>
  </property>
  <property>
    <name>hadoop.kms.authentication.signer.secret.provider.zookeeper.auth.type</name>
    <value>kerberos</value>
    <description>
      The Zookeeper authentication type, 'none' or 'sasl' (Kerberos).
    </description>
  </property>
  <property>
    <name>hadoop.kms.authentication.signer.secret.provider.zookeeper.kerberos.keytab</name>
    <value>/etc/hadoop/conf/kms.keytab</value>
    <description>
      The absolute path for the Kerberos keytab with the credentials to
      connect to Zookeeper.
    </description>
  </property>
  <property>
    <name>hadoop.kms.authentication.signer.secret.provider.zookeeper.kerberos.principal</name>
    <value>kms/#HOSTNAME#</value>
    <description>
      The Kerberos service principal used to connect to Zookeeper.
    </description>
  </property>

```


#### 委派Token

待决定

### KMS的Rest API

#### 创建key

请求：

POST `http://HOST:PORT/kms/v1/keys`

Content-Type: `application/json`

```JSON

{
  "name"        : "<key-name>",
  "cipher"      : "<cipher>",
  "length"      : <length>,        //int
  "material"    : "<material>",    //base64
  "description" : "<description>"
}
```

返回体：

201 `CREATED`

LOCATION: `http://HOST:PORT/kms/v1/key/<key-name>`  
Content-Type: `application/json`  

```JSON
{
  "name"        : "versionName",
  "material"    : "<material>",    //base64, not present without GET ACL
}
```

#### 滚动更新key

请求：

POST `http://HOST:PORT/kms/v1/key/<key-name>`  
Content-Type: `application/json`  

```JSON
{
  "material"    : "<material>",
}
```

返回体：

200 `OK`  
Content-Type: `application/json`

```JSON
{
  "name"        : "versionName",
  "material"    : "<material>",    //base64, not present without GET ACL
}

```


#### 删除key

请求：

DELETE `http://HOST:PORT/kms/v1/key/<key-name>`

返回体：

200 `OK`


#### 获取key的元数据

请求：

GET `http://HOST:PORT/kms/v1/key/<key-name>/_metadata`  

返回体：

200 `OK`

Content-Type: `application/json`

```JSON
{
  "name"        : "<key-name>",
  "cipher"      : "<cipher>",
  "length"      : <length>,        //int
  "description" : "<description>",
  "created"     : <millis-epoc>,   //long
  "versions"    : <versions>       //int
}
```


#### 获取当前key

请求:

GET `http://HOST:PORT/kms/v1/key/<key-name>/_currentversion`

返回体：

200 `OK`

Content-Type: `application/json`

```JSON
{
  "name"        : "versionName",
  "material"    : "<material>",    //base64
}
```


#### 为当前的key版本生成加密的key

请求:

GET `http://HOST:PORT/kms/v1/key/<key-name>/_eek?eek_op=generate&num_keys=<number-of-keys-to-generate>`

返回体：

200 `OK`

Content-Type: `application/json`

```JSON
[
  {
    "versionName"         : "encryptionVersionName",
    "iv"                  : "<iv>",          //base64
    "encryptedKeyVersion" : {
        "versionName"       : "EEK",
        "material"          : "<material>",    //base64
    }
  },
  {
    "versionName"         : "encryptionVersionName",
    "iv"                  : "<iv>",          //base64
    "encryptedKeyVersion" : {
        "versionName"       : "EEK",
        "material"          : "<material>",    //base64
    }
  },
  ...
]

```


#### 解密已加密的key

请求：

POST `http://HOST:PORT/kms/v1/keyversion/<version-name>/_eek?ee_op=decrypt`  
Content-Type: `application/json`


```JSON
{
  "name"        : "<key-name>",
  "iv"          : "<iv>",          //base64
  "material"    : "<material>",    //base64
}
```

返回体：


200 `OK` 
 
Content-Type: `application/json`

```JSON

{
  "name"        : "EK",
  "material"    : "<material>",    //base64
}
```

#### 获取key版本

请求：

GET `http://HOST:PORT/kms/v1/keyversion/<version-name>`


返回体：


200 `OK` 
 
Content-Type: `application/json`

```JSON
{
  "name"        : "versionName",
  "material"    : "<material>",    //base64
}
```
#### 获取key的多个版本

请求：

GET `http://HOST:PORT/kms/v1/key/<key-name>/_versions`


返回体：


200 `OK` 
 
Content-Type: `application/json`

```JSON
[
  {
    "name"        : "versionName",
    "material"    : "<material>",    //base64
  },
  {
    "name"        : "versionName",
    "material"    : "<material>",    //base64
  },
  ...
]
```
#### 获取key的多个名称

请求：

GET `http://HOST:PORT/kms/v1/keys/names`


返回体：


200 `OK` 
 
Content-Type: `application/json`

```JSON
[
  "<key-name>",
  "<key-name>",
  ...
]
```
#### 获取多个key的元数据

请求：

GET `http://HOST:PORT/kms/v1/keys/metadata?key=<key-name>&key=<key-name>,...`


返回体：


200 `OK` 
 
Content-Type: `application/json`

```JSON
[
  {
    "name"        : "<key-name>",
    "cipher"      : "<cipher>",
    "length"      : <length>,        //int
    "description" : "<description>",
    "created"     : <millis-epoc>,   //long
    "versions"    : <versions>       //int
  },
  {
    "name"        : "<key-name>",
    "cipher"      : "<cipher>",
    "length"      : <length>,        //int
    "description" : "<description>",
    "created"     : <millis-epoc>,   //long
    "versions"    : <versions>       //int
  },
  ...
]
```
#### 解密已加密的key

请求：

POST `http://HOST:PORT/kms/v1/keyversion/<version-name>/_eek?ee_op=decrypt`  
Content-Type: `application/json`


```JSON
{
  "name"        : "<key-name>",
  "iv"          : "<iv>",          //base64
  "material"    : "<material>",    //base64
}
```

返回体：


200 `OK` 
 
Content-Type: `application/json`

```JSON

{
  "name"        : "EK",
  "material"    : "<material>",    //base64
}
```
#### 解密已加密的key

请求：

POST `http://HOST:PORT/kms/v1/keyversion/<version-name>/_eek?ee_op=decrypt`  
Content-Type: `application/json`


```JSON
{
  "name"        : "<key-name>",
  "iv"          : "<iv>",          //base64
  "material"    : "<material>",    //base64
}
```

返回体：


200 `OK` 
 
Content-Type: `application/json`

```JSON

{
  "name"        : "EK",
  "material"    : "<material>",    //base64
}
```







