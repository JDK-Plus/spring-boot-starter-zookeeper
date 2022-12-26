package plus.jdk.zookeeper.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import plus.jdk.zookeeper.common.ZkClientException;

import java.util.List;
import java.util.concurrent.Semaphore;


@Slf4j
public class ZookeeperWatcher implements Watcher, AsyncCallback.ChildrenCallback{

    private final ZookeeperClient zookeeperClient;

    private final Semaphore connLock;

    public ZookeeperWatcher(Semaphore connLock, ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
        this.connLock = connLock;
    }

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children) {

    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getState()) {
            case ConnectedReadOnly:
            case SyncConnected:
                if (!zookeeperClient.isConnection()) {
                    connLock.release();//连接成功
                    log.warn("Zookeeper connection or retry success......");
                }
                break;
            case Expired://会话超时
                this.stateChange(event.getState());
                resetSession();
                break;
            case Disconnected://连接断开
                this.stateChange(event.getState());
                log.warn("Zookeeper connection break......");
                break;
            default:
                log.warn("Zookeeper state: " + event.getState());
                break;
        }
        switch (event.getType()) {
            case NodeChildrenChanged: //子节点变化
                this.childChange(event.getPath());
                break;
            case NodeDataChanged: //节点数据变化
                this.dataChange(event.getPath());
        }
    }

    private void resetSession() {
        log.warn("Zookeeper session timeout......");
        try {
            zookeeperClient.reconnection();
            log.warn("Zookeeper session timeout,retry success. ");
        } catch (ZkClientException e) {
            log.error("Zookeeper reset session faiil.", e);
        }
    }

    /**
     * 数据变化处理
     *
     * @param path
     */
    private void dataChange(String path) {

    }

    /**
     * 子节点发生变化
     *
     * @param path
     */
    private void childChange(String path) {

    }

    /**
     * 状态变化监听
     *
     * @param state
     */
    private void stateChange(Watcher.Event.KeeperState state) {
    }

}
