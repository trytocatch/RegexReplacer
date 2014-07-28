package trytocatch.regexreplacer.model.expression.funcs;

import trytocatch.regexreplacer.model.expression.FuncNode;
import trytocatch.regexreplacer.model.expression.utils.MathOperationUtil;

/**
 * result = a+b+c+...
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncAdd extends FuncNode {
	
	public FuncAdd(Object[] args) {
		super(args, true); 
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count >= 1;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		Number[] num = MathOperationUtil.convertToSameType(true, realArgs);
		if (num instanceof Double[]) {
			double doubleResult = 0D;
			for (Double d : (Double[]) num)
				doubleResult += d;
			return doubleResult;
		} else {// must be Long[]
			long longResult = 0L;
			for (Long l : (Long[]) num)
				longResult += l;
			return longResult;
		}
	}
}
