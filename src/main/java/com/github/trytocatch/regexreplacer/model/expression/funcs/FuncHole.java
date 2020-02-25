package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * black hole, result = ""
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncHole extends FuncNode {

	public FuncHole(Object[] args) {
		super(args, true); 
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return true;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		return "";
	}
}
