package com.therap.amin.currencyconverter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by amin on 5/18/16.
 */
public class FileProcessor {

    Context context;
    Map<String,Double> values;
    DecimalFormat numberFormat;

    public FileProcessor(Context context) {
        this.context = context;
        values = new HashMap<String, Double>();
        numberFormat = new DecimalFormat("#.00");
    }

    public void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public Map<String,Double> readFileAndProcess() {
        String s = readFromFile();
        try {
            JSONObject jsonObject = new JSONObject(s);
            jsonObject = jsonObject.getJSONObject("quotes");
            JSONArray jsonArray = jsonObject.names();

            for (int i=0;i<jsonArray.length();i++) {
                values.put(jsonArray.get(i).toString(),Double.parseDouble(jsonObject.getString(jsonArray.get(i).toString())));
                values.put(reverseCurrencyRelation(jsonArray.get(i).toString()), Double.parseDouble(numberFormat.format(1/Double.parseDouble(jsonObject.getString(jsonArray.get(i).toString())) )));
            }
            values = processRemainigCurrencies(values);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return values;
    }

    private String readFromFile() {

        String contentOfFile = "";

        try {
            InputStream inputStream = context.openFileInput("data.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                contentOfFile = stringBuilder.toString();

            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentOfFile;
    }

    public Map<String,Double> processRemainigCurrencies(Map<String,Double> input) {
        Set<String> keys = input.keySet();
        ArrayList<String> k = new ArrayList<>(keys);

        for (String s : k) {
            for (String s1 : k) {
                if (!s.equals(s1) && !s.endsWith("USD") && !s1.endsWith("USD")) {
                    input.put(s.substring(3)+s1.substring(3),Double.parseDouble(numberFormat.format(input.get(s1)/input.get(s))));
                }
            }
        }
        input.put("USDUSD",1.0);
        input.put("GBPGBP",1.0);
        input.put("BDTBDT",1.0);
        input.put("AUDAUD",1.0);
        input.put("CADCAD",1.0);
        return input;
    }

    public String reverseCurrencyRelation (String inputCurrencyRelation) {
        char[] inputArray = inputCurrencyRelation.toCharArray();
        for (int i=0;i<3;i++) {
            char ch;
            ch = inputArray[i];
            inputArray[i] = inputArray[i+3];
            inputArray[i+3] = ch;
        }
        inputCurrencyRelation = new String(inputArray);
        return inputCurrencyRelation;
    }
}
