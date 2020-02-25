package com.github.trytocatch.regexreplacer.model.expression;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.trytocatch.regexreplacer.model.expression.utils.node.DynamicReference;
import com.github.trytocatch.regexreplacer.model.expression.utils.node.GetAbsRowNumFunc;
import com.github.trytocatch.regexreplacer.model.expression.utils.node.GroupFunc;
import com.github.trytocatch.regexreplacer.model.expression.utils.node.MergeFunc;
import com.github.trytocatch.regexreplacer.model.expression.utils.node.ReferenceFunc;
import com.github.trytocatch.regexreplacer.model.expression.utils.node.SequenceFunc;
import com.github.trytocatch.regexreplacer.model.expression.utils.node.StaticReference;

/**
 * the parser to parse the replace expression
 * @author trytocatch@163.com
 * @date 2013-2-3
 */
public class ExpressionParser {
	public static final char ESCAPE_CHAR = '\\';
	public static final char FUNC_PREFIX = '$';
	public static final char PARAMETERS_START = '(';
	public static final char PARAMETERS_END = ')';
	public static final char ALIAS_START = '[';
	public static final char ALIAS_END = ']';
	public static final char PARAMETERS_SEPARATOR = ',';
	private static final String wrong_exp = "wrong expression";
	private Controller controller;
	private Map<String, String> funcAliasMap;

	{
		funcAliasMap = new HashMap<String, String>();
		funcAliasMap.put("+", "Add");
		funcAliasMap.put("-", "Subtract");
		funcAliasMap.put("*", "Multiply");
		funcAliasMap.put("/", "Divide");
		funcAliasMap.put("%", "Mod");
	}

	public ExpressionParser(Controller controller) {
		if (controller == null)
			throw new IllegalArgumentException("controller can't be null");
		this.controller = controller;
	}

	/**
	 * parse a expression
	 * @param expression
	 * @return a non-null Object
	 */
	public Object parse(String expression) {
		if (expression == null || expression.isEmpty())
			return "";
		ParseInfo pi = new ParseInfo();
		Object result = parse(expression.toCharArray(), pi, false);
		pi.doSomethingInTheEnd(controller);
		return result == null ? "" : result;
	}
	
	/** may be null */
	public Map<String, String> getFuncAliasMap() {
		return funcAliasMap;
	}

	public void setFuncAliasMap(Map<String, String> funcAliasMap) {
		this.funcAliasMap = funcAliasMap;
	}
	
	private String getAlias(String name) {
		String alias = null;
		if (funcAliasMap != null)
			alias = funcAliasMap.get(name);
		if (alias != null)
			return alias;
		return name;
	}

	private Object parse(char[] expression, ParseInfo pi,
			boolean isParseParameters) {
		List<Object> result = new LinkedList<Object>();
		StringBuilder sb = new StringBuilder();
		for (; pi.pos < expression.length; pi.pos++) {
			if (expression[pi.pos] == ESCAPE_CHAR) {
				if (++pi.pos >= expression.length)
					break;
			} else if (isParseParameters
					&& (expression[pi.pos] == PARAMETERS_SEPARATOR || expression[pi.pos] == PARAMETERS_END)) {
				break;// don't pos++, keep this char, parseExpressionNode() need it to know whether parameter follows
			} else if (expression[pi.pos] == FUNC_PREFIX) {
				if (sb.length() > 0) {
					result.add(sb.toString());
					sb.setLength(0);
				}
				pi.pos++;
				Object node = parseExpressionNode(expression, pi);
				if (node != null)
					result.add(node);
				continue;
			}
			sb.append(expression[pi.pos]);
		}
		if (isParseParameters && pi.pos >= expression.length)
			throw new IllegalArgumentException("expression ended without "+PARAMETERS_END);
		if (sb.length() > 0)
			result.add(sb.toString());
		if (result.size() == 1)
			return result.get(0);
		else if (result.size() > 1)
			return new MergeFunc(result.toArray());
		else
			// result.size()==0
			return "";
	}

	private ExpressionNode parseExpressionNode(char[] expression, ParseInfo pi) {
		List<Object> args = new LinkedList<Object>();
		StringBuilder sb = new StringBuilder(), alias = null;
		String functionName;
		boolean aliasFinished;
		int id = pi.applyId();
		if (pi.pos >= expression.length)
			throw new IllegalArgumentException(wrong_exp);
		aliasFinished = false;
		while (expression[pi.pos] != PARAMETERS_START) {
			// TODO check expression[pi.pos]
			if (alias == null) {
				if (expression[pi.pos] == ALIAS_START)
					alias = new StringBuilder();
				else
					sb.append(expression[pi.pos]);
			} else if (aliasFinished == false) {
				if (expression[pi.pos] == ALIAS_END)
					aliasFinished = true;
				else
					alias.append(expression[pi.pos]);
			}
			pi.pos++;
			if (pi.pos >= expression.length)
				throw new IllegalArgumentException(wrong_exp);
		}
		functionName = sb.toString();
		if (alias != null)
			pi.preRegisterAliasNode(alias.toString());
		sb.setLength(0);
		do {
			pi.pos++;
			args.add(parse(expression, pi, true));
		} while (expression[pi.pos] != PARAMETERS_END);
		ExpressionNode node = createExpressionNode(functionName,
				args.toArray(), pi);
		pi.registerNode(id, node);
		if (alias != null)
			pi.registerAliasNode(alias.toString(), node);
		return node;
	}

	private ExpressionNode createExpressionNode(String name, Object[] args,
			ParseInfo pi) {
		name = getAlias(name);
		if (name == null || name.isEmpty())// group function
			return new GroupFunc(args, controller);
		else if (GetAbsRowNumFunc.FUNC_NAME.equals(name))
			return GetAbsRowNumFunc.getInstance(controller);
		// else if (GetRowNumFunc.FUNC_NAME.equals(name))
		// return GetRowNumFunc.getInstance(controller);
		else if (SequenceFunc.FUNC_NAME.equals(name))
			return new SequenceFunc(args);
		else if (StaticReference.FUNC_NAME.equals(name)
				|| DynamicReference.FUNC_NAME.equals(name)) {
			Object refKey;
			boolean isStaticReference = StaticReference.FUNC_NAME.equals(name);
			ExpressionNode refNode;
			ReferenceFunc node;
			if (args == null || args.length != 1 || args[0] == null)
				throw new IllegalArgumentException("wrong parameters for "
						+ name);
			refKey = args[0];
			if (refKey instanceof Integer == false) {
				refKey = refKey.toString();// refKey.toString() won't be null in
											// general
				if (((String) refKey).matches("\\d+"))
					refKey = Integer.parseInt((String) refKey);
			}
			refNode = pi.getNode(refKey);
			if (isStaticReference)
				node = new StaticReference(refNode);
			else
				node = new DynamicReference(refNode);
			if (refNode == ParseInfo.CREATING_NODE || refNode == null) {
				if (isStaticReference == false
						&& refNode == ParseInfo.CREATING_NODE)
					throw new IllegalArgumentException("wrong parameters for "
							+ name + ", recursive reference of " + refKey);
				pi.registerUnFinishedReference(node, refKey);
			}
			return node;

		} else
			return FuncNode.createFuncNodeReflectImpl(name, args);
	}

	static class ParseInfo {
		private static final ExpressionNode CREATING_NODE = new ExpressionNode(
				false) {
			@Override
			protected Object getResultImpl() {
				throw new RuntimeException("you can't use this ExpressionNode");
			}
		};
		int pos = 0;
		int nodecount = 0;// start at 1, is same to id
		// the key is Integer(the id) or alias
		HashMap<Object, ExpressionNode> nodeMap = new HashMap<Object, ExpressionNode>();
		// the value is ReferenceFunc's reference key(id or alias)
		HashMap<ReferenceFunc, Object> unfinishedReferenceMap = new HashMap<ReferenceFunc, Object>();

		int applyId() {
			nodecount++;
			nodeMap.put(nodecount, CREATING_NODE);
			return nodecount;
		}

		ExpressionNode getNode(Object idOrAlias) {
			return nodeMap.get(idOrAlias);
		}

		void registerNode(int id, ExpressionNode node) {
			assert id <= nodecount : "node: " + id + " doesn't exists";
			assert nodeMap.get(id) == CREATING_NODE : "node: " + id
					+ " was registed already";
			nodeMap.put(id, node);
		}

		void preRegisterAliasNode(String alias) {
			if (nodeMap.containsKey(alias))
				throw new IllegalArgumentException("the alias: " + alias
						+ " already exists");
			nodeMap.put(alias, CREATING_NODE);
		}

		void registerAliasNode(String alias, ExpressionNode node) {
			assert CREATING_NODE == nodeMap.get(alias) : "the value should be set to CREATING_NODE before register real value, or repetitive register";
			nodeMap.put(alias, node);
		}

		void registerUnFinishedReference(ReferenceFunc node, Object refKey) {
			unfinishedReferenceMap.put(node, refKey);
		}

		/**
		 * set actual referenced node to unfinished reference and register all
		 * ResetObserver(all node) to Controller
		 */
		void doSomethingInTheEnd(Controller controller) {
			for (Entry<ReferenceFunc, Object> entry : unfinishedReferenceMap
					.entrySet()) {
				ExpressionNode refNode = nodeMap.get(entry.getValue());
				if (refNode == null)
					throw new IllegalArgumentException("the node: "
							+ entry.getValue() + " doesn't exist");
				entry.getKey().setReference(refNode);
			}
			for (Entry<Object, ExpressionNode> entry : nodeMap.entrySet()) {
				if (entry.getKey() instanceof Integer)//alias(String) nodes are repetitive, ignore
					controller.addResetObserver(entry.getValue());
			}
		}
	}
}
