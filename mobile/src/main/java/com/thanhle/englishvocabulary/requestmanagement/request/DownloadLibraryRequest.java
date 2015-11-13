package com.thanhle.englishvocabulary.requestmanagement.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.thanhle.englishvocabulary.requestmanagement.GsonRequest;
import com.thanhle.englishvocabulary.requestmanagement.RequestSuccessBase;
import com.thanhle.englishvocabulary.resource.LibraryInfosResource;
import com.thanhle.englishvocabulary.utils.Consts;

import java.lang.reflect.Type;

/**
 * Created by LaiXuanTam on 11/6/2015.
 */
public class DownloadLibraryRequest extends RequestSuccessBase<LibraryInfosResource> {
    public DownloadLibraryRequest(Context context) {
        super(context);
    }


    @Override
    protected void addParams(GsonRequest<LibraryInfosResource> request) {
        String token = getExtra().getString(Consts.PARAMConsts.TOKEN);
        request.addHeader("authorization", token);
    }

    @Override
    protected String buildRequestUrl() {

        return Consts.URLConsts.DOWNLOAD_LIBRARY + getExtra().getString(Consts.PARAMConsts.KEY_LIBRARY_ID);
    }

    @Override
    protected Type getClassOf() {
        return LibraryInfosResource.class;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public void postAfterRequest(LibraryInfosResource result) {
        if (result != null) {
            Log.e("DownloadLibraryInfoRequest", " Success ");
            Consts.LIBRARY_INFO.setName(result.getName());
            Consts.LIBRARY_INFO.setCard_total(result.getCard_total());
            Consts.LIBRARY_INFO.setDownloaded(result.getDownloaded());
        }
    }
}
