$(document).ready(function() {

    window.addEventListener('pageshow', function (event) {
        if (event.persisted) {
            window.location.reload();
        }
    });

    const isClientAddEditView = window.location.pathname.startsWith('/client/add') || window.location.pathname.startsWith('/client/editt');
    if(isClientAddEditView){
        const isCompanyFormChecked = document.getElementById('isChecked');
        const comapny = document.getElementById('companyForm');
        if(isCompanyFormChecked.value==='true'){
            comapny.classList.remove('d-none');
        }



        document.getElementById('showCompany').addEventListener('change', function() {
            comapny.classList.toggle('d-none', !this.checked);
            isCompanyFormChecked.value = this.checked.toString();

        });
    }
    $('#clientId').select2({
        theme: 'bootstrap-5',
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
    $('#client').select2({
        theme: 'bootstrap-5',
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

    $('#companyToAdd').select2({
        theme: 'bootstrap-5',
        placeholder: "Избери фирма...",
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
    $('#carToAdd').select2({
        theme: 'bootstrap-5',
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


    // 1) инициализация на select2 (както вече имаш)
    $('#car').select2({
        placeholder: "Избери кола...",
        allowClear: true,
        width: '100%'
    }).on('select2:open', function() {
        $('.select2-search__field').attr('placeholder', 'Пиши за търсене...');
    });

    // 2) хендлери за избор (работят и ако select2 е използван или не)
    // при select2:select - използваме e.params.data.element (DOM <option>) когато е налично
    $('#car').on('select2:select', function(e) {
        // e.params.data е обектът, ако select2 е инициализиран от HTML <option> то има .element
        const sel = e.params && e.params.data ? e.params.data : null;

        // първо опитваме да вземем оригиналния DOM <option> (най-надеждният вариант)
        let $opt = null;
        if (sel && sel.element) {
            $opt = $(sel.element);
        } else {
            // fallback: намери selected option в DOM
            $opt = $(this).find('option:selected');
        }

        // вземаме данните (data-*): използваме .data() и .attr() за сигурност
        const registration = $opt.data('registration') || $opt.attr('data-registration') || $opt.val();
        const make = $opt.data('make') || $opt.attr('data-make') || '';
        const model = $opt.data('model') || $opt.attr('data-model') || '';
        const year = $opt.data('year') || $opt.attr('data-year') || '';
        const cube = $opt.data('cube') || $opt.attr('data-cube') || '';
        const kw = $opt.data('kw') || $opt.attr('data-kw') || '';
        const hp = $opt.data('hp') || $opt.attr('data-hp') || '';
        const vin = $opt.data('vin') || $opt.attr('data-vin') || '';

        // debug (махни след като работи)
        console.log('selected option element:', $opt.prop('outerHTML'));
        console.log({ registration, make, model, year, cube, kw, hp, vin });

        // 3) попълваме input-ите и ги правим readonly
        $('#registrationNumber').val(registration).prop('readOnly', true);
        $('#make').val(make).prop('readOnly', true);
        $('#model').val(model).prop('readOnly', true);
        $('#year').val(year).prop('readOnly', true);
        $('#cube').val(cube).prop('readOnly', true);
        $('#kw').val(kw).prop('readOnly', true);
        $('#hp').val(hp).prop('readOnly', true);
        $('#vin').val(vin).prop('readOnly', true);
    });

    // ако селекцията бъде изчистена (clear) / unselect -> махаме readonly и изчистваме
    $('#car').on('select2:unselecting select2:clear', function(e) {
        ['registrationNumber','make','model','year','cube','kw','hp','vin'].forEach(id => {
            $('#' + id).val('').prop('readOnly', false);
        });
    });

    // Ако не ползваш select2 или искаш да поддържаш и обикновен change:
    $('#company_name').on('change', function() {
        const $optClient = $(this).find('option:selected');
        // същата логика като по-горе (вземане и попълване)
        const name = $optClient.data('company-name') || $optClient.attr('data-name') || $opt.val();
        const uic = $optClient.data('uic') || $optClient.attr('data-uic') || '';
        const vatNumber = $optClient.data('vatNumber') || $optClient.attr('data-vatNumber') || '';
        const address = $optClient.data('address') || $optClient.attr('data-address') || '';
        const accountablePerson = $optClient.data('accountableperson') || $optClient.attr('data-accountableperson') || '';


        $('#name').val(name).prop('readOnly', true);
        $('#uic').val(uic).prop('readOnly', true);
        $('#vatNumber').val(vatNumber).prop('readOnly', true);
        $('#address').val(address).prop('readOnly', true);
        $('#accountablePerson').val(accountablePerson).prop('readOnly', true);

    });
});
// sweet alert delete confirmation (delegated, robust)
document.addEventListener('click', function (e) {
    const btn = e.target.closest('.user-delete-btn');
    if (!btn) return; // not a delete button

    e.preventDefault();
    let message;
    if(window.location.pathname.startsWith('/client/clients')
    || window.location.pathname.startsWith('/car/cars') ||
    window.location.pathname.startsWith('/car/cars') ){
        message = "Ще се изтрият всички записи свързни с този обект!"
    } else{
        message = "Наистина ли искате да изтриете записа?"
    }

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
            text: message,
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