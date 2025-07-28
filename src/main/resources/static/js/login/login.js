// Đảm bảo toàn bộ HTML đã load xong trước khi chạy các đoạn JS bên trong
document.addEventListener('DOMContentLoaded', function () {
    // Lấy phần tử icon "con mắt" để ẩn/hiện mật khẩu
    const togglePassword = document.getElementById('togglePassword');
    // Lấy input của trường mật khẩu
    const matKhauInput = document.getElementById('matKhau');

    // Đăng ký sự kiện click cho icon "con mắt" để chuyển đổi ẩn/hiện mật khẩu
    togglePassword.addEventListener('click', function () {
        // Nếu trường mật khẩu hiện đang là type="password" thì đổi sang type="text" để hiện mật khẩu,
        // ngược lại thì đổi về type="password" để ẩn mật khẩu
        const type =
            matKhauInput.getAttribute('type') === 'password'
                ? 'text'
                : 'password';
        matKhauInput.setAttribute('type', type);

        // Đổi icon con mắt:
        // Nếu đang là icon mắt mở thì chuyển sang mắt đóng và ngược lại (fa-eye <-> fa-eye-slash)
        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
    });

    // Xử lý tự động ẩn popup thông báo sau 3 giây
    const alertPopup = document.getElementById('alert-popup');
    if (alertPopup) {
        // Sau 3 giây (3000ms), giảm dần độ mờ (opacity) của popup về 0 để tạo hiệu ứng mờ dần
        setTimeout(() => {
            alertPopup.style.opacity = 0;
            // Sau khi opacity về 0 (mất 300ms), ẩn hoàn toàn popup khỏi màn hình
            setTimeout(() => (alertPopup.style.display = 'none'), 300);
        }, 3000);
    }
});

// Hàm đóng popup ngay lập tức khi người dùng bấm vào nút đóng (×)
function closeAlertPopup() {
    const alertPopup = document.getElementById('alert-popup');
    if (alertPopup) {
        // Đặt opacity về 0 để mờ đi
        alertPopup.style.opacity = 0;
        // Sau 300ms, ẩn hoàn toàn popup khỏi màn hình
        setTimeout(() => (alertPopup.style.display = 'none'), 300);
    }
}