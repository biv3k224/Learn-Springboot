// API Configuration
const API_BASE_URL = 'http://localhost:8080/api/currency';
const DEFAULT_CURRENCIES = ['USD', 'EUR', 'GBP', 'JPY', 'CAD', 'AUD', 'CHF', 'CNY'];

// DOM Elements
const amountInput = document.getElementById('amount');
const fromCurrencySelect = document.getElementById('fromCurrency');
const toCurrencySelect = document.getElementById('toCurrency');
const convertBtn = document.getElementById('convertBtn');
const swapBtn = document.getElementById('swapCurrencies');
const resultSection = document.getElementById('resultSection');
const loadingElement = document.getElementById('loading');
const errorElement = document.getElementById('errorMessage');
const errorText = document.getElementById('errorText');
const quickButtons = document.getElementById('quickButtons');
const apiStatusIcon = document.getElementById('apiStatusIcon');
const apiStatusText = document.getElementById('apiStatusText');

// Result elements
const fromCodeElement = document.getElementById('fromCode');
const toCodeElement = document.getElementById('toCode');
const originalAmountElement = document.getElementById('originalAmount');
const convertedAmountElement = document.getElementById('convertedAmount');
const exchangeRateElement = document.getElementById('exchangeRate');
const fromRateCodeElement = document.getElementById('fromRateCode');
const toRateCodeElement = document.getElementById('toRateCode');
const rateValueElement = document.getElementById('rateValue');
const lastUpdatedElement = document.getElementById('lastUpdated');
const timestampElement = document.getElementById('timestamp');

// State
let availableCurrencies = [...DEFAULT_CURRENCIES];

// Initialize the application
async function init() {
    await checkApiStatus();
    await loadAvailableCurrencies();
    populateCurrencySelects();
    createQuickConversionButtons();
    setupEventListeners();

    // Perform initial conversion
    await performConversion();
}

// Check if API is running
async function checkApiStatus() {
    try {
        const response = await fetch(`${API_BASE_URL}/health`);
        if (response.ok) {
            apiStatusIcon.classList.add('connected');
            apiStatusIcon.style.color = '#4CAF50';
            apiStatusText.textContent = 'API Connected';
            apiStatusText.style.color = '#4CAF50';
        } else {
            throw new Error('API not responding');
        }
    } catch (error) {
        apiStatusIcon.style.color = '#f44336';
        apiStatusText.textContent = 'API Disconnected';
        apiStatusText.style.color = '#f44336';
        showError('Unable to connect to backend API. Make sure the Spring Boot application is running.');
    }
}

// Load available currencies from API
async function loadAvailableCurrencies() {
    try {
        const response = await fetch(`${API_BASE_URL}/rates?base=USD`);
        if (response.ok) {
            const data = await response.json();
            availableCurrencies = ['USD', ...Object.keys(data.rates)];
        }
    } catch (error) {
        console.warn('Could not load currency list, using defaults:', error);
        // Keep default currencies
    }
}

// Populate currency dropdowns
function populateCurrencySelects() {
    // Clear existing options
    fromCurrencySelect.innerHTML = '';
    toCurrencySelect.innerHTML = '';

    // Add options
    availableCurrencies.forEach(currency => {
        const option1 = document.createElement('option');
        option1.value = currency;
        option1.textContent = `${getCurrencySymbol(currency)} ${currency}`;
        fromCurrencySelect.appendChild(option1);

        const option2 = document.createElement('option');
        option2.value = currency;
        option2.textContent = `${getCurrencySymbol(currency)} ${currency}`;
        toCurrencySelect.appendChild(option2);
    });

    // Set default values
    fromCurrencySelect.value = 'USD';
    toCurrencySelect.value = 'EUR';
}

// Get currency symbol
function getCurrencySymbol(currency) {
    const symbols = {
        'USD': '$',
        'EUR': '€',
        'GBP': '£',
        'JPY': '¥',
        'CAD': 'C$',
        'AUD': 'A$',
        'CHF': 'Fr',
        'CNY': '¥',
        'INR': '₹'
    };
    return symbols[currency] || currency;
}

// Create quick conversion buttons
function createQuickConversionButtons() {
    const popularPairs = [
        { from: 'USD', to: 'EUR', label: 'USD → EUR' },
        { from: 'EUR', to: 'GBP', label: 'EUR → GBP' },
        { from: 'USD', to: 'JPY', label: 'USD → JPY' },
        { from: 'GBP', to: 'USD', label: 'GBP → USD' },
        { from: 'AUD', to: 'USD', label: 'AUD → USD' },
        { from: 'USD', to: 'CAD', label: 'USD → CAD' }
    ];

    quickButtons.innerHTML = '';

    popularPairs.forEach(pair => {
        const button = document.createElement('button');
        button.className = 'quick-btn';
        button.innerHTML = `
            <div style="font-size: 0.9rem; color: #666;">${pair.from} → ${pair.to}</div>
            <div style="font-weight: bold; margin-top: 5px;">Quick Convert</div>
        `;

        button.addEventListener('click', () => {
            fromCurrencySelect.value = pair.from;
            toCurrencySelect.value = pair.to;
            performConversion();
        });

        quickButtons.appendChild(button);
    });
}

// Setup event listeners
function setupEventListeners() {
    convertBtn.addEventListener('click', performConversion);

    swapBtn.addEventListener('click', () => {
        const temp = fromCurrencySelect.value;
        fromCurrencySelect.value = toCurrencySelect.value;
        toCurrencySelect.value = temp;
        performConversion();
    });

    amountInput.addEventListener('input', () => {
        // Validate amount
        const amount = parseFloat(amountInput.value);
        if (amount <= 0) {
            amountInput.style.borderColor = '#f44336';
        } else {
            amountInput.style.borderColor = '#ddd';
        }
    });

    // Auto-convert when amount or currencies change
    [amountInput, fromCurrencySelect, toCurrencySelect].forEach(element => {
        element.addEventListener('change', performConversion);
    });
}

// Perform currency conversion
async function performConversion() {
    const amount = parseFloat(amountInput.value);
    const fromCurrency = fromCurrencySelect.value;
    const toCurrency = toCurrencySelect.value;

    // Validate inputs
    if (!amount || amount <= 0) {
        showError('Please enter a valid amount greater than 0');
        return;
    }

    if (fromCurrency === toCurrency) {
        showError('Please select different currencies');
        return;
    }

    // Show loading
    showLoading();
    hideError();
    hideResult();

    try {
        // Build API URL
        const url = `${API_BASE_URL}/convert?from=${fromCurrency}&to=${toCurrency}&amount=${amount}`;

        // Make API call
        const response = await fetch(url);
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || 'Conversion failed');
        }

        // Display result
        displayResult(data);

    } catch (error) {
        showError(error.message || 'Failed to convert currency. Please try again.');
        console.error('Conversion error:', error);
    } finally {
        hideLoading();
    }
}

// Display conversion result
function displayResult(data) {
    // Update result elements
    fromCodeElement.textContent = data.fromCurrency;
    toCodeElement.textContent = data.toCurrency;
    originalAmountElement.textContent = formatNumber(data.amount);
    convertedAmountElement.textContent = formatNumber(data.convertedAmount);
    exchangeRateElement.textContent = formatNumber(data.exchangeRate);
    fromRateCodeElement.textContent = data.fromCurrency;
    toRateCodeElement.textContent = data.toCurrency;
    rateValueElement.textContent = formatNumber(data.exchangeRate);

    // Update timestamp
    const now = new Date();
    lastUpdatedElement.textContent = now.toLocaleString();
    timestampElement.textContent = `Converted at ${now.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}`;

    // Show result section
    showResult();
}

// Format numbers with commas and 2 decimal places
function formatNumber(num) {
    return parseFloat(num).toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

// UI Helper Functions
function showLoading() {
    loadingElement.style.display = 'block';
    convertBtn.disabled = true;
    convertBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Converting...';
}

function hideLoading() {
    loadingElement.style.display = 'none';
    convertBtn.disabled = false;
    convertBtn.innerHTML = '<i class="fas fa-sync-alt"></i> Convert Currency';
}

function showResult() {
    resultSection.style.display = 'block';
}

function hideResult() {
    resultSection.style.display = 'none';
}

function showError(message) {
    errorText.textContent = message;
    errorElement.style.display = 'flex';
}

function hideError() {
    errorElement.style.display = 'none';
}

// Initialize the application when page loads
document.addEventListener('DOMContentLoaded', init);

// Add animation to amount display
function animateValue(element, start, end, duration) {
    let startTimestamp = null;
    const step = (timestamp) => {
        if (!startTimestamp) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        element.textContent = formatNumber(progress * (end - start) + start);
        if (progress < 1) {
            window.requestAnimationFrame(step);
        }
    };
    window.requestAnimationFrame(step);
}