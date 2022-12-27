package plus.jdk.zookeeper.selector;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import plus.jdk.zookeeper.client.ZookeeperClientFactory;
import plus.jdk.zookeeper.common.SpringZookeeperContext;
import plus.jdk.zookeeper.config.ZookeeperProperties;
import plus.jdk.zookeeper.global.ZookeeperClientBeanPostProcessor;

@Configuration
public class ZookeeperClientSelector extends WebApplicationObjectSupport implements BeanFactoryAware, WebMvcConfigurer {

    private BeanFactory beanFactory;

    @Bean
    ZookeeperClientBeanPostProcessor getZookeeperClientDispatcher(ZookeeperProperties properties,
                                                                  ZookeeperClientFactory clientFactory) {
        return new ZookeeperClientBeanPostProcessor(properties, beanFactory, getApplicationContext(), clientFactory);
    }

    @Bean
    ZookeeperClientFactory getZookeeperClientFactory(ZookeeperProperties properties) {
        return new ZookeeperClientFactory(properties, beanFactory, getApplicationContext());
    }

    @Bean
    SpringZookeeperContext getSpringContext() {
        return new SpringZookeeperContext();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
