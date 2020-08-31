//= require webjars.js
//= require portal.js
//= require inc/notification.js
//= require inc/copy-to-clipboard.js
//= require inc/tabs.js
//= require inc/table-sort.js
//= require inc/accordion.js
//= require inc/message.js
//= require inc/file-input.js
//= require vendor/isotope.min.js
//= require inc/tiles.js
//= require vendor/Sortable.min.js
//= require inc/table-drag.js
//= require_self

var portal = new Portal();

var flashHolder = $(document).find('.portal-flash');
if (flashHolder) {
    var danger = flashHolder.find('.alert-danger');
    if (danger.length > 0) {
        var msg = danger.html();
        danger.remove();
        portal.notification('error', msg);
    }
    var success = flashHolder.find('.alert-success');
    if (success.length > 0) {
        var msg = success.html();
        success.remove();
        portal.notification('success', msg);
    }
    var info = flashHolder.find('.alert-info');
    if (info.length > 0) {
        var msg = info.html();
        info.remove();
        portal.notification('info', msg);
    }
}
