package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * get the unicode value(a Long)
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncAsc extends FuncNode {
	
	public FuncAsc(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count == 1;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		String str;
		if (realArgs[0] == null)
			return null;
		if (realArgs[0] instanceof Character)
			return (long) ((Character) realArgs[0]).charValue();
		str = realArgs[0].toString();
		if (str.length() >= 1)
			return (long) str.charAt(0);
		throw new IllegalArgumentException("wrong parameters for Asc");
	}
}
