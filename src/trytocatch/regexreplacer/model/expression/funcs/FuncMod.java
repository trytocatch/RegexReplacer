package trytocatch.regexreplacer.model.expression.funcs;

import trytocatch.regexreplacer.model.expression.FuncNode;
import trytocatch.regexreplacer.model.expression.utils.MathOperationUtil;

/**
 * result = a mod b (a % b)
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncMod extends FuncNode {
	
	public FuncMod(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count == 2;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		Number[] num = MathOperationUtil.convertToSameType(true, realArgs);
		if(num instanceof Long[])
			return num[0].longValue() % num[1].longValue();
		else//must be Double[]
			return num[0].doubleValue() % num[1].doubleValue(); 
	}
}
