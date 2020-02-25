package trytocatch.regexreplacer.model.expression.funcs;

import trytocatch.regexreplacer.model.expression.FuncNode;

/**
 * repeat the string for several times with the max length
 * @author trytocatch
 * @date 2016-10-07
 */
public class FuncRepeat extends FuncNode {

	public FuncRepeat(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int arg0) {
		return arg0 == 2 || arg0 == 3;
	}

	@Override
	protected Object workOut(Object[] arg0) {
		String str = arg0[0].toString();
		if(str.isEmpty())
			return "";
		int repeatTimes = Integer.parseInt(arg0[1].toString());
		int length;
		if (arg0.length == 3) {
			int max = Integer.parseInt(arg0[2].toString());
			if (max < 0)
				throw new IllegalArgumentException("[max length] can't be negative!");
			if (repeatTimes < 0)
				length = max;
			else
				length = Math.min(repeatTimes * str.length(), max);
		} else {
			if (repeatTimes < 0)
				throw new IllegalArgumentException("[repeat times] can't be negative without [max length]!");
			length = repeatTimes * str.length();
		}
		StringBuilder sb = new StringBuilder(length);
		for (int n = length / str.length(); n > 0; n--)
			sb.append(str);
		sb.append(str, 0, length % str.length());
		return sb.toString();
	}
}
