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

    $('.form-select').select2({
        theme: 'bootstrap-5',
                placeholder: "Избери...",
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
                this.textContent = 'Промени макра и модел';
            } else {
                this.textContent = 'Затвори';
            }
        });}


    $('#car').on('select2:select', function(e) {
        const sel = e.params && e.params.data ? e.params.data : null;
        let $opt = null;
        if (sel && sel.element) {
            $opt = $(sel.element);
        } else {
            $opt = $(this).find('option:selected');
        }

        const registration = $opt.data('registration') || $opt.attr('data-registration') || $opt.val();
        const make = $opt.data('make') || $opt.attr('data-make') || '';
        const model = $opt.data('model') || $opt.attr('data-model') || '';
        const year = $opt.data('year') || $opt.attr('data-year') || '';
        const cube = $opt.data('cube') || $opt.attr('data-cube') || '';
        const kw = $opt.data('kw') || $opt.attr('data-kw') || '';
        const hp = $opt.data('hp') || $opt.attr('data-hp') || '';
        const vin = $opt.data('vin') || $opt.attr('data-vin') || '';


        $('#registrationNumber').val(registration).prop('readOnly', true);
        $('#make').val(make).prop('readOnly', true);
        $('#model').val(model).prop('readOnly', true);
        $('#year').val(year).prop('readOnly', true);
        $('#cube').val(cube).prop('readOnly', true);
        $('#kw').val(kw).prop('readOnly', true);
        $('#hp').val(hp).prop('readOnly', true);
        $('#vin').val(vin).prop('readOnly', true);
    });

    $('#car').on('select2:unselecting select2:clear', function(e) {
        ['registrationNumber','make','model','year','cube','kw','hp','vin'].forEach(id => {
            $('#' + id).val('').prop('readOnly', false);
        });
    });

    $('#company_name').on('change', function() {
        const $optClient = $(this).find('option:selected');
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

document.addEventListener('click', function (e) {
    const btn = e.target.closest('.user-delete-btn');
    if (!btn) return;

    e.preventDefault();
    let message;
    if(window.location.pathname.startsWith('/client/clients')
    || window.location.pathname.startsWith('/car/cars') ||
    window.location.pathname.startsWith('/car/cars') ){
        message = "Ще се изтрият всички записи свързни с този обект!"
    } else{
        message = "Наистина ли искате да изтриете записа?"
    }

    const name = btn.dataset && btn.dataset.name
        ? btn.dataset.name
        : (btn.getAttribute && btn.getAttribute('data-name')) || 'този елемент';

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
        if (confirm(`Наистина ли искате да изтриете ${name}?`)) doDelete();
    }
});


$('select.form-select').on('select2:select', function(e) {
    const selected = e.params.data;
    console.log(selected.id);

    const $option = $(this).find('option').filter(function() {
        return $(this).val() === selected.id;
    });
    $(this).find('option').removeAttr('selected')
    if ($option.length) {
        $option.attr('selected', 'selected');
    }

    $(this).trigger('change');

    const value = $(this).val();
    const price = $option.data('price');
    const text = $option.text()

    console.log('Value:', value, 'Text:', text, 'Price:', price);
});
