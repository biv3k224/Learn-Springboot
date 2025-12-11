const API_BASE_URL = 'http://localhost:8080/api/products';

// DOM Elements
let productsTable = document.getElementById('productsTable');
let searchInput = document.getElementById('searchInput');
let categoryFilter = document.getElementById('categoryFilter');
let totalProductsEl = document.getElementById('totalProducts');
let inventoryValueEl = document.getElementById('inventoryValue');
let categoryCountEl = document.getElementById('categoryCount');
let productModal = document.getElementById('productModal');
let productForm = document.getElementById('productForm');
let modalTitle = document.getElementById('modalTitle');

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    loadProducts();
    updateStats();
});

// Load all products
async function loadProducts() {
    try {
        showLoading();
        const response = await fetch(API_BASE_URL);
        const products = await response.json();

        renderProducts(products);
        updateCategoryFilter(products);
    } catch (error) {
        showError('Failed to load products');
        console.error('Error:', error);
    }
}

// Render products table
function renderProducts(products) {
    if (products.length === 0) {
        productsTable.innerHTML = `
            <tr>
                <td colspan="7" class="loading">
                    <i class="fas fa-box-open" style="font-size: 2rem; margin-bottom: 10px;"></i>
                    <p>No products found. Add your first product!</p>
                </td>
            </tr>
        `;
        return;
    }

    productsTable.innerHTML = products.map(product => `
        <tr class="animate-fade-in">
            <td>#${product.id}</td>
            <td><strong>${product.name}</strong></td>
            <td>${product.description || '-'}</td>
            <td><span class="price">$${product.price.toFixed(2)}</span></td>
            <td><span class="category-tag">${product.category || 'Uncategorized'}</span></td>
            <td>${formatDate(product.createdAt)}</td>
            <td>
                <div class="action-buttons">
                    <button class="action-btn edit-btn" onclick="editProduct(${product.id})">
                        <i class="fas fa-edit"></i> Edit
                    </button>
                    <button class="action-btn delete-btn" onclick="deleteProduct(${product.id})">
                        <i class="fas fa-trash"></i> Delete
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Update stats
// Update stats
async function updateStats() {
    try {
        // Get count
        const countRes = await fetch(API_BASE_URL + '/count');
        const countData = await countRes.json();
        totalProductsEl.textContent = countData.count;

        // Get inventory value
        const valueRes = await fetch(API_BASE_URL + '/inventory/value');
        const valueData = await valueRes.json();
        const inventoryValue = valueData.inventoryValue || 0;
        inventoryValueEl.textContent = `$${parseFloat(inventoryValue).toFixed(2)}`;

        // Get category count
        const categoryRes = await fetch(API_BASE_URL + '/categories/count');
        const categoryData = await categoryRes.json();
        const categoryCount = categoryData.count || 0;
        categoryCountEl.textContent = categoryCount;

    } catch (error) {
        console.error('Error updating stats:', error);
        // Set default values
        totalProductsEl.textContent = '0';
        inventoryValueEl.textContent = '$0';
        categoryCountEl.textContent = '0';
    }
}

// Update category filter dropdown
function updateCategoryFilter(products) {
    const categories = [...new Set(products.map(p => p.category).filter(Boolean))];
    const categoriesDatalist = document.getElementById('categories');

    categoryFilter.innerHTML = '<option value="">All Categories</option>';
    categoriesDatalist.innerHTML = '';

    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category;
        option.textContent = category;
        categoryFilter.appendChild(option.cloneNode(true));
        categoriesDatalist.appendChild(option);
    });
}

// Search products
async function searchProducts() {
    const query = searchInput.value.trim();

    if (!query) {
        loadProducts();
        return;
    }

    try {
        showLoading();
        const response = await fetch(`${API_BASE_URL}/search?name=${encodeURIComponent(query)}`);
        const products = await response.json();
        renderProducts(products);
    } catch (error) {
        console.error('Search error:', error);
    }
}

// Filter by category
function filterByCategory() {
    const category = categoryFilter.value;

    if (!category) {
        loadProducts();
        return;
    }

    fetch(`${API_BASE_URL}/category/${encodeURIComponent(category)}`)
        .then(res => res.json())
        .then(products => renderProducts(products))
        .catch(err => console.error('Filter error:', err));
}

// Show add product form
function showAddForm() {
    modalTitle.textContent = 'Add New Product';
    productForm.reset();
    document.getElementById('productId').value = '';
    productModal.style.display = 'flex';
}

// Edit product
async function editProduct(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`);
        if (!response.ok) throw new Error('Product not found');

        const product = await response.json();

        modalTitle.textContent = 'Edit Product';
        document.getElementById('productId').value = product.id;
        document.getElementById('name').value = product.name;
        document.getElementById('description').value = product.description || '';
        document.getElementById('price').value = product.price;
        document.getElementById('category').value = product.category || '';

        productModal.style.display = 'flex';
    } catch (error) {
        alert('Failed to load product for editing');
        console.error('Edit error:', error);
    }
}

// Save product (create or update)
async function saveProduct(event) {
    event.preventDefault();

    const id = document.getElementById('productId').value;
    const product = {
        name: document.getElementById('name').value,
        description: document.getElementById('description').value,
        price: parseFloat(document.getElementById('price').value),
        category: document.getElementById('category').value || null
    };

    const url = id ? `${API_BASE_URL}/${id}` : API_BASE_URL;
    const method = id ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(product)
        });

        if (!response.ok) throw new Error('Failed to save');

        closeModal();
        loadProducts();
        updateStats();

        // Show success message
        showMessage('Product saved successfully!', 'success');
    } catch (error) {
        showMessage('Failed to save product', 'error');
        console.error('Save error:', error);
    }
}

// Delete product
async function deleteProduct(id) {
    if (!confirm('Are you sure you want to delete this product?')) return;

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, { method: 'DELETE' });

        if (!response.ok) throw new Error('Failed to delete');

        loadProducts();
        updateStats();
        showMessage('Product deleted successfully!', 'success');
    } catch (error) {
        showMessage('Failed to delete product', 'error');
        console.error('Delete error:', error);
    }
}

// Close modal
function closeModal() {
    productModal.style.display = 'none';
}

// Helper functions
function showLoading() {
    productsTable.innerHTML = `
        <tr>
            <td colspan="7" class="loading">
                <i class="fas fa-spinner fa-spin" style="margin-right: 10px;"></i>
                Loading products...
            </td>
        </tr>
    `;
}

function showError(message) {
    productsTable.innerHTML = `
        <tr>
            <td colspan="7" class="loading" style="color: #dc2626;">
                <i class="fas fa-exclamation-triangle" style="margin-right: 10px;"></i>
                ${message}
            </td>
        </tr>
    `;
}

function showMessage(message, type) {
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        ${message}
    `;

    // Add alert styles
    alert.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 25px;
        border-radius: 10px;
        background: ${type === 'success' ? '#10b981' : '#ef4444'};
        color: white;
        z-index: 1001;
        animation: slideInRight 0.3s ease-out;
    `;

    document.body.appendChild(alert);
    setTimeout(() => alert.remove(), 3000);
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

// Close modal when clicking outside
window.onclick = function(event) {
    if (event.target === productModal) {
        closeModal();
    }
}