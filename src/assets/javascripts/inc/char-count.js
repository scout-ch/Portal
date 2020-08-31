(function () {
  var recommended = '200';

  function init() {
    var wrappers = document.querySelectorAll('.form-wrap.char-count');
    var wLength = wrappers.length;

    for (var i = 0; i < wLength; i+=1) {
      var source = wrappers[i].querySelector('textarea,input');
      var counter = document.createElement('div');

      counter.classList.add('counter');
      counter.textContent = source.value.length + '/' + recommended;
      wrappers[i].appendChild(counter);

      source.addEventListener('keyup', (function onSourceChanged(counterElement) {
        return function() {
          counterElement.textContent = this.value.length + '/' + recommended;
        };
      }(counter)));
    }
  }



  window.addEventListener('DOMContentLoaded', init);
}());
