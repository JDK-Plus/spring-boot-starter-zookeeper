package plus.jdk.zookeeper.global;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import plus.jdk.zookeeper.annotation.ZookeeperNode;
import plus.jdk.zookeeper.client.ZookeeperClientFactory;
import plus.jdk.zookeeper.config.ZookeeperProperties;

import java.lang.reflect.Field;

public class ZookeeperClientBeanPostProcessor implements BeanPostProcessor {

    private final ZookeeperClientFactory zookeeperClientFactory;

    public ZookeeperClientBeanPostProcessor(ZookeeperProperties properties,
                                            BeanFactory beanFactory,
                                            ApplicationContext applicationContext,
                                            ZookeeperClientFactory zookeeperClientFactory) {
        this.zookeeperClientFactory = zookeeperClientFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (final Field field : bean.getClass().getDeclaredFields()) {
            final ZookeeperNode annotation = AnnotationUtils.findAnnotation(field, ZookeeperNode.class);
            if (annotation == null) {
                continue;
            }
            zookeeperClientFactory.processInjectionPoint(field, bean, field.getType(), annotation);
        }
        return bean;
    }
}
