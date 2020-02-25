package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.ExpressionNode;
import com.github.trytocatch.regexreplacer.model.expression.FuncNode;
import com.github.trytocatch.regexreplacer.model.expression.utils.LogicUtil;

/**
 * A powerful version of Iif, if p1 equals p2, returns p3, if p1 equals p4 returns p5...
 * if p1 equals p(2n) returns p(2n+1), if none of them equals p1, and the number of
 * the parameters is odd, then returns nothing, otherwise returns the last one.<br>
 * This function tests the cases from left to right, it will stop once it matches, and
 * has no affect on remaining parameters, even through it is a dynamic function like Seq.
 * @author trytocatch@163.com
 * @date Sep 19, 2015
 */
public class FuncCase extends FuncNode {

	public FuncCase(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int arg0) {
		return arg0 >= 3;
	}

	@Override
	public Object getResultImpl() {
		Object caseValue = args[0];
		if (caseValue instanceof ExpressionNode)
			caseValue = ((ExpressionNode) caseValue).getResult();
		int n = 1;
		for(;n<args.length-1;n+=2){
			if(LogicUtil.lenientEquals(caseValue,args[n]))
				return args[n+1];
		}
		if(n<args.length)
			return args[n];
		else
			return "";
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		throw new UnsupportedOperationException();
	}

}
