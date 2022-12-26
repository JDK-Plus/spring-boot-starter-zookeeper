package plus.jdk.zookeeper.common;

import java.net.SocketException;
import org.apache.zookeeper.Watcher.Event.EventType;


public interface IZookeeperListener {

    void listen(String path, EventType eventType, byte[] data) throws ZkClientException, SocketException;
}
