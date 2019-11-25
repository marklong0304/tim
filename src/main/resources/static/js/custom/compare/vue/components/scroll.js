import {ScrollTemplate} from "../templates/scrollTemplate.js";

export default {
    template: ScrollTemplate,
    data() {
        return {};
    },

    computed:{
        showCompareBar(){
            return this.$store.getters.compareBarStatus;
        },

        initialized() {
            return this.$store.getters.initialized;
        },

        currentQuote() {
            return this.$store.getters.currentQuote;
        },

        compare() {
            return this.$store.getters.compare;
        },

        comparePlans() {
            return this.$store.getters.comparePlans;
        },

        sendFilters(){
            return this.$store.getters.sendFilters;
        },
    },

    methods:{

        // get scroll and table offset for sticky column
        getOffsetLeft(){
            if(this.initialized && this.sendFilters && this.compare){
                this.$store.dispatch({
                    type: 'offsetLeft',
                    windowOffsetLeft: document.getElementsByClassName('scrlCont')[0].scrollLeft,
                    tableOffsetLeft: document.getElementById('my-table').getBoundingClientRect().left
                });
            }
        },

        clearCheckboxes(){
            let checkboxes = document.getElementsByClassName('checkbox-recomm');
            for(let i = 0; i < checkboxes.length; ++i){
                checkboxes[i].checked = false;
            }
        },

        clearCompare(){
            this.$store.dispatch('clearCompare');
            this.clearCheckboxes();
        },

        submitCompare(){
            if (this.comparePlans.length > 1){
                this.clearCheckboxes();
                this.$store.dispatch('changeView');
                this.$store.dispatch('setComparePlans');
                window.scrollTo(0, 0);
            }
        },

        closeCompareBar(){
            this.$store.dispatch({type: 'showCompareBar', status: false});
        }
    },

    mounted(){
        window.addEventListener('resize', this.getOffsetLeft);
    },

    beforeDestroy(){
        window.removeEventListener('resize', this.getOffsetLeft);
    }
}