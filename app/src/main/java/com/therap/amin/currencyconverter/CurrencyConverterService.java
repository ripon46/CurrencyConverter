package com.therap.amin.currencyconverter;

import android.os.Handler;
import android.os.Message;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

/**
 * Created by amin on 6/7/16.
 */
public class CurrencyConverterService {

    private AsyncHttpClient client;
    private Handler handler;

    public CurrencyConverterService(Handler handler) {
        client = new AsyncHttpClient();
        this.handler = handler;
    }

    public void retreiveData(String url, RequestParams params) {

        client.get(url,params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Message.obtain(handler, Constants.SUCCESS, response).sendToTarget();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Message.obtain(handler, Constants.FAILURE, responseString).sendToTarget();
            }
        });
    }
}
