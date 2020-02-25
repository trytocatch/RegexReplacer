package com.github.trytocatch.regexreplacer.model;

public class MatchResultInfo {
	private String matchedStr;
	private String replaceStr;
	private int startPos, endPos;
	
	public MatchResultInfo(String matchedStr, String replaceStr, int startPos,
			int endPos) {
		this.matchedStr = matchedStr;
		this.replaceStr = replaceStr;
		this.startPos = startPos;
		this.endPos = endPos;
	}
	public String getMatchedStr() {
		return matchedStr;
	}
	public String getReplaceStr() {
		return replaceStr;
	}
	public int getStartPos() {
		return startPos;
	}
	public int getEndPos() {
		return endPos;
	}
}
