(function() {
  function init() {
    var tables = document.querySelectorAll('.table-sortable');
    var tLength = tables.length;
    console.log(tables);

    for (var i = 0; i < tLength; i+=1) {
      var sort = new Tablesort(tables[i]);
      console.log(sort);
      addSortListener(tables[i]);
    }
  }


  function addSortListener(tableElement) {
    tableElement.addEventListener('beforeSort', function onBeforeTableSort(ev) {
      console.log('Before sort', ev);
    });
    tableElement.addEventListener('afterSort', function onAfterTableSort(ev) {
      console.log('After sort', ev);
      var orderHeading = tableElement.querySelector('th.order');
      if (!orderHeading.getAttribute('aria-sort')) {
        tableElement.classList.add('drag-disabled');
      } else {
        tableElement.classList.remove('drag-disabled');
      }
    });
  }



  window.addEventListener('DOMContentLoaded', init);
}());
