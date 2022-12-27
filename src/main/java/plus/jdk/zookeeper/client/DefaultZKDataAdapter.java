package plus.jdk.zookeeper.client;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class DefaultZKDataAdapter implements IZKDataAdapter {

    private final Gson gson = new Gson();

    @Override
    public <T> byte[] serialize(T data) {
        return gson.toJson(data).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] dataBytes, Type clazz) {
        return gson.fromJson(new String(dataBytes), clazz);
    }
}
