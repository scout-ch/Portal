(function () {
  function init() {
    var menuButton = document.getElementById('btn-menu');

    if (menuButton) {
      menuButton.addEventListener('click', function onMenuButtonClicked() {
        document.body.classList.toggle('nav-open');
      });
    }
  }



  window.addEventListener('DOMContentLoaded', init);
}());
