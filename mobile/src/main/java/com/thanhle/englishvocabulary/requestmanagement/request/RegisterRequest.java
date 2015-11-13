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
 * Created by thanhle on 2/11/2015.
 */
public class RegisterRequest extends RequestSuccessBase<UserResource> {

    public RegisterRequest(Context context) {
        super(context);
    }

    @Override
    protected void addParams(GsonRequest<UserResource> request) {
        String username = getExtra().getString(Consts.PARAMConsts.USERNAME);
        String email = getExtra().getString(Consts.PARAMConsts.EMAIL);
        String password = getExtra().getString(Consts.PARAMConsts.PASSWORD);
        String provider = getExtra().getString(Consts.PARAMConsts.PROVIDER);
        request.addParam(Consts.PARAMConsts.USERNAME, username);
        request.addParam(Consts.PARAMConsts.EMAIL, email);
        request.addParam(Consts.PARAMConsts.PASSWORD, password);
        request.addParam(Consts.PARAMConsts.PROVIDER, provider);
    }

    @Override
    protected String buildRequestUrl() {
        return Consts.URLConsts.REGISTER_URL;
    }

    @Override
    protected Type getClassOf() {
        return UserResource.class;
    }

    @Override
    protected int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public void postAfterRequest(UserResource result) {
        if ("facebook".equalsIgnoreCase(result.provider)) {
            SharePrefs.getInstance().save(Consts.LOGINFACEBOOK, true);
            result.fullname = Consts.USERNAME_FB_GG;
        } else if ("google".equalsIgnoreCase(result.provider)) {
            SharePrefs.getInstance().save(Consts.LOGINGOOGLE, true);
            result.fullname = Consts.USERNAME_FB_GG;
        }
        SharePrefs.getInstance().saveToken(result.token);
        SharePrefs.getInstance().saveUserInfo(result);
    }
}
