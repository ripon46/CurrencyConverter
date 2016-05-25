package com.therap.amin.currencyconverter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by amin on 5/17/16.
 */
public class FetchCurrencyValues {

    Context context;

    public FetchCurrencyValues(Context context) {
        this.context = context;
    }

    public InputStream openHttpConnection(String urlString) throws IOException {
        InputStream inputStream = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        if (!(connection instanceof HttpURLConnection))
            throw new IOException("Not an Http Connection");
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            response = httpURLConnection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return inputStream;
    }

    public void fetch(String url) {
        new DownloadCurrencies().execute(url);
    }

    private class DownloadCurrencies extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder total = new StringBuilder();
            try {
                InputStream inputStream = openHttpConnection(params[0]);
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                String string;
                while ((string = r.readLine()) != null) {
                    total.append(string);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return total.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.isEmpty()) {
                Toast.makeText(context, "Failed Loading Currency Values", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Successfully Loaded", Toast.LENGTH_LONG).show();
                FileProcessor fileProcessor = new FileProcessor(context);
                fileProcessor.writeToFile(s);
            }
        }
    }
}
