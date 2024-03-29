(function() {
  function init() {
    var inputs = document.querySelectorAll('.form-wrap input.copy-to-clipboard');
    var iLength = inputs.length;

    for (var i = 0; i < iLength; i+=1) {
      var wrapper = inputs[i].parentElement;
      var button = document.createElement('button');
      button.classList.add('btn-icon');
      button.classList.add('btn-input-after');
      button.type = 'button';
      button.innerHTML = '<svg aria-hidden="true" focusable="false" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512"><path fill="currentColor" d="M433.941 65.941l-51.882-51.882A48 48 0 0 0 348.118 0H176c-26.51 0-48 21.49-48 48v48H48c-26.51 0-48 21.49-48 48v320c0 26.51 21.49 48 48 48h224c26.51 0 48-21.49 48-48v-48h80c26.51 0 48-21.49 48-48V99.882a48 48 0 0 0-14.059-33.941zM266 464H54a6 6 0 0 1-6-6V150a6 6 0 0 1 6-6h74v224c0 26.51 21.49 48 48 48h96v42a6 6 0 0 1-6 6zm128-96H182a6 6 0 0 1-6-6V54a6 6 0 0 1 6-6h106v88c0 13.255 10.745 24 24 24h88v202a6 6 0 0 1-6 6zm6-256h-64V48h9.632c1.591 0 3.117.632 4.243 1.757l48.368 48.368a6 6 0 0 1 1.757 4.243V112z"></path></svg>';
      button.addEventListener('click', (function onCopyClicked(input) {
        return function() {
          copyTextToClipboard(input.value);
        };
      }(inputs[i])));
      wrapper.appendChild(button);
    }

    var copysharelink = document.querySelectorAll('.copy-share-link');
    var copysharelinkLength = copysharelink.length;

    for (var i = 0; i < copysharelinkLength; i++) {
      copysharelink[i].addEventListener('click', (function onCopyClicked (sharelink){
        return function(event) {
          event.preventDefault();
          var sharelinkurl = sharelink.href;
          copyTextToClipboard(sharelinkurl);
          portal.notification('success', 'Link copied: ' + sharelinkurl);
        };
      }(copysharelink[i])));
    }
  }

  function copyTextToClipboard(text) {
    var textArea = document.createElement("textarea");

    textArea.style.position = 'fixed';
    textArea.style.top = 0;
    textArea.style.left = 0;
    textArea.style.width = '2em';
    textArea.style.height = '2em';
    textArea.style.padding = 0;
    textArea.style.border = 'none';
    textArea.style.outline = 'none';
    textArea.style.boxShadow = 'none';
    textArea.style.background = 'transparent';

    textArea.value = text;

    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try {
      document.execCommand('copy');
    } catch (err) {
      console.warn('Unable to copy', err);
    }

    document.body.removeChild(textArea);
  }

  window.addEventListener('DOMContentLoaded', init);
}());
