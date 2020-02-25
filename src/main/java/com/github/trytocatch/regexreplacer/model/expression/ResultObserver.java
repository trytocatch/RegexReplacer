package com.github.trytocatch.regexreplacer.model.expression;

import java.util.List;

import com.github.trytocatch.regexreplacer.model.MatchResultInfo;

public interface ResultObserver {
	/**
	 * when calculation start
	 */
	public void onStart();
	/**
	 * be called while result changed, don't do time-consuming operation here
	 * @param result read-only
	 * @param errorInfo:
	 * 	if errorInfo!=null, result will be null; if errorInfo==null, result may be null, result!=null means no error
	 */
	public void onResultChanged(List<MatchResultInfo> result,
			String errorInfo);
}
