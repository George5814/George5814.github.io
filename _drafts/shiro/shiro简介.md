# Apache Shiro 简介
**使用 Apache Shiro 为 web 应用程序进行用户身份验证**

	Shiro 是一个 Apache 顶级项目，旨在简化身份验证和授权。
	在本文中，了解 Apache Shiro 并通过示例来在一个 Groovy web 应用程序中尝试使用 Shiro 进行身份验证和授权。

Apache Shiro 是一个框架，可用于身份验证和授权。本文提供了几个示例用来展示如何在 Java™ 应用程序中使用 Shiro 并给出了如何在一个 Grails web 应用程序中使用它的概述。为了从本文中最大限度地受益，您应该习惯于创建 Java 应用程序并安装了如下的几个组件：
- Java 1.6 JDK
- Grails（用来运行这些 web 应用程序示例）

## 身份验证和授权

在对系统进行安全保障时，有两个安全性元素非常重要：身份验证和授权。

虽然这两个术语代表的是不同的含义，但出于它们在应用程序安全性方面各自的角色考虑，它们有时会被交换使用。

身份验证 指的是验证用户的身份。
- 在验证用户身份时，需要确认用户的身份的确如他们所声称的那样。在大多数应用程序中，身份验证是通过用户名和密码的组合完成的。只要用户选择了他人很难猜到的密码，那么用户名和密码的组合通常就足以确立身份。但是，还有其他的身份验证方式可用，比如指纹、证书和生成键。
一旦身份验证过程成功地建立起身份，授权 就会接管以便进行访问的限制或允许。 所以，有这样的可能性：用户虽然通过了身份验证可以登录到一个系统，但是未经过授权，不准做任何事情。
- 还有一种可能是用户虽然具有了某种程度的授权，却并未经过身份验证。

在为应用程序规划安全性模型时，必须处理好这两个元素以确保系统具有足够的安全性。身份验证是应用程序常见的问题（特别是在只有用户和密码组合的情况下），所以让框架来处理这项工作是一个很好的做法。合理的框架可提供经过测试和维护的优势，让您可以集中精力处理业务问题，而不是解决其解决方案已经实现的问题。
Apache Shiro 提供了一个可用的安全性框架，各种客户机都可将这个框架应用于它们的应用程序。本文中的这些例子旨在介绍 Shiro 并着重展示对用户进行身份验证的基本任务。
回页首

## 了解 Shiro
Shiro 是一个用 Java 语言实现的框架，通过一个简单易用的 API 提供身份验证和授权。使用 Shiro，您就能够为您的应用程序提供安全性而又无需从头编写所有代码。

### 下载并安装 Shiro
Shiro 是预构建的二进制发行版。您可以下载 Shiro JAR 文件或将各项放入到 Apache Maven 或 Apache Ivy 来自动安装这些文件。本例使用 Ivy 下载 Shiro JAR 文件以及其他所需要的库，脚本很简单，如 清单 1 所示。
#### 清单 1. Apache Ivy 文件和 Apache Ant 脚本
```xml
<?xml version="1.0" encoding="UTF-8"?>
<ivy-module version="2.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://ant.apache.org/ivy/schemas/ivy.xsd">
    <info organisation="com.nathanagood.examples" module="shirotest" />
    <configurations>
        <conf name="dist" description="Dependency configuration for distribution." />
    </configurations>
    <dependencies>
        <dependency org="commons-logging" name="commons-logging"
            rev="1.1.1" conf="dist->default" />
        <dependency org="org.slf4j" name="slf4j-log4j12" rev="1.5.8"
            conf="dist->default" />
        <dependency org="org.apache.shiro" name="shiro-core" rev="1.0.0-incubating"
            conf="dist->default" />
        <dependency org="org.apache.shiro" name="shiro-web" rev="1.0.0-incubating"
            conf="dist->default" />
    </dependencies>
</ivy-module>

<project name="shiroTestApp" default="usage" basedir="." 
    xmlns:ivy="antlib:org.apache.ivy.ant">
    <property name="project.lib" value="lib" />
    <path id="ivy.task.path">
        <fileset dir="${basedir}/ivy-lib">
            <include name="**/*.jar" />
        </fileset>
    </path>

    <target name="resolve">
        <taskdef resource="org/apache/ivy/ant/antlib.xml" 
            uri="antlib:org.apache.ivy.ant" classpathref="ivy.task.path" />
        <ivy:resolve />
        <ivy:retrieve pattern="${project.lib}/[conf]/[artifact].[ext]" sync="true" />
    </target>

    <target name="usage">
        <echo message="Use --projecthelp to learn more about this project" />
    </target>
</project>
```


有关使用 Ivy 的更多信息，参见 参考资料。如果不使用 Maven 或 Ivy，可用从本文 参考资料 部分提供的下载站点下载这些 Shiro JAR 文件。
下载了这些库后，只需将它们添加到 CLASSPATH。编写 清单 2 内所示的简单代码，它获得对当前用户的一个引用并报告说用户未经身份验证。（使用 Subject 类来代表此用户。）



#### 清单 2. ShiroTest Java 类
```java
package com.nathanagood.examples.shirotest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiroTest {

    private static Logger logger = LoggerFactory.getLogger(ShiroTest.class);

    public static void main(String[] args) {
        // Using the IniSecurityManagerFactory, which will use the an INI file
        // as the security file.
        Factory<org.apache.shiro.mgt.SecurityManager> factory = 
            new IniSecurityManagerFactory("auth.ini");

        // Setting up the SecurityManager...
        org.apache.shiro.mgt.SecurityManager securityManager 
            = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        Subject user = SecurityUtils.getSubject();

        logger.info("User is authenticated:  " + user.isAuthenticated());
    }
}
```

添加了此代码后，创建一个名为 auth.ini 的文件。此时，这个文件是空白的；它的作用只是为了能够在这里运行这个示例来检查代码是否正常工作。
创建文件后，运行这个示例。应该会看到包含了一个 INFO 登录消息的输出，报告说用户没有登录。
SecurityUtils 对象是一个 singleton，这意味着不同的对象可以使用它来获得对当前用户的访问。一旦成功地设置了这个 SecurityManager，就可以在应用程序不同部分调用 SecurityUtils.getSubject() 来获得当前用户的信息。
### 用户令牌
在 Shiro 术语中，令牌 指的是一个键，可用它登录到一个系统。最基本和常用的令牌是 UsernamePasswordToken，用以指定用户的用户名和密码。
UsernamePasswordToken 类实现了 AuthenticationToken 接口，它提供了一种获得凭证和用户的主体（帐户身份）的方式。 UsernamePasswordToken 适用于大多数应用程序，并且您还可以在需要的时候扩展 AuthenticationToken 接口来将您自己获得凭证的方式包括进来。例如，可以扩展这个接口来提供您应用程序用来验证用户身份的一个关键文件的内容。
### 简单的身份验证
至此，这个简单的示例涵盖的内容包括：启动 Shiro SecurityManager、获得当前用户并记录下用户未经身份验证。接下来的这个例子将会使用 UsernamePasswordToken 和存储在 INI 文件内的一个用户记录来展示如何通过用户名和密码进行身份验证。

[清单 3](http://www.ibm.com/developerworks/cn/web/wa-apacheshiro/#list3) 内所示的 auth.ini 文件现在包含了一个用户记录；这个记录包含了用户名和密码。您可以在这个记录内定义角色以及为应用程序提供授权。


#### 清单 3. auth.ini 文件
```shell
[users]
bjangles = dance
```
现在，创建 前一节 中介绍的这个 UsernamePasswordToken 对象，如 **清单 4** 所示。
#### 清单 4. 使用 UsernamePasswordToken 类
```java
// snipped... same as before.
public class ShiroTest {

    private static Logger logger = LoggerFactory.getLogger(ShiroTest.class);

    public static void main(String[] args) {
        // Using the IniSecurityManagerFactory, which will use the an INI file
        // as the security file.
        Factory<org.apache.shiro.mgt.SecurityManager> factory = 
            new IniSecurityManagerFactory("auth.ini");

        // Setting up the SecurityManager...
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        Subject user = SecurityUtils.getSubject();

        logger.info("User is authenticated:  " + user.isAuthenticated());
        
        UsernamePasswordToken token = new UsernamePasswordToken("bjangles", "dance");
        
        user.login(token);
        
        logger.info("User is authenticated:  " + user.isAuthenticated());
    }
}
```


UsernamePasswordToken 对象由用户名和密码的组合实例化。随后，令牌被传递至 Subject 类的 login() 方法。
再次运行这个示例。注意到登录消息现在报告说此用户已经身份验证。

要确保代码正常工作且获得的不是一个误报，在代码内更改密码或更改这个 INI 文件并再次运行此示例。login() 方法现在会抛出一个 IncorrectCredentialsException。在生产代码内这个异常应被明确捕获以便应用程序在用户提供了不正确的代码时能够进行恰当的响应。
如果用户不正确，login() 方法就会抛出一个 UnknownAccountException。我们既要考虑如何处理这个异常，但又不应向用户提供太多信息。一种常见的做法是不要向用户提示用户名有效、只有密码不正确。这是因为如果有人试图通过猜测获得访问，那么您绝对不会想要暗示此人他所猜测的用户名是正确的。
### 用 LDA目录访问协议） 进行身份验证
LDAP 是用来在 TCP/IP 上查询目录的一种协议。这些目录可保存有关用户的任意数量的信息，包括用户 ID、联系信息以及组成员等。LDAP 目录对于公司的通讯簿很有用并被广泛使用。
AD DS 是一种用于用户和组管理的常见目录，它支持 LDAP。 Shiro 不包含通用的 LDAP 安全域，但它却包含了一个 ActiveDirectoryRealm 对象，允许针对 LDAP 进行用户的身份验证。本例使用了在 INI 文件内配置的 ActiveDirectoryRealm 对象来验证用户的身份。虽然 AD DS 与 LDAP 不同，但本文中使用的 Shiro 的这个版本并没有自带通用的 LDAP 对象。
让一个 LDAP 服务器来测试本例要比编写并运行示例本身需要更多工作。如果您不能访问一个 AD DS 服务器，那么可以考虑下载并安装 Apache Directory 来提供一个 LDAP 服务器的样例实现。

Apache Directory 是用 Java 语言编写的。同样地，Apache Active Directory Studio 是一个 Eclipse 插件，可用来浏览 LDAP 数据。它还具有一些样例数据，为您提供了一种快捷方式来针对已知值编写代码，而又无需疑惑您遇到的问题是代码问题还是数据问题。
**清单 5** 显示了用来对存储在 Apache Directory 内的一个用户进行身份验证所需的代码。


#### 清单 5. 使用 LDAP 进行身份验证
```java
// snipped...
public class ShiroLDAPTest {

    private static Logger logger = LoggerFactory.getLogger(ShiroLDAPTest.class);

    /**
     * @param args
     */
    public static void main(String[] args) {

        // Using the IniSecurityManagerFactory, which will use the an INI file
        // as the security file.
        Factory<org.apache.shiro.mgt.SecurityManager> factory = 
            new IniSecurityManagerFactory("actived.ini");

        // Setting up the SecurityManager...
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        Subject user = SecurityUtils.getSubject();

        logger.info("User is authenticated:  " + user.isAuthenticated());

        UsernamePasswordToken token = 
        new UsernamePasswordToken(
            "cn=Cornelius Buckley,ou=people,o=sevenSeas", "argh");

        user.login(token);

        logger.info("User is authenticated:  " + user.isAuthenticated());
    }
}
```
除了 INI 文件名和用户名及密码之外，代码与之前用 INI 文件内的记录进行身份验证的代码相同。这种类似性的出现是因为您可以使用 INI 文件来配置 Shiro。用来设置 Shiro 针对 Apache Directory 进行身份验证的这些 INI 记录如 清单 6 所示。
#### 清单 6. actived.ini 文件
```shell
[main]
activeDirectoryRealm = org.apache.shiro.realm.activedirectory.ActiveDirectoryRealm
activeDirectoryRealm.systemUsername = uid=admin,ou=system
activeDirectoryRealm.systemPassword = secret
activeDirectoryRealm.searchBase = o=sevenSeas,ou=people
activeDirectoryRealm.url = ldap://localhost:10389
```
注意： 我使用 Apache Directory Studio 来将用户的密码更改为一个能放入测试代码以确保它工作的值。
### 运行一个 Grails web 应用程序
您可以使用两种基本技巧将 Shiro 应用到 web 应用程序。

- 首先，您可以使用这个 API 来将这里所展示的的代码并入一个基础 servlet。
- 其次，您可以使用 Shiro 自带的 HTTP 过滤器。

本例展示了第二种技巧，因为使用过滤器充分利用了内置 web 应用服务器技术以及来自 Shiro 项目的预先编写好的代码。
本例显示了如何使用 Grails 内的这些过滤器。Grails 是一个项目，旨在让您通过使用一种惯例优先原则（convention-over-configuration）的方式尽快地编写 Groovy web 应用程序。


对于 Shiro 过滤器，您通常会手动地向 web.xml 文件添加必需的过滤器项。但是，Grails 会在您每次启动应用程序时生成 web.xml 文件，因此不必手动修改 web.xml。
幸运的是，Grails 提供了插件，可集成到 web.xml 生成过程并会让您也可以参与在 web.xml 文件内编写这些项。

如今，Grails 的可用插件很多，包括面向 Shiro 的这个插件。建议尝试使用这个 Shiro Grails 插件，它提供了几个新的脚本，运行这些脚本可以创建不同的域和控制器。
或者，如果您更愿意自己添加这些项并进行配置，您也可以编写您自己的插件。对于 Grails，编写一个新的插件很容易。要创建能将必需的 Shiro 过滤器项添加到 web.xml 文件的 Grails 插件，可以使用如下命令：

> grails create-plugin ShiroWebXml

创建了这个插件项目后，编辑 ShiroWebXmlPlugin.groovy 文件，并添加如 **清单 7** 所示的代码。
#### 清单 7. 示例插件
```groovy
class ShiroWebXmlPlugin {
   
   // snipped plugin details... 

   def doWithWebDescriptor = { xml ->

       def filterElement = xml.'filter'
       def lastFilter = filterElement[filterElement.size() - 1]

       lastFilter + {
           'filter' {
               'filter-name'("ShiroFilter")
               'filter-class'("org.apache.shiro.web.servlet.IniShiroFilter")
               'init-param' {
                   'param-name'("config")
                   'param-value'("\n#config")
               }
           }
       }

       def filterMappingElement = xml.'filter-mapping'
       def lastFilterMappingElement = 
           filterMappingElement[filterMappingElement.size() - 1]

       lastFilterMappingElement + {
           'filter-mapping' {
               'filter-name'("ShiroFilter")
               'url-pattern'("/*")
               }
           }
       }
}
```

当 Grails 执行这个 web.xml 文件时，此代码就会运行。

在启动这个插件应用程序测试它之前，先将用 Ivy 下载的 Shiro JAR 文件复制到此插件的 lib 文件夹。有了 JAR 文件后，用以下命令测试此插件是否能正常工作：
```
grails run-app
```
如果此插件应用程序成功启动，您可以将它打包以便用在一个示例项目中。要打包这个插件，使用如下命令：
```
grails package-plugin
```
可以用如下命令安装这个新的 ShiroWebXmlPlugin：
```
cd myapp
grails install-plugin /path/to/shiro-web-xml-1.0.zip
```


如果出现了 UnavailableSecurityManagerException，那么可能是 SecurityManager 未被正确设置。在调用 getSubject() 方法之前，请确保它设在 SecurityUtils 对象上。
连接到 LDAP 服务器可能会有些困难。如果得到一个 javax.naming.CommunicationException，那么请检查此 LDAP 服务器的主机名和端口。即便您没有使用 Apache Directory，Apache Directory Studio（可单独安装）也可以帮助您排除连接故障以及名称问题。
如果您没有使用 Ant Ivy 脚本来初始化您的环境，那么有可能会得到丢失类的错误。即便没有 Apache Commons Logging library (commons-logging)，这个 INI 文件示例仍可运行，但是运行这个 LDAP 示例会导致一个异常。这时，可以使用 Apace Commons BeanUtils 来解析这个 INI 文件并设置对象上的值。


Shiro 是 Apache的一个框架，可让您向应用程序中添加身份验证和授权。它支持不同的身份验证存储方式，比如 LDAP、Kerberos 和 AD DS。Shiro 最小限度的依赖性再加上它相对简单的配置让它成为了应用程序内安全性框架的一个很好的选择。