---
layout: post
title: 19.hadoop-2.7.2官网文档翻译-HDFS命令指南
category: 技术
tags:  Hadoop
keywords: 
description: HDFS命令指南。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html
---

{:toc}

## 概览

所有的HDFS命令都是调用`bin/hdfs`脚本。无参数调用hdfs脚本会打印所有命令的描述。

用法:`hdfs [SHELL_OPTIONS] COMMAND [GENERIC_OPTIONS] [COMMAND_OPTIONS]`

Hadoop有一个选项的解析框架，采用解析通用选项以及运行类。

|命令选项|描述|
|---|---|
|--config  --loglevel|shell选项的命令集，该文档在[命令手册](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/CommandsManual.html#Overview){:target="_blank"}页|
|GENERIC_OPTIONS|支持多个命令选项的通用集，请看[命令手册](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/CommandsManual.html#Overview){:target="_blank"}页|
|COMMAND COMMAND_OPTIONS|在下面的几段中会有各种命令和其选项的描述。命令被分组为用户命令和管理员命令|


## 用户命令

Hadoop集群的用户使用的命令

### classpath 

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



### dfs 

**用法:**`hdfs dfs [COMMAND [COMMAND_OPTIONS]]`

在Hadoop支持的文件系统上运行文件系统命令，许多`COMMAND_OPTIONS`可以在[文件系统shell指南]({% post_url 2016-07-05-5-hadoop-doc-translate %}){:target="_blank"}中找到。


### fetchdt

**用法:**` hdfs fetchdt [--webservice &lt namenode_http_addr&gt ] &lt path&gt `

|命令选项|描述|
|---|---|
|--webservice https_address|使用http协议替代RPC|
|fileName|存储token的文件名|

从NameNode获取授权token，可以查看[fetchdt]({% post_url 2016-07-20-18-hadoop-doc-translate %}#title14){:target="_blank"}



### fsck

**用法:**

```bash
hdfs fsck &lt path&gt 
          [-list-corruptfileblocks |
          [-move | -delete | -openforwrite]
          [-files [-blocks [-locations | -racks]]]
          [-includeSnapshots]
          [-storagepolicies] [-blockId &lt blk_Id&gt ]
```

|命令选项|描述|
|---|---|
|path|开始从该路径检查|
|-delete|删除损坏的问价|
|-files|打印出被检查的文件|
|-files -blocks|打印出块报告|
|-files -blocks -locations|打印出每个块的位置|
|-files -blocks -racks|打印出DataNode位置的网络拓扑|
|-includeSnapshots|如果给定的路径指明了快照目录或在快照目录下，则包含快照数据|
|-list-corruptfileblocks|打印出丢失的块的列表和他们包含的文件。|
|-move|将损坏的文件移到/lost+found|
|-openforwrite|打印打开的文件|
|-storagepolicies|打印出块的存储策略汇总|
|-blockId|打印出块的信息|

运行HDFS文件系统检查功能。更多信息请查看[fsck]({% post_url 2016-07-20-18-hadoop-doc-translate %}#title13){:target="_blank"}


### getconf

**用法:**

```bash
   hdfs getconf -namenodes
   hdfs getconf -secondaryNameNodes
   hdfs getconf -backupNodes
   hdfs getconf -includeFile
   hdfs getconf -excludeFile
   hdfs getconf -nnRpcAddresses
   hdfs getconf -confKey [key]
```

|命令选项|描述|
|---|---|
|-namenodes	|获取集群的NameNode列表|
|-secondaryNameNodes|获取集群的secondaryNameNode列表|
|-backupNodes|获取集群备份节点列表|
|-includeFile|获取定义可以加入集群的DataNode包含的文件路径|
|-excludeFile|获取定义可以加入集群的DataNode排除的文件路径|
|-nnRpcAddresses|获取NameNode的RPC地址|
|-confKey [key]|从配置文件出获取指定的key|

从配置目录，后处理程序中获取配置信息

### groups

**用法:**`hdfs groups [username ...]`

返回给定的一个或多个用户的组信息


### lsSnapshottableDir

**用法:**`hdfs lsSnapshottableDir [-help]`

|命令选项|描述|
|---|---|
|-help|显示帮助信息|

获取snapshottable目录的列表，在以超级用户运行时，会返回所有snapshottable 的目录，否则返回你那些属于当前用户的目录。


### jmxget

**用法:**`hdfs jmxget [-localVM ConnectorURL | -port port | -server mbeanserver | -service service]`

|命令选项|描述|
|---|---|
|-help|显示帮助信息|
|-localVM ConnectorURL|在同一机器上连接VM|
|-port mbean server port|指定mbean的服务端口，如果没有指定，会尝试连接同一VM的MBean服务|
|-service|默认指定JMS服务，DataNode或NameNode|

丢弃来自于某个服务的JMX信息

### oev(offline edits viewer)



**用法:**`hdfs oev [OPTIONS] -i INPUT_FILE -o OUTPUT_FILE`

**必选的命令行参数**

|命令选项|描述|
|---|---|
|-i,--inputFile arg|要处理的edits文件，扩展名为xml意味着是xml格式，任何其他的文件名都意味着是二进制格式|
|-o,--outputFile arg|输出文件的名称。如果指定文件存在，会被覆盖。格式文件可以使用`-p`选项定义。|

**可选的命令行参数**

|命令选项|描述|
|---|---|
|-f,--fix-txids|在输入时重新编号传输的id，这样就没有缺失或非法的传输id了|
|-h,--help|显示用法信息并退出|
|-r,--ecover|当读取二进制的edits日志时，使用恢复模式。这将让你选择跳过edits日志的损坏的部分。|
|-p,--processor arg|选择适合image文件的处理类型，当前支持的是二进制（Hadoop用户的原生二进制格式）、xml（默认，xml格式）和stats（显示edits文件的统计信息）|
|-v,--verbose|更多冗长的输出，打印输入和输出文件名。对于写文件的处理器，也显示在屏幕上。在巨大的image文件时该操作将会增加处理的时间，默认为false|

Hadoop离线edits查看器。

### oiv(offline image viewer)

**用法:**`hdfs oiv [OPTIONS] -i INPUT_FILE`


**必选的命令行参数**

|命令选项|描述|
|---|---|
|-i,--inputFile arg|要处理的edits文件，扩展名为xml意味着是xml格式，任何其他的文件名都意味着是二进制格式|

**可选的命令行参数**

|命令选项|描述|
|---|---|
|-h,--help|显示用法信息并退出|
|-o,--outputFile arg|输出文件的名称。如果指定文件存在，会被覆盖。格式文件可以使用`-p`选项定义。|
|-p,--processor arg|选择适合image文件的处理类型，当前支持的是二进制（Hadoop用户的原生二进制格式）、xml（默认，xml格式）和stats（显示edits文件的统计信息）|

Hadoop离线镜像查看器查看较新的镜像文件。

### oiv_legacy

**用法:**` hdfs oiv_legacy [OPTIONS] -i INPUT_FILE -o OUTPUT_FILE`

|命令选项|描述|
|---|---|
|-h,--help|显示用法信息并退出|
|-i,--inputFile arg|要处理的edits文件，扩展名为xml意味着是xml格式，任何其他的文件名都意味着是二进制格式|
|-o,--outputFile arg|输出文件的名称。如果指定文件存在，会被覆盖。格式文件可以使用`-p`选项定义。|

对于Hadoop较老版本的Hadoop离线镜像查看器

### snapshotDiff

**用法:**` hdfs snapshotDiff &lt path&gt  &lt fromSnapshot&gt  &lt toSnapshot&gt `

判断Hadoop快照间的不用。更多信息请看[hadoop 快照文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsSnapshots.html#Get_Snapshots_Difference_Report){:target="_blank"}



### version

**用法:**` hdfs version`

显示Hadoop的版本。


## 管理员命令

对Hadoop集群的管理员有用的命令


### balancer（均衡器）

**用法：**

```bash
    hdfs balancer
          [-threshold &lt threshold&gt ]
          [-policy &lt policy&gt ]
          [-exclude [-f &lt hosts-file&gt  | &lt comma-separated list of hosts&gt ]]
          [-include [-f &lt hosts-file&gt  | &lt comma-separated list of hosts&gt ]]
          [-idleiterations &lt idleiterations&gt ]
```


|命令选项|描述|
|---|---|
|-policy &lt policy&gt |DataNode（默认）：如果每个DataNode都均衡了，那集群就均衡了。  blockpool：如果每个块池中的每个DataNode都均衡了，那么集群也就均衡了。|
|-threshold &lt threshold&gt | 临界值，磁盘容量的百分比。会覆盖默认的临界值|
|-exclude -f &lt hosts-file&gt  \ &lt comma-separated list of hosts &gt|从均衡器中移除指定的需要均衡的DataNode|
|-include -f &lt hosts-file&gt  \  &lt comma-separated list of hosts &gt|均衡器中只包含指定的需要被均衡的DataNode|
|-idleiterations &lt iterations&gt |退出前最大的空闲迭代数量，会覆盖默认的空闲迭代(5)|

运行集群均衡的功能。管理员可以简单的按`ctrl+c`停止均衡进程。更多详细信息请看[均衡器]({% post_url 2016-07-20-18-hadoop-doc-translate %}#title10){:target="_blank"}

**注意：**`blockpool`策略比`DataNode`上的策略更为严格。

### cacheadmin

**用法：**`hdfs cacheadmin -addDirective -path &lt path&gt  -pool &lt pool-name&gt  [-force] [-replication &lt replication&gt ] [-ttl &lt time-to-live&gt ]`

更多信息请看[HDFS 缓存管理文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/CentralizedCacheManagement.html#cacheadmin_command-line_interface){:target="_blank"}


### crypto

**用法：**

```bash
  hdfs crypto -createZone -keyName &lt keyName&gt  -path &lt path&gt 
  hdfs crypto -help &lt command-name&gt 
  hdfs crypto -listZones

```

更多信息请看[HDFS透明加密文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/TransparentEncryption.html#crypto_command-line_interface){:target="_blank"}



### datanode

**用法：**`hdfs datanode [-regular | -rollback | -rollingupgrace rollback]`



|命令选项|描述|
|---|---|
|-regular|正常的DataNode启动(默认)|
|-rollback|将DataNode回滚到之前的版本。在停止DataNode和部署旧版Hadoop时使用|
|-rollingupgrade rollback|回滚滚动升级的操作|

运行HDFSDataNode

### dfsadmin

**用法：**

```
    hdfs dfsadmin [GENERIC_OPTIONS]
          [-report [-live] [-dead] [-decommissioning]]
          [-safemode enter | leave | get | wait]
          [-saveNamespace]
          [-rollEdits]
          [-restoreFailedStorage true |false |check]
          [-refreshNodes]
          [-setQuota &lt quota&gt  &lt dirname&gt ...&lt dirname&gt ]
          [-clrQuota &lt dirname&gt ...&lt dirname&gt ]
          [-setSpaceQuota &lt quota&gt  &lt dirname&gt ...&lt dirname&gt ]
          [-clrSpaceQuota &lt dirname&gt ...&lt dirname&gt ]
          [-setStoragePolicy &lt path&gt  &lt policyName&gt ]
          [-getStoragePolicy &lt path&gt ]
          [-finalizeUpgrade]
          [-rollingUpgrade [&lt query&gt  |&lt prepare&gt  |&lt finalize&gt ]]
          [-metasave filename]
          [-refreshServiceAcl]
          [-refreshUserToGroupsMappings]
          [-refreshSuperUserGroupsConfiguration]
          [-refreshCallQueue]
          [-refresh &lt host:ipc_port&gt  &lt key&gt  [arg1..argn]]
          [-reconfig &lt datanode |...&gt  &lt host:ipc_port&gt  &lt start |status&gt ]
          [-printTopology]
          [-refreshNamenodes datanodehost:port]
          [-deleteBlockPool datanode-host:port blockpoolId [force]]
          [-setBalancerBandwidth &lt bandwidth in bytes per second&gt ]
          [-allowSnapshot &lt snapshotDir&gt ]
          [-disallowSnapshot &lt snapshotDir&gt ]
          [-fetchImage &lt local directory&gt ]
          [-shutdownDatanode &lt datanode_host:ipc_port&gt  [upgrade]]
          [-getDatanodeInfo &lt datanode_host:ipc_port&gt ]
          [-triggerBlockReport [-incremental] &lt datanode_host:ipc_port&gt ]
          [-help [cmd]]
```



|命令选项|描述|
|---|---|
|-report [-live] [-dead] [-decommissioning]|基本文件系统信息和统计嘻嘻报告。选项flags可能用来过滤显示的DataNode列表 |
|-report [-live] [-dead] [-decommissioning]|安全模式维护命令。安全模式是NameNode的一种状态。 1.不接受命名空间的改变（只读）；2，不会副本和删除块；在NameNode启动时，自动进入安全模式，并在配置的最小百分比的块满足最小复制条件时自动离开安全模式。安全模式也可以手动进入，而且也能手动关闭|
|-saveNamespace|将当前的命名空间保存在存储目录中，并充值edits日志。需要安全模式|
|-rollEdits|在激活的NameNode上滚动edits日志|
|-restoreFailedStorage true/false/check|该选项会自动开/关来尝试恢复失败的存储副本，如果失败的存储又可以访问了，系统将会在检查点期间尝试恢复edits或者fsimage。“check”选项将会返回当前的设置|
|-refreshNodes|重新读取hosts和排除文件来更新允许连接到NameNode上和那些退役或重新启用的DataNode的集合。|
|-setQuota &lt quota&gt  &lt dirname&gt …&lt dirname&gt |详情请看[HDFS配额指南 ](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsQuotaAdminGuide.html#Administrative_Commands){:target="_blank"}|
|-clrQuota &lt dirname&gt …&lt dirname&gt |详情请看[HDFS配额指南 ](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsQuotaAdminGuide.html#Administrative_Commands){:target="_blank"}|
|-setSpaceQuota &lt quota&gt  &lt dirname&gt …&lt dirname&gt |详情请看[HDFS配额指南 ](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsQuotaAdminGuide.html#Administrative_Commands){:target="_blank"}|
|-clrSpaceQuota &lt dirname&gt …&lt dirname&gt |详情请看[HDFS配额指南 ](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsQuotaAdminGuide.html#Administrative_Commands){:target="_blank"}|
|-setStoragePolicy &lt path&gt  &lt policyName&gt |设置文件或目录的存储策略|
|-getStoragePolicy &lt path&gt |获取文件或目录的存储策略|
|-finalizeUpgrade|完成HDFS的升级。在NameNode删除它们以前版本的工作目录后，DataNode会做同样的操作。|
|-rollingUpgrade [&lt query&gt /&lt prepare&gt /&lt finalize&gt ]|详情请看[滚动升级文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsRollingUpgrade.html#dfsadmin_-rollingUpgrade){:atrget="_blank"}|
|-metasave filename|将NameNode主数据结构保存到`hadoop.log.dir`属性指定的目录中的文件。如果文件filename存在会被覆盖。每个线面的情况文件filename都会保存一行：1.DataNode到NameNode的心跳；2.等待被复制的块；3.当前被复制的块；4.等待被删除的块|
|-refreshServiceAcl|重新加载服务级别的认证策略文件|
|-refreshUserToGroupsMappings|刷新用户到组的映射|
|-refreshSuperUserGroupsConfiguration|刷新超级用户代理组的映射|
|-refreshCallQueue|从配置中重新加载call队列|
|-refresh &lt host:ipc_port&gt  &lt key&gt  [arg1..argn]|触发参数key指定的在“host:ipc_port”上的资源运行时刷新服务。后面的所有其他参数将会被发送到该主机|
|-reconfig &lt datanode /…&gt  &lt host:ipc_port&gt  &lt start/status&gt |开启配置或者获取运行中的配置的状态。第二个参数指定node的类型，只支持DataNode的配置|
|-printTopology|打印架构树，并且它们的节点会被NameNode报告|
|-refreshNamenodes datanodehost:port|对于给定的DataNode，重新加载配置文件，停止被删除的块池服务，并启动新的块池服务。|
|-deleteBlockPool datanode-host:port blockpoolId [force]|如果force被设置，给定的DataNode上的块池id的块池目录会连带内容被删除。否则只有在目录为空时删除。如果DataNode的块池仍然在服务中，该命令会失败。参考`refreshNamenodes`来关闭DataNode上的某个块池服务。|
|-setBalancerBandwidth &lt bandwidth in bytes per second&gt |改变每个DataNode使用HDFS块平衡的网络带宽。`bandwidth`是每个DataNode每秒使用的最大字节数。该值会覆盖`dfs.balance.bandwidthPerSec`参数。**注意：**新值不会持久化到DataNode中。|
|-allowSnapshot &lt snapshotDir&gt | 允许目录快照被创建。如果操作成功完成，目录将会变成可快照的|
|-disallowSnapshot &lt snapshotDir&gt |不允许目录快照被创创建。在禁用快照之前，所有的目录快照都必须被删除。详情请看[HDFS 快照文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsSnapshots.html){:target="_blank"}|
|-fetchImage &lt local directory&gt |从NameNode上下载最近的fsimage，并将他们保存在指定的本地目录中。|
|-shutdownDatanode &lt datanode_host:ipc_port&gt  [upgrade]|向给定的DataNode提交关闭请求。详情请看[滚动升级文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsRollingUpgrade.html#dfsadmin_-shutdownDatanode){:target=+_blank}|
|-getDatanodeInfo &lt datanode_host:ipc_port&gt |获取给定的DataNode信息。详情请看[滚动升级文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsRollingUpgrade.html#dfsadmin_-shutdownDatanode){:target=+_blank}|
|-triggerBlockReport [-incremental] &lt datanode_host:ipc_port&gt |触发对给定的DataNode的块报告。如果‘incremental’被指定。将会被覆盖，将会是完整的块报告。|
|-help [cmd]|显示给定命令的帮助信息，如果没有指定会显示所有命令的信息。|

运行HDFS dfsadmin 客户端


### haadmin

**用法：**

```
    hdfs haadmin -checkHealth <serviceId>
    hdfs haadmin -failover [--forcefence] [--forceactive] <serviceId> <serviceId>
    hdfs haadmin -getServiceState <serviceId>
    hdfs haadmin -help <command>
    hdfs haadmin -transitionToActive <serviceId> [--forceactive]
    hdfs haadmin -transitionToStandby <serviceId>

```



|命令选项|描述|
|---|---|
|-checkHealth|检查给定NameNode的健康状态|
|-failover|启动两个节点的故障转移|
|-getServiceState|判断给定的NameNode是激活还是备用|
|-transitionToActive|将给定NameNode的状态转变为激活|
|-transitionToStandby|将给定NameNode的状态转变为备用|

更多信息请查看[ HDFS HA with NFS ](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithNFS.html#Administrative_commands){:target="_blank"}或者[ HDFS HA with QJM ](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html#Administrative_commands){:target="_blank"}。


### journalnode

**用法：**`hdfs journalnode`


该命令启动一个journalnode节点，请参考[ HDFS HA with QJM ](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html#Administrative_commands){:target="_blank"}


### mover

**用法：**`hdfs mover [-p <files/dirs> | -f <local file name>]`



|命令选项|描述|
|---|---|
|-f <local file>|指定一个本地文件来包含要迁移的HDFS文件和目录的列表。|
|-p <files/dirs>|指定要迁移的HDFS文件或目录的空格分隔的列表|

运行数据迁移功能,详情请看[mover](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/ArchivalStorage.html#Mover_-_A_New_Data_Migration_Tool){:target="_blank"}

**注意：**`-p`和`-f`选项省略时，默认路径是root目录

### namenode（均衡器）

**用法：**

```
  hdfs namenode [-backup] |
          [-checkpoint] |
          [-format [-clusterid cid ] [-force] [-nonInteractive] ] |
          [-upgrade [-clusterid cid] [-renameReserved<k-v pairs>] ] |
          [-upgradeOnly [-clusterid cid] [-renameReserved<k-v pairs>] ] |
          [-rollback] |
          [-rollingUpgrade <downgrade |rollback> ] |
          [-finalize] |
          [-importCheckpoint] |
          [-initializeSharedEdits] |
          [-bootstrapStandby] |
          [-recover [-force] ] |
          [-metadataVersion ]

```



|命令选项|描述|
|---|---|
|-backup|启动备份节点|
|-checkpoint|启动检查点节点|
|-format [-clusterid cid] [-force] [-nonInteractive]|格式化指定的纳闷，如果启动NameNode，格式化然后将其关闭。'-force'选项在name目录存在时也会强制格式化。' -nonInteractive'选项在Name目录存在时会终端操作，除非指定'-force'选项。|
|-upgrade [-clusterid cid] [-renameReserved <k-v pairs>]|在部署了新版本的Hadoop后，NameNode应该使用升级选项启动。|
|-upgradeOnly [-clusterid cid] [-renameReserved <k-v pairs>]|升级指定的NameNode然后将其关闭。|
|-rollback|将NameNode回滚到以前的版本。该命令在停止集群并部署了旧的Hadoop版本后使用。|
|-rollingUpgrade &lt downgrade/rollback/started &gt|详情请看[滚动升级](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsRollingUpgrade.html#NameNode_Startup_Options){:target="_blank"}|
|-finalize|该选项将会删文件系统以前的状态，最近的升级将会成为固定态。回滚操作将不可用。完成后会关闭NameNode|
|-importCheckpoint|从一个检查点目录加载镜像并将其保存在当前一个点上。检查点目录是从`fs.checkpoint.dir`属性读取。|
|-initializeSharedEdits|格式化一个新的分享edits目录并在足够的edit日志段中赋值。因此备用NameNode可以启动。|
|-bootstrapStandby|允许备用NameNode存储目录自助的从激活的NameNode上复制最新的命名空间快照。该命令在第一次配置高可用集群时使用。|
|-recover [-force]|从中断的文件系统上回复丢失的元数据。详情请看[HDFS用户指南]({% post_url 2016-07-20-18-hadoop-doc-translate %}#title15){:target="_blank"}|
|-metadataVersion|核实配置目录存在，然后打印出软件和镜像的元数据版本。|

运行NameNode，更多关于升级，回滚和完成清理的信息请看[HDFS用户指南]({% post_url 2016-07-20-18-hadoop-doc-translate %}#title16){:target="_blank"}

### nfs3

**用法：**`hdfs nfs3`

该命令会启用NFS3网关，请看[ HDFS NFS3 服务](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsNfsGateway.html#Start_and_stop_NFS_gateway_service){:target="_blank"}



### portmap

**用法：**`hdfs portmap`

该命令启用RPC的端口映射，请看[ HDFS NFS3 服务](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsNfsGateway.html#Start_and_stop_NFS_gateway_service){:target="_blank"}


### secondarynamenode

**用法：**`hdfs secondarynamenode [-checkpoint [force]] | [-format] | [-geteditsize]`



|命令选项|描述|
|---|---|
|-checkpoint [force]|如果edits日志大小超过`fs.checkpoint.size`设置大小，将会为SecondaryNameNode设置检查点。如果使用了`force`,检查点将不考虑edit日志大小。|
|-format|在启动时格式化本地存储|
|-geteditsize|打印NameNode上未经检查点处理的数值|

运行HDFS的Secondary的NameNode。请看[HDFS用户指南]({% post_url 2016-07-20-18-hadoop-doc-translate %}#title6){:target="_blank"}

### storagepolicies

**用法：**`hdfs storagepolicies`

列出所有的存储策略，更多信息请看[HDFS存储策略文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/ArchivalStorage.html){:target="_blank"}



### zkfc

**用法：**`hdfs zkfc [-formatZK [-force] [-nonInteractive]]`



|命令选项|描述|
|---|---|
|-formatZK|格式化ZooKeeper实例|
|-h|显示帮助信息|



## Debug 命令

该命令对帮助管理员调试HDFS问题很有用，向校验块文件和调用恢复权。

### verify

**用法：**`hdfs debug verify [-meta <metadata-file>] [-block <block-file>]`



|命令选项|描述|
|---|---|
|-block block-file|可选项，指定本地文件系统的数据节点上的块文件的绝对路径。|
|-meta metadata-file|本地文件系统的数据节点的元数据文件的绝对路径。|

验证HDFS元数据和块文件。如果指定了块文件，会校验元数据文件内的校验和是否与块文件匹配。

### recoverLease

**用法：**`hdfs debug recoverLease [-path <path>] [-retries <num-retries>]`



|命令选项|描述|
|---|---|
|[-path path]|恢复租约的HDFS路径|
|[-retries num-retries]|客户端尝试调用`recoverLease`的次数。默认次数为1|


恢复指定路径上的租约。路径必须在HDFS文件系统中存在。默认重试的次数为1。

