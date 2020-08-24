PNotify.defaults.styling = 'bootstrap4'; // Bootstrap version 4
PNotify.defaults.icons = 'fontawesome5'; // Font Awesome 5

Portal.prototype.notification = function (level, msg) {
    var _this = this;
    _this.notificationWithTitle(level, undefined, msg)
};

Portal.prototype.notificationWithTitle = function (level, title, msg) {

    var _this = this;

    var delay = 2000;

    if (level === 'error') {
        delay = 4000;
    }

    var data = {
        text: msg,
        type: level,
        delay: delay,
        modules: {
            Buttons: {
                closer: true
            }
        }
    };

    if (title) {
        data.title = title;
    }

    PNotify.alert(data);

}