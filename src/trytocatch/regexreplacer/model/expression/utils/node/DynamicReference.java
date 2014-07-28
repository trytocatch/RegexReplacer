package trytocatch.regexreplacer.model.expression.utils.node;

import trytocatch.regexreplacer.model.expression.ExpressionNode;

public class DynamicReference extends ReferenceFunc {
	public static final String FUNC_NAME = "Ref";

	public DynamicReference(ExpressionNode refNode) {
		super(refNode);
	}

	@Override
	public Object getReferenceValue() {
		return refNode.getResultWithoutCacheUpdate();
	}
}
