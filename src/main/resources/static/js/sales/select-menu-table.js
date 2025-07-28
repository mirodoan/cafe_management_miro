// 3. Chọn thực đơn: Kiểm tra đã chọn bàn, trạng thái bàn phải là AVAILABLE hoặc RESERVED hoặc OCCUPIED
function initSelectMenuButton() {
  const selectMenuButton = document.getElementById('select-menu-button');
  const selectMenuForm = document.getElementById('select-menu-form');
  const tableIdInput = document.getElementById('select-menu-table-id');

  // Nếu thiếu phần tử cần thiết thì dừng
  if (!selectMenuButton || !selectMenuForm || !tableIdInput) return;

  selectMenuButton.addEventListener('click', function (event) {
    event.preventDefault();

    // Lấy bàn được chọn
    const selectedTable = document.querySelector('.table-selected');
    if (!selectedTable) {
      showError('Vui lòng chọn một bàn trước khi chọn thực đơn');
      return;
    }

    // Kiểm tra trạng thái bàn
    const status = selectedTable.dataset.status;
    if (status !== 'AVAILABLE' && status !== 'RESERVED' && status !== 'OCCUPIED') {
      showError('Chỉ có thể chọn thực đơn cho bàn trống, đã đặt hoặc đang sử dụng.');
      return;
    }

    // Gán tableId và submit form
    const tableId = selectedTable.getAttribute('data-table-id');
    tableIdInput.value = tableId;
    selectMenuForm.submit();
  });
}

// Khởi tạo tất cả handlers
document.addEventListener('DOMContentLoaded', function () {
  initSelectMenuButton();
});
