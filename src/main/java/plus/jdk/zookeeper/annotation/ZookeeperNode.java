package plus.jdk.zookeeper.annotation;

import plus.jdk.zookeeper.client.DefaultZKDataAdapter;
import plus.jdk.zookeeper.client.IZKDataAdapter;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ZookeeperNode {

    /**
     * zookeeper节点路径
     * @return zookeeper path
     */
    String value();

    /**
     * 指定该zk节点数据序列化的处理类
     * @return 返回一个Adapter实例
     */
    Class<? extends IZKDataAdapter> adapter() default DefaultZKDataAdapter.class;
}
