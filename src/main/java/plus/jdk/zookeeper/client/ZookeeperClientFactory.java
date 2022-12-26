package plus.jdk.zookeeper.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;
import plus.jdk.zookeeper.annotation.ZookeeperNode;
import plus.jdk.zookeeper.common.SpringZookeeperContext;
import plus.jdk.zookeeper.config.ZookeeperProperties;
import plus.jdk.zookeeper.model.ZookeeperListenerModel;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ZookeeperClientFactory {

    private final ZookeeperProperties properties;

    private final ConfigurableBeanFactory configurableBeanFactory;

    private final ApplicationContext applicationContext;

    private final ZookeeperClient zookeeperClient;

    private final static ConcurrentHashMap<Object, ZookeeperListenerModel<?>> listenerModelMap = new ConcurrentHashMap<>();

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
        if (configurableBeanFactory.containsBean(beanName)) {
            return (ZookeeperClient) configurableBeanFactory.getBean(beanName);
        }
        synchronized (ZookeeperClient.class) {
            if (configurableBeanFactory.containsBean(beanName)) {
                return (ZookeeperClient) configurableBeanFactory.getBean(beanName);
            }
            zookeeperClient = new ZookeeperClient(properties);
            configurableBeanFactory.registerSingleton(beanName, zookeeperClient);
        }
        return zookeeperClient;
    }

    public <T> T distributeZKNodeDataForBeanField(ZookeeperNode zookeeperNode, Class<T> clazz, Watcher watcher, Object bean, Field field) {
        while (true) {
            try{
                T data = zookeeperClient.getData(zookeeperNode.value(), clazz, watcher);
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, bean, data);
                listenerModelMap.put(bean, new ZookeeperListenerModel<>(zookeeperNode, bean, field, clazz, watcher));
                return data;
            }catch (Exception | Error e) {
                e.printStackTrace();
                log.error("distributeZKNodeDataForBeanField, msg:{}", e.getMessage());
                try {
                    TimeUnit.SECONDS.sleep(1);
                }catch (Exception | Error ignored) {};
            }
        }
    }

    public static <T> void reRegisterBeanFieldZKNodeDataWatcher() {
        ZookeeperClientFactory clientFactory = SpringZookeeperContext.getBean(ZookeeperClientFactory.class);
        if (clientFactory == null || listenerModelMap.isEmpty()) {
            return;
        }
        for (Object bean : listenerModelMap.keySet()) {
            ZookeeperListenerModel<T> listenerModel = (ZookeeperListenerModel<T>) listenerModelMap.get(bean);
            if (listenerModel == null) {
                continue;
            }
            try {
                clientFactory.distributeZKNodeDataForBeanField(listenerModel.getZookeeperNode(), listenerModel.getClazz(),
                        listenerModel.getWatcher(), listenerModel.getBeanInstance(), listenerModel.getField());
            } catch (Exception | Error e) {
                log.error("distributeZKNodeDataForBeanField failed, msg:{}, listenerModel:{}", e.getMessage(), listenerModel);
            }
        }
    }
}
