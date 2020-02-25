package com.github.trytocatch.regexreplacer.model.expression.utils.node;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * merge the parameters in string mode
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class MergeFunc extends FuncNode {
	public MergeFunc(Object[] realArgs){
		super(realArgs ,true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return true;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		StringBuilder sb=new StringBuilder();
		for(Object o:realArgs)
			if(o!=null)
				sb.append(o);
		return sb.toString();
	}
}
