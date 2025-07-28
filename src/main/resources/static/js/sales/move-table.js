// 8. Chuyển bàn: Kiểm tra trạng thái, chỉ cho phép chọn bàn OCCUPIED, sau đó mở form chuyển bàn
function initMoveTableButton() {
  // Lấy phần tử nút "Chuyển bàn"
  const moveTableButton = document.getElementById('move-table-button');

  // Nếu không có nút thì thoát
  if (!moveTableButton) return;

  // Thêm sự kiện click cho nút "Chuyển bàn"
  moveTableButton.addEventListener('click', function (event) {
    event.preventDefault(); // Ngăn hành vi mặc định (ví dụ submit form)

    // Lấy bàn đang được chọn (có class .table-selected)
    const selectedTable = document.querySelector('.table-selected');
    if (!selectedTable) {
      showError('Vui lòng chọn một bàn để chuyển!');
      return;
    }

    // Lấy trạng thái của bàn
    const status = selectedTable.dataset.status;

    // Kiểm tra nếu bàn không phải OCCUPIED (đang sử dụng) thì không cho chuyển
    if (status !== 'OCCUPIED') {
      showError('Bàn muốn chuyển phải là bàn đang sử dụng!');
      return;
    }

    // Lấy tableId để truyền vào URL
    const tableId = selectedTable.getAttribute('data-table-id');

    // Điều hướng đến trang với modal chuyển bàn
    window.location.href = `/sale/show-move-table-form?selectedTableId=${tableId}`;
  });
}

// Khởi tạo handler khi DOM đã sẵn sàng
document.addEventListener('DOMContentLoaded', function () {
  initMoveTableButton();
});
