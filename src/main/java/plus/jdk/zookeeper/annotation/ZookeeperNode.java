package plus.jdk.zookeeper.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ZookeeperNode {

    /**
     * zookeeper节点路径
     */
    String value();
}
