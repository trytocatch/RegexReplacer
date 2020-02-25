package com.github.trytocatch.regexreplacer.model.expression.utils.node;

class SequenceDouble extends Sequence<Double> {

	public SequenceDouble(Double initialValue, Double step, Double threshold) {
		super(initialValue, step, threshold);
	}

	@Override
	protected Object getResultImpl() {
		Double result = initialValue + step * counter++;
		if (threshold != null)
			if (step > 0 && result + step > threshold || step < 0
					&& result + step < threshold)
				needReset = true;
		return result;
	}
}
