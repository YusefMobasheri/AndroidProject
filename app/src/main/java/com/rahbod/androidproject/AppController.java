package com.rahbod.androidproject;

import android.app.Application;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class AppController extends Application {

    private static final String BASE_URL = "http://visit365.ir/";
    // visit
    private static final String CLIENT_ID = "UiRgEQt91vL60-8pixMTkxplOB-jAplL24rdfgDFgS6RTSf6eBGuFZ8ckmLXFT0nBRrz_6C5rGbmmY2f";
    private static final String CLIENT_SECRET = "huHxZTH6EZ_3Y8b71wcFetW34aFGc2P1x2H_yHVh9-uxB5lbcF12ds81mM8SB5IDWGZGpasd211";

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void sendRequest(String url, final JSONObject params, Response.Listener<JSONObject> resListener) {
        sendRequest(url, params, resListener, TAG);
    }

    public void sendRequest(String url, final JSONObject params, Response.Listener<JSONObject> resListener, final String tag) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, BASE_URL + url, params, resListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                String token = Base64.encodeToString(
                        (CLIENT_ID + ":" + CLIENT_SECRET).getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + token);
                return headers;
            }
        };
        req.setTag(tag);
        addToRequestQueue(req);
    }
}