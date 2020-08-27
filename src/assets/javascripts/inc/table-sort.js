(function() {
  function init() {
    var tables = document.querySelectorAll('.table-sortable');
    var tLength = tables.length;

    for (var i = 0; i < tLength; i+=1) {
      new Tablesort(tables[i]);
      addSortListener(tables[i]);
    }
  }


  function addSortListener(tableElement) {
    var orderHeading = tableElement.querySelector('th.order');

    if (orderHeading && tableElement.classList.contains('table-draggable')) {
      tableElement.addEventListener('afterSort', function onAfterTableSort() {
        if (!orderHeading.getAttribute('aria-sort')) {
          tableElement.classList.add('drag-disabled');
        } else {
          tableElement.classList.remove('drag-disabled');
        }
      });
    }
  }



  window.addEventListener('DOMContentLoaded', init);
}());
