
<h3 align="center">一个集成Zookeeper依赖的Springboot扩展</h3>
<p align="center">
    <a href="https://github.com/JDK-Plus/spring-boot-starter-zookeeper/blob/master/LICENSE"><img src="https://img.shields.io/github/license/JDK-Plus/spring-boot-starter-zookeeper.svg" /></a>
    <a href="https://github.com/JDK-Plus/spring-boot-starter-zookeeper/releases"><img src="https://img.shields.io/github/release/JDK-Plus/spring-boot-starter-zookeeper.svg" /></a>
    <a href="https://github.com/JDK-Plus/spring-boot-starter-zookeeper/stargazers"><img src="https://img.shields.io/github/stars/JDK-Plus/spring-boot-starter-zookeeper.svg" /></a>
    <a href="https://github.com/JDK-Plus/spring-boot-starter-zookeeper/network/members"><img src="https://img.shields.io/github/forks/JDK-Plus/spring-boot-starter-zookeeper.svg" /></a>
</p>


## 引入依赖

```xml
<dependency>
    <groupId>plus.jdk.zookeeper</groupId>
    <artifactId>spring-boot-starter-zookeeper</artifactId>
    <version>1.0.1</version>
</dependency>
```

## 需要添加的配置项

```
# 是否开启该组件
plus.jdk.zookeeper.enabled=true
# 指定zookeeper集群列表
plus.jdk.zookeeper.hosts=127.0.0.1:2181,127.0.0.2:2181
# 连接超时时间
plus.jdk.zookeeper.conn-timeout=3000
# 每个节点数据多久更新一次
plus.jdk.zookeeper.heart-rate=30
# 会话超时时间
plus.jdk.zookeeper.session-timeout=3000
# 处理节点数据变化事件（节点删除、节点新增、节点修改）的核心线程数，默认10个
plus.jdk.zookeeper.watcher-thread-core-poll-size=10
# 指定一个用于数据序列化的实现类
plus.jdk.zookeeper.data-adapter=plus.jdk.zookeeper.client.DefaultZKDataAdapter
```

## 引入后如何使用

当上文配置完成后，您只需要在对应的bean实例中使用 `@ZookeeperNode` 注解来指定要获取哪个节点的数据即可，示例如下：

```java
import org.springframework.stereotype.Component;
import plus.jdk.grpc.client.INameResolverConfigurer;
import plus.jdk.grpc.model.GrpcNameResolverModel;
import plus.jdk.zookeeper.annotation.ZookeeperNode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class GrpcGlobalNameResolverConfigurer implements INameResolverConfigurer {


    private final RSACipherService rsaCipherService;


    @ZookeeperNode(value = "/brand/grpc/name/provider", adapter = DefaultZKDataAdapter.class)
    private List<GrpcNameResolverModel> grpcNameResolverModels;

}
```