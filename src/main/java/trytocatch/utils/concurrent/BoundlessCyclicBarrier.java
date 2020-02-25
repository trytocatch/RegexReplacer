package trytocatch.utils.concurrent;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/**
 * <p>
 * A concurrent tool, like CyclicBarrier, it has a version, the thread will
 * be blocked who calls {@code await}. For the {@code awaitWithAssignedVersion},
 * if the current version is equal to or later than assigned version, the caller
 * thread won't be blocked, otherwise it will be blocked to wait the assigned
 * version reached.</p>
 * <p>
 * When calls {@code cancel}, the waiting threads will be unblocked. If calls
 * {@code nextCycle} in quick succession, the threads blocked doesn't with assigned
 * version is certain to be unblocked, but the threads blocked with assigned
 * version may not be unblocked, it may still wait for the assigned version's reach.
 * 
 * <p>note: the version can only increase, overflow is OK.
 * 
 * <p>This tool is thread-safe, beside all those wait methods, the others are 
 * nonblocking.
 * @author trytocatch@163.com
 * @time 2013-1-31
 */
public class BoundlessCyclicBarrier {
	protected final AtomicReference<VersionQueue<Thread>> waitQueueRef;

	public BoundlessCyclicBarrier() {
		this(0);
	}

	public BoundlessCyclicBarrier(int startVersion) {
		waitQueueRef = new AtomicReference<VersionQueue<Thread>>(new VersionQueue<Thread>(startVersion));
	}
	
	/**
	 * 
	 * @param assignVersion is myVersion available
	 * @param myVersion wait for this version
	 * @param nanosTimeout wait time(nanosTimeout <=0 means that nanosTimeout is invalid)
	 * @return if timeout, or be canceled and doesn't reach myVersion, returns false
	 * @throws InterruptedException
	 */
	protected boolean awaitImpl(boolean assignVersion, int myVersion,
			long nanosTimeout) throws InterruptedException {
		boolean timeOutEnable = nanosTimeout > 0;
		long lastTime = System.nanoTime();
		VersionQueue<Thread> newQueue = waitQueueRef.get();
		if (assignVersion && newQueue.version - myVersion >= 0)
			return true;
		while (true) {
			VersionQueue<Thread> submitQueue = newQueue;
			submitQueue.queue.add(Thread.currentThread());
			while (true) {
				newQueue = waitQueueRef.get();
				if (newQueue != submitQueue){//it's a new cycle
					if(assignVersion == false)
						return true;
					else if(newQueue.version - myVersion >= 0)
						return true;
					else if (newQueue.isCancelQueue)// be canceled
						return false;
					else//just like invoking awaitImpl again
						break;
				}
				if (timeOutEnable) {
					if (nanosTimeout <= 0)
						return false;
					LockSupport.parkNanos(this, nanosTimeout);
					long now = System.nanoTime();
					nanosTimeout -= now - lastTime;
					lastTime = now;
				} else
					LockSupport.park(this);
				if (Thread.interrupted())
					throw new InterruptedException();
			}
		}
	}

	public final void awaitWithAssignedVersion(int myVersion)
			throws InterruptedException {
		awaitImpl(true, myVersion, 0);
	}
	
	/**
	 * 
	 * @param myVersion
	 * @param nanosTimeout
	 * @return if timeout, or be canceled and doesn't reach myVersion, returns false
	 * @throws InterruptedException
	 */
	public final boolean awaitWithAssignedVersion(int myVersion, long nanosTimeout) throws InterruptedException {
		return awaitImpl(true, myVersion, nanosTimeout);
	}
	
	public final void await() throws InterruptedException {
		awaitImpl(false, 0, 0);
	}
	
	/**
	 * 
	 * @param nanosTimeout
	 * @return if and only if timeout, returns false
	 * @throws InterruptedException
	 */
	public final boolean await(long nanosTimeout)
			throws InterruptedException {
		return awaitImpl(false, 0, nanosTimeout);
	}

	/**
	 * pass and version++(some threads may not be unparked when awaitImpl is in process, but it's OK in this Barrier)
	 * @return old queue version
	 */
	public int nextCycle() {
		VersionQueue<Thread> oldQueue = waitQueueRef.get();
		VersionQueue<Thread> newQueue = new VersionQueue<Thread>(oldQueue.version + 1);
		for(;;){
			if (waitQueueRef.compareAndSet(oldQueue, newQueue)) {
				for (Thread t : oldQueue.queue)
					LockSupport.unpark(t);
				break;
			}
			oldQueue = waitQueueRef.get();
			newQueue.version = oldQueue.version + 1;
		}
		return oldQueue.version;
	}
	
	/**
	 * pass and assign the next cycle version(the version should be increasing, caller
	 * should make sure that the newAssignVersion is right)
	 * @param newAssignVersion
	 */
	public void nextCycle(int newAssignVersion) {
		VersionQueue<Thread> oldQueue = waitQueueRef.getAndSet(new VersionQueue<Thread>(newAssignVersion));
		for (Thread t : oldQueue.queue)
			LockSupport.unpark(t);
	}
	
//	/**
//	 * awake no assignVersion threads 
//	 */
//	public void pass(){
//		VersionQueue<Thread> oldQueue = waitQueueRef.get();
//		if(oldQueue.queue.isEmpty() == false)
//			if (waitQueueRef.compareAndSet(oldQueue, new VersionQueue<Thread>(oldQueue.version)))
//				for (Thread t : oldQueue.queue)
//					LockSupport.unpark(t);
//	}
	
	/**
	 * if version update has stopped, invoke this to awake all threads
	 */
	public void cancel() {
		VersionQueue<Thread> oldQueue = waitQueueRef.get();
		if (waitQueueRef.compareAndSet(oldQueue, new VersionQueue<Thread>(
				oldQueue.version, true)))
			for (Thread t : oldQueue.queue)
				LockSupport.unpark(t);
	}

	public final int getVersion() {
		return waitQueueRef.get().version;
	}

	private static final class VersionQueue<E> {
		final private ConcurrentLinkedQueue<E> queue;
		int version;
		final boolean isCancelQueue;

		VersionQueue(int curVersion){
			this(curVersion, false);
		}
		
		VersionQueue(int curVersion, boolean isCancelQueue) {
			this.version = curVersion;
			this.isCancelQueue = isCancelQueue;
			queue = new ConcurrentLinkedQueue<E>();
		}
	}
}
