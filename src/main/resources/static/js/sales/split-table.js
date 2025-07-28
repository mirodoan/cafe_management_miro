// 7. Tách bàn: Kiểm tra trạng thái, nếu hợp lệ thì chuyển hướng đến form tách bàn
function initSplitTableButton() {
  // Lấy phần tử nút "Tách bàn"
  const splitTableButton = document.getElementById('split-table-button');

  // Nếu không tìm thấy nút thì thoát
  if (!splitTableButton) return;

  // Gắn sự kiện click cho nút "Tách bàn"
  splitTableButton.addEventListener('click', function (event) {
    event.preventDefault(); // Ngăn chặn hành vi mặc định

    // Lấy bàn đang được chọn
    const selectedTable = document.querySelector('.table-selected');
    if (!selectedTable) {
      showError('Vui lòng chọn bàn cần tách');
      return;
    }

    // Kiểm tra trạng thái bàn
    const status = selectedTable.dataset.status;
    if (status !== 'OCCUPIED') {
      showError('Vui lòng chọn bàn đang sử dụng muốn tách');
      return;
    }

    // Lấy tableId của bàn được chọn
    const tableId = selectedTable.getAttribute('data-table-id');

    // Điều hướng đến form tách bàn với tableId
    window.location.href = `/sale/show-split-table-form?selectedTableId=${tableId}`;
  });
}

// Khởi tạo handler khi DOM đã sẵn sàng
document.addEventListener('DOMContentLoaded', function () {
  initSplitTableButton();
});
