package com.therap.amin.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    TextView tvOutputCurrency,tvOutputCurrencyValue, tvInputCurrency;
    EditText etInputCurrencyValue;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            setContentView(R.layout.activity_main_tab);
            Toast.makeText(getApplicationContext(),"set",Toast.LENGTH_SHORT).show();
        } else {
            setContentView(R.layout.activity_main);
            Log.d(Constants.TAG, "onCreate: ");

            tvOutputCurrency = (TextView) findViewById(R.id.tvOutputCurrency);
            tvOutputCurrencyValue = (TextView) findViewById(R.id.tvOutputCurrencyValue);
            tvInputCurrency = (TextView) findViewById(R.id.tvInputCurrency);
            etInputCurrencyValue = (EditText) findViewById(R.id.etCurrencyAmount);
            tvOutputCurrencyValue.setText("0");

            sharedPreferences = getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE);

            etInputCurrencyValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals("")) {
                        double convertedAmount = Double.parseDouble(s.toString());
                        double conversionVal = Double.parseDouble(sharedPreferences.getString(Constants.CONVERSION_RATE_KEY, ""));
                        convertedAmount *= conversionVal;
                        tvOutputCurrencyValue.setText(String.format("%.2f",convertedAmount));

                    } else {
                        tvOutputCurrencyValue.setText("0");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            return false;
        } else {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.main, menu);
            return true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            return super.onOptionsItemSelected(item);
        } else {
            switch (item.getItemId()) {
                case R.id.menu_currencyvalues:
                    Intent intent = new Intent(MainActivity.this, CurrencyValueSaverActivity.class);
                    startActivityForResult(intent,1);
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!tabletSize) {
            if (!sharedPreferences.contains(Constants.INPUT_CURRENCY_KEY)) {
                Toast.makeText(getApplicationContext(), "Please set value at first", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, CurrencyValueSaverActivity.class);
                startActivityForResult(intent,1);
            } else {
                tvInputCurrency.setText(sharedPreferences.getString(Constants.INPUT_CURRENCY_KEY, ""));
                tvOutputCurrency.setText(sharedPreferences.getString(Constants.OUTPUT_CURRENCY_KEY, ""));
            }

            Log.d(Constants.TAG, "onResume: ");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!tabletSize) {
            if (requestCode == 1) {
                if (resultCode != RESULT_OK) {
                    finish();
                }
            }
            Log.d(Constants.TAG, "onActivityResult: ");
        }

    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(300);
        // 1dp/ms
        //a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}

