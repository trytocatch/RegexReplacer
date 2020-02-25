package com.github.trytocatch.regexreplacer.model.expression.utils.node;

import com.github.trytocatch.regexreplacer.model.expression.ExpressionNode;

public class StaticReference extends ReferenceFunc {
	public static final String FUNC_NAME = "StcRef";

	public StaticReference(ExpressionNode refNode) {
		super(refNode);
	}

	@Override
	public Object getReferenceValue() {
		return refNode.getResultCache();
	}
}
