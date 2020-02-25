package com.github.trytocatch.regexreplacer.model.expression.funcs;

import com.github.trytocatch.regexreplacer.model.expression.FuncNode;
/**
 * dynamic repeat<br>
 * repeat the string for several times with the max length.<br>
 * read the new value for the first argument at each repeat<br>
 * @author trytocatch
 * @date 2016-10-16
 */
public class FuncDynRepeat extends FuncNode {
	private static final int MAX_CONSECUTIVE_EMPTY_STR_COUNT = 100000;
	public FuncDynRepeat(Object[] args) {
		super(args, true);
	}

	@Override
	protected boolean checkArgsLength(int count) {
		return count == 2 || count == 3;
	}

	@Override
	public Object getResultImpl() {
		int repeatTimes = Integer.parseInt(args[1].toString());
		int maxLength = -1;
		if (args.length == 3) {
			maxLength = Integer.parseInt(args[2].toString());
			if (maxLength < 0)
				throw new IllegalArgumentException("[max length] can't be negative!");
			else if(maxLength == 0)
				return "";
		} else {
			if (repeatTimes < 0)
				throw new IllegalArgumentException("[repeat times] can't be negative without [max length]!");
		}
		StringBuilder sb = new StringBuilder(maxLength>0?maxLength:16);
		String str;
		// maxLength != 0
		int emptyCount = 0;
		for(;;){
			if(repeatTimes>=0 && --repeatTimes<0)
				break;
			str = args[0].toString();
			if(str.isEmpty() && repeatTimes < 0){
				if(++emptyCount > MAX_CONSECUTIVE_EMPTY_STR_COUNT)
					throw new IllegalArgumentException("[repeat str] always returns an empty string, just stop to avoid endless loop.");
			}else {
				emptyCount = 0;
			}
			if(maxLength > 0 &&  + str.length() >= maxLength - sb.length()){
				sb.append(str,0,maxLength - sb.length());
				break;
			}else
				sb.append(str);
		}
		return sb.toString();
	}
	
	@Override
	protected Object workOut(Object[] realArgs) {
		throw new UnsupportedOperationException();
	}
}
