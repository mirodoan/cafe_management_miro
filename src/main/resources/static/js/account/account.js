// Quản lý trạng thái chỉnh sửa và cảnh báo khi đăng xuất trên trang cá nhân
let isDirty = false;

// Hàm gọi sau khi lưu thành công (submit hoặc hiện modal thành công)
function onSaveSuccess() {
  isDirty = false;
}

document.addEventListener('DOMContentLoaded', function () {
saveButton.addEventListener('click', function(e) {
  e.preventDefault();

  var data = $('form').serialize(); // Nếu dùng jQuery và form là <form>
  $.post('/account/update', data, function(response) {
    if (response.success) {
      onSaveSuccess();
      window.location.href = '/account';
    } else {
      alert(response.error || 'Có lỗi xảy ra!');
    }
  });
});

  // Focus vào ô đầu tiên có thể chỉnh sửa khi load trang
  var firstInput = document.querySelector('input.editable-input:not([readonly])');
  if (firstInput) {
    firstInput.focus();
  }

  // Theo dõi thay đổi trong các input chỉnh sửa để set isDirty
  document.querySelectorAll('input.editable-input:not([readonly]), textarea.editable-input:not([readonly])')
    .forEach(function (input) {
      input.addEventListener('input', function () {
        isDirty = true;
      });
    });

  // Nếu nút Đăng xuất có id là 'logoutButton'
  var logoutButton = document.getElementById('logoutButton');
  if (logoutButton) {
    logoutButton.addEventListener('click', function (e) {
      if (isDirty) {
        alert('Vui lòng lưu các lỗi trước khi lưu!');
        e.preventDefault();
      }
    });
  }

  // Các hàm validate
  window.validateFullName = function (input) {
    const value = input.value.trim();
    const errorElement = input.closest('.input-wrapper').querySelector('.text-red-500');
    if (value.length < 5) {
      showFieldError(input, 'Họ tên phải có ít nhất 5 ký tự');
      return false;
    } else if (value.length > 20) {
      showFieldError(input, 'Họ tên không được vượt quá 50 ký tự');
      return false;
    } else if (!/^[a-zA-ZÀ-ỹ\s]+$/.test(value)) {
      showFieldError(input, 'Họ tên chỉ được chứa chữ cái và khoảng trắng');
      return false;
    } else {
      clearFieldErrorByElement(errorElement);
      return true;
    }
  };

  window.validateAddress = function (input) {
    const value = input.value.trim();
    const errorElement = input.closest('.input-wrapper').querySelector('.text-red-500');
    if (value === '') {
      clearFieldErrorByElement(errorElement);
      return true;
    } else if (value.length < 5) {
      showFieldError(input, 'Địa chỉ phải có ít nhất 5 ký tự nếu nhập');
      return false;
    } else if (value.length > 20) {
      showFieldError(input, 'Địa chỉ không được vượt quá 20 ký tự');
      return false;
    } else {
      clearFieldErrorByElement(errorElement);
      return true;
    }
  };

  window.validateImageUrl = function (input) {
    const value = input.value.trim();
    const errorElement = document.querySelector('#avatarImage').closest('.avatar-container').querySelector('.text-red-500');
    if (value === '') {
      clearFieldErrorByElement(errorElement);
      return true;
    } else if (!/^(https?:\/\/).*\.(jpg|jpeg|png|gif|bmp|webp)(\?.*)?$/i.test(value)) {
      showFieldError(input, 'URL ảnh phải là một liên kết hợp lệ và có đuôi file ảnh (jpg, jpeg, png, gif, bmp, webp)');
      return false;
    } else {
      clearFieldErrorByElement(errorElement);
      return true;
    }
  };

  window.validatePassword = function (input) {
    const value = input.value;
    const error = document.getElementById('passwordError');
    if (value && value.length > 0 && value.length < 6) {
      error.textContent = 'Mật khẩu mới phải có ít nhất 6 ký tự hoặc để trống!';
      error.style.display = 'block';
      return false;
    } else {
      error.textContent = '';
      error.style.display = 'none';
      return true;
    }
  };

  // Validate số điện thoại chỉ nhận ký tự số
  var inputPhone = document.getElementById('phoneNumber');
  if (inputPhone) {
    inputPhone.addEventListener('input', function () {
      this.value = this.value.replace(/\D/g, '');
    });
  }

  // Helper functions for error display
  function showFieldError(input, message) {
    let errorElement;
    if (input.id === 'avatarUrlInput') {
      errorElement = document.querySelector('#avatarImage').closest('.avatar-container').querySelector('.text-red-500');
    } else {
      errorElement = input.closest('.input-wrapper')?.querySelector('.text-red-500');
    }
    if (errorElement) {
      errorElement.textContent = message;
      errorElement.style.display = 'block';
    }
  }
  function clearFieldErrorByElement(errorElement) {
    if (errorElement) {
      errorElement.textContent = '';
      errorElement.style.display = 'none';
    }
  }

  // Các biến DOM và logic chuyển đổi chế độ chỉnh sửa
  const editButton = document.getElementById('editButton');
  const saveButton = document.getElementById('saveButton');
  const cancelButton = document.getElementById('cancelButton');
  const avatarImage = document.getElementById('avatarImage');
  const avatarUrlInput = document.getElementById('avatarUrlInput');
  const avatarEditIcon = document.getElementById('editAvatar');
  const editIcons = document.querySelectorAll('.edit-icon');
  const form = document.querySelector('form');

  // Ngăn chặn form submit khi có lỗi
  form.addEventListener('submit', function (e) {
    const errorElements = document.querySelectorAll('.text-red-500');
    let hasBackendError = false;
    errorElements.forEach((element) => {
      if (
        element.textContent &&
        element.textContent.trim().length > 0 &&
        element.style.display !== 'none'
      ) {
        hasBackendError = true;
      }
    });

    let hasFrontendError = false;
    const fullNameInput = document.getElementById('fullName');
    const phoneNumberInput = document.getElementById('phoneNumber');
    const addressInput = document.getElementById('address');
    const avatarUrlInput = document.getElementById('avatarUrlInput');
    const passwordInput = document.getElementById('newPassword');

    if (!fullNameInput.readOnly && !validateFullName(fullNameInput)) {
      hasFrontendError = true;
    }
    if (!phoneNumberInput.readOnly && typeof validatePhoneNumber === 'function' && !validatePhoneNumber(phoneNumberInput)) {
      hasFrontendError = true;
    }
    if (!addressInput.readOnly && !validateAddress(addressInput)) {
      hasFrontendError = true;
    }
    if (!validateImageUrl(avatarUrlInput)) {
      hasFrontendError = true;
    }
    if (passwordInput && !passwordInput.readOnly && !window.validatePassword(passwordInput)) {
      hasFrontendError = true;
    }

    if (hasBackendError || hasFrontendError) {
      e.preventDefault();
      e.stopPropagation();
      alert('Vui lòng sửa các lỗi trước khi lưu!');
      return false;
    }
  });

  const editableInputs = [
    document.getElementById('fullName'),
    document.getElementById('phoneNumber'),
    document.getElementById('address'),
    document.getElementById('newPassword'),
    document.getElementById('avatarUrlInput'),
  ];

  const positionNameInput = document.querySelector('input[th\\:field="*{positionName}"][type="text"]');
  const positionNameHidden = document.querySelector('input[th\\:field="*{positionName}"][type="hidden"]');
  let originalPositionName = positionNameInput ? positionNameInput.value : '';
  const originalValues = {};

  function updateHeaderAvatar(newUrl) {
    const headerAvatar = document.querySelector('header img[alt="avatar"]');
    if (headerAvatar && newUrl) {
      headerAvatar.src = newUrl;
    }
  }

  function onAccountUpdateSuccess() {
    const avatarUrlInput = document.getElementById('avatarUrlInput');
    if (avatarUrlInput) {
      updateHeaderAvatar(avatarUrlInput.value);
    }
  }

  function setEditMode(editing) {
    editableInputs.forEach((input) => {
      if (input) input.readOnly = !editing;
      const field = input?.closest('.info-field');
      if (field) {
        if (editing) {
          field.classList.add('editing');
          if (!originalValues[input.id]) originalValues[input.id] = input.value;
        } else {
          field.classList.remove('editing');
        }
      }
    });

    const passwordField = document.getElementById('password');
    const newPasswordField = document.getElementById('newPassword');
    if (passwordField && newPasswordField) {
      passwordField.style.display = editing ? 'none' : 'block';
      newPasswordField.style.display = editing ? 'block' : 'none';
    }

    if (positionNameInput && positionNameHidden) {
      if (!originalPositionName)
        originalPositionName = positionNameInput.value;
      positionNameInput.value = originalPositionName;
      positionNameHidden.value = originalPositionName;
    }

    editIcons.forEach((icon) => (icon.style.display = editing ? 'flex' : 'none'));
    editButton.style.display = editing ? 'none' : 'flex';
    saveButton.style.display = editing ? 'flex' : 'none';
    cancelButton.style.display = editing ? 'flex' : 'none';

    if (editing && !originalValues.avatar) {
      originalValues.avatar = avatarImage.src;
      originalValues.avatarUrlInput = avatarUrlInput.value;
    }
  }

  editButton.addEventListener('click', () => {
    setEditMode(true);
    updateSaveButtonState();
  });

  cancelButton.addEventListener('click', () => {
    editableInputs.forEach((input) => (input.value = originalValues[input.id]));
    if (originalValues.avatar) {
      avatarImage.src = originalValues.avatar;
    }
    if (originalValues.avatarUrlInput !== undefined) {
      avatarUrlInput.value = originalValues.avatarUrlInput;
    }
    if (positionNameInput && positionNameHidden) {
      positionNameInput.value = originalPositionName;
      positionNameHidden.value = originalPositionName;
    }
    document.querySelectorAll('.text-red-500').forEach((errorElement) => {
      if (errorElement.textContent && !errorElement.getAttribute('th:if')) {
        errorElement.textContent = '';
        errorElement.style.display = 'none';
      }
    });
    setEditMode(false);
  });

  avatarEditIcon.addEventListener('click', () => {
    const currentUrl = avatarUrlInput.value || '';
    const newUrl = prompt('Nhập URL ảnh đại diện mới:', currentUrl);
    if (newUrl !== null) {
      const oldUrl = avatarUrlInput.value;
      const oldImageSrc = avatarImage.src;
      avatarUrlInput.value = newUrl.trim();
      if (validateImageUrl(avatarUrlInput)) {
        if (newUrl.trim() === '') {
          avatarImage.src = 'https://randomuser.me/api/portraits/men/32.jpg';
        } else {
          avatarImage.src = newUrl.trim();
        }
        const testImg = new Image();
        testImg.onload = function () {};
        testImg.onerror = function () {
          showFieldError(avatarUrlInput, 'URL ảnh không thể tải được. Vui lòng kiểm tra lại.');
        };
        testImg.src = newUrl.trim();
      } else {
        avatarUrlInput.value = oldUrl;
        avatarImage.src = oldImageSrc;
      }
      setTimeout(() => {
        updateSaveButtonState();
      }, 100);
    }
  });

  function updateSaveButtonState() {
    const errorElements = document.querySelectorAll('.text-red-500');
    let hasBackendError = false;
    errorElements.forEach((element) => {
      if (
        element.textContent &&
        element.textContent.trim().length > 0 &&
        element.style.display !== 'none' &&
        element.offsetParent !== null
      ) {
        hasBackendError = true;
      }
    });
    if (saveButton) {
      const isEditMode = saveButton.style.display !== 'none';
      if (isEditMode && hasBackendError) {
        saveButton.disabled = true;
        saveButton.style.opacity = '0.5';
        saveButton.style.cursor = 'not-allowed';
        saveButton.title = 'Vui lòng sửa các lỗi trước khi lưu';
        saveButton.classList.add('disabled-button');
        saveButton.removeEventListener('click', preventSubmit);
        saveButton.addEventListener('click', preventSubmit);
      } else if (isEditMode) {
        saveButton.disabled = false;
        saveButton.style.opacity = '1';
        saveButton.style.cursor = 'pointer';
        saveButton.title = 'Lưu thay đổi';
        saveButton.classList.remove('disabled-button');
        saveButton.removeEventListener('click', preventSubmit);
      }
    }
  }

  function preventSubmit(e) {
    e.preventDefault();
    e.stopPropagation();
    alert('Vui lòng sửa các lỗi trước khi lưu!');
    return false;
  }

  function clearFieldError(fieldName) {
    const mainInput = document.getElementById(fieldName);
    if (mainInput) {
      const parentWrapper = mainInput.closest('.info-field') || mainInput.closest('.input-wrapper');
      if (parentWrapper) {
        const errorElements = parentWrapper.querySelectorAll('.text-red-500');
        errorElements.forEach((errorElement) => {
          if (errorElement.textContent && errorElement.textContent.trim().length > 0) {
            errorElement.textContent = '';
            errorElement.style.display = 'none';
          }
        });
      }
    }
    const inputElements = document.querySelectorAll(`input[th\\:field="*{${fieldName}}"]`);
    inputElements.forEach((inputElement) => {
      const parentWrapper =
        inputElement.closest('.input-wrapper') ||
        inputElement.closest('.info-field') ||
        inputElement.parentElement;
      if (parentWrapper) {
        const errorElements = parentWrapper.querySelectorAll('.text-red-500');
        errorElements.forEach((errorElement) => {
          if (errorElement.textContent && errorElement.textContent.trim().length > 0) {
            errorElement.textContent = '';
            errorElement.style.display = 'none';
          }
        });
      }
    });
  }

  updateSaveButtonState();

  editableInputs.forEach((input) => {
    input.addEventListener('input', function () {
      const inputId = input.id;
      let isValid = false;
      if (inputId === 'fullName') {
        isValid = validateFullName(input);
        if (isValid && input.value.trim().length >= 2) {
          clearFieldError('fullName');
        }
      } else if (inputId === 'phoneNumber' && typeof validatePhoneNumber === 'function') {
        isValid = validatePhoneNumber(input);
        if (isValid && /^[0-9]{10,11}$/.test(input.value.trim())) {
          clearFieldError('phoneNumber');
        }
      } else if (inputId === 'address') {
        isValid = validateAddress(input);
        if (isValid && input.value.trim().length >= 5) {
          clearFieldError('address');
        }
      }
      setTimeout(() => {
        updateSaveButtonState();
      }, 100);
    });

    input.addEventListener('blur', function () {
      const inputId = input.id;
      if (!input.readOnly) {
        if (inputId === 'fullName') {
          validateFullName(input);
        } else if (inputId === 'phoneNumber' && typeof validatePhoneNumber === 'function') {
          validatePhoneNumber(input);
        } else if (inputId === 'address') {
          validateAddress(input);
        }
        setTimeout(() => {
          updateSaveButtonState();
        }, 100);
      }
    });
  });

  /*<![CDATA[*/
  var isEditMode = /*[[${editMode}]]*/ false;
  if (isEditMode) {
    setEditMode(true);
  }
  /*]]>*/
});