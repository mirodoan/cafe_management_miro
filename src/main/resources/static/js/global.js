document.querySelectorAll('.btn-delete').forEach((button) => {
  button.addEventListener('click', function () {
    const id = this.getAttribute('data-id');
    const name = this.getAttribute('data-name');
    Swal.fire({
      title: 'Xác nhận xoá?',
      text: `Bạn có chắc muốn xóa "${name}" không?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Có',
      cancelButtonText: 'Không',
      confirmButtonColor: '#16a34a',
      cancelButtonColor: '#ef4444',
    }).then((result) => {
      if (result.isConfirmed) {
        document.getElementById('deleteForm-' + id).submit();
      }
    });
  });
});

// Global notification functions
window.showError = function (message) {
  showNotification(message, 'error');
};

window.showSuccess = function (message) {
  showNotification(message, 'success');
};

// Global notification function
window.showNotification = function (message, type = 'error') {
  // Try to find notification area, if not available, create one
  let notificationArea = document.getElementById('notification-area');

  if (!notificationArea) {
    // Create notification area if not exists
    notificationArea = document.createElement('div');
    notificationArea.id = 'notification-area';
    notificationArea.className =
      'mt-6 flex justify-center fixed top-4 left-1/2 transform -translate-x-1/2 z-50';
    document.body.appendChild(notificationArea);
  }

  // Clear old notifications
  notificationArea.innerHTML = '';

  const isError = type === 'error';
  const bgColor = isError ? 'bg-red-100' : 'bg-green-100';
  const borderColor = isError ? 'border-red-400' : 'border-green-400';
  const textColor = isError ? 'text-red-700' : 'text-green-700';
  const iconColor = isError ? 'text-red-400' : 'text-green-400';

  // Create new notification popup with beautiful styling
  const alertDiv = document.createElement('div');
  alertDiv.className = `${bgColor} ${borderColor} ${textColor} px-6 py-4 rounded-lg shadow-lg max-w-md border-l-4 transition-all duration-300`;
  alertDiv.innerHTML = `
        <div class="flex items-center">
            <div class="flex-shrink-0">
                ${
                  isError
                    ? `<svg class="h-5 w-5 ${iconColor}" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                    </svg>`
                    : `<svg class="h-5 w-5 ${iconColor}" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                    </svg>`
                }
            </div>
            <div class="ml-3 flex-1">
                <p class="text-sm font-medium">${message}</p>
            </div>
            <button onclick="this.parentElement.parentElement.remove()" class="ml-4 ${
              isError
                ? 'text-red-500 hover:text-red-700'
                : 'text-green-500 hover:text-green-700'
            } transition-colors">
                <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
            </button>
        </div>
    `;

  notificationArea.appendChild(alertDiv);

  // Fade in effect
  setTimeout(() => {
    alertDiv.style.opacity = '0';
    alertDiv.style.transform = 'translateY(-10px)';
    alertDiv.style.transition = 'all 0.3s ease';
    setTimeout(() => {
      alertDiv.style.opacity = '1';
      alertDiv.style.transform = 'translateY(0)';
    }, 10);
  }, 10);

  // Auto hide after 4 seconds
  setTimeout(() => {
    if (alertDiv && alertDiv.parentNode) {
      alertDiv.style.opacity = '0';
      alertDiv.style.transform = 'translateY(-10px)';
      setTimeout(() => {
        if (alertDiv.parentNode) {
          alertDiv.remove();
        }
      }, 300);
    }
  }, 4000);
};
