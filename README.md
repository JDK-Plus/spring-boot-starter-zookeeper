
<h3 align="center">A Springboot extension that integrates Zookeeper dependencies</h3>
<p align="center">
    <a href="https://github.com/JDK-Plus/spring-boot-starter-zookeeper/blob/master/LICENSE"><img src="https://img.shields.io/github/license/JDK-Plus/spring-boot-starter-zookeeper.svg" /></a>
    <a href="https://github.com/JDK-Plus/spring-boot-starter-zookeeper/releases"><img src="https://img.shields.io/github/release/JDK-Plus/spring-boot-starter-zookeeper.svg" /></a>
    <a href="https://github.com/JDK-Plus/spring-boot-starter-zookeeper/stargazers"><img src="https://img.shields.io/github/stars/JDK-Plus/spring-boot-starter-zookeeper.svg" /></a>
    <a href="https://github.com/JDK-Plus/spring-boot-starter-zookeeper/network/members"><img src="https://img.shields.io/github/forks/JDK-Plus/spring-boot-starter-zookeeper.svg" /></a>
</p>


## Import

```xml
<dependency>
    <groupId>plus.jdk.zookeeper</groupId>
    <artifactId>spring-boot-starter-zookeeper</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Configuration items to be added

```
# Whether to enable the component
plus.jdk.zookeeper.enabled=true
# Specifies the list of zookeeper clusters
plus.jdk.zookeeper.hosts=127.0.0.1:2181,127.0.0.2:2181
# Connection timeout
plus.jdk.zookeeper.conn-timeout=3000
# How often is the data of each node updated
plus.jdk.zookeeper.heart-rate=30
# Session timeout
plus.jdk.zookeeper.session-timeout=3000
# Number of core threads that process node data change events 
# (node deletion, node addition, node modification). Default number: 10
plus.jdk.zookeeper.watcher-thread-core-poll-size=10
# Specify an implementation class for data serialization
plus.jdk.zookeeper.data-adapter=plus.jdk.zookeeper.client.DefaultZKDataAdapter
```

## How to use it after import

When the above configuration is complete, you only need to specify which node to get data from using the `@ZookeeperNode` 
annotation in the corresponding bean instance, as shown in the following example:

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