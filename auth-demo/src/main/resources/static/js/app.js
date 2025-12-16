// Global variables
let token = localStorage.getItem('jwtToken') || null;
let currentUser = JSON.parse(localStorage.getItem('currentUser')) || null;

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    updateAuthButtons();
    if (token) {
        getUserInfo();
    }
});

// Update authentication buttons in navbar
function updateAuthButtons() {
    const authButtons = document.getElementById('authButtons');

    if (token) {
        authButtons.innerHTML = `
            <span class="navbar-text me-3">
                Welcome, <strong>${currentUser?.username || 'User'}</strong>
                <span class="badge bg-${currentUser?.role === 'ADMIN' ? 'danger' : 'primary'} ms-2">
                    ${currentUser?.role || 'USER'}
                </span>
            </span>
            <button class="btn btn-outline-light btn-sm me-2" onclick="logout()">Logout</button>
        `;
    } else {
        authButtons.innerHTML = `
            <button class="btn btn-outline-light btn-sm me-2" data-bs-toggle="modal" data-bs-target="#loginModal">Login</button>
            <button class="btn btn-light btn-sm" data-bs-toggle="modal" data-bs-target="#registerModal">Register</button>
        `;
    }
}

// Login function
async function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorDiv = document.getElementById('loginError');

    errorDiv.style.display = 'none';

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            token = data.token;
            currentUser = {
                username: data.username,
                role: data.role
            };

            // Save to localStorage
            localStorage.setItem('jwtToken', token);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));

            // Close modal and update UI
            document.getElementById('loginForm').reset();
            bootstrap.Modal.getInstance(document.getElementById('loginModal')).hide();

            updateAuthButtons();
            getUserInfo();
            showResult('Login successful!', true);
        } else {
            errorDiv.textContent = data.message || 'Login failed';
            errorDiv.style.display = 'block';
        }
    } catch (error) {
        errorDiv.textContent = 'Network error. Please try again.';
        errorDiv.style.display = 'block';
    }
}

// Register function
async function register() {
    const username = document.getElementById('regUsername').value;
    const password = document.getElementById('regPassword').value;
    const errorDiv = document.getElementById('registerError');

    errorDiv.style.display = 'none';

    if (password.length < 6) {
        errorDiv.textContent = 'Password must be at least 6 characters';
        errorDiv.style.display = 'block';
        return;
    }

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            // Close modal and show success
            document.getElementById('registerForm').reset();
            bootstrap.Modal.getInstance(document.getElementById('registerModal')).hide();

            showResult(`User ${username} registered successfully! You can now login.`, true);
        } else {
            errorDiv.textContent = data.message || 'Registration failed';
            errorDiv.style.display = 'block';
        }
    } catch (error) {
        errorDiv.textContent = 'Network error. Please try again.';
        errorDiv.style.display = 'block';
    }
}

// Logout function
function logout() {
    token = null;
    currentUser = null;
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('currentUser');

    updateAuthButtons();
    document.getElementById('userInfo').innerHTML = '<p class="text-muted">Not logged in</p>';
    document.getElementById('tokenInfo').style.display = 'none';
    showResult('Logged out successfully', true);
}

// Get current user info
async function getUserInfo() {
    if (!token) return;

    try {
        const response = await fetch('/api/auth/me', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            currentUser = data;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));

            displayUserInfo(data);
            updateAuthButtons();
        } else {
            // Token might be expired
            logout();
        }
    } catch (error) {
        console.error('Error getting user info:', error);
    }
}

// Display user info
function displayUserInfo(user) {
    const userInfoDiv = document.getElementById('userInfo');
    const tokenInfoDiv = document.getElementById('tokenInfo');
    const tokenDisplay = document.getElementById('tokenDisplay');

    userInfoDiv.innerHTML = `
        <p>
            <strong>Username:</strong> ${user.username}
            <span class="badge bg-${user.role === 'ADMIN' ? 'danger' : 'primary'}">
                ${user.role}
            </span>
        </p>
        <p><strong>Status:</strong> <span class="text-success">Authenticated</span></p>
    `;

    if (token) {
        tokenDisplay.textContent = token.substring(0, 50) + '...';
        tokenInfoDiv.style.display = 'block';
    }
}

// Test public endpoint
async function testPublic() {
    try {
        const response = await fetch('/test');
        const text = await response.text();
        showResult(`GET /test\nStatus: ${response.status}\nResponse: ${text}`, response.ok);
    } catch (error) {
        showResult(`Error: ${error.message}`, false);
    }
}

// Test user profile endpoint
async function testUserProfile() {
    if (!token) {
        showResult('Please login first to access protected endpoints', false);
        return;
    }

    try {
        const response = await fetch('/user/profile', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        const text = await response.text();
        showResult(`GET /user/profile\nStatus: ${response.status}\nResponse: ${text}`, response.ok);
    } catch (error) {
        showResult(`Error: ${error.message}`, false);
    }
}

// Test admin dashboard endpoint
async function testAdminDashboard() {
    if (!token) {
        showResult('Please login first to access protected endpoints', false);
        return;
    }

    try {
        const response = await fetch('/admin/dashboard', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        const text = await response.text();
        showResult(`GET /admin/dashboard\nStatus: ${response.status}\nResponse: ${text}`, response.ok);
    } catch (error) {
        showResult(`Error: ${error.message}`, false);
    }
}

// Show result in the result div
function showResult(message, isSuccess) {
    const resultDiv = document.getElementById('result');
    resultDiv.innerHTML = `
        <div class="result-box ${isSuccess ? 'success-box' : 'error-box'}">
            <strong>${isSuccess ? '✅ Success' : '❌ Error'}</strong><br>
            ${message}
        </div>
    `;

    // Auto-hide after 5 seconds
    setTimeout(() => {
        resultDiv.innerHTML = '';
    }, 5000);
}

// Copy token to clipboard
function copyToken() {
    if (token) {
        navigator.clipboard.writeText(token).then(() => {
            alert('Token copied to clipboard!');
        });
    }
}