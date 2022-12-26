package plus.jdk.zookeeper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.zookeeper.Watcher;
import plus.jdk.zookeeper.annotation.ZookeeperNode;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
public class ZookeeperListenerModel<T> {

    /**
     * 字段注解
     */
    private ZookeeperNode zookeeperNode;

    /**
     * 对应的bean实例
     */
    private Object beanInstance;

    /**
     * 需要刷新的字段
     */
    private Field field;

    /**
     * 字段类型
     */
    private Class<T> clazz;

    /**
     * 用的哪个监听器
     */
    private Watcher watcher;
}
