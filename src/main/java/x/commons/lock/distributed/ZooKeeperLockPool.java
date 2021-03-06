package x.commons.lock.distributed;

import org.apache.zookeeper.ZooKeeper;

import x.commons.lock.LockException;
import x.commons.lock.SimpleLock;


public class ZooKeeperLockPool extends AbstractLockPool {
	
	private final ZooKeeper zk;
	private final String nodePath;

	public ZooKeeperLockPool(ZooKeeper zk, String nodePath, int size) {
		super(size);
		this.zk = zk;
		this.nodePath = nodePath;
	}

	@Override
	protected SimpleLock newLock(String key) throws LockException {
		return new ZooKeeperLock(this.zk, this.nodePath, key);
	}

}
