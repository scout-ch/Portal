//= require /webjars/tom-select/2.2.2/dist/js/tom-select.complete.js
//= require_self

function tomSelect_init() {

    var isiDevice = /ipad|iphone|ipod/i.test(navigator.userAgent.toLowerCase());
    var isAndroid = /android/i.test(navigator.userAgent.toLowerCase());

    if (!isiDevice && !isAndroid) {
        var selector = 'select.tom-select-autoinit';
        var selects = document.querySelectorAll(selector);
        if (selects) {
            for (var i = 0; i < selects.length; i++) {
                new TomSelect(selects[i], {
                    create: false,
                    allowEmptyOption: true,
                    sortField: {
                        field: "text",
                        direction: "asc"
                    },
                    render: {
                        no_results: function (data, escape) {
                            return '<div class="no-results">Keine Ergebnisse für "' + escape(data.input) + '"</div>';
                        },
                    }
                });
            }
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    tomSelect_init();
});

