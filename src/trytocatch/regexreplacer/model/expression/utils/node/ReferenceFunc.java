package trytocatch.regexreplacer.model.expression.utils.node;

import trytocatch.regexreplacer.model.expression.ExpressionNode;

public abstract class ReferenceFunc extends ExpressionNode {
	protected ExpressionNode refNode;
	
	public ReferenceFunc(ExpressionNode refNode){
		super(false);
		this.refNode=refNode;
	}
	
	public void setReference(ExpressionNode refNode){
		this.refNode=refNode;
	}
	
	@Override
	public Object getResultImpl() {
		if(refNode == null)
			throw new RuntimeException("the reference is null");
		return getReferenceValue();
	}
	
	public abstract Object getReferenceValue();
}
