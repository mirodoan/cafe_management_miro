// 9. Thanh toán
function initPaymentButton() {
  const paymentButton = document.getElementById('payment-button');
  if (!paymentButton) return;
  paymentButton.addEventListener('click', function (event) {
    event.preventDefault();
    const selectedTable = document.querySelector('.table-selected');
    if (!selectedTable) {
      showError('Vui lòng chọn một bàn để thanh toán!');
      return;
    }
    const status = selectedTable.dataset.status;
    if (status !== 'OCCUPIED') {
      showError('Chỉ có thể thanh toán cho bàn đang sử dụng!');
      return;
    }
    const tableId = selectedTable.getAttribute('data-table-id');
    window.location.href = `/sale/show-payment-modal?tableId=${tableId}`;
  });
}

document.addEventListener('DOMContentLoaded', function () {
  initPaymentButton();
});