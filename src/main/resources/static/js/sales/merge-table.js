function initMergeTableButton() {
  const mergeTableButton = document.getElementById('merge-table-button');
  if (!mergeTableButton) return;

  mergeTableButton.addEventListener('click', function (event) {
    event.preventDefault();
    // Điều hướng về controller để lấy view chứa modal gộp bàn
    window.location.href = '/sale/show-merge-table-form';
  });
}

document.addEventListener('DOMContentLoaded', function () {
  initMergeTableButton();
});