(function () {
    function init() {
        var tables = document.querySelectorAll('.table-draggable');
        var tLength = tables.length;

        for (var i = 0; i < tLength; i += 1) {
            var body = tables[i].querySelector('tbody');
            new Sortable(body, {
                draggable: 'tr',
                handle: '.grip-handle',
                forceFallback: true, // required for automated testing
                onUpdate: (function (tableBody) {
                    return function () {
                        var sortUrl = tableBody.dataset.sortUrl;
                        var rows = tableBody.querySelectorAll('tr');
                        var rLength = rows.length;
                        var tileIdMap = {};

                        for (var j = 0; j < rLength; j++) {
                            var id = rows[j].getAttribute('data-id');
                            if (id) {
                                tileIdMap[id] = j;
                            }
                            var sortIndex = rows[j].querySelector('[data-sort]');
                            sortIndex.setAttribute('data-sort', j);
                        }
                        saveNewSortOrder(sortUrl, tileIdMap);
                    };
                }(body))
            });
        }
    }


    function saveNewSortOrder(sortUrl, tileIdMap) {
        var xhr = new XMLHttpRequest();

        xhr.open('POST', sortUrl, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        portal.addCsrfHeader(xhr);
        xhr.onreadystatechange = function () {
            if (this.readyState !== 4) return;

            if (this.status === 200) {
                var data = JSON.parse(this.responseText);
                portal.notification('success', data.message);
            } else {
                var contentType = this.getResponseHeader('content-type');
                if (contentType === 'application/json') {
                    var response = JSON.parse(this.responseText);
                    portal.notification('error', response.message);
                } else {
                    portal.notification('error', 'Request failed ' + this.statusText);
                }
            }
        };
        xhr.send(JSON.stringify(tileIdMap));
    }


    window.addEventListener('DOMContentLoaded', init);
}());
