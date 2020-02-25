package trytocatch.regexreplacer.model.expression.funcs;

import trytocatch.regexreplacer.model.expression.FuncNode;
import trytocatch.regexreplacer.model.expression.utils.LogicUtil;
/**
 * like the Iif in visual basic, returns c while a==b, 
 * otherwise returns d(no affect on d even through d is a dynamic function like Seq)
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
		return LogicUtil.lenientEquals(args[0], args[1])?args[2]:args[3];
	}
	
	@Override
	protected Object workOut(Object[] realArgs) {
		throw new UnsupportedOperationException();
	}
}
