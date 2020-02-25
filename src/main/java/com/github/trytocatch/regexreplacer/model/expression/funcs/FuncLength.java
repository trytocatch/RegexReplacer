package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * String.length
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncLength extends FuncNode {

	public FuncLength(Object[] args) {
		super(args, true); 
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count == 1;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		if(realArgs[0] != null){
			return realArgs[0].toString().length();
		}else{
			return 0;
		}
	}
}
