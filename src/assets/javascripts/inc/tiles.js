(function() {
  function init() {
    var tiles = document.querySelectorAll('.tiles');
    var tLength = tiles.length;

    for (var i = 0; i < tLength; i+=1) {
      new Isotope(tiles[i], {
        itemSelector: '.tile',
        masonry: {
          columnWidth: '.grid-sizer',
          gutter: '.gutter-sizer'
        },
        percentPosition: true
      });
    }
  }



  window.addEventListener('DOMContentLoaded', init);
}());
