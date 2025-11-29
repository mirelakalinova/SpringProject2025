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
    mounted() {
        document.addEventListener('click', this.onDocumentClick);

    },
    beforeDestroy() {
        document.removeEventListener('click', this.onDocumentClick);
    },
    created() {
        this.fetchMakes();

    },
    methods: {
        onDocumentClick(event) {
            if (this.$refs.makeWrapper && this.$refs.makeWrapper.contains(event.target)) {
                return;
            }

            this.filteredMakes = [];
            this.filteredModels =[];

        },
        onMakeBlur() {
            if (this.searchMake && (!this.selectedMake || this.selectedMake.name !== this.searchMake)) {
                this.selectedMake = { id: null, name: this.searchMake };
            }
            this.filteredMakes = [];

        },

        handleMakeBlur() {
            this.filteredMakes = [];
            if (!this.selectedMake || this.selectedMake.name !== this.searchMake) {
                console.log("влиза");
                this.selectedMake = { id: null, name: this.searchMake };

                const makeInput = document.getElementById('make');
                if (makeInput) {
                    makeInput.value = this.searchMake;
                    makeInput.setAttribute('value', this.searchMake);
                }

            }
        },

        onModelBlur() {
            if (this.searchModel && (!this.selectedModel || this.selectedModel.name !== this.searchModel)) {
                this.selectedModel = { id: null, name: this.searchModel };
            }
            this.filteredModels = [];

        },

        handleModelBlur() {
            this.filteredModels = [];
            if (!this.selectedModel || this.selectedModel.name !== this.searchModel) {
                this.selectedModel = { id: null, name: this.searchModel };

                const modelInput = document.getElementById('model');
                if (modelInput) {
                    modelInput.value = this.searchModel;
                    modelInput.setAttribute('value', this.searchModel);
                }

            }
        },

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

        async fetchMakes() {
            try {
                const response = await fetch(`http://localhost:8080/api/makes`);

                if (!response.ok) {
                    alert('Неуспешен отговор от сървъра за марки автомобили!');
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
                this.models = data.models || [];
                this.filteredModels = this.models;
            } catch (error) {
                console.error('Грешка при зареждането на моделите автомобили!', error);
            }
        },


        selectMake(make) {

            this.selectedMake = make;
            this.searchMake = make.name;
            this.filteredMakes = [];
            this.fetchModels();
            const isEditView = window.location.pathname.startsWith('/car/edit') || window.location.pathname.startsWith('/car/add')
            || window.location.pathname.startsWith('/client/add') ;
            if(isEditView){
                const makeName = document.getElementById('make');
                makeName.value  = make.name;
                makeName.setAttribute('value', make.name);

            }
        },


        selectModel(model) {
            this.selectedModel = model;
            this.searchModel = model.name;
            this.filteredModels = [];
            const isEditView = window.location.pathname.startsWith('/car/edit');
            if(isEditView){
                const modelName = document.getElementById('model');
                modelName.value  = model.name;
                modelName.setAttribute('value', model.name);

            }
        }
    }
});





