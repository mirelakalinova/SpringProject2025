new Vue({
    el: '#order',
    data: {
        cars: [],
        clients: [],
        companies: [],
        selectedCar: null,
        selectedClient: null,
        selectedCompany: null,
        selectedCarData: null,
        selectedClientData: null,
        selectedCompanyData: null,
        enableClientDropdown: null,
        enableCompanyDropdown: null,
    },


    created() {
        this.fetchCars();

    },

    methods: {
        async fetchCars() {
            const dtoCarVal = document.getElementById('dtoCar')?.value || '';
            const dtoClientVal = document.getElementById('dtoClient')?.value || '';
            const dtoCompanyVal = document.getElementById('dtoCompany')?.value || '';
            try {
                const res = await fetch('/car/fetch/cars');
                if (!res.ok) throw new Error('fetch cars failed');
                const data = await res.json();

                this.cars = data.cars || [];

                await this.$nextTick();

                if (dtoCarVal ) {

                    const $selCar = $('#order-car');
                    $selCar.val(dtoCarVal).trigger('change');
                    const hidden = document.getElementById('car');
                    if (hidden) hidden.value = dtoCarVal;
                    await this.handleCarChange(dtoCarVal);
                }

                if (dtoClientVal) {
                    const $selClient = $('#order-client');
                    $selClient.val(dtoClientVal).trigger('change');
                    const hidden = document.getElementById('client');
                    if (hidden) hidden.value = dtoClientVal;
                    await this.handleClientChange(dtoClientVal);

                    if (dtoCompanyVal ) {
                        const $selCompany = $('#order-company');
                        $selCompany.val(dtoCompanyVal).trigger('change');
                        const hidden = document.getElementById('company');
                        if (hidden) hidden.value = dtoCompanyVal;


                        await this.handleCompanyChange(dtoCompanyVal);}

                }
            } catch (err) {
                console.error('fetchCars error', err);
            }
        },

        async handleCarChange(id) {
            if (!id) {
                this.selectedCar = null;
                this.selectedCarData = null;
                return;
            }
            this.enableClientDropdown = true;
            const found = this.cars.find(c => String(c.id) === String(id));
            if (!found) {
                console.warn('Car not found in this.cars yet; id=', id);
                return;
            }


            this.selectedCar = found.id;
            this.selectedCarData = found;

            const hidden = document.getElementById('car');
            const dtoCar = document.getElementById('dtoCar');
            const dtoClient = document.getElementById('dtoClient');
            const dtoCompany = document.getElementById('dtoCompany');
            if (hidden) hidden.value = found.id;
            if (dtoCar) dtoCar.value = found.id;
            if (dtoClient) dtoClient.value = null;
            if (dtoCompany) dtoCompany.value = null;
            this.selectedClient =  await this.fetchClientsForCar(id);
        },


        async fetchClientsForCar(id) {
            try {


                const response = await fetch(`/car/fetch/client/${id}`);

                if (!response.ok) {
                    throw new Error('Неуспешен отговор от сървъра за клиентите');
                }
                const data = await response.json();


                this.clients = data.clients || [];
            } catch (error) {

                console.error('Грешка при зареждането на клиентите', error);
            }

        },



        async  handleClientChange(id) {

            if (!id) {
                this.selectedClient = null;
                this.selectedClientData = null;
                return;
            }
            this.enableCompanyDropdown = true;
            const found = this.clients.find(c => String(c.id) === String(id));
            if (!found) {
                console.warn('Client not found in this.cars yet; id=', id);
                return;
            }

            this.selectedClient = found.id;
            this.selectedClientData = found;


            const hidden = document.getElementById('client');
            const dtoClient = document.getElementById('dtoClient');
            const dtoCompany = document.getElementById('dtoCompany');
            if (hidden) hidden.value = id;
            if (dtoClient) dtoClient.value = id;
            if (dtoCompany) dtoCompany.value = null;
            this.selectedCompany =  await this.fetchCompaniesForClient(id);

        },


        async fetchCompaniesForClient(clientId) {
            try {

                const response = await fetch(`/client/fetch/companies/${clientId}`);

                if (!response.ok) {
                    throw new Error('Неуспешен отговор от сървъра за фирмите');
                }
                const data = await response.json();
                this.companies = data.companies || [];
            } catch (error) {
                console.error('Грешка при зареждането на фирмите', error);
            }
        },

        handleCompanyChange(id) {
            if (!id) {
                this.selectedCompany = null;
                this.selectedCompanyData = null;
                return;
            }

            const found = this.companies.find(c => String(c.id) === String(id));
            if (!found) {
                console.warn('Company not found in this.cars yet; id=', id);
                return;
            }

            this.selectedCompany = found.id;
            this.selectedCompanyData = found;

            const hidden = document.getElementById('company');
            const dtoCompany = document.getElementById('dtoCompany');
            if (hidden) hidden.value = found.id;
            if (dtoCompany) dtoCompany.value = found.id;

        }
    },

    mounted() {
        const vm = this;

        $('select.form-select').select2();

        $('select.form-select').on('select2:select', function(e) {
            const selectId = $(this).attr('id');
            const selectItem = $(this);
            const selectedVal = e.params.data.id;

            if (selectId === "order-car") {

                vm.handleCarChange(selectedVal);

            } else if (selectId === "order-client"){
                this.enableCompanyDropdown = null;
                vm.handleClientChange(selectedVal);
            }else if (selectId === "order-company"){

                vm.handleCompanyChange(selectedVal);
            }


        });


    }
});



$('select.form-select').on('select2:select', function(e) {
    const selectId = $(this).attr('data-id');
    const selectItem = $(this);
    const selected = e.params.data;
    const $option = $(this).find('option').filter(function() {

        return $(this).val() == selected.id;
    });
    $(this).find('option').removeAttr('selected')
    if ($option.length) {
        $option.attr('selected', 'selected');
    }

    $(this).trigger('change');

    const value = $(this).val();
    const price = parseFloat($option.data('price')).toFixed(2);
    const text = $option.text();


    $('#price-' + selectId).val(price).trigger('change');;

});


$('.add-item').on('click', function(e) {
    const clickedBtn = $(this).attr('id');
    let selectedItem;

    let name;
    let id;
    let item;
    let price;
    let pcs;
    if(clickedBtn === "btn-part"){
        item="part";
        price = $("#price-part").val();
        pcs = $("#count-part").val();
        selectedItem= $('#partSelect option:selected');
        name = $('#partSelect option:selected').val();
        id = $('#partSelect option:selected').attr('id');
        dataId = $('#partSelect option:selected').attr('data-id');
    }else if(clickedBtn === "btn-repair"){
        item="repair";
        price = $("#price-repair").val();

        pcs = $("#count-repair").val();
        selectedItem= $('#repair option:selected');
        name = $('#repair option:selected').val();
        id = $('#repair option:selected').attr('id');
        dataId = $('#repair option:selected').attr('data-id');
    }

    if(!id){
        Swal.fire({
            icon: 'warning',
            title: 'Няма избран елемент!',
            text: 'Моля, изберете нещо преди да продължите.'
        });
        return;
    }

    addPartOrServiceRow(item ,pcs, price, id,dataId, name);

    if(clickedBtn === "btn-part"){

        $("#partSelect").find('option').removeAttr('selected');
        $("#partSelect").val(null).trigger('change');
        $('#price-part').val(0);
        $('.count-input').val(1);
    } else if(clickedBtn === "btn-repair"){
        $("#repair").find('option').removeAttr('selected');
        $("#repair").val(null).trigger('change');
        $('#price-repair').val(0);
        $('.count-input').val(1);
    }


});


$(document).ready(function() {

    const parts = Array.from(document.querySelectorAll('#dtoParts li'))
        .map(li => ({
        id: li.getAttribute('value'),
        price: parseFloat(li.dataset.price),
        quantity: parseInt(li.dataset.quantity),
        name: li.textContent.trim()
    }));

    const repairs = Array.from(document.querySelectorAll('#dtoRepairs li'))
        .map(li => ({
        id: li.getAttribute('value'),
        price: parseFloat(li.dataset.price),
        quantity: parseInt(li.dataset.quantity),
        name: li.textContent.trim()
    }));
    if(parts.length !== 0){
        for (let i = 0; i < parts.length; i++) {
            const item = "part";
            let dataId = parts[i].id;
            let id = item + 's[' + i + ']';
            let price = parts[i].price;

            let pcs = parts[i].quantity;

            let name = parts[i].name;
            addPartOrServiceRow(item ,pcs, price, id,dataId,  name)
        }
        updateTableTotals();
    }


    if(repairs.length !== 0){

        for (let i = 0; i < repairs.length; i++) {
            const item = "repair";
            let dataId = repairs[i].id;
            let id = item + 's[' + i + ']';
            let price = repairs[i].price;
            let pcs = repairs[i].quantity;
            let name = repairs[i].name;

            addPartOrServiceRow(item ,pcs, price, id,dataId,  name)
        }
    }


})
function addPartOrServiceRow(item ,pcs, price, id, dataId, name){


    let table = $('#table-' + item);
    let rowNumber = parseInt(countRows(item));
    var newRow = $('<tr></tr>');
    newRow.append(
        '<td class="row-number">'
        +
        '<input type="hidden" name="'+ item  + 's[' + rowNumber + ']' + '.id"  value="' + dataId + '">' + '</td>' +
        '<td id="' + id + '" name="' + id + '">' + name +
        '<input type="hidden" name="'+ item  + 's[' + rowNumber + ']' + '.name"  value="' + name + '">' + '</td>' +
        '<td>' + pcs +
        '<input type="hidden" name="'+ item  + 's[' + rowNumber + ']' + '.quantity"  value="' + pcs + '">' + '</td>' +
        '<td>' + parseFloat(price).toFixed(2) +
        '<input type="hidden" name="'+ item  + 's[' + rowNumber + ']' + '.price"  value="' + parseFloat(price).toFixed(2) + '">' + '</td>' +
        '<td>' + parseFloat(pcs * price).toFixed(2) +
        '<input type="hidden" name="'+ item  + 's[' + rowNumber + ']' + '.total"  value="' + parseFloat(pcs * price).toFixed(2) + '">'+ '</td>' +
        '<td><button type="button" class="circle-btn btn-danger" data-price="' + pcs * price +'">-</button></td>'
    );

    $('#table-' + item + ' tbody').append(newRow);

    let total = $('#total-table-' + item);
    let sum = parseFloat(total.text()) || 0;
    let priceNum = parseFloat(price) || 0;
    sum = parseFloat(sum + pcs* priceNum).toFixed(2);

    total.text(sum);
    updateTableTotals();
    newRow.find('button').click(function () {
        let updatedSum = parseFloat(total.text ()) || 0;
        let priceToDecrease = parseFloat($(this).data('price')).toFixed(2) || 0;
        let finalPrice =parseFloat(updatedSum - priceToDecrease).toFixed(2)  || 0;;

        total.text(finalPrice);
        $(this).closest('tr').remove();
        updateTableTotals();
    });


}
function updateTableTotals(){

    let totalSumField =  $('#order-total');
    let TaxField =  $('#tax');
    let sumWithoutTaxField =  $('#total-without-tax');
    let discountField = $('#discount');
    let discountSum = parseFloat($('#discount-sum').val()) || 0;
    let discountPercent = parseFloat($('#discount-perc').val()) || 0;
    let partSum = parseFloat($('#total-table-part').text()) || 0;
    let repairSum = parseFloat($('#total-table-repair').text()) || 0;
    let discount;
    if(discountPercent>0){
        discount = (partSum + repairSum) * discountPercent / 100;
    } else if(discountSum>0){
        discount = discountSum;
    } else {
        discount=0;
    }
    let totalSum = parseFloat(partSum + repairSum - discount).toFixed(2) || 0;
    let sumWithoutTax = parseFloat(totalSum - totalSum*0.20).toFixed(2)  || 0 ;
    let tax = parseFloat(totalSum*0.20).toFixed(2)   || 0 ;
    $('#discount').val(parseFloat(discount).toFixed(2));
    $('#dtoSubtotal').val(sumWithoutTax);
    $('#dtoTax').val(tax);
    $('#dtoDiscount').val(parseFloat(discount).toFixed(2));
    $('#dtoTotal').val(totalSum);

    totalSumField.val(totalSum);
    sumWithoutTaxField.val(sumWithoutTax);
    sumWithoutTaxField.val(sumWithoutTax);
    TaxField.val(tax);

}
function countRows(item){

    let selector = `#table-${item} tbody tr`;
    let count = document.querySelectorAll(selector).length;
    return count;
}
function makeDiscount(){

    let discountField = $('#discount');
    let totalSum =  $('#order-total').val();

    let discountPercent = parseFloat($('#discount-percent').val());

    let discountAmount =  parseFloat($('#discount-amount').val());

    if(discountPercent >0 && discountAmount>0){
        Swal.fire({
            icon: 'warning',
            title: 'Не може да приложите едновременно две отстъпки!',
            text: 'Моля, изберете само една.'
        });
        $('#discount-percent').val(0);
        $('#dtoDiscount').val(0);
        $('#discount-percent').val(0);
        $('#dtoDiscountPercent').val(0);
        $('#discount-amount').val(0);
        $('#dtoDiscountAmount').val(0);
        return;
    }
    if(discountAmount>0){

        $('#discount-perc').val(0);
        $('#discount-sum').val(discountAmount);
        $('#dtoDiscountAmount').val(discountAmount);
        $('#discount-percent').val(0);
        $('#dtoDiscountPercent').val(0);
        $('#discount-amount').val(0);
        updateTableTotals();
    } else if (discountPercent >0 ){

        $('#discount-perc').val(discountPercent);
        $('#dtoDiscountPercent').val(discountPercent);
        $('#discount-sum').val(0);
        $('#dtoDiscountAmount').val(0);
        $('#discount-percent').val(0);
        $('#discount-amount').val(0);
        updateTableTotals();
    }

}


$('#order-car').on('select2:unselecting select2:clear', function(e) {
    ['order-client','order-car', 'order-company', 'dtoCar', 'dtoClient', 'dtoCompany'].forEach(id => {
        $('#' + id).val(null).trigger('change');
    });

    const vue = document.getElementById('order').__vue__;
    if (vue) {
        vue.selectedCar = null;
        vue.selectedClient = null;
        vue.selectedCompany = null;
        vue.selectedCarData = null;
        vue.selectedClientData = null;
        vue.selectedCompanyData = null;
        vue.enableClientDropdown = false;
        vue.enableCompanyDropdown = false;

    }
});

$('#order-client').on('select2:unselecting select2:clear', function(e) {
    ['order-client', 'order-company', 'dtoClient', 'dtoCompany'].forEach(id => {
        $('#' + id).val(null).trigger('change');
    });

    const vue = document.getElementById('order').__vue__;
    if (vue) {

        vue.selectedClient = null;
        vue.selectedCompany = null;
        vue.selectedClientData = null;
        vue.selectedCompanyData = null;
        vue.enableCompanyDropdown = false;

    }
});

$('#order-company').on('select2:unselecting select2:clear', function(e) {
    [ 'order-company', 'dtoCompany'].forEach(id => {
        $('#' + id).val(null).trigger('change');
    });

    const vue = document.getElementById('order').__vue__;
    if (vue) {

        vue.selectedClient = null;
        vue.selectedCompany = null;
        vue.selectedClientData = null;
        vue.selectedCompanyData = null;
        vue.enableCompanyDropdown = false;

    }
});
$('#partSelect').on('select2:unselecting select2:clear', function(e) {
    $('#price-part').val('').trigger('change');
    });
$('#repair').on('select2:unselecting select2:clear', function(e) {
    $('#price-repair').val('').trigger('change');
});
