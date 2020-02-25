package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * upper case to lower case
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncLower extends FuncNode {
	
	public FuncLower(Object[] args) {
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
		return realArgs[0].toString().toLowerCase();
	}
}
