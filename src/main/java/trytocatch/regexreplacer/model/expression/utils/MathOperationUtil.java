package trytocatch.regexreplacer.model.expression.utils;

/**
 * some tools for math & number issue
 * @author trytocatch@163.com
 * @date 2013-2-3
 */
public class MathOperationUtil {
	/**
	 * test whether decimal in args(Double, Float, or String with decimal point)
	 * 
	 * @param args
	 * @return
	 */
	public static boolean hasDecimal(Object... args) {
		if (args == null)
			return false;
		for (Object num : args) {
			if (num == null)
				continue;
			if (num instanceof Number) {
				if (num instanceof Double || num instanceof Float)
					return true;
			} else {
				if (num.toString().indexOf('.') >= 0)
					return true;
			}
		}
		return false;
	}

	/**
	 * convert args to same type(Long or Double)
	 * 
	 * @param args
	 * @return the result array, it won't be null
	 */
	public static Number[] convertToSameType(boolean convertNullToZero,
			Object... args) {
		Number result[];
		int n;
		if (args == null)
			return new Long[0];
		n = 0;
		if (hasDecimal(args)) {
			result = new Double[args.length];
			for (Object o : args) {
				if (o == null)
					result[n++] = convertNullToZero ? 0D : null;
				else if (o instanceof Number)
					if (o instanceof Double)
						result[n++] = (Double) o;
					else
						result[n++] = new Double(((Number) o).doubleValue());
				else if (o.toString().isEmpty())
					result[n++] = 0D;
				else
					result[n++] = Double.parseDouble(o.toString());
			}
		} else {
			result = new Long[args.length];
			for (Object o : args) {
				if (o == null)
					result[n++] = convertNullToZero ? 0L : null;
				else if (o instanceof Number)
					if (o instanceof Long)
						result[n++] = (Long) o;
					else
						result[n++] = new Long(((Number) o).longValue());
				else if (o.toString().isEmpty())
					result[n++] = 0L;
				else
					result[n++] = Long.parseLong(o.toString());
			}
		}
		return result;
	}
}
