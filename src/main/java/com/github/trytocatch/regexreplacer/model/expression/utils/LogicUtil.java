package com.github.trytocatch.regexreplacer.model.expression.utils;

import com.github.trytocatch.regexreplacer.model.expression.ExpressionNode;

/**
 * some logic operation tools
 * @author trytocatch@163.com
 * @date Sep 19, 2015
 */
public class LogicUtil {
	
	/**
	 * whether objA equals to objB in a lenient way
	 * @param objA
	 * @param objB
	 * @return
	 */
	public static boolean lenientEquals(Object objA, Object objB) {
		if (objA instanceof ExpressionNode)
			objA = ((ExpressionNode) objA).getResult();
		if (objB instanceof ExpressionNode)
			objB = ((ExpressionNode) objB).getResult();
		if (objA == objB)
			return true;
		else if (objA == null || objB == null)
			return false;
		else if (objA.getClass() == objB.getClass())
			return objA.equals(objB);
		else if (objA instanceof Number && objB instanceof Number) {
			Number n1 = (Number) objA;
			Number n2 = (Number) objB;
			return n1.longValue() == n2.longValue() && n1.doubleValue() == n2.doubleValue();
		} else
			return objA.toString().equals(objB.toString());
	}
}
