package plus.jdk.zookeeper.annotation;

import org.springframework.context.annotation.Import;
import plus.jdk.zookeeper.selector.ZookeeperClientSelector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Import(ZookeeperClientSelector.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableZookeeperClient {

}