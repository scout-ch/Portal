(function () {
  var wrapper = null;
  var defaultTimeout = 2000;
  var errorTimeout = 4000;

  function init() {
    wrapper = document.getElementById('notifications');
    var notifications = wrapper.querySelectorAll('.alert');
    var nLength = notifications.length;

    for (var i = 0; i < nLength; i+=1) {
      setTimeout((function(notification) {
        return function() {
          wrapper.removeChild(notification);
        };
      }(notifications[i])), notifications[i].classList.contains('error') ? errorTimeout : defaultTimeout);
    }

    Portal.prototype.notification = function (level, msg) {
      createNotification(level, msg);
    }
  }


  function createNotification(level, msg) {
    if (!wrapper) {
      return;
    }

    var notification = document.createElement('div');
    notification.classList.add('alert');
    notification.classList.add(level);
    notification.textContent = msg;
    wrapper.appendChild(notification);

    setTimeout(function() {
      wrapper.removeChild(notification);
    }, level === 'error' ? errorTimeout : defaultTimeout);
  }



  window.addEventListener('DOMContentLoaded', init);
}());
