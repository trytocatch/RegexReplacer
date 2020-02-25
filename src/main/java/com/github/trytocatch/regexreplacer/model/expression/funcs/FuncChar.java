package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * unicode to char
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncChar extends FuncNode {
	
	public FuncChar(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count==1;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		int i;
		if(realArgs[0]==null)
			return null;
		if (realArgs[0] instanceof Number)
			i =((Number)realArgs[0]).intValue();
		else
			i = Integer.parseInt(realArgs[0].toString());
		return (char) i;
	}

}
