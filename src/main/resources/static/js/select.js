$(document).ready(function() {
    $('#client').select2({
        placeholder: "Избери клиент...",
        allowClear: true,
        width: '100%',

        language: {
            noResults: function() {
                return "Няма намерени клиенти";
            }
        }
    }).on('select2:open', function() {
        $('.select2-search__field').attr('placeholder', 'Пиши за търсене...');
    });

    const isEditView = window.location.pathname.startsWith('/car/edit');
    if(isEditView){
    document.getElementById('change-make').addEventListener('click', function() {
        const make = document.getElementById('searchMake');
        const model = document.getElementById('searchModel');
        make.classList.toggle('d-none');
        model.classList.toggle('d-none');
        if (make.classList.contains('d-none')) {
            this.textContent = 'Промени';
        } else {
            this.textContent = 'Затвори';
        }
    });}
});
// sweet alert delete confirmation (delegated, robust)
document.addEventListener('click', function (e) {
    const btn = e.target.closest('.user-delete-btn');
    if (!btn) return; // not a delete button

    e.preventDefault();

//     diagnostics (можеш да махнеш)
     console.log('Delete button clicked:', btn);
     console.log('dataset:', btn.dataset, 'data-name attr:', btn.getAttribute('data-name'));

    // safe read of data-name
    const name = btn.dataset && btn.dataset.name
        ? btn.dataset.name
        : (btn.getAttribute && btn.getAttribute('data-name')) || 'този елемент';

    // if Swal is not loaded, fallback to confirm()
    const doDelete = () => {
        const form = btn.closest('form');
        if (form) {
            form.submit();
            return;
        }
        const href = btn.getAttribute('href') || btn.dataset.href;
        if (href) window.location.href = href;
        else console.warn('No form or href to perform deletion for', btn);
    };

    if (typeof Swal !== 'undefined' && Swal.fire) {
        Swal.fire({
            title: 'Сигурни ли сте?',
            text: `Наистина ли искате да изтриете ${name}?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Да, изтрий',
            cancelButtonText: 'Отказ',
            reverseButtons: true
        }).then(result => {
            if (result.isConfirmed) doDelete();
        });
    } else {
        // fallback
        if (confirm(`Наистина ли искате да изтриете ${name}?`)) doDelete();
    }
});