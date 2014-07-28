package trytocatch.regexreplacer.model.expression.funcs;

import trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * lower case to upper case
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncUpper extends FuncNode {

	public FuncUpper(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count == 1;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		if (realArgs[0] == null)
			return null;
		return realArgs[0].toString().toUpperCase();
	}
}
