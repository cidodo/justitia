# Justitia
Justitia可以帮助联盟成员生成和维护自己的全部证书、简单方便的部署和维护自己的节点。另一方面，它可以帮助使用者管理联盟和通道的相关配置，其中包括成员和策略等配置。  

最初，我们设想创建一个大的联盟链，联盟的所有参与者相互平等。他们各自维护自己在网络上的相关节点和证书，并且利用fabric多通道的特性联盟中部分或全部成员可以在不同通道内进行独立的业务合作。于是我们便开始尝试去构建这样一个网络，但是在实际操作过程中我们发现以运行脚本的方式去部署fabric节点的过程中配置复杂且容易出错，并且整个联盟难以扩展和维护。因此我们迫切需要一个工具来帮我们管理Fabric网络上的身份、节点、通道和联盟成员。
开发Justitia是为了帮助客户更加简单的部署fabric节点，以便于客户更加方便的加入到我们的业务中来。Justitia不仅可以帮助联盟成员管理他在联盟中全部的身份数据，还可以帮助使用者部署fabric节点、动态创建通道和管理通道以及联盟的成员。


## 开发环境
fabric管理采用spring boot + React实现前后端分离。后端部分是一个Maven项目，安装开发环境需要满足以下条件：
- JDK 1.8+
- Maven 3
- Mysql 5.7+ 
- Docker server 1.11.x
- Docker remote API v1.23
- fabric 1.3

## 部署准备
我们提供了预览[截图](https://github.com/shijinshi/justitia/tree/master/doc/screenshot.md)在您部署前可以查看项目现有情况，已决定要不要使用。

1. 项目运行前需要准备至少一个fabric环境配置完整的主机（或虚拟机）作为fabric节点的运行环境，环境配置需要具备以下要求
    - Docker server 1.11.x
    - Docker remote API v1.23
    - fabric 1.3
2. 项目运行环境必须和运行节点的宿主机在同一网络环境下，以确保程序能够请求到节点宿主机的docker服务。
### 配置主机Docker环境
远程的宿主机启用docker服务有不使用TLS验证和使用TLS验证两种方式，推荐开启TLS验证。
1. 使用TLS验证
使用TLS验证开启docker服务，参考以下文章
https://blog.csdn.net/qq_36956154/article/details/82180551
2. 不使用TLS验证 
不使用TLS验证的方式并不安全，网络内任何人都可以通过docker侵入宿主机。这种方式强烈不推荐。具体开启docker server方式如下： 
打开docker server配置文件
```
vi /lib/systemd/system/docker.service
```
找到Execstart=/usr/bin/dockerd后加上
```
-H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock
```
### 配置主机fabric环境
在节点宿主机上运行“downloadfabricimage.sh”脚本下载fabric相关镜像
#### 在Peer镜像中增加系统链码CMSCC
由于本项目基于Fabric 1.3版本开发，在1.3版本时通道任意成员都可以实例化和升级合约致使合约的版本和内容难以保持一直。为了保证
通道成员管理的整个审批流程不被篡改，而且该审批流程不会发生变化。多以选择将CMSCC作为系统链码部署到Peer中。  
在Peer中增加系统链码教程可参考[系统链码开发](https://github.com/shijinshi/justitia/tree/master/doc/add-system-chaincode.md)

## 部署
1. 配置系统环境  
按照先决条件配置系统环境  
2. 创建数据库  
在mysql数据库上创建一个新的数据库，运行“fabricmanager.sql”脚本创建对应的数据表  
3. 编译打包  
本项目是一个maven项目，将项目打包成一个jar包。并确保源文件中“fabric-ca-server”和“resources"文件夹与jar文件保持同一目录下  
4. 修改jar包中application.yaml配置文件中数据库连接配置和服务端口（可在打包前完成）  
5. 运行jar  
本项目必须运行在linux系统上
6. [部署前端程序](https://github.com/shijinshi/justitia/tree/master/web/README.md)

## 使用说明
在浏览器上访问nginx配置的前端地址，即可访问。

## 项目设计
本项目使用Fabric ca管理和维护组织身份信息，借助Docker的http api维护和监控Fabric节点，通过fabric-sdk-java对fabric网络上的数据进行维护。整个项目的结构设计上分为四个部分，如下图所示。其中DB/File用于存储身份数据（包括证书和私钥）。Service定义了一些业务模块，提供给Scheduler调度。Scheduler进行身份校验，并定义了一系列REST接口；WEB通过Scheduler提供的REST接口进行数据展示和服务调用。
![](https://github.com/shijinshi/justitia/tree/master/doc/img/structure.png)

1.	Dao server
提供数据存储和读取的接口，可以根据实际需求使用不同数据库，默认使用MYSQL。
2.	Fabric server
封装了fabric-sdk-java，提供一些简单的服务接口。
3.	Chaincode server
通过调用Fabric SDK提供安装、实例化和升级等链码维护功能。链码安装支持上传本地合约和从代码仓库获取代码两种方式。
4.	Node server
通过调用Docker Rest API来创建和管理节点容器，使用者可根据需求动态调整节点数量。在部署节点时，系统为节点提供一个默认配置，使用者也可以根据实际情况覆盖这些配置。系统监控节点状态信息，当节点状态异常时系统通过邮件或短信等方式通知管理员处理相关情况。
5.	Identity server
身份信息维护借助于fabric ca来完成，包括证书的登记、续期和吊销。在数据库中我们会保存证书和私钥，以便当用户需要部署一个新的节点或使用一个新的客户端用户时，此服务可以为其提供所需的全部证书和私钥。当证书因为私钥泄露等原因需要使原有证书失效时，系统可以帮用户吊销相关证书，并将CRL更新到通道配置中去。
6.	Channel server
通道服务通过提交通道配置来管理联盟和通道成员。
由联盟管理员（掌握orderer节点管理员用户私钥），通过修改系统通道中的联盟信息，以达到修改联盟配置和增删联盟成员的目的。申请人通过本系统生成包含本组织身份信息的通道配置数据，然后将次配置数据作为申请材料通过邮件等其他形式向联盟管理员发起加入申请。若联盟管理员同意将其加入联盟（相应的审批机制有具体业务决定），便从orderer节点获取最新的系统通道配置区块，将申请者提交的配置信息增加到联盟中生成配置更新交易。此后申请者便作为联盟成员与其他成员拥有相同的权力，也可以创建通道。删除联盟成员则以相同操作，从联盟配置中去除于此成员的相关配置。
![](https://github.com/shijinshi/justitia/tree/master/doc/img/Add-member-to-consortium.png)
通道成员和通道配置的管理与管理联盟的方式类似，由通道成员的管理员用户发起修改通道配置的交易来实现。但是与修改系统通道不同的是，在默认策略下想要修改通道配置需要通道内超过半数的组织的管理员对修改提案背书，当然这个策略是可修改的。另一方面，组织管理员用户背书的过程无法在链上完成，如果引入一个中心化的服务帮助完成这个签名流程又太过复杂。为了在链上解决这个问题，我们在peer节点镜像中增加了一个系统链码CMSCC（channel manager system chaincode）辅助完成这个签名的审批流程。成员加入通道的过程如下图所示，过程与组织加入联盟类似。申请者向介绍人（通道中原有的任意成员）发起加入申请，介绍人获取通道最新配置区块生成通道更新交易。借助于CMSCC将此交易广播给通道中的其他成员，其他成员可以选择接受或者拒绝此申请。直到接受的成员数量满足通道策略时，介绍人将签名结果和更新交易发送给Orderer节点更新通道配置。此后申请者便作为通道成员与其他成员拥有相同的权力。删除通道成员则以相同操作，从通道配置中去除对应成员的配置。
![](https://github.com/shijinshi/justitia/tree/master/doc/img/Add-member-to-channel.png)

## 等待完善
当前本版的代码只是一个简单的demo，在用户友好性和功能的优化上任然有很多工作未完成，当前已知的未解决问题可查看[Issue文档](doc\Issue.md)