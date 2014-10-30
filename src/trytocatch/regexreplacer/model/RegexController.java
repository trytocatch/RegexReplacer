package trytocatch.regexreplacer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import trytocatch.regexreplacer.model.expression.Controller;
import trytocatch.regexreplacer.model.expression.ExpressionParser;
import trytocatch.regexreplacer.model.expression.ResetObserver;
import trytocatch.regexreplacer.model.expression.ResultObserver;
import trytocatch.utils.concurrent.LatestResultsProvider;

/**
 * not thread-safe
 * @author trytocatch@163.com
 * @date 2013-2-3
 */
public class RegexController extends LatestResultsProvider implements Controller{
	private static final int DEFAULT_UPDATE_DELAY = 0;
	
	private final Vector<ResetObserver> observers;
	/** one ResultObserver at most */
	private volatile ResultObserver resultObserver;

	private volatile boolean liveUpdate;
	private volatile ExpressionParser parser;
	
	/**it's visible in getRealReplaceResult(happens-before rules in updateAndWait
	 * and barrier.nextCycle)
	 */
	private Object replacer = "";
	private Pattern pattern;
	private Matcher matcher;
	private boolean expressionAvailable;
	
	private String textCache = "";
	private String expressionCache = "";
	private String regexCache = "";
	private int patternFlag = 0;

	private boolean replaceExpressionChanged;
	private boolean patternChanged;
	
	private List<MatchResultInfo> result;
	private int absRowNum;//no volatile


	public RegexController() {
		this(true, DEFAULT_UPDATE_DELAY);
	}

	public RegexController(boolean liveUpdate, int updateDelay) {
		super(updateDelay, -1);
		observers = new Vector<ResetObserver>();
		parser = new ExpressionParser(this);
		this.liveUpdate = liveUpdate;
		expressionAvailable = true;
	}

	public ExpressionParser getParser() {
		return parser;
	}

	public void setParser(ExpressionParser parser) {
		if (parser == null)
			throw new IllegalArgumentException("ExpressionParser can't be null");
		this.parser = parser;
	}

	public boolean isLiveUpdate() {
		return liveUpdate;
	}

	/**
	 * @param liveUpdate
	 *            true:RegexController will update the result while some
	 *            parameters changed
	 */
	public void setLiveUpdate(boolean liveUpdate) {
		this.liveUpdate = liveUpdate;
		if (liveUpdate)
			update();
	}

	private void updateParameters(){
		updateParametersVersion();
		if (liveUpdate)
			update();
		else
			stopCurrentWorking();
	}
	

	public boolean isExpressionAvailable() {
		return expressionAvailable;
	}

	/**
	 * 
	 * @param expressionAvailable
	 *            true: use function to replace false: replace with text
	 *            directly
	 */
	public void setExpressionAvailable(boolean expressionAvailable) {
		if (this.expressionAvailable != expressionAvailable) {
			this.expressionAvailable = expressionAvailable;
			replaceExpressionChanged = true;
			updateParameters();
		}
	}
	
	public void setText(String text) {
		if(setTextImpl(text)){
			updateParameters();
		}
	}

	private boolean setTextImpl(String text) {
		if (text == null)
			text = "";
		if (textCache.hashCode() != text.hashCode() || !textCache.equals(text)) {
			textCache = text;
			return true;
		}
		return false;
	}

	public void setPatternFlag(int flag) {
		if(setPatternFlagImpl(flag)){
			updateParameters();
		}
	}

	private boolean setPatternFlagImpl(int flag) {
		if (patternFlag != flag) {
			patternFlag = flag;
			patternChanged = true;
			return true;
		}
		return false;
	}

	public void setRegexStr(String regexStr) {
		if(setRegexStrImpl(regexStr))
			updateParameters();
	}

	private boolean setRegexStrImpl(String regexStr) {
		if (regexStr == null)
			regexStr = "";
		if (regexCache.equals(regexStr) == false) {
			regexCache = regexStr;
			patternChanged = true;
			return true;
		}
		return false;
	}

	public void setReplaceExpression(String expression) {
		if(setReplaceExpressionImpl(expression))
			updateParameters();
	}

	private boolean setReplaceExpressionImpl(String expression) {
		if (expression == null)
			expression = "";
		if (expressionCache.equals(expression) == false) {
			expressionCache = expression;
			replaceExpressionChanged = true;
			return true;
		}
		return false;
	}
	
	@Override
	protected void calculateResult() {
		if (resultObserver != null)
			resultObserver.onStart();
		try {
			workOutResultImpl();
		} catch (Throwable t) {
			stopCurrentWorking();
			if (resultObserver != null)
				resultObserver.onResultChanged(
						null,
						t.getClass().getSimpleName()
								+ (t.getMessage() != null ? ": "
										+ t.getMessage() : ""));
		}
	}

	private void workOutResultImpl() {
		ArrayList<MatchResultInfo> resultTemp = null;
		if (patternChanged && isWorking())
			reBuildPattern();
		if (replaceExpressionChanged && isWorking())
			reParseReplaceExpression();
		if (isWorking() && pattern != null && !textCache.isEmpty()) {
			matcher = pattern.matcher(textCache);
			reset();
			if (matcher != null && replacer != null) {// replacer ==null won't
														// happen
				resultTemp = new ArrayList<MatchResultInfo>(
						textCache.length() / 10);
				while (matcher.find() && isWorking()) {
					absRowNum++;
					resultTemp
							.add(new MatchResultInfo(matcher.group(), replacer
									.toString(), matcher.start(), matcher.end()));
				}
			}
		} else
			matcher = null;
		result = resultTemp;
		if (resultObserver != null && isWorking())
			resultObserver
					.onResultChanged(
							Collections
									.unmodifiableList(result == null ? new ArrayList<MatchResultInfo>(
											0) : result), null);
	}
	
	/**
	 * 
	 * @param indexToBeReplace
	 *            : should in ascending order(1, 2, 4, 7), if
	 *            indexToBeReplace==null return all result
	 * 
	 * @return
	 * @throws Exception
	 *             The result has changed and indexToBeReplace!=null !
	 */
	public List<MatchResultInfo> getRealReplaceResult(int[] indexToBeReplace)
			throws Exception {
		if (updateAndWait() != UPDATE_NO_NEED_TO_UPDATE && indexToBeReplace != null)
			throw new Exception(
					"The result has changed! Operation is canceled!");
		if (indexToBeReplace == null)
			return Collections
					.unmodifiableList(result == null ? new ArrayList<MatchResultInfo>(
							0) : result);
		ArrayList<MatchResultInfo> realReplaceResult = new ArrayList<MatchResultInfo>(
				indexToBeReplace.length);
		if (indexToBeReplace.length != 0) {
			reset();
			int row = 0, index = 0;
			if (matcher != null && replacer != null)
				while (matcher.find()) {
					absRowNum++;
					if (index >= indexToBeReplace.length)
						break;
					if (row++ != indexToBeReplace[index])
						continue;
					index++;
					realReplaceResult
							.add(new MatchResultInfo(matcher.group(), replacer
									.toString(), matcher.start(), matcher.end()));
				}
		}
		return Collections.unmodifiableList(realReplaceResult);
	}
	
	private void reParseReplaceExpression() {
		replaceExpressionChanged = false;
		observers.clear();
		if (expressionAvailable)
			try {
				replacer = parser.parse(expressionCache);
			} catch (Throwable t) {
				replaceExpressionChanged = true;
				if (t instanceof Error)
					throw (Error) t;
				else if (t instanceof RuntimeException)
					throw (RuntimeException) t;
			}
		else
			replacer = expressionCache == null ? "" : expressionCache;
	}

	private void reBuildPattern() {
		patternChanged = false;
		if (regexCache.isEmpty())
			pattern = null;
		else
			try{
				pattern = Pattern.compile(regexCache, patternFlag);
			}catch(Throwable t){
				patternChanged = true;
				if(t instanceof Error)
					throw (Error)t;
				else if(t instanceof RuntimeException)
					throw (RuntimeException)t;
			}
	}

	private void reset() {
		fireReset();
		absRowNum = 0;
		if (matcher != null)
			matcher.reset();
	}

	protected void fireReset() {
		for (ResetObserver observer : observers)
			if (observer != null)
				observer.onReset();
	}

	public void setResultObserver(ResultObserver resultObserver) {
		this.resultObserver = resultObserver;
	}

	@Override
	public void addResetObserver(ResetObserver observer) {
		observers.add(observer);
	}

	@Override
	public void addResetObserver(Collection<ResetObserver> observers) {
		this.observers.addAll(observers);
	}

	@Override
	public void removeResetObserver(ResetObserver observer) {
		observers.remove(observer);
	}

	@Override
	public Integer getAbsRowNum() {
		return absRowNum;
	}

	@Override
	public String getGroup(int n) {
		return matcher.group(n);
	}

	@Override
	public String getGroup(String groupName) {
		return matcher.group(groupName);
	}

}
