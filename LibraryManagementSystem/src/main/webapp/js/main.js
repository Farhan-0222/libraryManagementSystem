// Sidebar toggle on small screens
document.addEventListener('DOMContentLoaded', function () {
  var btn = document.getElementById('menuBtn');
  var bar = document.getElementById('sidebar');
  if (btn && bar) btn.addEventListener('click', function () { bar.classList.toggle('open'); });

  // Fill the Add/Edit modal. Buttons with class "btn-edit" carry the row's data in data-* attributes
  // whose names match the form input names (e.g. data-title -> <input name="title">).
  document.querySelectorAll('.crud-modal').forEach(function (modal) {
    modal.addEventListener('show.bs.modal', function (e) {
      var trigger = e.relatedTarget;
      var form = modal.querySelector('form');
      var editing = trigger && trigger.classList.contains('btn-edit');
      form.reset();
      form.elements['action'].value = editing ? 'update' : 'add';
      modal.querySelector('.modal-title-text').textContent = (editing ? 'Edit ' : 'Add ') + modal.dataset.entity;
      var pw = form.elements['password'];
      if (pw) {
        pw.required = !editing;
        pw.placeholder = editing ? 'Leave blank to keep the current password' : 'At least 6 characters';
      }
      var un = form.elements['username'];
      if (un) un.readOnly = editing;               // a username cannot be changed after creation
      if (editing) {
        Object.keys(trigger.dataset).forEach(function (key) {
          var field = form.elements[key];
          if (field) field.value = trigger.dataset[key];
        });
      }
    });
  });

  // Book detail popup on the catalog page
  var detail = document.getElementById('detailModal');
  if (detail) {
    detail.addEventListener('show.bs.modal', function (e) {
      var d = e.relatedTarget.dataset;
      detail.querySelector('#dTitle').textContent = d.title;
      detail.querySelector('#dAuthor').textContent = d.author;
      ['isbn', 'publisher', 'category', 'year', 'copies'].forEach(function (k) {
        detail.querySelector('#d_' + k).textContent = d[k];
      });
    });
  }

  // Auto-hide flash messages after a few seconds
  var flash = document.querySelector('.flash-alert');
  if (flash) setTimeout(function () { flash.classList.remove('show'); }, 6000);
});

// Show a book cover image if it loads, otherwise keep the coloured placeholder
function coverFail(img) { img.style.display = 'none'; }
