// XỬ LÝ UI BÀN VÀ NÚT CHỨC NĂNG
// ---
// 1. Chọn bàn: Click để chọn/bỏ chọn, chỉ cho phép chọn 1 bàn tại 1 thời điểm
function initTableSelection() {
  const tableElements = document.querySelectorAll('[data-table]');
  console.log('Found table elements:', tableElements.length);

  tableElements.forEach((table, index) => {
    console.log(`Adding click listener to table ${index}:`, table);
    table.addEventListener('click', function (event) {
      event.preventDefault();
      console.log('Table clicked:', this);

      if (this.classList.contains('table-selected')) {
        this.classList.remove('table-selected');
        console.log('Table deselected');
      } else {
        // Bỏ chọn tất cả bàn khác
        tableElements.forEach((t) => {
          t.classList.remove('table-selected');
        });
        // Chọn bàn hiện tại
        this.classList.add('table-selected');
        console.log('Table selected:', this.getAttribute('data-table-id'));
      }
    });
  });
}

// Khởi tạo handlers
document.addEventListener('DOMContentLoaded', function () {
  initTableSelection();
});
