package com.therap.amin.currencyconverter;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by amin on 5/18/16.
 */
public class FileProcessor {

    Context context;
    Map<String, Double> values;
    DecimalFormat numberFormat;

    public FileProcessor(Context context) {
        this.context = context;
        values = new HashMap<String, Double>();
        values = readFileAndProcess();
        numberFormat = new DecimalFormat("#.00");
    }

    public void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            values = readFileAndProcess();
            Method method = context.getClass().getMethod("updateUI");
            method.invoke(context);
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> readFileAndProcess() {
        String s = readFromFile();
        try {
            JSONObject jsonObject = new JSONObject(s);
            jsonObject = jsonObject.getJSONObject("quotes");
            JSONArray jsonArray = jsonObject.names();

            for (int i = 0; i < jsonArray.length(); i++) {
                values.put(jsonArray.get(i).toString().substring(3), Double.parseDouble(jsonObject.getString(jsonArray.get(i).toString())));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return values;
    }

    private String readFromFile() {

        String contentOfFile = "";

        try {
            InputStream inputStream = context.openFileInput("data.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                contentOfFile = stringBuilder.toString();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentOfFile;
    }


    public double calculateConversionRate(String fromCurrency, String toCurrency) {
        if (values.containsKey(fromCurrency) || values.containsKey(toCurrency)) {
            if (fromCurrency.equals("USD")) {
                return values.get(toCurrency);
            } else if (toCurrency.equals("USD")) {
                return Double.parseDouble(numberFormat.format(1 / values.get(fromCurrency)));
            } else {
                return Double.parseDouble(numberFormat.format(values.get(toCurrency) / values.get(fromCurrency)));
            }
        }
        return -1;
    }

}