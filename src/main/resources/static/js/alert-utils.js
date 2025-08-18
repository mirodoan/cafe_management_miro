function showSuccess(message = "Thành công!") {
    Swal.fire({
        icon: 'success',
        title: 'Thành công!',
        text: message,
        timer: 3000,
        showConfirmButton: false, // ẩn nút OK
        allowOutsideClick: true,  // cho phép click ra ngoài để tắt
        allowEscapeKey: true
    });
}

function showError(message = "Đã xảy ra lỗi!") {
    Swal.fire({
        icon: 'error',
        title: 'Lỗi!',
        text: message,
//        timer: 5000
    });
}

function showWarning(message = "Cảnh báo!") {
    Swal.fire({
        icon: 'warning',
        title: 'Cảnh báo!',
        text: message,
        timer: 5000
    });
}

function showConfirm(title, message, onConfirm) {
    Swal.fire({
        title: title || 'Xác nhận?',
        text: message || '',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Có',
        cancelButtonText: 'Không',
        confirmButtonColor: '#16a34a',
        cancelButtonColor: '#ef4444'
    }).then((result) => {
        if (result.isConfirmed && typeof onConfirm === "function") {
            onConfirm();
        }
    });
}
