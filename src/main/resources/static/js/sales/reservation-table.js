// 2. Đặt bàn: Kiểm tra đã chọn bàn, trạng thái bàn phải là AVAILABLE, nếu hợp lệ thì chuyển sang form đặt bàn
function initReservationButton() {
  // Lấy phần tử nút "Đặt bàn"
  const reservationButton = document.getElementById('reservation-button');

  // Nếu không tìm thấy nút thì thoát
  if (!reservationButton) return;

  // Gắn sự kiện click cho nút "Đặt bàn"
  reservationButton.addEventListener('click', function (event) {
    event.preventDefault(); // Ngăn chặn hành vi mặc định (ví dụ submit form)

    // Lấy bàn đang được chọn (có class .table-selected)
    const selectedTable = document.querySelector('.table-selected');
    if (!selectedTable) {
      showError('Vui lòng chọn một bàn trước khi đặt bàn');
      return;
    }

    // Lấy trạng thái của bàn
    const status = selectedTable.dataset.status;

    // Nếu bàn không ở trạng thái AVAILABLE thì báo lỗi
    if (status !== 'AVAILABLE') {
      showError('Chỉ có thể đặt bàn khi bàn đang trống. Vui lòng chọn bàn khác!');
      return;
    }

    // Lấy tableId từ dataset (ưu tiên chính)
    const tableId =
      selectedTable.dataset.tableId ||
      selectedTable.querySelector('[data-table-id]')?.dataset.tableId;

    // Điều hướng sang form đặt bàn với tableId
    window.location.href = `/sale/show-reservation-form?tableId=${tableId}`;
  });
}

// Khởi tạo handler khi DOM đã sẵn sàng
document.addEventListener('DOMContentLoaded', function () {
  initReservationButton();
});
