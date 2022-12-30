package plus.jdk.zookeeper.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import plus.jdk.zookeeper.client.DefaultZKDataAdapter;
import plus.jdk.zookeeper.client.IZKDataAdapter;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "plus.jdk.zookeeper")
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
    private Integer sessionTimeout = 3000;

    /**
     * 连接超时时间
     */
    private Integer connTimeout = 3000;

    /**
     * 监听者线程池核心线程数
     */
    private Integer watcherThreadCorePollSize = 10;

    /**
     * 刷新周期，默认30秒
     */
    private Integer heartRate = 30;

    /**
     * 指定默认的数据序列化、反序列化工具类
     */
    private Class<? extends IZKDataAdapter> dataAdapter = DefaultZKDataAdapter.class;
}
