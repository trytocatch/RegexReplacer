package com.github.trytocatch.regexreplacer.model.expression.utils.node;

class SequenceLong extends Sequence<Long> {

	public SequenceLong(Long initialValue, Long step, Long threshold) {
		super(initialValue, step, threshold);
	}

	@Override
	protected Object getResultImpl() {
		Long result = initialValue + step * counter++;
		if (threshold != null)
			if (step > 0 && result + step > threshold || step < 0
					&& result + step < threshold)
				needReset = true;
		return result;
	}
}
