package trytocatch.regexreplacer.model.expression.utils.node;

import trytocatch.regexreplacer.model.expression.Controller;
import trytocatch.regexreplacer.model.expression.ExpressionNode;

/**
 * get the absolute row number(relative to the matched count)
 * 
 * @author trytocatch@163.com
 * @date 2012-12-19
 */
public class GetAbsRowNumFunc extends ExpressionNode {
	public static final String FUNC_NAME="AbsRow";
	private static GetAbsRowNumFunc instance;
	private Controller controller;

	private GetAbsRowNumFunc(Controller controller) {
		super(false);
		if(controller==null)
			throw new IllegalArgumentException("controller can't be null");
		this.controller=controller;
	}
	/**
	 * get the instance of GetAbsRowNumFunc
	 * @param controller: GetAbsRowNumFuncs have same performs with same controller
	 * @return
	 */
	public static GetAbsRowNumFunc getInstance(Controller controller) {
		if(instance==null || instance.controller != controller)
			instance = new GetAbsRowNumFunc(controller);
		return instance;
	}

	@Override
	public Object getResultImpl() {
		return controller.getAbsRowNum();
	}

}
