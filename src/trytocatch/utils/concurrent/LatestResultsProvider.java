package trytocatch.utils.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * <p>
 * This is a tool for working out the latest result. It was written for this situation:
 * you want do some time-consuming calculations for some data and get the result, but
 * the data changes frequently by multiple threads, and it's meaningless to continue
 * with the calculation once the data has changed. So it need to cancel the current
 * calculation with low cost and it must be thread-safe.
 * <p>
 * The request has divided into two operation, 'update the parameters' and 'update',
 * once you changed the data, you need to call the method 'updateParametersVersion' to
 * let this tool know, if there is a calculation on the way, it will be canceled(the
 * task left to you is canceling your calculation in the implementation of calculateResult
 * once you detect that 'isWorking' returns false) and restart, or do nothing in other
 * cases. The 'update' mean that you want to get the latest result. It will launch
 * the calculation if the data has changed since the last calculation finished, otherwise
 * nothing will be done. There are several variants of 'update', such as 'updateAndWait',
 * you can use it to wait for the result. It will stop waiting if a newer result has worked
 * out or the method 'stopCurrentWorking' was called or that thread was interrupted.
 * <p>
 * If the calculation is hard to cancel, and the data changes frequently, you can use
 * 'setUpdateDelay' to delay the launch of the calculation. But this will lead to another
 * issue(or you may not think it is an issue), the data changes so frequently that the
 * calculation never be launched because its interval is less than 'UpdateDelay'.
 * If you think it is an issue, you can use 'setDelayUpperLimit' to avoid it. If the sum
 * of all 'UpdateDelay' greater than 'DelayUpperLimit', the delay mechanism will be
 * disabled for this time.
 * <p>
 * The methods except 'updateAndWait' are nonblocking at <b>wait-free</b> level.
 * <p>
 * As described in 'Package java.util.concurrent.atomic Description':<br>
 * compareAndSet and all other read-and-update operations such as getAndIncrement have
 * the memory effects of both reading and writing volatile variables.<br>
 * So there is no thread-safe problem if you call 'updateParametersVersion' after changing
 * the data, the new data will be visible at 'calculateResult' even through there is no
 * synchronization has been made.<br>
 * 
 * 'updateParametersVersion' <b>happens-before</b> the worker thread enter 'calculateResult' in
 * its turn.
 * <p>
 * How to use it:<br>
 * Implement the abstract method 'calculateResult' to do your calculation with your data,
 * cancel your calculation if you detect that the method 'isWorking' returns false, call
 * the method 'updateParametersVersion' once the data has changed, call the method
 * 'update' or 'updateAndWait' to launch the calculation if you want to get the result.
 * 
 * @author trytocatch@163.com
 * @date 2013-2-2
 */
public abstract class LatestResultsProvider {
	/** update return value */
	public static final int UPDATE_FAILED = -1;
	public static final int UPDATE_NO_NEED_TO_UPDATE = 0;
	public static final int UPDATE_SUCCESS = 1;
	public static final int UPDATE_COMMITTED = 2;
	/** update return value */
	
	/** work states*/
	private static final int WS_OFF = 0;
	private static final int WS_NEW_TASK = 1;
	private static final int WS_WORKING = 2;
	private static final int WS_DELAYING = 3;
	private static final int WS_DELAY_RESET = 4;
	private static final int WS_CANCELED = 5;
	/** work states*/
	private final AtomicInteger workState;

	private int sleepPeriod = 30;

	private final AtomicInteger parametersVersion;
	private volatile int updateDelay;// updateDelay>=0
	private volatile int delayUpperLimit;

	private final BoundlessCyclicBarrier barrier;
	private Thread workThread;

	/**
	 * 
	 * @param updateDelay unit: millisecond
	 * @param delayUpperLimit limit the sum of the delay, disabled 
	 * while delayUpperLimit<0, unit: millisecond
	 */
	public LatestResultsProvider(int updateDelay, int delayUpperLimit) {
		if (updateDelay < 0)
			this.updateDelay = 0;
		else
			this.updateDelay = updateDelay;
		this.delayUpperLimit = delayUpperLimit;
		barrier = new BoundlessCyclicBarrier(0);
		workState = new AtomicInteger(WS_OFF);
		parametersVersion = new AtomicInteger(0);
		initThread();
	}

	private void initThread() {
		workThread = new Thread("trytocatch's worker") {
			@Override
			public void run() {
				int sleepCount = 0;
				for (;;) {
					try {
						while (!workState.compareAndSet(WS_NEW_TASK,
								updateDelay > 0 ? WS_DELAY_RESET : WS_WORKING)) {
							if (workState.compareAndSet(WS_CANCELED, WS_OFF)) {
								barrier.cancel();
							}
							LockSupport.park();
							interrupted();
						}
						if (workState.get() == WS_DELAY_RESET) {
							int delaySum = 0;
							for (;;) {
								if (workState.compareAndSet(WS_DELAY_RESET,
										WS_DELAYING)) {
									sleepCount = (updateDelay + sleepPeriod - 1)
											/ sleepPeriod;
								}
								sleep(sleepPeriod);
								if (--sleepCount <= 0
										&& workState.compareAndSet(WS_DELAYING,
												WS_WORKING))
									break;
								if (delayUpperLimit >= 0) {
									delaySum += sleepPeriod;
									if (delaySum >= delayUpperLimit) {
										if (!workState.compareAndSet(
												WS_DELAYING, WS_WORKING))
											workState.compareAndSet(
													WS_DELAY_RESET, WS_WORKING);
										break;
									}
								}
								if (workState.get() != WS_DELAYING
										&& workState.get() != WS_DELAY_RESET)
									break;
							}
						}
						if (isWorking()) {
							int workingVersion = parametersVersion.get();
							try {
								calculateResult();
								if (workState.compareAndSet(WS_WORKING, WS_OFF))
									barrier.nextCycle(workingVersion);
							} catch (Throwable t) {
								t.printStackTrace();
								workState.set(WS_CANCELED);
							}
						}
					} catch (InterruptedException e) {
						workState.compareAndSet(WS_DELAYING, WS_CANCELED);
						workState.compareAndSet(WS_DELAY_RESET, WS_CANCELED);
					}
				}// for(;;)
			}// run()
		};
		workThread.setDaemon(true);
		workThread.start();
	}

	public int getUpdateDelay() {
		return updateDelay;
	}

	/**
	 * @param updateDelay
	 *            delay time. unit: millisecond
	 */
	public void setUpdateDelay(int updateDelay) {
		this.updateDelay = updateDelay < 0 ? 0 : updateDelay;
	}
	
	public int getDelayUpperLimit() {
		return delayUpperLimit;
	}

	/**
	 * @param delayUpperLimit limit the sum of the delay, disabled 
	 * while delayUpperLimit<0, unit: millisecond
	 */
	public void setDelayUpperLimit(int delayUpperLimit) {
		this.delayUpperLimit = delayUpperLimit;
	}
	
	public final void stopCurrentWorking() {
		workState.set(WS_CANCELED);
	}

	/**
	 * 
	 * @return NO_NEED_TO_UPDATE, COMMITTED
	 */
	public final int update() {
		if (isResultUptodate())
			return UPDATE_NO_NEED_TO_UPDATE;
		if (workState.compareAndSet(WS_CANCELED, WS_NEW_TASK)
				|| workState.compareAndSet(WS_OFF, WS_NEW_TASK))
			LockSupport.unpark(workThread);
		return UPDATE_COMMITTED;
	}

	/**
	 * @param timeout
	 *            unit:nanoseconds
	 * @return FAILED, NO_NEED_TO_UPDATE, SUCCESS
	 * @throws InterruptedException
	 */
	public final int updateAndWait(long nanosTimeout)
			throws InterruptedException {
		int newVersion = parametersVersion.get();
		if (update() == UPDATE_NO_NEED_TO_UPDATE)
			return UPDATE_NO_NEED_TO_UPDATE;
		barrier.awaitWithAssignedVersion(newVersion, nanosTimeout);
		return barrier.getVersion() - newVersion >= 0 ? UPDATE_SUCCESS
				: UPDATE_FAILED;
	}

	/**
	 * @return FAILED, NO_NEED_TO_UPDATE, SUCCESS
	 * @throws InterruptedException
	 */
	public final int updateAndWait() throws InterruptedException {
		return updateAndWait(0);
	}
	
	public final boolean isResultUptodate() {
		return parametersVersion.get() == barrier.getVersion();
	}

	/**
	 * be used in calculateResult()
	 * @return true: the work state is working, worth to calculate the 
	 * result absolutely, otherwise you can cancel the current calculation
	 */
	protected final boolean isWorking() {
		return workState.get()==WS_WORKING;
	}

	/**
	 * you must call this after update the parameters, and before calling the
	 * update
	 */
	protected final void updateParametersVersion() {
		int pVersion = parametersVersion.get();
		//CAS failed means that another thread do the same work already
		if (parametersVersion.compareAndSet(pVersion, pVersion + 1))
			if (!workState.compareAndSet(WS_DELAYING, WS_DELAY_RESET))
				workState.compareAndSet(WS_WORKING, WS_NEW_TASK);
	}
	
	/**
	 * implement this to deal with you task
	 */
	protected abstract void calculateResult();
}
