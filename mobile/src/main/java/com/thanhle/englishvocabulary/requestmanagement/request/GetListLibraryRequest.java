package com.thanhle.englishvocabulary.requestmanagement.request;

import android.content.Context;

import com.android.volley.Request;
import com.thanhle.englishvocabulary.requestmanagement.GsonRequest;
import com.thanhle.englishvocabulary.requestmanagement.RequestSuccessBase;
import com.thanhle.englishvocabulary.resource.LibraryResource;
import com.thanhle.englishvocabulary.resource.ListLibraryResource;
import com.thanhle.englishvocabulary.utils.Consts;

import java.lang.reflect.Type;

/**
 * Created by LaiXuanTam on 10/7/2015.
 */
public class GetListLibraryRequest extends RequestSuccessBase<ListLibraryResource> {

    public GetListLibraryRequest(Context context) {
        super(context);
    }

    @Override
    protected void addParams(GsonRequest<ListLibraryResource> request) {
        String token = getExtra().getString(Consts.PARAMConsts.TOKEN);
        request.addParam(Consts.PARAMConsts.TOKEN, token);
    }

    @Override
    protected String buildRequestUrl() {
        return Consts.URLConsts.GETLISTLIBRARY;
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
            if (Integer.valueOf(result.item_total) == 1) {
                LibraryResource libraryResource = new LibraryResource();
                libraryResource.setName(result.data[0].name);
                libraryResource.setPrice(result.data[0].price);
                libraryResource.set_id(result.data[0]._id);
                libraryResource.setDescription(result.data[0].description);
                Consts.LIST_LIBRARY.add(libraryResource);

            } else if (Integer.valueOf(result.item_total) > 1) {
                for (int i = 0; i < Integer.valueOf(result.item_total); i++) {
                    LibraryResource libraryResource = new LibraryResource();
                    libraryResource.setName(result.data[i].name);
                    libraryResource.setPrice(result.data[i].price);
                    libraryResource.set_id(result.data[i]._id);
                    libraryResource.setDescription(result.data[i].description);
                    Consts.LIST_LIBRARY.add(libraryResource);
                }
            }
        }

    }
}
