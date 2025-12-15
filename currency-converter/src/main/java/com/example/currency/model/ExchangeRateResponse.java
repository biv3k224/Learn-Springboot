package com.example.currency.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class ExchangeRateResponse {

    private BigDecimal amount;
    private String base;
    private LocalDate date;
    private Map<String, BigDecimal> rates;

    public ExchangeRateResponse(){

    }

    public ExchangeRateResponse(BigDecimal amount, String base, LocalDate date, Map<String, BigDecimal> rates) {
        this.amount = amount;
        this.base = base;
        this.date = date;
        this.rates = rates;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBase() {
        return base;
    }
    public void setBase(String base) {
        this.base = base;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }
    public void setRate(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }

    @Override
    public String toString() {
        return "ExchangeRateResponse{" +
                "amount=" + amount +
                ", base='" + base + '\'' +
                ", date=" + date +
                ", rates=" + rates +
                '}';
    }
}
