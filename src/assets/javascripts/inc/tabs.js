(function() {
  function init() {
    var tabs = document.querySelectorAll('.tabs');
    var tLength = tabs.length;

    for (var i = 0; i < tLength; i+=1) {
      setupTab(tabs[i]);
    }
  }


  function setupTab(tabElement) {
    var buttons = tabElement.querySelectorAll('.btn-tab');
    var elements = tabElement.querySelectorAll('.tab-content');
    var bLength = buttons.length;

    for (var i = 0; i < bLength; i+=1) {
      var id = buttons[i].dataset['tabid'];
      if (id === undefined) {
        continue;
      }
      var element = getTabElementById(elements, id);
      if (element === false) {
        continue;
      }

      buttons[i].addEventListener('click', (function onTabButtonClicked(tabElement) {
        return function() {
          setTabsInactive(buttons, elements);
          this.classList.add('active');
          tabElement.classList.add('active');
        };
      }(element)));
    }
  }


  function getTabElementById(elements, id) {
    var eLength = elements.length;

    for (var i = 0; i < eLength; i+=1) {
      if (elements[i].dataset['tabid'] && elements[i].dataset['tabid'] === id) {
        return elements[i];
      }
    }

    return false;
  }


  function setTabsInactive(buttons, elements) {
    var bLength = buttons.length;

    for (var i = 0; i < bLength; i+=1) {
      buttons[i].classList.remove('active');
      if (elements[i]) {
        elements[i].classList.remove('active');
      }
    }
  }



  window.addEventListener('DOMContentLoaded', init);
}());
