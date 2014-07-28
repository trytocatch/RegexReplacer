package trytocatch.regexreplacer.model.expression.funcs;

import trytocatch.regexreplacer.model.expression.ExpressionNode;
import trytocatch.regexreplacer.model.expression.FuncNode;
/**
 * like the Iif in visual basic, return c while a==b, otherwise return d
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncIif extends FuncNode {
	
	public FuncIif(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count == 4;
	}

	@Override
	public Object getResultImpl() {
		Object args0;
		Object args1;
		if(args[0] instanceof ExpressionNode)
			args0 = ((ExpressionNode)args[0]).getResult();
		else
			args0 = args[0];
		if(args[1] instanceof ExpressionNode)
			args1 = ((ExpressionNode)args[1]).getResult();
		else
			args1 = args[1];
		if (args0 == args1)
			return args[2];
		else if (args0 == null || args1 == null)
			return args[3];
		else if (args0.getClass() == args1.getClass())
			return args0.equals(args1) ? args[2] : args[3];
		else if (args0 instanceof Number && args1 instanceof Number) {
			Number a = (Number) args0;
			Number b = (Number) args1;
			return a.longValue() == b.longValue()
					&& a.doubleValue() == a.doubleValue() ? args[2]
					: args[3];
		} else
			return args0.toString().equals(args1.toString()) ? args[2]
					: args[3];
	}
	
	@Override
	protected Object workOut(Object[] realArgs) {
		throw new UnsupportedOperationException();
	}
}
