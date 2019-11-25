import {CompareTemplate} from "../templates/compareTemplate.js";

axios.defaults.headers.common = {
    'X-Requested-With': 'XMLHttpRequest',
    'X-CSRF-TOKEN': $('meta[name="_csrf"]')[0].content
};

export default {
    template: CompareTemplate,
    data() {
        return {
            purchaseQId: null,
            cellWidth: 175,
            cellHeight: 48,
            offset: {
                top: 0,
                left: 0
            },
            started: false,
            columnWidth: 175,
            headerHeight: 300,
            pageHeaderHeights: {
                navbar: 0,
                summary: 0,
                results: 0
            },
            windowOffsetTop: 0,
            rowsHeights: [],
            setHights: true,
            filterBtnWidth: 42,
            loadingCertificate: false,
        };
    },

    computed: {
        stickyHeaderStyles() {
            return {
                'width': `${this.activeProducts.length * this.cellWidth}px`,
                'min-width': `${this.activeProducts.length * this.cellWidth}px`,
                'max-width': `${this.activeProducts.length * this.cellWidth}px`,
                'table-layout': 'fixed',
                'top': '0px',
                'position': 'fixed',
                'margin-bottom': '0px',
                'background-color': 'white',
                'left': `${this.tableOffsetLeft}px`,
            }
        },

        stickyColumnStyles() {
            return {
                'width': `${this.columnWidth}px`,
                'min-width': `${this.columnWidth}px`,
                'max-width': `${this.columnWidth}px`,
                'table-layout': 'fixed',
                'top': `${this.offset.top}px`,
                'position': 'fixed',
                'margin-bottom': '0px',
                'background-color': 'white',
                'left': `${this.filterBtnWidth}px`,
            }
        },

        stickyCornerStyles() {
            return {
                'width': `${this.columnWidth}px`,
                'min-width': `${this.columnWidth}px`,
                'max-width': `${this.columnWidth}px`,
                'table-layout': 'fixed',
                'top': '0px',
                'position': 'fixed',
                'margin-bottom': '0px',
                'background-color': 'white',
                'left': `${this.filterBtnWidth}px`,
            }
        },

        tableStyles() {
            return {
                'width': `${this.activeProducts.length * this.cellWidth}px`,
                'min-width': `${this.activeProducts.length * this.cellWidth}px`,
                'max-width': `${this.activeProducts.length * this.cellWidth}px`,
                'table-layout': 'fixed',
                'overflow-x': 'scroll'
            }
        },

        resultsStyle() {
            return {
                'height': `${this.pageHeaderHeights.results}px`
            }
        },

        compare() {
            return this.$store.getters.compare;
        },

        comparePlans() {
            return this.$store.getters.comparePlans;
        },

        filtersWidth() {
            return this.$store.getters.filtersWidth;
        },

        activeProducts() {
            return this.$store.getters.products.filter(product => product.isActive && product.isActiveFilters);
        },

        products() {
            return this.$store.getters.products;
        },

        initialized() {
            return this.$store.getters.initialized;
        },

        groups() {
            return this.$store.getters.groups;
        },

        currentQuote() {
            return this.$store.getters.currentQuote;
        },

        requestId() {
            return this.$store.getters.requestId;
        },

        fakeBest() {
            return this.$store.getters.fakeBest;
        },

        tableOffsetLeft: {
            get() {
                return this.$store.getters.tableOffsetLeft;
            },
            set(offset) {
                this.$store.commit('SET_TABLE_OFFSET_LEFT', offset);
            }
        },

        windowOffsetLeft() {
            return this.$store.getters.windowOffsetLeft;
        },

        existPlan() {
            return this.$store.getters.existPlan;
        },

        cleanRequest() {
            return this.$store.getters.cleanRequest;
        },

        sendFilters() {
            return this.$store.getters.sendFilters;
        },

        finished() {
            return this.$store.getters.finished;
        },
    },

    methods: {

        getTableHeight() {
            this.$store.dispatch({type: 'tableHeight', height: this.$refs.tableContainer.clientHeight});
        },

        isBest(product) {
            return this.fakeBest.policyMetaUniqueCode === product.policyMetaUniqueCode;
        },

        formatPrice(value) {
            return '$' + value.toFixed(2).toString();
        },

        getPurchase(policyMetaCode, quoteRequestJson) {
            return axios.post(context + 'results/getCopyOfQuoteRequest/' + this.currentQuote, {quoteRequestJson: quoteRequestJson})
                .then(response => {
                    this.purchaseQId = response.data;
                    return axios.get(context + 'results/purchasePage/' + this.currentQuote,
                        {
                            params: {
                                uniqueCode: policyMetaCode,
                                purchaseQuoteId: this.purchaseQId,
                                purchaseRequestId: this.requestId
                            }
                        })
                })
                .then(response => {
                    window.location = context + 'results/purchasePage/' + this.currentQuote
                        + '?uniqueCode=' + policyMetaCode
                        + '&purchaseQuoteId=' + this.purchaseQId
                        + '&purchaseRequestId=' + this.requestId;
                });
        },

        hideProduct(policyCode) {
            this.$store.dispatch({type: 'hideProduct', policyCode: policyCode});
        },

        openCategoryDetails(policyMetaId, categoryId) {
            axios.post(context + 'details/' + policyMetaId + '/' + categoryId)
                .then(response => {
                    const modalInfo = {
                        category: response.data.category,
                        content: response.data.content,
                        vendorCode: 'https://d3u70gwj4bg98w.cloudfront.net/vendorLogo/get/' + response.data.vendorCode + '.png',
                        policyMetaName: response.data.policyMetaName
                    };
                    this.$eventHub.$emit('getModalInfo', modalInfo);
                });
        },

        canHide() {
            return this.products.filter(product => product.isActive && product.isActiveFilters).length > 2;
        },

        getOffsetLeft() {
            if (this.initialized && this.sendFilters) {
                this.offset.left = this.$refs.tableContainer.getBoundingClientRect().left;
            }
        },

        getOffsetTop() {
            if (this.initialized && this.sendFilters) {
                this.offset.top = this.$refs.tableContainer.getBoundingClientRect().top;
            }
            this.windowOffsetTop = window.scrollY;
        },

        showColumn() {
            return this.windowOffsetLeft > this.tableOffsetLeft + this.filtersWidth;
        },

        showHeader() {
            return this.windowOffsetTop > this.offset.top + this.headerHeight
                + (this.pageHeaderHeights.summary + this.pageHeaderHeights.navbar + this.pageHeaderHeights.results);
        },

        showFilterBtn() {
            return this.windowOffsetLeft > this.tableOffsetLeft + this.filtersWidth;
        },

        getPageHeaderHeights() {
            this.pageHeaderHeights.summary = document.getElementsByClassName('summary')[0].clientHeight;
            this.pageHeaderHeights.navbar = document.getElementsByClassName('navigation')[0].clientHeight;
            this.pageHeaderHeights.results = document.getElementsByClassName('results')[0].clientHeight;
            if (typeof window.orientation !== "undefined" || navigator.userAgent.indexOf('IEMobile') !== -1) {
                document.getElementsByClassName('summary')[0].style.height = `${document.getElementsByClassName('pattern')[0].clientHeight}px`;
            }
        },

        changeCompareList(productCode) {
            const status = document.getElementsByName(productCode)[0].checked;
            this.$store.dispatch({
                type: 'changeCompareList',
                product: this.products.find(product => product.policyMetaUniqueCode === productCode),
                status: status
            });
            if (!status) {
                this.$store.dispatch({type: 'showCompareBar', status: true});
            }

        },

        updateHeights() {
            let stickyCells = document.querySelectorAll('.sticky-column>tbody>tr');
            let firstColCells = document.querySelectorAll('.col-0');
            const offset = 2;
            stickyCells.forEach(function (val, key) {
                val.style.height = `${firstColCells[key + offset].offsetHeight}px`;
            });
        },

        parseLink(certificateLink) {
            if (certificateLink.match(/travelinsured/g) !== null) {
                let target = event.target;
                target.style.display = 'none';
                target.nextElementSibling.style.display = 'block';
                axios.get(certificateLink,
                    {
                        responseType: 'blob',
                        headers: {
                            'Accept-Language': 'en-US,en;q=0.9',
                            'Accept': 'application/pdf',
                        }
                    })
                    .then((response) => {
                        target.style.display = 'block';
                        target.nextElementSibling.style.display = 'none';
                        const url = window.URL.createObjectURL(response.data);
                        window.open(url);
                    })
                    .catch((error) =>
                        console.log(error)
                    );
            } else {
                window.open(certificateLink);
            }
        },
    },

    updated() {
        this.getPageHeaderHeights();
        if (this.initialized && this.sendFilters) {
            this.getTableHeight();
            window.dispatchEvent(new Event('change'));
            this.updateHeights();
        }
    },


    mounted() {
        this.tableOffsetLeft = this.filtersWidth;
        this.getPageHeaderHeights();

        this.$store.dispatch('load');

        window.addEventListener('scroll', this.getOffsetTop);
        window.addEventListener('resize', this.getOffsetTop);
        window.addEventListener('change', this.getOffsetTop);

        window.addEventListener('resize', this.getPageHeaderHeights);
    },

    beforeDestroy() {
        window.removeEventListener('scroll', this.getOffsetTop);
        window.removeEventListener('resize', this.getOffsetTop);
        window.removeEventListener('change', this.getOffsetTop);

        window.removeEventListener('resize', this.getPageHeaderHeights);
    }
}