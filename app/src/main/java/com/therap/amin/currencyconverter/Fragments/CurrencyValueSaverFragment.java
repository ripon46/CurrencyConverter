package com.therap.amin.currencyconverter.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.therap.amin.currencyconverter.Constants;
import com.therap.amin.currencyconverter.CurrencyConversionApplication;
import com.therap.amin.currencyconverter.R;
import com.therap.amin.currencyconverter.interfaces.ValueSaverFragmentViewInterface;
import com.therap.amin.currencyconverter.interfaces.ValueSaverPresenterInterface;

import javax.inject.Inject;

/**
 * @author Ripon
 */
public class CurrencyValueSaverFragment extends Fragment implements AdapterView.OnItemSelectedListener, ValueSaverFragmentViewInterface {

    private Spinner inputCurrencySpinner;
    private Spinner outputCurrencySpinner;
    private Button saveButton;
    private EditText etConversionValue;
    private TextView tvPresentCurrencyRelation;

    @Inject
    ValueSaverPresenterInterface currencyValueSaverPresenter;

    @Inject
    ArrayAdapter<String> currencyAdapter;

    @Inject
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency_rate_saver, container, false);
    }

    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CurrencyConversionApplication.getComponent().inject(this);
        currencyValueSaverPresenter.setView(this, getActivity());

        inputCurrencySpinner = view.findViewById(R.id.spnLeftCurrency);
        outputCurrencySpinner = view.findViewById(R.id.spnRightCurrency);
        saveButton = view.findViewById(R.id.btnSave);
        etConversionValue = view.findViewById(R.id.etRightCurrencyValue);
        tvPresentCurrencyRelation = view.findViewById(R.id.tvPresentCurrencyRelation);

        inputCurrencySpinner.setAdapter(currencyAdapter);
        outputCurrencySpinner.setAdapter(currencyAdapter);

        if (sharedPreferences.contains(Constants.LAST_SAVED_FROM_CURRENCY_KEY)) {
            inputCurrencySpinner.setSelection(currencyAdapter.getPosition(sharedPreferences.getString(Constants.LAST_SAVED_FROM_CURRENCY_KEY, "")));
            outputCurrencySpinner.setSelection(currencyAdapter.getPosition(sharedPreferences.getString(Constants.LAST_SAVED_TO_CURRENCY_KEY, "")));
        }

        inputCurrencySpinner.setOnItemSelectedListener(this);
        outputCurrencySpinner.setOnItemSelectedListener(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencyValueSaverPresenter.saveButtonClick(etConversionValue.getText().toString(), inputCurrencySpinner.getSelectedItem().toString(),
                        outputCurrencySpinner.getSelectedItem().toString());
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        updateUI();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


    @Override
    public void setConversionValue(String string) {
        etConversionValue.setText(string);
    }

    @Override
    public void updateUI() {
        currencyValueSaverPresenter.calculateConversionRate(inputCurrencySpinner.getSelectedItem().toString(), outputCurrencySpinner.getSelectedItem().toString());
        currencyValueSaverPresenter.saveToPreference(inputCurrencySpinner.getSelectedItem().toString(), outputCurrencySpinner.getSelectedItem().toString());
    }

    @Override
    public void setPresentCurrencyRelationText(Double conversionRate, String from, String to) {
        if (conversionRate != -1) {
            tvPresentCurrencyRelation.setText(String.format("1 %s = %.2f %s", from, conversionRate, to));
        } else {
            tvPresentCurrencyRelation.setText(getContext().getResources().getString(R.string.not_available));
        }
    }
}
