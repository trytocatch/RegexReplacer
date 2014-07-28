package trytocatch.regexreplacer.model.expression;

import java.util.Collection;

/**
 * I'm not sure this interface is meaningful
 * @author trytocatch@163.com
 * @date 2012-12-19
 */
public interface Controller {
	
	public void addResetObserver(ResetObserver observer);
	
	public void addResetObserver(Collection<ResetObserver> observers);
	
	public void removeResetObserver(ResetObserver observer);
	
	/** get the absolute row number(relative to the matched count) */
	public Integer getAbsRowNum();

	/** get the capturing group */
	public String getGroup(int n);
	
	/** get the named-capturing group */
	public String getGroup(String groupName);
}
