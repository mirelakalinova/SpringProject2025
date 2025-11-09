
new Vue({
    el: '#app',
    data: {
        searchMake: '',
        searchModel: '',
        makes: [],
        filteredMakes: [],
        models: [],
        filteredModels: [],
        selectedMake: null,
        selectedModel: null,

    },
    created() {
        // Зареждаме всички марки
        this.fetchMakes();

    },
    methods: {
        handleCarChange(event) {
            var selectedCar = event.target.selectedOptions[0];

            if (selectedCar) {
                this.isCarSelected = true;
                this.selectedMake = null;
                this.selectedModel = null;
                this.searchMake = '';
                this.searchModel = '';
                this.filteredModels = [];
            } else {
                this.isCarSelected = false;
            }
        },

        searchMakes: _.debounce(function () {
            if (this.searchMake.length < 3) {
                this.filteredMakes = [];

                return;
            }
            this.filteredMakes = this.makes.filter(make =>
            make.name.toLowerCase().includes(this.searchMake.toLowerCase())
            );


        }, 500),

        searchModels: _.debounce(function () {
            if (this.searchModel.length < 1 || !this.selectedMake) {
                this.filteredModels = [];
                return;
            }
            this.filteredModels = this.models.filter(model =>
            model.name.toLowerCase().includes(this.searchModel.toLowerCase())
            );
        }, 500),

        // Зареждане на марките от сървъра
        async fetchMakes() {
            try {
                const response = await fetch(`http://localhost:8080/api/makes`);

                if (!response.ok) {
                    alert('Неуспешен отговор от сървъра за марки автомобили!');
//                    throw new Error('Неуспешен отговор от сървъра за марки автомобили!');
                }

                const data = await response.json();;
                this.makes = data.makes || [];
                this.filteredMakes = this.makes;
            } catch (error) {
                alert('Грешка при зареждането на марките автомобили!', error);
            }
        },


        async fetchModels() {
            if (!this.selectedMake) return;

            try {
                const response = await fetch(`http://localhost:8080/api/models/${this.selectedMake.id}`);
                if (!response.ok) {
                    throw new Error('Неуспешен отговор от сървъра за модели автомобили!');
                }
                const data = await response.json();
                this.models = data.models || []; // Задаваме моделите
                this.filteredModels = this.models; // Инициализираме филтрираните модели
            } catch (error) {
                console.error('Грешка при зареждането на моделите автомобили!', error);
            }
        },

        // Избиране на марка
        selectMake(make) {

            this.selectedMake = make; // Задаваме избраната марка
            this.searchMake = make.name; // Поставяме името на марката в полето за търсене
            this.filteredMakes = []; // Изчистваме филтрираните марки
            this.fetchModels(); // Зареждаме моделите за избраната марка
            const isEditView = window.location.pathname.startsWith('/car/edit');
            if(isEditView){
                const makeName = document.getElementById('make');
                makeName.value  = make.name;

            }
        },

        // Избиране на модел
        selectModel(model) {
            this.selectedModel = model; // Задаваме избрания модел
            this.searchModel = model.name; // Поставяме името на модела в полето за търсене
            this.filteredModels = []; // Изчистваме филтрираните модели
            const isEditView = window.location.pathname.startsWith('/car/edit');
            if(isEditView){
                const modelName = document.getElementById('model');
                modelName.value  = model.name;

            }
        }
    }
});
