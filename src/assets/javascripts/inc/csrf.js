(function () {

  function init() {

    var header = document.querySelector("meta[name='_csrf_header']").getAttribute('content');
    var token = document.querySelector("meta[name='_csrf_token']").getAttribute('content');

    Portal.prototype.addCsrfHeader = function (xhr) {
      xhr.setRequestHeader(header, token);
    }
  }

  window.addEventListener('DOMContentLoaded', init);
}());
