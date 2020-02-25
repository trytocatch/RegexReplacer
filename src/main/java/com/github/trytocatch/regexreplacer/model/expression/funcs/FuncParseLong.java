package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * parse a string to long
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncParseLong extends FuncNode {
	
	public FuncParseLong(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count == 1 || count == 2;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		int radix;
		if(realArgs.length == 1)
			radix = 10;
		else{
			if(realArgs[1] instanceof Number)
				radix = ((Number)realArgs[1]).intValue();
			else
				radix = Integer.parseInt(realArgs[1].toString());
		}
		return Long.parseLong(realArgs[0].toString(), radix);
	}

}
