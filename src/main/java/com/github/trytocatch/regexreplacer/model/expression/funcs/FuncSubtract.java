package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;
import com.github.trytocatch.regexreplacer.model.expression.utils.MathOperationUtil;

/**
 * result = a - b
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class FuncSubtract extends FuncNode {

	public FuncSubtract(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count == 2;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		Number[] num = MathOperationUtil.convertToSameType(true, realArgs);
		if (num instanceof Double[]) {
			return (Double) num[0] - (Double) num[1];
		} else {// must be Long[]
			return (Long) num[0] - (Long) num[1];
		}
	}
}
