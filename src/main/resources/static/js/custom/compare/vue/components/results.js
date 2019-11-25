import {ResultsTemplate} from "../templates/resultsTemplate.js";

export default {
    template: ResultsTemplate,
    data() {
        return {
            sortOrder: 'LTH',
            resultsHeight: 104,
            resultsW: false
        };
    },

    computed: {

        compare() {
            return this.$store.getters.compare;
        },

        initialized() {
            return this.$store.getters.initialized;
        },

        activeProductsSize() {
            return this.$store.getters.products.filter(product => product.isActive && product.isActiveFilters).length;
        },

        hiddenProducts() {
            return this.$store.getters.hiddenProducts;
        },

        currentQuote() {
            return this.$store.getters.currentQuote;
        },

        appliedFilters(){
            return this.$store.getters.appliedFilters;
        },

        resultsStyle(){
            return {
                'bottom' :  `-${this.resultsHeight}px`
            }
        },

        filtersWidth() {
            return this.$store.getters.filtersWidth;
        },

        tableOffsetLeft: {
            get() {
                return this.$store.getters.tableOffsetLeft;
            },
            set(offset) {
                this.$store.commit('SET_TABLE_OFFSET_LEFT', offset);
            }
        },

        windowOffsetLeft: {
            get() {
                return this.$store.getters.windowOffsetLeft;
            },
            set(offset) {
                this.$store.commit('SET_WINDOW_OFFSET_LEFT', offset);
            }
        },

        sendFilters(){
            return this.$store.getters.sendFilters;
        },
    },

    methods: {

        // list view <-> compare all
        changeView(){
            if (this.compare) {
                // clear array with plans to compare
                this.$store.dispatch('clearCompare');
            } else {
                // clear 'add to compare' checkboxes
                let checkboxes = document.getElementsByClassName('checkbox-recomm');
                for(let i = 0; i < checkboxes.length; ++i){
                    checkboxes[i].checked = false;
                }
            }
            this.$store.dispatch('changeView');

            // reset scroll offsets
            this.tableOffsetLeft = this.filtersWidth;
            this.windowOffsetLeft = 0;
        },

        // for fake results block
        getResultsHeight(){
            this.resultsHeight = document.getElementsByClassName('results')[0].clientHeight;
            this.$store.dispatch({ type: 'resultsHeight', height: this.resultsHeight });
            if (typeof window.orientation !== "undefined" || navigator.userAgent.indexOf('IEMobile') !== -1) {
                $('.summary .results').offset($('.fake-results').offset())
            }
        },

        getResultsWidth() {
            this.resultsW = document.getElementById('my-table').getBoundingClientRect().width >
              (document.documentElement.clientWidth - document.getElementById('my-table').getBoundingClientRect().left);
        },

        restoreHiddenProducts() {
            if (this.hiddenProducts.length !== 0) {
                this.$store.dispatch('restoreAllHidden');
            }
        },

        restoreLastHidden() {
            if (this.hiddenProducts.length !== 0) {
                this.$store.dispatch('restoreLastHidden');
            }
        },

        changeOrder() {
            this.$store.dispatch({ type: 'changeOrder', order: this.sortOrder });
        },

        closeAll() {
            this.$store.dispatch('reset');
        },

        closeFilter(filterCode) {
            this.$store.dispatch({ type: 'closeFilter', code: filterCode });
        },

        easeInOutQuad(t, b, c, d) {
            t /= d / 2;
            if (t < 1) return c / 2 * t * t + b;
            t--;
            return -c / 2 * (t * (t - 2) - 1) + b;
        },

        showMorePlans() {
            // alert( document.documentElement.clientWidth );
            let start = document.getElementsByClassName('scrlCont')[0].scrollLeft,
              change = (document.documentElement.clientWidth - document.getElementById('my-table').getBoundingClientRect().left) - start,
              currentTime = 0,
              increment = 50,
              duration = 300;

            let animatedScroll = () => {
                currentTime += increment;
                document.getElementsByClassName('scrlCont')[0].scrollLeft = this.easeInOutQuad(currentTime, start, change, duration);
                document.getElementsByClassName('fScrollCont')[0].scrollLeft = this.easeInOutQuad(currentTime, start, change, duration);
                if (currentTime < duration) {
                    setTimeout(animatedScroll, increment);
                }
            };
            let removeMorePlansButton = () => {
                if (Math.floor(document.getElementById('my-table').getBoundingClientRect().width - (document.documentElement.clientWidth - document.getElementById('my-table').getBoundingClientRect().left)) !== 0)  {
                    document.getElementById('morePlans').style.display = 'block';
                } else {
                    document.getElementById('morePlans').style.display = 'none';
                }
            };
            animatedScroll();
            setTimeout(removeMorePlansButton, 300);

        },


    },

    updated() {
        this.getResultsHeight();
        this.getResultsWidth();
    },

    mounted() {
        this.getResultsHeight();
        this.getResultsWidth();
        window.addEventListener('resize', this.getResultsHeight);
        window.addEventListener('resize', this.getResultsWidth);
    },

    beforeDestroy() {
        window.removeEventListener('resize', this.getResultsHeight);
        window.removeEventListener('resize', this.getResultsWidth);
    }
}