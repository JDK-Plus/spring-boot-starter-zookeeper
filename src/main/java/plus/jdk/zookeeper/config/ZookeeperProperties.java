package plus.jdk.zookeeper.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "plus.jdk.grpc")
public class ZookeeperProperties {

    /**
     * 是否开启组件
     */
    private Boolean enabled = false;

    /**
     * 默认的zookeeper主机列表
     */
    private String hosts;

    /**
     * 会话超时时间 会话超时时间
     */
    private int sessionTimeout = 3000;

    /**
     * 连接超时时间
     */
    private int connTimeout = 3000;
}
