package com.example.currency.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConversion {

    private String fromCurrency;
    private String toCurrency;
    private BigDecimal amount;

    private BigDecimal exchangeRate;
    private BigDecimal convertedAmount;

    public CurrencyConversion() {

    }

    public CurrencyConversion(String fromCurrency, String toCurrency, BigDecimal amount) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
    }

    public CurrencyConversion(String fromCurrency, String toCurrency, BigDecimal amount, BigDecimal exchangeRate, BigDecimal convertedAmount) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.exchangeRate = exchangeRate;
        this.convertedAmount = convertedAmount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }
    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }
    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal  getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }
    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public void calculateConvertedAmount(){
        if(amount != null && exchangeRate != null){
            this.convertedAmount = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public String getFormattedConvertedAmount() {
        if (convertedAmount != null) {
            return String.format("%s %.2f", toCurrency, convertedAmount);
        }
        return null;
    }

    @Override
    public String toString() {
        return "CurrencyConversion{" +
                "fromCurrency='" + fromCurrency + '\'' +
                ", toCurrency='" + toCurrency + '\'' +
                ", amount=" + amount +
                ", exchangeRate=" + exchangeRate +
                ", convertedAmount=" + convertedAmount +
                '}';
    }
}
