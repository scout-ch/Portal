(function () {
    function init() {
        var tilesContainers = document.querySelectorAll('.tiles-draggable');
        var tLength = tilesContainers.length;
        for (var i = 0; i < tLength; i += 1) {

            var tileContainer = tilesContainers[i];
            new Sortable(tileContainer, {
                draggable: '.tile',
                handle: '.btn-move',
                filter: '.no-sort',
                forceFallback: true, // required for automated testing
                onUpdate: (function (tileContainer) {
                    return function (event) {
                        var sortUrl = tileContainer.dataset.sortUrl;
                        var tiles = tileContainer.querySelectorAll('.tile');
                        var rLength = tiles.length;
                        var tileIdMap = {};

                        for (var j = 0; j < rLength; j++) {
                            var id = tiles[j].dataset.id;
                            if (id) {
                                tileIdMap[id] = j;
                                tiles[j].dataset.position = j;
                            }
                        }
                        saveNewSortOrder(sortUrl, tileIdMap);
                        // notify isotope
                        tileContainer.dispatchEvent(new Event('positions-updated'));
                    };
                }(tileContainer))
            });
        }
        var toggleTileEditor = document.getElementById('toggleTileEditor');
        if (toggleTileEditor) {
            toggleTileEditor.addEventListener('click', function () {
                toggleTileEditor.classList.toggle('active');
                for (var i = 0; i < tLength; i += 1) {
                    var tileContainer = tilesContainers[i];
                    tileContainer.classList.toggle('tiles-not-editable');
                }
                ;
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
