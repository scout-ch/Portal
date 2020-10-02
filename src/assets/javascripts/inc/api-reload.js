(function () {
  function init() {
    var reloadButton = document.getElementById('btn-reload-api');
    var apiInput = document.getElementById('apiKey-input');

    if (reloadButton && apiInput) {
      reloadButton.addEventListener('click', function onReloadApiClicked() {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/admin/masterTile/generateApiKey', true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        portal.addCsrfHeader(xhr);
        xhr.onreadystatechange = function () {
          if (this.readyState !== 4) return;

          if (this.status === 200) {
            var data = JSON.parse(this.responseText);
            portal.notification('success', data.message);
            apiInput.value = data.apiKey;
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
        xhr.send();
      });
    }
  }



  window.addEventListener('DOMContentLoaded', init);
}());
