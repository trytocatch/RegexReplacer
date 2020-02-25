package com.github.trytocatch.regexreplacer.model.expression.utils.node;

import com.github.trytocatch.regexreplacer.model.expression.Controller;
import com.github.trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * get the capturing group
 * @author trytocatch
 * @date Jun 2, 2014
 */
public class GroupFunc extends FuncNode {
	private Controller controller;

	public GroupFunc(Object[] args, Controller controller) {
		super(args, false);
		if (controller == null)
			throw new IllegalArgumentException("controller can't be null");
		this.controller = controller;
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count == 1;
	}

	@Override
	protected Object workOut(Object[] realArgs) {
		if (realArgs[0] == null)
			throw new IllegalArgumentException("wrong parameters for group");
		if (realArgs[0] instanceof Number)
			return controller.getGroup(((Number) realArgs[0]).intValue());
		else {
			String str = realArgs[0].toString();
			if (str.matches("\\d+"))
				return controller.getGroup(Integer.parseInt(str));
			else
				try{
					return controller.getGroup(str);
				}catch(NoSuchMethodError e){
					throw new RuntimeException("Named group is supported since java 1.7");
				}
		}
	}

}
