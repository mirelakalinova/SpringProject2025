new Vue({
    el: '#order',
    data: {
        cars: [], // Тук ще съхраняваме всички коли
        clients: [], // Тук ще съхраняваме клиентите
        companies: [], // Тук ще съхраняваме фирмите
        selectedCar: null, // Избрана кола
        selectedClient: null, // Избран клиент
        selectedCompany: null, // Избрана фирма
        selectedCarData: null,
        selectedClientData: null,
        selectedCompanyData: null,
        enableClientDropdown: null,
        enableCompanyDropdown: null,
    },


    created() {
        this.fetchCars();
        //        this.fetchClients();
        //			this.fetchCompanies();// Зареждаме автомобилите
    },

    methods: {
        async fetchCars() {
            const dtoCarVal = document.getElementById('dtoCar')?.value || '';
            const dtoClientVal = document.getElementById('dtoClient')?.value || '';
            const dtoCompanyVal = document.getElementById('dtoCompany')?.value || '';
            try {
                const res = await fetch('/car/fetch/cars'); // коригирай URL ако трябва
                if (!res.ok) throw new Error('fetch cars failed');
                const data = await res.json();
                // попълваме state
                this.cars = data.cars || [];

                // след като cars са в state, чакаме Vue да рендерира options
                await this.$nextTick();

                // ако имаме dtoCarVal > 0, селектираме я и извикваме handler
                if (dtoCarVal ) {
                    //                    console.log('Found dtoCar:', dtoCarVal);
                    // 1) селектираме select (ако ползваш select2 - сетни и trigger)
                    const $selCar = $('#order-car');
                    $selCar.val(dtoCarVal).trigger('change'); // ако няма select2 .trigger('change') е безвредно
                    // 2) сетваме hidden input за submit
                    const hidden = document.getElementById('car');
                    if (hidden) hidden.value = dtoCarVal;
                    // 3) извикваме обработчика на Vue
                    await this.handleCarChange(dtoCarVal);
                }
                //                console.log("dtoClientVal fetchCars =====>>>>>");
                //                console.log(dtoClientVal );
                if (dtoClientVal) {
                    //                    console.log('Found dtoClientVal:', dtoClientVal);
                    // 1) селектираме select (ако ползваш select2 - сетни и trigger)
                    const $selClient = $('#order-client');
                    $selClient.val(dtoClientVal).trigger('change'); // ако няма select2 .trigger('change') е безвредно
                    // 2) сетваме hidden input за submit
                    const hidden = document.getElementById('client');
                    if (hidden) hidden.value = dtoClientVal;

                    //                    console.log("this.clients if (dtoClientVal && Number(dtoClientVal) > 0) { ===== >>> ")
                    //                    console.log(this.clients)
                    // 3) извикваме обработчика на Vue
                    await this.handleClientChange(dtoClientVal);

                    if (dtoCompanyVal ) {
                        console.log('========================');
                        console.log('Found dtoCompanyVal:', dtoCompanyVal);
                        // 1) селектираме select (ако ползваш select2 - сетни и trigger)
                        const $selCompany = $('#order-company');
                        $selCompany.val(dtoCompanyVal).trigger('change'); // ако няма select2 .trigger('change') е безвредно
                        // 2) сетваме hidden input за submit
                        const hidden = document.getElementById('company');
                        if (hidden) hidden.value = dtoCompanyVal;

                        console.log("this.companies  if (dtoCompanyVal && Number(dtoCompanyVal) > 0) {{ ===== >>> ")
                        console.log(this.clients)
                        // 3) извикваме обработчика на Vue
                        await this.handleCompanyChange(dtoCompanyVal);}

                }
            } catch (err) {
                console.error('fetchCars error', err);
            }
        },

        // handleCarChange - намира колата в this.cars и прави следващите стъпки
        async handleCarChange(id) {
            //            console.log('handleCarChange called with id=', id);
            if (!id) {
                this.selectedCar = null;
                this.selectedCarData = null;
                return;
            }
            this.enableClientDropdown = true;
            // намери колата в локалния масив
            const found = this.cars.find(c => String(c.id) === String(id));
            if (!found) {
                console.warn('Car not found in this.cars yet; id=', id);
                // опция: можеш да заредиш единично /car/{id} от сървъра тук
                return;
            }

            // сетваме state
            this.selectedCar = found.id;
            this.selectedCarData = found;

            // сетваме hidden полето за формата
            const hidden = document.getElementById('car');
            if (hidden) hidden.value = found.id;
            this.selectedClient =  await this.fetchClientsForCar(id); // Зареждаме клиента за този автомобил
            // (пример) след това зареждаме клиенти за тая кола
            // await this.fetchClientsForCar(found.id);

            //            console.log('selectedClient set ====>:', this.selectedCarData);
            //            console.log('selectedCarData set:', this.selectedCarData);
        },



        // Зареждаме клиентите, свързани с автомобила (чрез carId)
        async fetchClientsForCar(id) {
            try {


                const response = await fetch(`/car/fetch/client/${id}`); // Тук подаваме carId


                if (!response.ok) {
                    throw new Error('Неуспешен отговор от сървъра за клиентите');
                }
                const data = await response.json();
                //                console.log("data in fetch clients for car =>>>>>");
                //                console.log(data);

                this.clients = data.clients || []; // Зареждаме клиентите за даден автомобил
                //                console.log("clients fetchClientsForCar ====>");
                //                console.log(this.clients);
            } catch (error) {

                console.error('Грешка при зареждането на клиентите', error);
            }

        },


        // Функция при избор на клиент
        async  handleClientChange(id) {

            //            console.log('handleClientChange called with id=', id);
            if (!id) {
                this.selectedClient = null;
                this.selectedClientData = null;
                return;
            }
            this.enableCompanyDropdown = true;
            console.log("this.clients handleClientChange ===== >>> ")
            console.log(this.clients)
            //            console.log("this.clients[0] handleClientChange =====>>")
            //            console.log(this.clients[0])
            // намери клиента в локалния масив
            const found = this.clients.find(c => String(c.id) === String(id));
            //            console.log("found handleClientChange ===== >>>");
            //            console.log(found);
            if (!found) {
                console.warn('Client not found in this.cars yet; id=', id);
                // опция: можеш да заредиш единично /car/{id} от сървъра тук
                return;
            }

            // сетваме state
            this.selectedClient = found.id;
            this.selectedClientData = found;

            // сетваме hidden полето за формата
            const hidden = document.getElementById('client');
            if (hidden) hidden.value = id;
            this.selectedCompany =  await this.fetchCompaniesForClient(id); // Зареждаме компаниитеза този клиент

            console.log('selectedClientData set:', this.selectedClientData);
            //
            //
            //
            //            this.enableCompanyDropdown = false;
            //
            //            this.selectedCompany = null;
            //            this.selectedCompanyData = null;
            //
            //            this.selectedCompany = null;
            //            this.$nextTick(() => {
            //                $('#order-company').val(null).trigger('change');
            //            });
            //
            //
            //            selectedClient = this.clients[0];
            //            if (id) {
            //                this.enableCompanyDropdown = true;
            //                this.selectedClientData = selectedClient;
            //                $('#client').val(id);
            //                this.fetchCompaniesForClient(id); // Зареждаме фирмите за този клиент
            //            } else {
            //                console.error("Не е намерен клиент с id:", this.selectedClient);
            //            }


        },

        // Зареждаме фирмите за избрания клиент
        async fetchCompaniesForClient(clientId) {
            try {

                const response = await fetch(`/client/fetch/companies/${clientId}`);

                if (!response.ok) {
                    throw new Error('Неуспешен отговор от сървъра за фирмите');
                }
                const data = await response.json();
                //                console.log("data in fetch companies for clients =>>>>>");
                //                console.log(data);
                this.companies = data.companies || [];
                //                console.log("companies in fetch companies for clients =>>>>>");
                //                console.log(companies);
            } catch (error) {
                console.error('Грешка при зареждането на фирмите', error);
            }
        },

        // Функция при избор на фирма
        handleCompanyChange(id) {

            console.log('handleCоompanyChange called with id=', id);
            if (!id) {
                this.selectedCompany = null;
                this.selectedCompanyData = null;
                return;
            }

            const found = this.companies.find(c => String(c.id) === String(id));
            if (!found) {
                console.warn('Company not found in this.cars yet; id=', id);
                // опция: можеш да заредиш единично /car/{id} от сървъра тук
                return;
            }

            // сетваме state
            this.selectedCompany = found.id;
            this.selectedCompanyData = found;

            // сетваме hidden полето за формата
            const hidden = document.getElementById('company');
            if (hidden) hidden.value = found.id;
            //            this.selectedClient =  this.fetchClientsForCar(id); // Зареждаме клиента за този автомобил
            // (пример) след това зареждаме клиенти за тая кола
            // await this.fetchClientsForCar(found.id);

            console.log('selectedCompanyData set:', this.selectedCompanyData);


            //            if (id) {
            //                selectedCompany = this.companies.find(c => c.id == id);
            //                this.selectedCompanyData = selectedCompany;
            //                $('#company').val(id);
            //            } else {
            //                console.error("Не е намерена фирма с id:", this.selectedCompany);
            //            }
        }
    },

    mounted() {
        const vm = this;

        $('select.form-select').select2();

        $('select.form-select').on('select2:select', function(e) {
            const selectId = $(this).attr('id');          // id на селекта
            const selectItem = $(this);          // id на селекта
            const selectedVal = e.params.data.id;         // стойността на избрания option

            if (selectId === "order-car") {

                vm.handleCarChange(selectedVal);     // извикваме Vue метода

            } else if (selectId === "order-client"){
                this.enableCompanyDropdown = null;
                vm.handleClientChange(selectedVal);
            }else if (selectId === "order-company"){

                vm.handleCompanyChange(selectedVal);
            }


        });


    }
});


//part & repair
$('select.form-select').on('select2:select', function(e) {
    const selectId = $(this).attr('data-id');
    const selectItem = $(this);
    const selected = e.params.data; // данни от Select2
    //        console.log("selected.id");
    //        console.log(selected.id);
    console.log(selectId);
    // маркираме оригиналния option
    const $option = $(this).find('option').filter(function() {

        return $(this).val() == selected.id;
    });
    $(this).find('option').removeAttr('selected')
    if ($option.length) {
        //        console.log("Влиза да селектира опцията в оригиналния селект");
        $option.attr('selected', 'selected');
    }

    // Обновяваме UI на Select2
    $(this).trigger('change');

    // Вече можеш да обработваш стойностите
    const value = $(this).val();                    // value на избрания option
    const price = parseFloat($option.data('price')).toFixed(2);           // data-price
    const text = $option.text();                   // видим текст

    //    console.log('Value:', value, 'Text:', text, 'Price:', price);


    $('#price-' + selectId).val(price).trigger('change');;

});

//add part and reapir to order list
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
    //    console.log(clickedBtn);
    //    console.log("Option selected Value");
    //    console.log(name);
    //    console.log("Option selected id");
    //    console.log(id);
    //    console.log("=========");
    //    console.log("Option selected dataId");
    //    console.log(dataId);
    //    console.log("Option selected price");
    //    console.log(price);
    //    console.log("Option selected pcs");
    //    console.log(pcs);

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






    // todo -> да добавя зачистване на форми и селекти
});


//Добавяне на редова след binding result has errors
$(document).ready(function() {

    const parts = Array.from(document.querySelectorAll('#dtoParts li'))
        .map(li => ({
        id: li.getAttribute('value'),
        price: parseFloat(li.dataset.price),
        quantity: parseInt(li.dataset.quantity),
        name: li.textContent.trim()
    }));

    //    console.log("parts ===========>");
    //    console.log(parts);
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
    //    console.log("repairs ===========>");
    //    console.log(repairs);


})
function addPartOrServiceRow(item ,pcs, price, id, dataId, name){

    //    console.log("item is ==========");
    //    console.log(item);
    //да вземем релевантната таблица
    let table = $('#table-' + item);
    let rowNumber = parseInt(countRows(item));
    //    console.log("rowNumber is ==========");
    //    console.log(rowNumber);
    console.log("ID is ==========");
    console.log(id);
    console.log("DATAID is ==========");
    console.log(dataId);

    //функция за смятане на тотал на всяка таблица...



    //да добавим нов ред
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
    console.log("Влиза при сумите");
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


    totalSumField.val(totalSum);
    sumWithoutTaxField.val(sumWithoutTax);
    sumWithoutTaxField.val(sumWithoutTax);
    TaxField.val(tax);

}
function countRows(item){

    let selector = `#table-${item} tbody tr`;
    let count = document.querySelectorAll(selector).length;
    console.log("Count e ===== " + count);
    console.log("selector e ===== " + selector);
    return count;
}
function makeDiscount(){

    console.log("Влиза при discount");
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
        $('#discount-amount').val(0);
        return;
    }
    if(discountAmount>0){

        $('#discount-perc').val(0);
        $('#discount-sum').val(discountAmount);
        $('#discount-percent').val(0);
        $('#discount-amount').val(0);
        updateTableTotals();
    } else if (discountPercent >0 ){

        $('#discount-perc').val(discountPercent);
        $('#discount-sum').val(0);
        $('#discount-percent').val(0);
        $('#discount-amount').val(0);
        updateTableTotals();
    }

}


$('#order-car').on('select2:unselecting select2:clear', function(e) {
    ['order-client','order-car', 'order-company'].forEach(id => {
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
    ['order-client', 'order-company'].forEach(id => {
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



//
//$('form').on('submit', function(e){
//    console.log($(this).serializeArray()); // виж кои имена/стойности се пращат
//    $('[name^="parts"], [name^="repairs"]').each(function(){
//        console.log(this.tagName, this.name, this.value, this);
//    });
//         e.preventDefault(); // временна забрана на submit ако искаш да инспектираш
//});