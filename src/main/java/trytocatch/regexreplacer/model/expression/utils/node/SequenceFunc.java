package trytocatch.regexreplacer.model.expression.utils.node;

import trytocatch.regexreplacer.model.expression.ExpressionNode;
import trytocatch.regexreplacer.model.expression.utils.MathOperationUtil;

/**
 * A Sequence has initial value, step value, threshold
 * @author trytocatch@163.com
 * @date 2012-12-22
 */
public class SequenceFunc extends ExpressionNode {
	public static final String FUNC_NAME = "Seq";
	private Object[] resetArgs;
	private Sequence<?> realSequence;
	private Sequence<Long> longSequenceReuse;
	private Sequence<Double> doubleSequenceReuse;

	public SequenceFunc(Object[] resetArgs) {
		super(false);
		if (resetArgs == null || resetArgs.length != 2 && resetArgs.length != 3)
			throw new IllegalArgumentException("wrong parameters for function:"
					+ FUNC_NAME);
		this.resetArgs = resetArgs;
	}

	@Override
	public void onReset() {
		super.onReset();
		realSequence = null;
	}

	protected void doReset() {
		Object[] argsTemp = new Object[3];
		for (int n = 0; n < resetArgs.length; n++)
			if (resetArgs[n] instanceof ExpressionNode)
				argsTemp[n] = ((ExpressionNode) resetArgs[n]).getResult();
			else
				argsTemp[n] = resetArgs[n];
		Number[] nums = MathOperationUtil.convertToSameType(false, argsTemp);
		if (nums instanceof Long[]) {
			if (longSequenceReuse == null)
				realSequence = longSequenceReuse = new SequenceLong(
						(Long) nums[0], (Long) nums[1], (Long) nums[2]);
			else
				realSequence = longSequenceReuse.reuse((Long) nums[0],
						(Long) nums[1], (Long) nums[2]);
		} else { // must be Double[]
			if (doubleSequenceReuse == null)
				realSequence = doubleSequenceReuse = new SequenceDouble(
						(Double) nums[0], (Double) nums[1], (Double) nums[2]);
			else
				realSequence = doubleSequenceReuse.reuse((Double) nums[0],
						(Double) nums[1], (Double) nums[2]);
		}
	}

	@Override
	protected Object getResultImpl() {
		if (realSequence == null || realSequence.isNeedReset())
			doReset();
		return realSequence.getResultWithoutCacheUpdate();
	}
}
