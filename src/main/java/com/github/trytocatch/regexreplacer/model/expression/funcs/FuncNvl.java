package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * like the Nvl and Nvl2 in plsql
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncNvl extends FuncNode {
	
	public FuncNvl(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count == 2 || count == 3;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		if (realArgs.length == 2)
			return realArgs[0] != null ? realArgs[0] : realArgs[1];
		else//realArgs.length == 2
			return realArgs[0] != null ? realArgs[1] : realArgs[2];
	}

}
