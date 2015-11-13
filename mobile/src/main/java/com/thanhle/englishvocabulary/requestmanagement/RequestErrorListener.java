package com.thanhle.englishvocabulary.requestmanagement;

public interface RequestErrorListener {
	public void onError(int status, String code, String message);
}
