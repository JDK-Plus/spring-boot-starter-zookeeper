package plus.jdk.zookeeper.common;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

public class SpringZookeeperContext implements ApplicationContextAware, BeanFactoryAware {

    private static ApplicationContext context = null;

    private static BeanFactory beanFactory = null;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SpringZookeeperContext.beanFactory = beanFactory;
    }

    public static <T> T getBean(String name, Class<T> clazz)
    {
        if(context == null) {
            return null;
        }
        Object object = context.getBean(name);
        return clazz.cast(object);
    }

    public static <T> T getBean(Class<T> beanClass){
        if(context == null) {
            return null;
        }
        return context.getBean(beanClass);
    }

    public static <T> T getProperty(String key, Class<T> clazz) {
        Environment environment = SpringZookeeperContext.getBean(Environment.class);
        if(environment == null) {
            return null;
        }
        return environment.getProperty(key, clazz);
    }
}
