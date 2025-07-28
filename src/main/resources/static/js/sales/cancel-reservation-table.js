// 5. Hủy bàn: Kiểm tra trạng thái, nếu hợp lệ thì chuyển hướng đến form xác nhận
function initCancelTableButton() {
  // Lấy nút "Hủy bàn"
  const cancelTableButton = document.getElementById('cancel-table-button');

  // Nếu không tìm thấy nút thì dừng
  if (!cancelTableButton) return;

  // Gắn sự kiện click cho nút "Hủy bàn"
  cancelTableButton.addEventListener('click', function (event) {
    event.preventDefault(); // Ngăn hành vi mặc định (vd: submit form)

    // Lấy bàn đang được chọn
    const selectedTable = document.querySelector('.table-selected');
    if (!selectedTable) {
      showError('Hãy chọn một bàn để hủy');
      return;
    }

    // Lấy trạng thái bàn và tableId
    const status = selectedTable.dataset.status;
    const tableId = selectedTable.getAttribute('data-table-id');

    // Nếu bàn đang trống → không thể hủy
    if (status === 'AVAILABLE') {
      showError('Bàn này đang trống.');
      return;
    }

    // Nếu bàn đang sử dụng → yêu cầu thanh toán trước
    if (status === 'OCCUPIED') {
      showError('Bạn cần thanh toán trước khi hủy bàn.');
      return;
    }

    // Nếu là RESERVED → điều hướng đến form xác nhận hủy bàn
    window.location.href = `/sale/show-cancel-reservation-form?tableId=${tableId}`;
  });
}

// Khởi tạo handler khi DOM đã sẵn sàng
document.addEventListener('DOMContentLoaded', function () {
  initCancelTableButton();
});
