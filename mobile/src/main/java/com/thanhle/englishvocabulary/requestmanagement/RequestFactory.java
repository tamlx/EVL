package com.thanhle.englishvocabulary.requestmanagement;

import android.content.Context;

import com.thanhle.englishvocabulary.requestmanagement.request.DownloadLibraryRequest;
import com.thanhle.englishvocabulary.requestmanagement.request.GetLibraryInfoRequest;
import com.thanhle.englishvocabulary.requestmanagement.request.GetListLibraryRequest;
import com.thanhle.englishvocabulary.requestmanagement.request.LoginRequest;
import com.thanhle.englishvocabulary.requestmanagement.request.RegisterRequest;
import com.thanhle.englishvocabulary.requestmanagement.request.SearchLibraryRequest;
import com.thanhle.englishvocabulary.requestmanagement.request.UpdateProfileRequest;
import com.thanhle.englishvocabulary.utils.Actions;

/**
 * return request class base on action
 *
 * @author thanhle
 */
public class RequestFactory {
    @SuppressWarnings("rawtypes")
    public static RequestSuccessBase getRequest(Context context, int action) {
        switch (action) {
            case Actions.LOGIN:
                return new LoginRequest(context);
            case Actions.REGISTER:
                return new RegisterRequest(context);
            case Actions.UPDATEPROFILE:
                return new UpdateProfileRequest(context);
            case Actions.GETLISTLIBRARY:
                return new GetListLibraryRequest(context);
            case Actions.SEARCHLIBRARY:
                return new SearchLibraryRequest(context);
            case Actions.GETLIBRARYINFO:
                return new GetLibraryInfoRequest(context);
            case Actions.DOWNLOAD_LIBRARY:
                return new DownloadLibraryRequest(context);
            default:
                return null;
        }
    }
}
