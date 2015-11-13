package com.thanhle.englishvocabulary.requestmanagement;

public interface RequestSuccessListener<T> {
	public void postAfterRequest(T result);
}
