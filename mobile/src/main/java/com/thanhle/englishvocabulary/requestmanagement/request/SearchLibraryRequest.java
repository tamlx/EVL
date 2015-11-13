package com.thanhle.englishvocabulary.requestmanagement.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.thanhle.englishvocabulary.requestmanagement.GsonRequest;
import com.thanhle.englishvocabulary.requestmanagement.RequestSuccessBase;
import com.thanhle.englishvocabulary.resource.LibraryResource;
import com.thanhle.englishvocabulary.resource.ListLibraryResource;
import com.thanhle.englishvocabulary.utils.Consts;

import java.lang.reflect.Type;

/**
 * Created by LaiXuanTam on 10/8/2015.
 */
public class SearchLibraryRequest extends RequestSuccessBase<ListLibraryResource> {


    public SearchLibraryRequest(Context context) {
        super(context);
    }

    @Override
    protected void addParams(GsonRequest<ListLibraryResource> request) {
        String token = getExtra().getString(Consts.PARAMConsts.TOKEN);
        request.addParam(Consts.PARAMConsts.TOKEN, token);

        super.addParams(request);
    }

    @Override
    protected String buildRequestUrl() {
        return Consts.URLConsts.SEARCHLIBRARY + getExtra().getString(Consts.PARAMConsts.KEY_SEARCH);
    }

    @Override
    protected Type getClassOf() {
        return ListLibraryResource.class;
    }

    @Override
    protected int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public void postAfterRequest(ListLibraryResource result) {

        if (result != null) {
            if (Integer.valueOf(result.item_total) > 0) {
                if (Consts.LIST_LIBRARY_SEARCH.size() > 0)
                    Consts.LIST_LIBRARY_SEARCH.clear();
                Log.e("SearchLibraryRequest", " Success " + result.item_total);
                for (int i = 0; i < Integer.valueOf(result.item_total); i++) {
                    LibraryResource libraryResource = new LibraryResource();
                    libraryResource.set_id(result.data[i]._id);
                    libraryResource.setName(result.data[i].name);
                    libraryResource.setPrice(result.data[i].price);
                    libraryResource.setDescription(result.data[i].description);
                    Consts.LIST_LIBRARY_SEARCH.add(libraryResource);
                }
            } else {
                if (Consts.LIST_LIBRARY_SEARCH.size() > 0)
                    Consts.LIST_LIBRARY_SEARCH.clear();
                Log.e("SearchLibraryRequest", " Khong tim thay library phu hop");
            }
        }
    }
}
