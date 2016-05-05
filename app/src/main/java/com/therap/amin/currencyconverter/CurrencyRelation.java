package com.therap.amin.currencyconverter;

/**
 * Created by amin on 5/4/16.
 */
public class CurrencyRelation {

    private String leftCurrency;
    private String rightCurrency;
    private int conversionValue;

    public CurrencyRelation(String leftCurrency, String rightCurrency, int conversionValue) {
        this.leftCurrency = leftCurrency;
        this.rightCurrency = rightCurrency;
        this.conversionValue = conversionValue;
    }

    public String getLeftCurrency() {
        return leftCurrency;
    }

    public void setLeftCurrency(String leftCurrency) {
        this.leftCurrency = leftCurrency;
    }

    public String getRightCurrency() {
        return rightCurrency;
    }

    public void setRightCurrency(String rightCurrency) {
        this.rightCurrency = rightCurrency;
    }

    public int getConversionValue() {
        return conversionValue;
    }

    public void setConversionValue(int conversionValue) {
        this.conversionValue = conversionValue;
    }

    @Override
    public String toString() {
        return "CurrencyRelation{" +
                "leftCurrency='" + leftCurrency + '\'' +
                ", rightCurrency='" + rightCurrency + '\'' +
                ", conversionValue=" + conversionValue +
                '}';
    }
}
