// Xem bàn: Kiểm tra đã chọn bàn, nếu chưa chọn thì showError, nếu bàn trống thì showError, nếu đã đặt/đang sử dụng thì submit form truyền thống
function initViewTableButton() {
  const viewTableButton = document.getElementById('view-table-button');
  if (!viewTableButton) return;

  viewTableButton.addEventListener('click', function (event) {
    event.preventDefault();

    // Tìm bàn được chọn
    const selectedTable = document.querySelector('.table-selected');
    if (!selectedTable) {
      showError('Hãy chọn một bàn để xem chi tiết');
      return;
    }

    const status = selectedTable.dataset.status;
    const tableId = selectedTable.getAttribute('data-table-id');

    // Nếu bàn đang trống thì không cho xem chi tiết
    if (status === 'AVAILABLE') {
      showError('Bàn này hiện đang trống, chưa có thông tin order để hiển thị.');
      return;
    }

    // Nếu là RESERVED hoặc OCCUPIED thì submit form truyền thống (GET)
    const form = document.createElement('form');
    form.method = 'GET';
    form.action = '/sale/view-detail';

    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = 'tableId';
    input.value = tableId;

    form.appendChild(input);
    document.body.appendChild(form);
    form.submit();
  });
}

// Khởi tạo tất cả handlers khi DOM đã sẵn sàng
document.addEventListener('DOMContentLoaded', function () {
  initViewTableButton();
});