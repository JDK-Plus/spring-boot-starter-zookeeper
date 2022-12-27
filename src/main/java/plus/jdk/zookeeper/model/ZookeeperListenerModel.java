package plus.jdk.zookeeper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.zookeeper.Watcher;
import plus.jdk.zookeeper.annotation.ZookeeperNode;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Data
@AllArgsConstructor
public class ZookeeperListenerModel {

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
    private Type clazz;

    /**
     * 用的哪个监听器
     */
    private Watcher watcher;

    /**
     * 是否已提交任务
     */
    private Boolean hasTiming  = false;

    public String id() {
        return String.format("%d-%d", beanInstance.hashCode(), field.hashCode());
    }
}
