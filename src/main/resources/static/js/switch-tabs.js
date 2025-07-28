document.addEventListener('DOMContentLoaded', function () {
  // Tab switching logic
  const tabProducts = document.getElementById('tab-products');
  const tabTransactions = document.getElementById('tab-transactions');
  const productsSection = document.getElementById('products-section');
  const transactionsSection = document.getElementById('transactions-section');

  // Function to load transactions data via AJAX
  function loadTransactionsData(keyword = '', page = 0, size = 10) {
    const params = new URLSearchParams({
      keyword: keyword || '',
      page: page,
      size: size,
    });

    fetch(`/warehouse/transaction/api?${params}`)
      .then((response) => response.json())
      .then((data) => {
        updateTransactionsTable(data);
      })
      .catch((error) => {
        console.error('Error loading transactions:', error);
        // Hiển thị thông báo lỗi hoặc dữ liệu mặc định
      });
  }

  // Function to update transactions table with data
  function updateTransactionsTable(data) {
    const tbody = document.querySelector('#transactions-section tbody');
    if (!tbody) return;

    // Clear existing data
    tbody.innerHTML = '';

    if (data.transactions && data.transactions.length > 0) {
      data.transactions.forEach((transaction) => {
        const row = createTransactionRow(transaction);
        tbody.appendChild(row);
      });
    } else {
      tbody.innerHTML = `
              <tr>
                <td colspan="8" class="text-center py-6 text-gray-400">
                  Không có giao dịch nào.
                </td>
              </tr>
            `;
    }

    // Update pagination info
    updateTransactionsPagination(data);
  }

  // Function to create transaction row
  function createTransactionRow(transaction) {
    const row = document.createElement('tr');
    row.className = 'hover:bg-[#f9f9f9] transition';

    const typeClass =
      transaction.type === 'IMPORT'
        ? 'bg-green-100 text-green-800'
        : 'bg-red-100 text-red-800';
    const typeText = transaction.type === 'IMPORT' ? 'Nhập' : 'Xuất';
    const date = transaction.importDate || transaction.exportDate;

    row.innerHTML = `
            <td class="px-4 py-3 font-medium">${
              transaction.productName || ''
            }</td>
            <td class="px-4 py-3">
              <span class="px-2 py-1 text-xs rounded-full ${typeClass}">
                ${typeText}
              </span>
            </td>
            <td class="px-4 py-3">${formatDate(date)}</td>
            <td class="px-4 py-3">${transaction.quantity || 0}</td>
            <td class="px-4 py-3">${transaction.unitName || ''}</td>
            <td class="px-4 py-3 text-right">${formatCurrency(
              transaction.unitPrice
            )}</td>
            <td class="px-4 py-3 text-right">${formatCurrency(
              transaction.totalAmount
            )}</td>
            <td class="px-4 py-3">
              <div class="flex justify-center gap-2">
                <a href="#" class="bg-yellow-400 hover:bg-yellow-500 text-white text-xs font-medium px-3 py-1.5 rounded shadow transition-colors duration-200">
                  Sửa
                </a>
                <form style="display: inline-block">
                  <button type="button" data-id="${
                    transaction.id
                  }" data-name="${transaction.productName}" 
                    class="btn-delete-transaction bg-red-500 hover:bg-red-600 text-white text-xs font-medium px-3 py-1.5 rounded shadow transition-colors duration-200">
                    Xóa
                  </button>
                </form>
              </div>
            </td>
          `;

    return row;
  }

  // Helper functions
  function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
  }

  function formatCurrency(amount) {
    if (!amount) return '0 đ';
    return new Intl.NumberFormat('vi-VN').format(amount) + ' đ';
  }

  function updateTransactionsPagination(data) {
    // Update pagination info - implement if needed
    const paginationInfo = document.querySelector(
      '#transactions-section .flex.items-center.justify-between.mt-6 div:first-child'
    );
    if (paginationInfo) {
      const totalImport = data.transactions.filter(
        (t) => t.type === 'IMPORT'
      ).length;
      const totalExport = data.transactions.filter(
        (t) => t.type === 'EXPORT'
      ).length;

      paginationInfo.innerHTML = `
              <span>Trang ${data.pageNumber + 1} / ${data.totalPages}</span>
              <span>Tổng: ${data.totalElements} giao dịch</span>
              <div class="flex items-center gap-2">
                <span class="text-green-600">● ${totalImport} nhập</span>
                <span class="text-red-600">● ${totalExport} xuất</span>
              </div>
            `;
    }
  }

  // Function to switch to products tab
  function switchToProductsTab() {
    // Fade out current section
    transactionsSection.style.opacity = '0';

    setTimeout(() => {
      // Hide transactions, show products
      transactionsSection.style.display = 'none';
      productsSection.style.display = 'block';

      // Fade in new section
      setTimeout(() => {
        productsSection.style.opacity = '1';
      }, 50);
    }, 150);

    // Update tab styles with smooth transition
    tabProducts.className =
      'tab-btn px-8 py-3 rounded-xl text-2xl font-bold border-2 transition-all duration-300 ease-in-out focus:outline-none bg-blue-600 border-blue-600 text-white shadow-lg transform hover:scale-105';

    tabTransactions.className =
      'tab-btn px-8 py-3 rounded-xl text-2xl font-bold border-2 transition-all duration-300 ease-in-out focus:outline-none bg-white border-green-500 text-green-700 shadow-lg transform hover:scale-105';
  }

  // Function to switch to transactions tab
  function switchToTransactionsTab() {
    // Fade out current section
    productsSection.style.opacity = '0';

    setTimeout(() => {
      // Hide products, show transactions
      productsSection.style.display = 'none';
      transactionsSection.style.display = 'block';

      // Load transactions data when switching to tab
      loadTransactionsData();

      // Fade in new section
      setTimeout(() => {
        transactionsSection.style.opacity = '1';
      }, 50);
    }, 150);

    // Update tab styles with smooth transition
    tabTransactions.className =
      'tab-btn px-8 py-3 rounded-xl text-2xl font-bold border-2 transition-all duration-300 ease-in-out focus:outline-none bg-blue-600 border-blue-600 text-white shadow-lg transform hover:scale-105';

    tabProducts.className =
      'tab-btn px-8 py-3 rounded-xl text-2xl font-bold border-2 transition-all duration-300 ease-in-out focus:outline-none bg-white border-green-500 text-green-700 shadow-lg transform hover:scale-105';
  }

  // Tab click events
  if (tabProducts) {
    tabProducts.addEventListener('click', function (e) {
      e.preventDefault();
      switchToProductsTab();
    });
  }

  if (tabTransactions) {
    tabTransactions.addEventListener('click', function (e) {
      e.preventDefault();
      switchToTransactionsTab();
    });
  }

  // Initialize - check URL params first
  const urlParams = new URLSearchParams(window.location.search);
  const showTab = urlParams.get('showTab');

  if (showTab === 'transactions') {
    // Show transactions tab if URL param indicates so
    switchToTransactionsTab();
  } else {
    // Default to products tab
    productsSection.style.opacity = '1';
    switchToProductsTab();
  }

  // Products search functionality
  const searchProductInput = document.getElementById('searchProductInput');
  const clearProductBtn = document.getElementById('clearProductBtn');
  const searchProductForm = searchProductInput.closest('form');

  // Show clear button if there's a keyword from server
  if (searchProductInput && clearProductBtn) {
    const hasKeyword =
      searchProductInput.value && searchProductInput.value.trim() !== '';
    clearProductBtn.style.display = hasKeyword ? 'block' : 'none';
  }

  // Clear product search
  if (clearProductBtn) {
    clearProductBtn.addEventListener('click', function (e) {
      e.preventDefault();
      searchProductInput.value = '';
      clearProductBtn.style.display = 'none';
      window.location.href = '/warehouse/product';
    });
  }

  // Show/hide clear button on input
  searchProductInput.addEventListener('input', function () {
    if (clearProductBtn) {
      clearProductBtn.style.display =
        this.value.trim() === '' ? 'none' : 'block';
    }
  });

  // Enter key to submit form
  searchProductInput.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
      e.preventDefault();
      searchProductForm.submit();
    }
  });

  // Transactions search functionality
  const searchTransactionInput = document.getElementById(
    'searchTransactionInput'
  );
  const clearTransactionBtn = document.getElementById('clearTransactionBtn');
  const searchTransactionForm = searchTransactionInput.closest('form');

  // Show clear button if there's a keyword from server
  if (searchTransactionInput && clearTransactionBtn) {
    const hasKeyword =
      searchTransactionInput.value &&
      searchTransactionInput.value.trim() !== '';
    clearTransactionBtn.style.display = hasKeyword ? 'block' : 'none';
  }

  // Clear transaction search
  if (clearTransactionBtn) {
    clearTransactionBtn.addEventListener('click', function (e) {
      e.preventDefault();
      searchTransactionInput.value = '';
      clearTransactionBtn.style.display = 'none';
      loadTransactionsData(); // Reload data without search
    });
  }

  // Show/hide clear button on input
  searchTransactionInput.addEventListener('input', function () {
    if (clearTransactionBtn) {
      clearTransactionBtn.style.display =
        this.value.trim() === '' ? 'none' : 'block';
    }
  });

  // Handle transaction search form submission
  const transactionSearchForm = searchTransactionInput
    ? searchTransactionInput.closest('form')
    : null;
  if (transactionSearchForm) {
    transactionSearchForm.addEventListener('submit', function (e) {
      e.preventDefault();
      const keyword = searchTransactionInput.value.trim();
      loadTransactionsData(keyword);
    });
  }

  // Enter key to submit form
  searchTransactionInput.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
      e.preventDefault();
      const keyword = this.value.trim();
      loadTransactionsData(keyword);
    }
  });

  // Delete transaction functionality
  const deleteTransactionButtons = document.querySelectorAll(
    '.btn-delete-transaction'
  );

  deleteTransactionButtons.forEach((button) => {
    button.addEventListener('click', function (e) {
      e.preventDefault();
      const transactionId = this.getAttribute('data-id');
      const productName = this.getAttribute('data-name');

      if (confirm(`Bạn có chắc chắn muốn xóa giao dịch "${productName}"?`)) {
        const form = document.getElementById(
          'deleteTransactionForm-' + transactionId
        );
        if (form) {
          form.submit();
        }
      }
    });
  });
});
