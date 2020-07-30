### 实习期项目代码

#### 模块划分

- word-count

利用java基础的集合、IO、多线程知识实现读取多个小文件内容按照单词分类统计每个单词的数量最终写入到一个结果文件中。

- spring-practise

通过spring-boot框架，配置web、lombok、swagger、mybatis、mysql的dependency,完成单表的增删改查接口。

- ftp-collect

实现利用FTP客户端实现文件的上传、下载和删除；实现定时从FTP服务器下载txt文件，然后将文件内容存入mysql数据库中。

- https-share

完成一个文件共享服务，在服务器A上提供https文件下载功能，服务器A当有新文件（每个文件名中有数据时间字段，yyyyMMdd格式,格式如：VIM-NFV-RP-VM-20200722-001.gzip）写入后，会将文件的下载信息（消息格式见1.1)写入到kafka的SOURCE_CM的topic中，文件共享服务订阅SOURCE_CM这个topic，当有消息写入后接收消息然后将文件下载并上传到FTP服务器B上，对应目录比如是（/ftpuser/share/nfvo/APP-HZZX001/CM/20200722/VIM-NFV-RP-VM-20200722-001.gzip)，保持后面5层级目录名称不变，写完后发送通知消息到kafka中， kafka消息中包含文件命中的时间和ftp地址的全路径，kafka消息格式见1.2。中间在redis中记录，kafka消费，下载，写ftp，发送kafka四个阶段文件处理成功和失败数量。
1.1

```json
{
 "date": "20200722",
 "url":"https://10.10.10.10:9000/ftpuser/share/nfvo/APP-HZZX001/CM/20200722/VIM-NFV-RP-VM-20200722-001.gzip"
}
```

1.2

```json
{
 "date": "20200722",
 "url":"ftp://username@host:/ftpuser/share/nfvo/APP-HZZX001/CM/20200722/VIM-NFV-RP-VM-20200722-001.gzip"
}
```

#### 代码要求

1. 每次提交的代码需要在本地编译通过，如果有单元测试需要单元测试通过。
2. 注意代码的分包和代码注释的编写
3. 自行在pom中添加所需要的依赖
4. 如有问题及时与我联系
