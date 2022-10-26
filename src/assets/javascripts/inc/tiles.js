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

    var filter = document.querySelector('.filter-catalog');
    if (filter) {
      var titleInput = filter.querySelector('input[name="title"]');
      var categorySelect = filter.querySelector('select[name="category"]');

      var tileFilter = function (element, titleInput, categorySelect) {
        var fTitle = titleInput.value;
        var fCategory = categorySelect.value;
        var eTitle = element.dataset.tileTitle;
        var eCategoryId = element.dataset.tileCategoryId;

        var matches = true;
        if (fTitle) {
          matches = matches && eTitle.toUpperCase().includes(fTitle.toUpperCase());
        }
        if (fCategory) {
          matches = matches && fCategory === eCategoryId
        }
        return matches;
      };

      var eventhandler = (function (iso, titleInput, categorySelect) {
        return function (event) {
          iso.arrange({
            filter: function (element) {
              return tileFilter(element, titleInput, categorySelect);
            }
          });

          var fTitle = titleInput.value;
          var fCategory = categorySelect.value;
          var url = new URL(window.location);
          if (fTitle) {
            url.searchParams.set('filterTitle', fTitle);
          } else if (url.searchParams.has('filterTitle')) {
            url.searchParams.delete('filterTitle');
          }
          if (fCategory) {
            url.searchParams.set('filterCategory', fCategory);
          } else if (url.searchParams.has('filterCategory')) {
            url.searchParams.delete('filterCategory');
          }
          window.history.pushState({}, document.title, url);
        }
      })(iso, titleInput, categorySelect);

      titleInput.addEventListener('keyup', eventhandler);
      titleInput.addEventListener('change', eventhandler);
      categorySelect.addEventListener('change', eventhandler);

      eventhandler(null);
    }

  }



  window.addEventListener('DOMContentLoaded', init);
}());
