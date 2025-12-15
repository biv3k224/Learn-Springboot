package com.example.currency.service;

import com.example.currency.model.CurrencyConversion;
import com.example.currency.model.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;

    public CurrencyService(RestTemplate restTemplate,
                           @Value("${currency.api.base-url}") String apiBaseUrl) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
    }

    public CurrencyConversion convertCurrency(String fromCurrency,
                                              String toCurrency,
                                              BigDecimal amount) {

        validateInput(fromCurrency, toCurrency, amount);
        BigDecimal exchangeRate = getExchangeRate(fromCurrency, toCurrency);

        CurrencyConversion conversion = new CurrencyConversion(
                fromCurrency.toUpperCase(),
                toCurrency.toUpperCase(),
                amount
        );

        conversion.setExchangeRate(exchangeRate);
        conversion.calculateConvertedAmount();

        return conversion;
    }

    private BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        String url = buildApiUrl(fromCurrency, toCurrency);

        try {
            ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity(
                    url,
                    ExchangeRateResponse.class
            );

            ExchangeRateResponse apiResponse = response.getBody();

            if (apiResponse == null || apiResponse.getRates() == null) { // Use getRate() if that's your getter
                throw new RuntimeException("Invalid response from currency API");
            }

            Map<String, BigDecimal> rates = apiResponse.getRates(); // Use getRate() if that's your getter
            BigDecimal rate = rates.get(toCurrency.toUpperCase());

            if (rate == null) {
                throw new IllegalArgumentException(
                        "Currency not supported: " + toCurrency
                );
            }

            return rate;

        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException(
                    "Invalid currency code or API error: " + e.getMessage()
            );
        } catch (ResourceAccessException e) {
            throw new RuntimeException(
                    "Unable to connect to currency API. Please try again later.",
                    e
            );
        }
    }

    private String buildApiUrl(String fromCurrency, String toCurrency) {
        // FIXED: Using fromUriString() instead of fromHttpUrl()
        return UriComponentsBuilder.fromUriString(apiBaseUrl)
                .path("/latest")
                .queryParam("from", fromCurrency.toUpperCase())
                .queryParam("to", toCurrency.toUpperCase())
                .build()
                .toUriString();

        // OR use simple string concatenation:
        // return apiBaseUrl + "/latest?from=" + fromCurrency.toUpperCase() +
        //        "&to=" + toCurrency.toUpperCase();
    }

    private void validateInput(String fromCurrency, String toCurrency, BigDecimal amount) {
        if (fromCurrency == null || fromCurrency.trim().isEmpty()) {
            throw new IllegalArgumentException("From currency cannot be empty");
        }

        if (toCurrency == null || toCurrency.trim().isEmpty()) {
            throw new IllegalArgumentException("To currency cannot be empty");
        }

        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            throw new IllegalArgumentException("Currencies must be different");
        }

        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (fromCurrency.length() != 3 || toCurrency.length() != 3) {
            throw new IllegalArgumentException("Currency codes must be 3 characters (e.g., USD, EUR)");
        }
    }

    // Add this method for the controller
    public BigDecimal getExchangeRateOnly(String fromCurrency, String toCurrency) {
        validateInput(fromCurrency, toCurrency, BigDecimal.ONE);
        return getExchangeRate(fromCurrency, toCurrency);
    }

    // Add this method for the controller (simplified version)
    public Map<String, BigDecimal> getAvailableCurrencies(String baseCurrency) {
        String url = apiBaseUrl + "/latest?from=" + baseCurrency.toUpperCase();

        ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity(
                url,
                ExchangeRateResponse.class
        );

        // Use getRate() if that's your getter name
        return response.getBody().getRates();
    }
}