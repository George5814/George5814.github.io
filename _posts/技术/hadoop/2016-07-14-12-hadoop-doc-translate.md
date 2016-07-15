---
layout: post
title: 12.hadoop-2.7.2官网文档翻译-机架感知
category: 技术
tags:  Hadoop
keywords: 
description: Hadoop机架感知。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/RackAwareness.html
---

{:toc}

### 机架感知

Hadoop的组件是机架感知的。
举例来说，HDFS块安置将会使用机架感知来通过在不同机架上放置一个块复制进行容错。
这提供了在集群内的网络交换机故障或分区的情况下的数据可用性。

Hadoop主守护进程通过调用外部脚本或者配置文件中指定的java类获取到集群从节点的机架id。
不管是拓扑使用java类还是外部脚本，数据必须遵守java接口`org.apache.hadoop.net.DNSToSwitchMapping`。
该接口预计将保持一对一的对应，并且在`/myrack/myhost`格式的拓扑信息，其中'/'为拓扑分隔符，'myrack'为机架分隔符，'myhost'是合法的主机。
假设每个机架是简单的/24子网，可以使用`/192.168.100.0/192.168.100.5`这样的格式作为唯一的机架主机的拓扑映射。

为了在拓扑映射中使用java类，java名称被配置文件中`topology.node.switch.mapping.impl`参数指定。举个例子，`NetworkTopology.java`包含在Hadoop的发布版本中，并且可以被Hadoop管理员自定义。
使用java类替换外部脚本的性能优势就是当新的从节点注册时，Hadoop不需要fork一个外部的进程。

如果实现一个外部脚本，可以使用配置文件中`topology.script.file.name`参数指定它。
不同于java类，外部拓扑脚本不包括在Hadoop的发行版本中，并且是管理员提供的。在fork拓扑脚本时，Hadoop将会给ARGV发送多个ip地址。
发送到拓扑脚本的IP地址的数量会被`net.topology.script.number.args`参数控制，默认为100。如果`net.topology.script.number.args`被改变为1，拓扑脚本在DataNode或者NodeManager的每个IP提交时会被fork。

如果`topology.script.file.name`或者` topology.node.switch.mapping.impl`没有被设置，对任何通过的IP地址，机架id`/default-rack`会被返回。
虽然这种行为似乎是可取的，但它可能会造成HDFS块复制的问题。因为默认行为是写一个复制的块关闭机架并且是无法做到的，因为只有一个叫`/default-rack`机架。

另外一种额外配置是`mapreduce.jobtracker.taskcache.levels`,它决定了将会被使用的缓存MapReduce的级别(在网络拓扑中)的数量。因此，举例来说，如果默认值四e，两个级别的缓存将会被构建--一个为了主机(主机->任务映射)而另一个是为机架(机架->任务映射)。
给我们的一对一映射`/myrack/myhost`。

### python举例

```python
#!/usr/bin/python
# this script makes assumptions about the physical environment.
#  1) each rack is its own layer 3 network with a /24 subnet, which
# could be typical where each rack has its own
#     switch with uplinks to a central core router.
#
#             +-----------+
#             |core router|
#             +-----------+
#            /             \
#   +-----------+        +-----------+
#   |rack switch|        |rack switch|
#   +-----------+        +-----------+
#   | data node |        | data node |
#   +-----------+        +-----------+
#   | data node |        | data node |
#   +-----------+        +-----------+
#
# 2) topology script gets list of IP's as input, calculates network address, and prints '/network_address/ip'.

import netaddr
import sys
sys.argv.pop(0)                                                  # discard name of topology script from argv list as we just want IP addresses

netmask = '255.255.255.0'                                        # set netmask to what's being used in your environment.  The example uses a /24

for ip in sys.argv:                                              # loop over list of datanode IP's
address = '{0}/{1}'.format(ip, netmask)                      # format address string so it looks like 'ip/netmask' to make netaddr work
try:
   network_address = netaddr.IPNetwork(address).network     # calculate and print network address
   print "/{0}".format(network_address)
except:
   print "/rack-unknown"                                    # print catch-all value if unable to calculate network address
```


### bash举例

```bash
#!/bin/bash
# Here's a bash example to show just how simple these scripts can be
# Assuming we have flat network with everything on a single switch, we can fake a rack topology.
# This could occur in a lab environment where we have limited nodes,like 2-8 physical machines on a unmanaged switch.
# This may also apply to multiple virtual machines running on the same physical hardware.
# The number of machines isn't important, but that we are trying to fake a network topology when there isn't one.
#
#       +----------+    +--------+
#       |jobtracker|    |datanode|
#       +----------+    +--------+
#              \        /
#  +--------+  +--------+  +--------+
#  |datanode|--| switch |--|datanode|
#  +--------+  +--------+  +--------+
#              /        \
#       +--------+    +--------+
#       |datanode|    |namenode|
#       +--------+    +--------+
#
# With this network topology, we are treating each host as a rack.  This is being done by taking the last octet
# in the datanode's IP and prepending it with the word '/rack-'.  The advantage for doing this is so HDFS
# can create its 'off-rack' block copy.
# 1) 'echo $@' will echo all ARGV values to xargs.
# 2) 'xargs' will enforce that we print a single argv value per line
# 3) 'awk' will split fields on dots and append the last field to the string '/rack-'. If awk
#    fails to split on four dots, it will still print '/rack-' last field value

echo $@ | xargs -n 1 | awk -F '.' '{print "/rack-"$NF}'
```
