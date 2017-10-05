package com.therap.amin.currencyconverter.service;

import android.os.Handler;
import android.os.Message;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.therap.amin.currencyconverter.Constants;

import org.json.JSONObject;

import javax.inject.Inject;

import cz.msebera.android.httpclient.Header;

/**
 * @author Ripon
 */
public class CurrencyConverterService {

    @Inject
    AsyncHttpClient client;

    @Inject
    public CurrencyConverterService(AsyncHttpClient client) {
        this.client = client;
    }

    public void retreiveData(String url, RequestParams params, final Handler handler) {

        client.get(url, params, new JsonHttpResponseHandler() {
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
