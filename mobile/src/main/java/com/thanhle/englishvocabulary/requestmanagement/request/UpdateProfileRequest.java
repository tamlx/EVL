package com.thanhle.englishvocabulary.requestmanagement.request;

import android.content.Context;

import com.android.volley.Request;
import com.thanhle.englishvocabulary.requestmanagement.GsonRequest;
import com.thanhle.englishvocabulary.requestmanagement.RequestSuccessBase;
import com.thanhle.englishvocabulary.resource.UserResource;
import com.thanhle.englishvocabulary.utils.Consts;
import com.thanhle.englishvocabulary.utils.SharePrefs;

import java.lang.reflect.Type;

/**
 * Created by LaiXuanTam on 10/2/2015.
 */
public class UpdateProfileRequest extends RequestSuccessBase<UserResource> {


    public UpdateProfileRequest(Context context) {
        super(context);
    }


    @Override
    protected void addParams(GsonRequest<UserResource> request) {
        UserResource info = SharePrefs.getInstance().getUserInfo();

        String email = info.email;
        String oldpassword = getExtra().getString(Consts.PARAMConsts.OLDPASSWORD);
        String password = getExtra().getString(Consts.PARAMConsts.PASSWORD);
        String username = getExtra().getString(Consts.PARAMConsts.FULLNAME);

        request.addParam(Consts.PARAMConsts.FULLNAME, username);
        request.addParam(Consts.PARAMConsts.EMAIL, email);
        request.addParam(Consts.PARAMConsts.PASSWORD, password);
        request.addParam(Consts.PARAMConsts.OLDPASSWORD, oldpassword);
    }

    @Override
    protected String buildRequestUrl() {
        return Consts.URLConsts.UPDATEPROFILE_URL + SharePrefs.getInstance().getUserInfo().token;
    }

    @Override
    protected Type getClassOf() {
        return UserResource.class;
    }

    @Override
    protected int getMethod() {
        return Request.Method.PUT;
    }

    @Override
    public void postAfterRequest(UserResource result) {
        SharePrefs.getInstance().removeUserInfo();
        SharePrefs.getInstance().saveUserInfo(result);
    }
}
