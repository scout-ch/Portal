(function() {
  function init() {
    var tileLists = document.querySelectorAll('.tiles');
    var tLength = tileLists.length;

    for (var i = 0; i < tLength; i+=1) {
      setupIsotopeInstance(tileLists[i]);
    }
  }



  function setupIsotopeInstance(tiles) {
    var iso = new Isotope(tiles, {
      itemSelector: '.tile',
      masonry: {
        columnWidth: '.grid-sizer',
        gutter: '.gutter-sizer'
      },
      percentPosition: true,
      sortBy: 'position',
      getSortData: {
        position: '[data-position]'
      }
    });

    var imagesLoaded = 0;
    var images = tiles.querySelectorAll('.tile .image img');
    var iLength = images.length;
    var onImageLoaded = function() {
      imagesLoaded++;
      if (imagesLoaded === iLength) {
        iso.arrange();
      }
    };

    for (var j = 0; j < iLength; j++) {
      if (images[j].complete) {
        onImageLoaded();
      } else {
        images[j].addEventListener('load', onImageLoaded);
      }
    }

    // handle updates from SortableJS
    tiles.addEventListener('positions-updated', (function (iso) {
      return function () {
        iso.updateSortData();
        iso.arrange();
      }
    })(iso));

  }



  window.addEventListener('DOMContentLoaded', init);
}());
