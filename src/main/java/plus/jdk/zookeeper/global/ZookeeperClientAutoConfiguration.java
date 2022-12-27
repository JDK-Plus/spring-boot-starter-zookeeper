package plus.jdk.zookeeper.global;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import plus.jdk.zookeeper.annotation.EnableZookeeperClient;
import plus.jdk.zookeeper.config.ZookeeperProperties;

@Configuration
@EnableZookeeperClient
@ConditionalOnProperty(prefix = "plus.jdk.zookeeper", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperClientAutoConfiguration {

}
