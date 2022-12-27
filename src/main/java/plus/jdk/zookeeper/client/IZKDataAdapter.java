package plus.jdk.zookeeper.client;

import java.lang.reflect.Type;

public interface IZKDataAdapter {

    <T> byte[] serialize(T data);

    <T> T deserialize(byte[] dataBytes, Type clazz);
}
