package plus.jdk.zookeeper.client;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import plus.jdk.zookeeper.annotation.ZookeeperNode;
import plus.jdk.zookeeper.config.ZookeeperProperties;

public class ZookeeperClientFactory {

    private final ZookeeperProperties properties;

    private final ConfigurableBeanFactory configurableBeanFactory;

    private final ApplicationContext applicationContext;

    private final ZookeeperClient zookeeperClient;

    public ZookeeperClientFactory(ZookeeperProperties properties, BeanFactory beanFactory, ApplicationContext context) {
        this.properties = properties;
        this.applicationContext = context;
        this.configurableBeanFactory = ((ConfigurableApplicationContext) this.applicationContext).getBeanFactory();
        zookeeperClient = getZookeeperClient(properties);
    }

    private String getBeanName(ZookeeperProperties properties) {
        return "ZookeeperClient -> " + properties.getHosts();
    }

    public ZookeeperClient getZookeeperClient(ZookeeperProperties properties) {
        ZookeeperClient zookeeperClient = null;
        String beanName = getBeanName(properties);
        if(configurableBeanFactory.containsBean(beanName)) {
            return (ZookeeperClient)configurableBeanFactory.getBean(beanName);
        }
        synchronized (ZookeeperClient.class) {
            if(configurableBeanFactory.containsBean(beanName)) {
                return (ZookeeperClient)configurableBeanFactory.getBean(beanName);
            }
            zookeeperClient = new ZookeeperClient(properties);
            configurableBeanFactory.registerSingleton(beanName, zookeeperClient);
        }
        return zookeeperClient;
    }

    public <T> T getZooNodeData(ZookeeperNode zookeeperNode, Class<T> clazz) {
        return zookeeperClient.getData(zookeeperNode.value(), clazz);
    }
}
