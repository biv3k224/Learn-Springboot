package com.example.currency.controller;

import com.example.currency.model.CurrencyConversion;
import com.example.currency.model.ErrorResponse;
import com.example.currency.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);
    private final CurrencyService currencyService;

    // Constructor injection
    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    /**
     * Convert currency from one to another
     * GET /api/currency/convert?from=USD&to=EUR&amount=100
     */
    @GetMapping("/convert")
    public ResponseEntity<?> convertCurrency(
            @RequestParam(value = "from", required = true) String fromCurrency,
            @RequestParam(value = "to", required = true) String toCurrency,
            @RequestParam(value = "amount", required = true) BigDecimal amount) {

        logger.info("Received conversion request: {} to {}, amount: {}",
                fromCurrency, toCurrency, amount);

        try {
            CurrencyConversion result = currencyService.convertCurrency(
                    fromCurrency.toUpperCase(),
                    toCurrency.toUpperCase(),
                    amount
            );

            logger.info("Conversion successful: {}", result);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            logger.warn("Bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                            "Bad Request",
                            e.getMessage(),
                            "/api/currency/convert")
            );
        } catch (Exception e) {
            logger.error("Internal server error during conversion: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Internal Server Error",
                            "An unexpected error occurred",
                            "/api/currency/convert")
            );
        }
    }

    /**
     * Get exchange rate between two currencies
     * GET /api/currency/rate?from=USD&to=EUR
     */
    @GetMapping("/rate")
    public ResponseEntity<?> getExchangeRate(
            @RequestParam(value = "from", defaultValue = "USD") String fromCurrency,
            @RequestParam(value = "to", defaultValue = "EUR") String toCurrency) {

        logger.info("Getting exchange rate: {} to {}", fromCurrency, toCurrency);

        try {
            BigDecimal rate = currencyService.getExchangeRateOnly(
                    fromCurrency.toUpperCase(),
                    toCurrency.toUpperCase()
            );

            // Create a simple response object
            Map<String, Object> response = Map.of(
                    "from", fromCurrency.toUpperCase(),
                    "to", toCurrency.toUpperCase(),
                    "rate", rate,
                    "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                            "Bad Request",
                            e.getMessage(),
                            "/api/currency/rate")
            );
        }
    }

    /**
     * Get all available rates for a base currency
     * GET /api/currency/rates?base=USD
     */
    @GetMapping("/rates")
    public ResponseEntity<?> getAllRates(
            @RequestParam(value = "base", defaultValue = "EUR") String baseCurrency) {

        logger.info("Getting all rates for base currency: {}", baseCurrency);

        try {
            Map<String, BigDecimal> rates = currencyService.getAvailableCurrencies(
                    baseCurrency.toUpperCase()
            );

            Map<String, Object> response = Map.of(
                    "base", baseCurrency.toUpperCase(),
                    "rates", rates,
                    "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching rates: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Internal Server Error",
                            "Failed to fetch exchange rates",
                            "/api/currency/rates")
            );
        }
    }

    /**
     * Health check endpoint
     * GET /api/currency/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check requested");
        return ResponseEntity.ok("Currency Converter API is running! Status: OK");
    }

    /**
     * Get API information
     * GET /api/currency/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getApiInfo() {
        Map<String, String> info = Map.of(
                "name", "Currency Converter API",
                "version", "1.0",
                "description", "REST API for currency conversion using Frankfurter.app",
                "status", "active",
                "documentation", "Visit /api/currency/health for health check"
        );

        return ResponseEntity.ok(info);
    }

    /**
     * Global exception handler for this controller
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                "/api/currency"
        );

        logger.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle missing request parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = "Missing required parameter: " + ex.getParameterName();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                "/api/currency"
        );

        logger.warn("Missing parameter: {}", ex.getParameterName());
        return ResponseEntity.badRequest().body(error);
    }

    @GetMapping("/")
    public String index() {
        logger.info("Serving index.html");
        return "index";
    }


}