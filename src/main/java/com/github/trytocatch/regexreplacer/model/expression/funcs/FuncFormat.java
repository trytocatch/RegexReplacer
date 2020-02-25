package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * result = String.format(a,b,c,d...)
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncFormat extends FuncNode {
	
	public FuncFormat(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count >= 1;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		if (realArgs.length == 1)
			return String.format(realArgs[0].toString());
		else {
			Object[] args = new Object[realArgs.length - 1];
			System.arraycopy(realArgs, 1, args, 0, realArgs.length - 1);
			return String.format(realArgs[0].toString(), args);
		}
	}

}
