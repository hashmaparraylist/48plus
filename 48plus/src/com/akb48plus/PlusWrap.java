/**
 * 
 */
package com.akb48plus;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.akb48plus.common.auth.AuthUtils;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusRequest;

/**
 * @author QuSheng
 */
public class PlusWrap {
    private static final HttpTransport HTTP_TRANSPORT = AndroidHttp
            .newCompatibleTransport();
    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private final Plus plus;
    private static final String TAG = PlusWrap.class.getName();

    /**
     * 
     * @param ctx
     */
    public PlusWrap(Context ctx) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                AuthUtils.PREF_NAME, 0);
        final String accessToken = prefs.getString("accessToken", null);
        Log.d(TAG, "Plus Init AccessToken=" + accessToken);
        final GoogleAccessProtectedResource protectedResource = new GoogleAccessProtectedResource(
                accessToken);
        plus = Plus
                .builder(HTTP_TRANSPORT, JSON_FACTORY)
                .setApplicationName("302050057200.apps.googleusercontent.com")          // Application Name
                .setHttpRequestInitializer(protectedResource)
                .setJsonHttpRequestInitializer(
                        new JsonHttpRequestInitializer() {
                            @Override
                            public void initialize(JsonHttpRequest request) {
                                PlusRequest plusRequest = (PlusRequest) request;
                                plusRequest.setKey("AIzaSyBjeUX9A35iPjNk8DhjWOw41iOZ82aTl-4"); // OAuth 2.0's API Key
                            }
                        }).build();
    }

    public Plus get() {
        return plus;
    }
}
