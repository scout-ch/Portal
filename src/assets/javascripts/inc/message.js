(function() {
  var filter = null;
  var messages = [];
  var totalCount = null;



  function init() {
    messages = document.querySelectorAll('.messages .message');
    filter = document.getElementById('message-filter-sender');
    totalCount = document.querySelectorAll('.message-count-total');
    var mLength = messages.length;

    for (var i = 0; i < mLength; i+=1) {
      var newMessage = messages[i].getAttribute('data-read') !== 'true';
      if (newMessage) {
        messages[i].addEventListener('accordion-opened', onFirstOpen);
      }
    }

    if (filter) {
      initFilter();
      filterMessages();
    }
  }



  function filterMessages() {
    var mLength = messages.length;

    for (var i = 0;  i < mLength; i+=1) {
      var tileId = messages[i].getAttribute('data-tileid');

      if (filter.value === 'all' || tileId === filter.value) {
        messages[i].classList.remove('hidden');
      } else {
        messages[i].classList.add('hidden');
      }
    }
  }


  function initFilter() {
    var filter = document.getElementById('message-filter-sender');

    if (filter) {
      filter.addEventListener('change', filterMessages);
    }
  }


  function onFirstOpen() {
    var messageId = this.getAttribute('data-messageid');
    if (!messageId) {
      return;
    }

    var message = this;
    var xhr = new XMLHttpRequest();

    xhr.open('POST', '/message/setRead/' + messageId, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
      if (this.readyState !== 4) return;

      if (this.status === 200) {
        var newIndicator = message.querySelector('.indicator-new');
        if (newIndicator) {
          newIndicator.parentElement.removeChild(newIndicator);
        }
        message.setAttribute('data-read', true);
        message.removeEventListener('accordion-opened', onFirstOpen);
        reduceTotalCount();

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
    xhr.send();
  }


  function reduceTotalCount() {
    var tCount = totalCount.length;

    if (tCount > 0) {
      var current = parseInt(totalCount[0].textContent);

      for (var i = 0; i < tCount; i+=1) {
        if (!isNaN(current) && current > 0) {
          totalCount[i].textContent = current - 1;
        }
      }
    }
  }



  window.addEventListener('DOMContentLoaded', init);
}());
