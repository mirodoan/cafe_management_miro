// 6. Gộp bàn: Kiểm tra trạng thái, chỉ cho phép chọn bàn OCCUPIED, sau đó mở form gộp bàn
function initMergeTableButton() {
  // Lấy phần tử nút "Gộp bàn"
  const mergeTableButton = document.getElementById('merge-table-button');
  if (!mergeTableButton) return;

  // Thêm sự kiện click cho nút "Gộp bàn"
  mergeTableButton.addEventListener('click', function (event) {
    event.preventDefault(); // Ngăn hành vi mặc định (nếu là nút trong form)

    // Lấy bàn đang được chọn (có class .table-selected)
    const selectedTable = document.querySelector('.table-selected');
    if (!selectedTable) {
      showError('Vui lòng chọn một bàn đang sử dụng để bắt đầu gộp bàn!');
      return;
    }

    // Lấy trạng thái của bàn
    const status = selectedTable.dataset.status;
    if (status !== 'OCCUPIED') {
      showError('Chỉ có thể gộp các bàn đang sử dụng!');
      return;
    }

    // Lấy tableId để truyền vào URL
    const tableId = selectedTable.getAttribute('data-table-id');

    // Điều hướng đến trang với modal gộp bàn
    window.location.href = `/sale/show-merge-table-form?selectedTableId=${tableId}`;
  });
}

// Khởi tạo handler khi DOM đã sẵn sàng
document.addEventListener('DOMContentLoaded', function () {
  initMergeTableButton();
});