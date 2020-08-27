(function() {
  function closeAllElements(headers) {
    var hLength = headers.length;
    for (var i = 0; i < hLength; i++) {
      headers[i].parentElement.classList.remove('open');
    }
  }


  function init() {
    var accordions = document.querySelectorAll('.accordion');
    var aLength = accordions.length;
    for (var i = 0; i < aLength; i++) {
      setupClickListeners(accordions[i]);
    }
  }


  function setupClickListeners(accordion) {
    var headers = accordion.querySelectorAll('.accordion-header');
    var hLength = headers.length;
    var openEvent = document.createEvent('Event');
    openEvent.initEvent('accordion-opened', true, true);

    for (var i = 0; i < hLength; i++) {
      headers[i].addEventListener('click', function onAccordionHeaderClicked() {
        var open = this.parentElement.classList.contains('open');
        closeAllElements(headers);

        if (open) {
          this.parentElement.classList.remove('open');
        } else {
          this.parentElement.classList.add('open');
          this.parentElement.dispatchEvent(openEvent);
        }
      });
    }
  }



  window.addEventListener('DOMContentLoaded', init);
}());
