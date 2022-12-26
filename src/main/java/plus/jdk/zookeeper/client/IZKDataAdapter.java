package plus.jdk.zookeeper.client;

public interface IZKDataAdapter {

    <T> byte[] serialize(T data);

    <T> T deserialize(byte[] dataBytes, Class<T> clazz);
}
