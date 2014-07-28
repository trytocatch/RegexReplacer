package trytocatch.regexreplacer.model.expression;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>The subclass's class name must start with "Func"
 * (eg: FuncAdd, "Add" is the real function name to be used in expression), 
 * and have a public constructor with a Object[] parameter, and use that parameter to call super constructor
 * <p><code><pre>
 * public FuncAdd(Object[] args){
 * 	super(args,false);
 * 	...
 * }</pre></code>
 * and the class must be inside the package 'trytocatch.regexreplacer.model.expression.funcs'<br>
 * @author trytocatch@163.com
 */
public abstract class FuncNode extends ExpressionNode {

	private static final String FUNC_PACKAGE_PREFIX = FuncNode.class.getPackage().getName() + ".funcs.Func";

	/** has nodes which returns different result at different time */
	private boolean hasDynamicNode;

	/** to store the parameters, it won't be null, realArgs.length==0 at least */
	protected Object args[];

	/**
	 * 
	 * @param args
	 * @param cacheSupported
	 *            if getResult always(although in replacing other line) returns
	 *            the same results with same parameters, you can let
	 *            cacheSupported == true
	 */
	public FuncNode(Object[] args, boolean cacheSupported) {
		// if (args == null)
		// throw new IllegalArgumentException("Parameters can't be null");
		super(cacheSupported);
		if (args == null)
			args = new Object[0];
		if (!checkArgsLength(args.length))
			throw new IllegalArgumentException("Parameters unmatch '"+getClass().getSimpleName()+"'");
		this.args = args;
		hasDynamicNode = false;
		for (Object o : this.args) {
			if (o instanceof ExpressionNode) {
				if (((ExpressionNode) o).isCacheAvailable() == false)
					hasDynamicNode = true;
				break;
			}
		}
	}

	protected abstract boolean checkArgsLength(int count);

	@Override
	public boolean isCacheAvailable() {
		return super.isCacheAvailable() && hasDynamicNode == false;
	}

	/**
	 * to work out the result
	 * 
	 * @param realArgs
	 *            :it won't be null, at least realArgs.length==0;
	 * @return
	 */
	protected abstract Object workOut(Object[] realArgs);

	@Override
	public Object getResultImpl() {
		Object realArgs[] = new Object[args.length];
		for (int n = 0; n < args.length; n++) {
			if (args[n] instanceof ExpressionNode)
				realArgs[n] = ((ExpressionNode) args[n]).getResult();
			else
				realArgs[n] = args[n];
		}
		return workOut(realArgs);
	}

	public static FuncNode createFuncNodeReflectImpl(String funcName, Object[] args) {
		Object o = null;
		if (funcName == null || funcName.isEmpty())
			return null;
		try {
			// because of newInstance(Object... a), Object[] is similar to
			// Object, so covert args from Object[] to Object
			o = Class.forName(FUNC_PACKAGE_PREFIX + funcName)
					.getConstructor(Object[].class).newInstance((Object) args);
		} catch (SecurityException e) {
			assert false : "FunNode.parseFunc, shouldn't happen";
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("the function: '" + funcName
					+ "' should have a public Constructor with (Object[] args)");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			assert false : "FunNode.parseFunc, shouldn't happen";
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("the function: '" + funcName
					+ "' doesn't exist");
		} catch (InstantiationException e) {
			assert false : "FunNode.parseFunc, shouldn't happen";
		} catch (IllegalAccessException e) {
			assert false : "FunNode.parseFunc, shouldn't happen";
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException)
				throw (RuntimeException) e.getTargetException();
			else
				assert false : "FunNode.parseFunc, shouldn't happen";
		}
		if (o instanceof FuncNode)
			return (FuncNode) o;
		else if (o != null)
			throw new IllegalArgumentException("Wrong function name");
		else
			throw new RuntimeException("something wrong in FunNode.parseFunc");
	}
}
