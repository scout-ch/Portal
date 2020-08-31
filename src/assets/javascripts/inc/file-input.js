(function () {
  function init() {
    var fileInputs = document.querySelectorAll('.form-wrap input[type="file"]');
    var fLength = fileInputs.length;

    for (var i = 0; i < fLength; i+=1) {
      addChangeListener(fileInputs[i]);
      addDeleteButtonListener(fileInputs[i]);
    }
  }



  function addChangeListener(input) {
    var wrapper = input.parentElement;

    input.addEventListener('change', function onFileInputChanged(ev) {
      var newField = wrapper.querySelector('.newFile');

      if (!newField) {
        var newFileInput = document.createElement('input');
        newFileInput.name = input.name + '-new';
        newFileInput.classList.add('newFile');
        newFileInput.type = 'hidden';
        newFileInput.value = true;
        wrapper.appendChild(newFileInput);
      } else if (!this.value) {
        wrapper.removeChild(newField);
      }
    });
  }


  function addDeleteButtonListener(input) {
    var wrapper = input.parentElement;
    var deleteButton = wrapper.querySelector('.btn-delete');
    if (!deleteButton) {
      return;
    }

    deleteButton.addEventListener('click', function onDeleteClicked() {
      var preview = deleteButton.parentElement;
      var deleteField = wrapper.querySelector('.deleteFile');

      wrapper.removeChild(preview);

      if (!deleteField) {
        var deleteInput = document.createElement('input');
        deleteInput.classList.add('deleteFile');
        deleteInput.name = input.name + '-delete';
        deleteInput.type = 'hidden';
        deleteInput.value = true;
        wrapper.appendChild(deleteInput);
      }
    });
  }



  window.addEventListener('DOMContentLoaded', init);
}());
