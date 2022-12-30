package plus.jdk.zookeeper.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.springframework.context.ApplicationContext;
import plus.jdk.zookeeper.common.SpringZookeeperContext;
import plus.jdk.zookeeper.common.ZkClientException;
import plus.jdk.zookeeper.config.ZookeeperProperties;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Semaphore;

@Slf4j
@Data
public class ZookeeperClient {

    private IZKDataAdapter dataAdapter;

    private ZookeeperProperties properties;

    /**
     * 连接同步锁
     */
    private final Semaphore connLock = new Semaphore(0);

    private final ZookeeperWatcher watcher;

    private ZooKeeper zooKeeper;

    public ZookeeperClient(ZookeeperProperties zookeeperProperties) {
        this.dataAdapter = new DefaultZKDataAdapter();
        this.properties = zookeeperProperties;
        this.watcher = new ZookeeperWatcher(connLock, this);
        this.connection();
    }

    public <T> T getData(String path, Type clazz) throws ZkClientException {
        return getData(path, false, clazz);
    }

    public <T> T getData(String path, boolean watcher, Type clazz) throws ZkClientException {
        this.checkStatus();
        try {
            byte[] dataBytes = this.zooKeeper.getData(path, watcher, null);
            return dataAdapter.deserialize(dataBytes, clazz);
        } catch (Exception e) {
            throw new ZkClientException("getData node " + path, e);
        }
    }

    public <T> T getData(String path, Type clazz, Watcher watcher) throws ZkClientException {
        this.checkStatus();
        try {
            byte[] dataBytes = this.zooKeeper.getData(path, watcher, null);
            return dataAdapter.deserialize(dataBytes, clazz);
        } catch (Exception e) {
            throw new ZkClientException("getData node " + path, e);
        }
    }

    public <T> T getData(String path, Type clazz, Watcher watcher, IZKDataAdapter adapter) throws ZkClientException {
        this.checkStatus();
        try {
            byte[] dataBytes = this.zooKeeper.getData(path, watcher, null);
            return adapter.deserialize(dataBytes, clazz);
        } catch (Exception e) {
            throw new ZkClientException("getData node " + path, e);
        }
    }

    public <T> Stat setData(String path, T data) throws ZkClientException {
        this.checkStatus();
        try {
            byte[] dataBytes = dataAdapter.serialize(data);
            return this.zooKeeper.setData(path, dataBytes, -1);
        } catch (Exception e) {
            throw new ZkClientException("setData node " + path, e);
        }
    }

    protected List<String> getChild(String path) throws ZkClientException {
        return this.getChild(path, false);
    }

    public List<String> getChild(String path, boolean watcher) throws ZkClientException {
        this.checkStatus();
        try {
            return this.zooKeeper.getChildren(path, watcher);
        } catch (Exception e) {
            throw new ZkClientException("getChildren node " + path, e);
        }
    }

    public <T> String create(String path, T data, CreateMode mode) throws ZkClientException {
        this.checkStatus();
        String createNode;
        try {
            byte[] dataBytes = dataAdapter.serialize(data);
            createNode = this.zooKeeper.create(path, dataBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
        } catch (Exception e) {
            throw new ZkClientException("create node " + path + ", mode=" + mode.name(), e);
        }
        return createNode;
    }

    public <T> String create(String path, T data, boolean reCreate) throws ZkClientException {
        return this.create(path, data, CreateMode.EPHEMERAL);
    }

    public <T> void create(String path, T data) throws ZkClientException {
        this.create(path, data, CreateMode.PERSISTENT);
    }


    public void delete(String path) throws ZkClientException {
        this.checkStatus();
        try {
            this.zooKeeper.delete(path, -1);
        } catch (Exception e) {
            throw new ZkClientException("delete node " + path, e);
        }
    }


    private synchronized void connection() throws ZkClientException {
        if (this.checkConnection()) {
            throw new ZkClientException("Has been connected to the server, please do not repeat connection. host:"
                    + properties.getHosts());
        }
        try {
            connLock.drainPermits();
            zooKeeper = new ZooKeeper(properties.getHosts(), properties.getSessionTimeout(), watcher);
        } catch (IOException e) {
            throw new ZkClientException("Connect zookeeper fail, hosts=" + properties.getHosts(), e);
        }
    }

    public void reconnection() throws ZkClientException {
        this.connection();
    }

    public void close() throws ZkClientException {
        try {
            if (zooKeeper != null && zooKeeper.getState().isConnected()) {
                zooKeeper.close();
            }
        } catch (InterruptedException e) {
            throw new ZkClientException("close zookeeper client error.", e);
        }
    }

    public boolean exists(String path) throws ZkClientException {
        this.checkStatus();
        try {
            return this.zooKeeper.exists(path, false) != null;
        } catch (Exception e) {
            throw new ZkClientException("exists node " + path, e);
        }
    }

    public boolean checkConnection() {
        boolean conn = false;
        if (zooKeeper != null) {
            conn = zooKeeper.getState().isConnected();
        }
        return conn && this.isConnection();
    }

    public boolean checkStatus() throws ZkClientException {
        if (zooKeeper == null) {
            throw new ZkClientException("Not connected to the zookeeper server,host=" + properties.getHosts() + ",invoking this.connect().");
        }
        if (zooKeeper.getState().isAlive()) {
            return true;
        }
        throw new ZkClientException("Not connected to the zookeeper server,host=" + properties.getHosts() + ",state: " + zooKeeper.getState());
    }

    public boolean isConnection() {
        return this.zooKeeper.getState().isAlive();
    }

    public static void main(String[] args) {
        String hosts = "10.185.10.65:12181";
        ZookeeperProperties properties = new ZookeeperProperties();
        properties.setHosts(hosts);
        ZookeeperClient zookeeperClient = new ZookeeperClient(properties);
        String data = zookeeperClient.getData("/brand/grpc/name/provider", String.class);
        log.info("{}", data);
    }
}
