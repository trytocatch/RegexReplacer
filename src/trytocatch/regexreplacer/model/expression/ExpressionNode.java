package trytocatch.regexreplacer.model.expression;

/**
 * the base class of expression node(the functions in the replace expression)
 * @author trytocatch@163.com
 */
public abstract class ExpressionNode implements ResetObserver {
	private Object cache;

	protected boolean cacheSupported = false;

	public ExpressionNode(boolean cacheSupported) {
		this.cacheSupported = cacheSupported;
	}

	public void onReset() {
		cache = null;
	}
	
	public Object getResult() {
		if (isCacheAvailable() && getResultCache() != null)
			return getResultCache();
		else
			return cache = getResultImpl();
	}
	
	public Object getResultWithoutCacheUpdate() {
		if (isCacheAvailable() && getResultCache() != null)
			return getResultCache();
		else
			return getResultImpl();
	}

	public Object getResultCache() {
		return cache;
	}

	/**
	 * whether can it use the cache
	 * if getResult always(although in replacing other line) returns the same
	 * results with same parameters, you can use the cache
	 */
	public boolean isCacheAvailable() {
		return cacheSupported;
	}

	protected abstract Object getResultImpl();

	public String toString() {
		Object o = getResult();
		if (o != null)
			return o.toString();
		else
			return "";
	}
}
