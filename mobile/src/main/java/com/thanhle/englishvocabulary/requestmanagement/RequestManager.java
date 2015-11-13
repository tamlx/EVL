package com.thanhle.englishvocabulary.requestmanagement;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.thanhle.englishvocabulary.utils.VolleyHttpClient;

/**
 * Request API Management class
 *
 * @author thanhlcm
 */
public class RequestManager {
    private volatile static RequestManager mInstance;
    /**
     * Volley Request queue
     */
    private RequestQueue mRequestQueue;
    private Context mContext;

    public static RequestManager getInstance() {
        if (mInstance == null) {
            synchronized (RequestManager.class) {
                if (mInstance == null) {
                    mInstance = new RequestManager();
                }
            }
        }
        return mInstance;
    }

    public RequestManager() {
    }

    public void init(Context context) {
        if (mContext != null)
            return;
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context, new VolleyHttpClient());
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void request(int action, Bundle bundle, Listener listener, RequestErrorListener errorListener) {
        RequestSuccessBase request = RequestFactory.getRequest(mContext, action);
        if (request != null) {
            request.setExtra(bundle);
            request.execute(action, listener, errorListener);
        }
    }

    RequestFilter mStopAllFilter = new RequestFilter() {
        @Override
        public boolean apply(Request<?> request) {
            return true;
        }
    };

    public void stopAllRequest() {
        // stop all request
        mRequestQueue.cancelAll(mStopAllFilter);
    }
}
