package plus.jdk.zookeeper.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.ReflectionUtils;
import plus.jdk.zookeeper.annotation.ZookeeperNode;
import plus.jdk.zookeeper.common.SpringZookeeperContext;
import plus.jdk.zookeeper.config.ZookeeperProperties;
import plus.jdk.zookeeper.model.ZookeeperListenerModel;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.concurrent.*;

@Slf4j
public class ZookeeperClientFactory implements SmartLifecycle {

    private final ZookeeperProperties properties;

    private final ConfigurableBeanFactory configurableBeanFactory;

    private final ConfigurableApplicationContext configurableApplicationContext;

    private final ApplicationContext applicationContext;

    private final ZookeeperClient zookeeperClient;

    private final ScheduledExecutorService scheduledExecutorService;

    private Boolean started = false;

    private final static ConcurrentHashMap<String, ZookeeperListenerModel> listenerModelMap = new ConcurrentHashMap<>();

    public ZookeeperClientFactory(ZookeeperProperties properties, BeanFactory beanFactory, ApplicationContext context) {
        this.properties = properties;
        this.applicationContext = context;
        this.configurableApplicationContext = (ConfigurableApplicationContext) this.applicationContext;
        this.configurableBeanFactory = this.configurableApplicationContext.getBeanFactory();
        this.zookeeperClient = getZookeeperClient(properties);
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(properties.getWatcherThreadCorePollSize(), (runnable, executor) -> runnable.run());
        this.configurableApplicationContext.getBeanDefinitionNames();
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

    protected <T> void synchronizeDataFromZookeeper(ZookeeperListenerModel listenerModel) {
        try {
            T data = zookeeperClient.getData(listenerModel.getZookeeperNode().value(),
                    listenerModel.getClazz(), listenerModel.getWatcher());
            ReflectionUtils.makeAccessible(listenerModel.getField());
            ReflectionUtils.setField(listenerModel.getField(), listenerModel.getBeanInstance(), data);
        } catch (Exception | Error e) {
            e.printStackTrace();
            log.error("distributeZKNodeDataForBeanField, msg:{}", e.getMessage());
        }
    }

    public <T> void processInjectionPoint(final Field field, final Object bean,
                                          final Type injectionType, final ZookeeperNode zookeeperNode) {
        distributeZKNodeDataForBeanField(zookeeperNode, injectionType, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                distributeZKNodeDataForBeanField(zookeeperNode, injectionType, this, bean, field);
            }
        }, bean, field);
    }

    public <T> void distributeZKNodeDataForBeanField(ZookeeperNode zookeeperNode, Type clazz, Watcher watcher, Object bean, Field field) {
        ZookeeperListenerModel listenerModel = new ZookeeperListenerModel(zookeeperNode, bean, field, clazz, watcher, false);
        if (listenerModelMap.containsKey(listenerModel.id())) {
            listenerModel = listenerModelMap.get(listenerModel.id());
        }
        listenerModelMap.put(listenerModel.id(), listenerModel);
        ZookeeperListenerModel finalListenerModel = listenerModel;
        scheduledExecutorService.submit(() -> {
            synchronizeDataFromZookeeper(finalListenerModel);
        });
        if (listenerModel.getHasTiming()) {
            return;
        }
        int fixRate = properties.getHeartRate();
        synchronized (ZookeeperListenerModel.class) {
            if (listenerModel.getHasTiming()) {
                return;
            }
            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                synchronizeDataFromZookeeper(finalListenerModel);
            }, fixRate, fixRate, TimeUnit.SECONDS);
        }
    }

    public static <T> void reRegisterBeanFieldZKNodeDataWatcher() {
        ZookeeperClientFactory clientFactory = SpringZookeeperContext.getBean(ZookeeperClientFactory.class);
        if (clientFactory == null || listenerModelMap.isEmpty()) {
            return;
        }
        for (Object bean : listenerModelMap.keySet()) {
            ZookeeperListenerModel listenerModel = listenerModelMap.get(bean);
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

    private void injectDataForAllBeanInstances() {
        String[] beanNames = configurableApplicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = configurableBeanFactory.getBean(beanName);
            for (Field field : bean.getClass().getDeclaredFields()) {
                ZookeeperNode zookeeperNode = field.getAnnotation(ZookeeperNode.class);
                if (zookeeperNode == null) {
                    continue;
                }
                processInjectionPoint(field, bean, field.getGenericType(), zookeeperNode);
            }
        }
    }

    @Override
    public void start() {
        injectDataForAllBeanInstances();
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public boolean isRunning() {
        return started;
    }
}
