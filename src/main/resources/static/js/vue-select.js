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
        dropdownMakeInitialized: false,
        dropdownModelInitialized: false
    },
    mounted() {
        document.addEventListener('click', this.onDocumentClick);
        this.$nextTick(() => {
            const hiddenMake = document.getElementById('hiddenMake');
            const hiddenModel = document.getElementById('hiddenModel');
            if (hiddenMake && hiddenMake.value) {
                this.searchMake = hiddenMake.value;
                this.dropdownMakeInitialized = true;
            }
            if (hiddenModel && hiddenModel.value) {
                this.searchModel = hiddenModel.value;
                this.dropdownModelInitialized = true;
            }
        });
    },
    beforeDestroy() {
        document.removeEventListener('click', this.onDocumentClick);
    },
    created() {
        this.fetchMakes();

    },
    methods: {
        onDocumentClick(event) {
            if ((this.$refs.makeWrapper && this.$refs.makeWrapper.contains(event.target)) ||
            (this.$refs.modelWrapper && this.$refs.modelWrapper.contains(event.target))) {
                return;
            }
            this.filteredMakes = [];
            this.filteredModels = [];
        },

        onMakeFocus() {
            if (this.dropdownMakeInitialized) {
                this.dropdownMakeInitialized = false;
            }

            const q = (this.searchMake || '').trim();
            if (!q) {
                this.filteredMakes = this.makes;
                return;
            }
            const qq = q.toLowerCase();
            this.filteredMakes = this.makes.filter(m => ((m && m.name) || '').toLowerCase().includes(qq));
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
                this.selectedMake = { id: null, name: this.searchMake };
                const makeInput = document.getElementById('make');
                if (makeInput) {
                    makeInput.value = this.searchMake;
                    makeInput.setAttribute('value', this.searchMake);
                }
            }
        },

        onModelFocus() {
            if (this.dropdownModelInitialized) {
                this.dropdownModelInitialized = false;
            }
            if (this.filteredModels && this.filteredModels.length > 0) return;
            if (this.selectedMake && this.selectedMake.id) {
                this.fetchModels();
                return;
            }
            const q = (this.searchMake || '').trim();
            if (!q) {
                this.filteredModels = [];
                return;
            }
            const found = this.makes.find(m => ((m && m.name) || '').toLowerCase() === q.toLowerCase());
            if (found && found.id) {
                this.selectedMake = found;
                this.$nextTick(() => this.fetchModels());
                return;
            }
            const partial = this.makes.find(m => ((m && m.name) || '').toLowerCase().includes(q.toLowerCase()));
            if (partial && partial.id) {
                this.selectedMake = partial;
                this.$nextTick(() => this.fetchModels());
                return;
            }
            this.filteredModels = [];
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
                this.filteredMakes = [];
            } else {
                this.isCarSelected = false;
            }
        },

        searchMakes() {
            const q = (this.searchMake || '').trim();
            if (q.length < 3) {
                this.filteredMakes = [];
                return;
            }
            const qq = q.toLowerCase();
            this.filteredMakes = this.makes.filter(make => ((make && make.name) || '').toLowerCase().includes(qq));
        },

        searchModels() {
            const q = (this.searchModel || '').trim().toLowerCase();
            if (q.length < 1 || !this.selectedMake || !this.selectedMake.id) {
                this.filteredModels = this.models && this.models.length > 0 ? this.models : [];
                return;
            }
            if (!this.models || this.models.length === 0) {
                this.fetchModels();
                return;
            }
            this.filteredModels = this.models.filter(model => ((model && model.name) || '').toLowerCase().includes(q));
        },

        async fetchMakes() {
            try {
                const response = await fetch(`http://localhost:8080/api/makes`);
                if (!response.ok) alert('Неуспешен отговор от сървъра за марки автомобили!');
                const data = await response.json();
                this.makes = data.makes || [];
                this.filteredMakes = this.makes;
            } catch (error) {
                alert('Грешка при зареждането на марките автомобили!', error);
            }
        },

        async fetchModels() {
            const makeId = this.selectedMake && this.selectedMake.id ? this.selectedMake.id : null;
            if (!makeId) return;
            try {
                const response = await fetch(`http://localhost:8080/api/models/${makeId}`);
                if (!response.ok) throw new Error('Неуспешен отговор от сървъра за модели автомобили!');
                const data = await response.json();
                this.models = data.models || [];
                if (!this.searchModel) this.filteredModels = this.models;
                else {
                    const q = (this.searchModel || '').toLowerCase().trim();
                    this.filteredModels = this.models.filter(m => ((m && m.name) || '').toLowerCase().includes(q));
                }
            } catch (error) {
                console.error('Грешка при зареждането на моделите автомобили!', error);
                this.models = [];
                this.filteredModels = [];
            }
        },

        selectMake(make) {
            this.selectedMake = make;
            this.searchMake = make.name;
            this.filteredMakes = [];
            const hiddenMake = document.getElementById('hiddenMake');
            if (hiddenMake) hiddenMake.value = make.name;
            if (this.selectedMake && this.selectedMake.id) this.$nextTick(() => this.fetchModels());
            const isEditView = window.location.pathname.startsWith('/car/edit') || window.location.pathname.startsWith('/car/add') ;

            if (isEditView) {

                if (hiddenMake) {
                    hiddenMake.value = make.name;
                    hiddenMake.setAttribute('value', make.name);
                }
            }
        },

        selectModel(model) {
            this.selectedModel = model;
            this.searchModel = model.name;
            this.filteredModels = [];
            const hiddenModel = document.getElementById('hiddenModel');
            if (hiddenModel) hiddenModel.value = model.name;
            const isEditView = window.location.pathname.startsWith('/car/edit') || window.location.pathname.startsWith('/car/add') ;

            if (isEditView) {

                if (hiddenModel) {
                    hiddenModel.value = model.name;
                    hiddenModel.setAttribute('value', model.name);
                }
            }
        }
    }
});
