package x.commons.lock.distributed;

import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.commons.lock.LockException;
import x.commons.lock.SimpleLock;

public abstract class AbstractReentrantLock implements SimpleLock {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final ReentrantLock reentrantLock = new ReentrantLock();
	private final ThreadLocal<byte[]> globalLockStatus = new ThreadLocal<byte[]>();
	
	private volatile boolean isLocked = false;
	
	@Override
	public void lock() throws LockException {
		try {
			// 当前线程 在进程内 与其他线程竞争本地锁
			reentrantLock.lock();
			logger.debug(String.format("Thread %d acquired the local lock.", Thread.currentThread().getId()));
			
			// 当前线程 代表本进程 与其他进程竞争全局锁
			if (globalLockStatus.get() == null) {
				// 未持有全局锁，尝试获取
				logger.debug(String.format("Thread %d try to acquire the global lock...", Thread.currentThread().getId()));
				this.lockGlobal();
				logger.debug(String.format("Thread %d acquired the global lock.", Thread.currentThread().getId()));
				
				globalLockStatus.set(new byte[0]);
				logger.debug(String.format("Thread %d cached its global lock.", Thread.currentThread().getId()));
				
			} else {
				// 已持有全局锁
				logger.debug(String.format("Thread %d already holds the global lock.", Thread.currentThread().getId()));
			}
			
			isLocked = true;
		} catch (Exception e) {
			if (e instanceof LockException) {
				throw (LockException) e;
			} else {
				throw new LockException(e);
			}
		}
		/*
		// 异常发生后的解锁代码不要在此实现，
		// 应由调用者对lock()方法进行异常捕获，并在finally中调用unlock()来实现
		finally {
			reentrantLock.unlock(); 
		}
		*/
	}

	@Override
	public void unlock() throws LockException {
		/*
		 * 解锁原则：
		 * 1 只能解自己持有的锁
		 * 2 解锁后须保持“一致”状态，即本地锁与全局锁要么同时持有，要么同时放弃
		 */
		
		if (!reentrantLock.isHeldByCurrentThread()) {
			// 当前线程未持有本地锁（根据加锁代码，此时也不会持有全局锁）：违反原则1，禁止解锁
			throw new LockException(String.format("Current thread %d doesn't hold the local lock.", Thread.currentThread().getId()));
		} else if (globalLockStatus.get() == null) {
			// 当前线程持有本地锁但未持有全局锁：仅解除本地锁，达到“一致”状态
			try {
				this.isLocked = false; // 此变量须在解本地锁之前赋值
				reentrantLock.unlock();
				logger.debug(String.format("Thread %d released the local lock.", Thread.currentThread().getId()));
			} catch (IllegalMonitorStateException e) {
				// just ignore
			}
			throw new LockException(String.format("Current thread %d doesn't hold the global lock.", Thread.currentThread().getId()));
		} else {
			// 当前线程同时持有本地锁和全局锁：只有解全局锁成功才解本地锁
			try {
				logger.debug(String.format("Thread %d try to release the global lock...", Thread.currentThread().getId()));
				this.unlockGlobal();
				logger.debug(String.format("Thread %d released the global lock.", Thread.currentThread().getId()));
				
				globalLockStatus.remove();
				logger.debug(String.format("Thread %d doesn't cache its global lock any more.", Thread.currentThread().getId()));
				
				this.isLocked = false; // 此变量须在解本地锁之前赋值
				reentrantLock.unlock();
				logger.debug(String.format("Thread %d released the local lock.", Thread.currentThread().getId()));
			} catch (Exception e) {
				if (e instanceof LockException) {
					throw (LockException) e;
				} else {
					throw new LockException(e);
				}
			}
		}
	}
	
	@Override
	public boolean isLocked() {
		return this.isLocked;
	}
	
	protected abstract void lockGlobal() throws Exception;
	
	protected abstract void unlockGlobal() throws Exception;
	
	
}
