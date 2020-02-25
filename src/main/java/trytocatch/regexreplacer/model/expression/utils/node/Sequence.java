package trytocatch.regexreplacer.model.expression.utils.node;

import trytocatch.regexreplacer.model.expression.ExpressionNode;

/**
 * the real sequence works in SequenceFunc
 * 
 * @author trytocatch@163.com
 * @date 2013-1-3
 * @param <N>
 */
abstract class Sequence<N extends Number> extends ExpressionNode {
	protected N initialValue;
	/** step increment */
	protected N step;
	/** the counter */
	protected int counter;
	/** the threshold to reset */
	protected N threshold;
	/** a flag indicate that it needs reset next time  */
	protected boolean needReset;

	Sequence(N initialValue, N step, N threshold) {
		super(false);
		this.initialValue = initialValue;
		this.step = step;
		this.threshold = threshold;
	}
	
	Sequence<N> reuse(N initialValue, N step, N threshold){
		this.initialValue = initialValue;
		this.step = step;
		this.threshold = threshold;
		counter = 0;
		needReset = false;
		return this;
	}
	
	boolean isNeedReset(){
		return needReset;
	}
}
