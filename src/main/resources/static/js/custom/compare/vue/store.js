const store = new Vuex.Store({
        state: {
            products: [],
            bestPlanCode: '',
            plans: '',
            currentQuote: '',
            requestId: '',
            groups: [],
            initialized: false,
            categories: [],
            zeroCost: false,
            planType: 'COMPREHENSIVE',
            hiddenProducts: [],
            appliedFilters: [],
            filterCounters: [],
            fakeBest: '',
            finished: false,
            order: 'LTH',
            disableCancellation: true,
            heights: {
                results: 0,
                table: 0,
                filters: 0,
                presetFilters: 1500,
            },
            filtersWidth: 0,
            sticky: null,
            windowOffsetLeft: 0,
            tableOffsetLeft: 0,
            compare: true,

            cleanRequest: true,
            existPlan: false,
            comparePlans: [],
            compareBarStatus: false,

            sendFilters: false,

            stopFetching: false,
        },

        getters: {
            finished(state) {
                return state.finished;
            },

            sendFilters(state) {
                return state.sendFilters;
            },

            compareBarStatus(state) {
                return state.compareBarStatus;
            },

            comparePlans(state) {
                return state.comparePlans;
            },

            existPlan(state) {
                return state.existPlan;
            },

            cleanRequest(state) {
                return state.cleanRequest;
            },

            compare(state) {
                return state.compare;
            },

            windowOffsetLeft(state) {
                return state.windowOffsetLeft;
            },

            tableOffsetLeft(state) {
                return state.tableOffsetLeft;
            },

            filtersWidth: (state) => {
                return state.filtersWidth;
            },

            filtersHeight: (state) => {
                return state.heights.filters;
            },

            products: (state) => {
                return state.products;
            },

            categories: (state) => {
                return state.categories;
            },

            initialized: (state) => {
                return state.initialized;
            },

            groups: (state) => {
                return state.groups;
            },

            currentQuote: (state) => {
                return state.currentQuote;
            },

            hiddenProducts: (state) => {
                return state.hiddenProducts;
            },

            zeroCost: (state) => {
                return state.zeroCost;
            },

            planType: (state) => {
                return state.planType;
            },

            appliedFilters: (state) => {
                return state.appliedFilters;
            },

            requestId: (state) => {
                return state.requestId;
            },

            filterCounters: (state) => {
                return state.filterCounters;
            },

            fakeBest: (state) => {
                return state.fakeBest;
            },

            disableCancellation: (state) => {
                return state.disableCancellation;
            }

        },

        actions: {
            setFiltersPresets({commit, dispatch, state}, payload) {
                commit('SET_GROUPS', payload.presetGroups);
                commit('SET_CATEGORIES', payload.presetCategories);
            },

            load({commit, dispatch, state}) {
                if (typeof window.orientation !== "undefined" || navigator.userAgent.indexOf('IEMobile') !== -1) {
                    document.getElementsByClassName('summary')[0].style.height = `${document.getElementsByClassName('pattern')[0].clientHeight}px`;
                    commit('SET_VIEW', false);
                }
                commit('SET_STOP_FETCHING', false);
                commit('SET_QUOTE', document.getElementById('quoteId').value);
                axios.post(context + 'comparePlans/baseResults/' + state.currentQuote,
                    null, {params: {plans: state.plans, bestPlanCode: state.bestPlanCode}})
                    .then(response => {
                        commit('SET_REQUEST_ID', response.data.requestId);
                        document.getElementById('requestId').value = response.data.requestId;
                        document.getElementById('editQuote').classList.remove('disabled-link');
                        if (sessionStorage.getItem(`products_${state.currentQuote}_${state.planType}`) !== null
                            && sessionStorage.getItem(`categories_${state.currentQuote}_${state.planType}`) !== null
                            && sessionStorage.getItem(`groups_${state.currentQuote}_${state.planType}`) !== null) {
                            dispatch('restoreFromStorage');
                        } else {
                            dispatch({type: 'fetchProducts', action: 'load'});
                        }
                    });
            },

            fetchProducts({commit, dispatch, state}, payload) {
                axios.get(context + 'comparePlans/preparedResults/' + state.currentQuote + '/' + state.requestId,
                    {params: {bestPlanCode: state.bestPlanCode}})
                    .then(response => {
                        if (!state.stopFetching) {
                            commit('SET_DISABLE_CANCELLATION', false);

                            // set products
                            response.data.products.forEach(product => {
                                product.isActive = !state.hiddenProducts.map(hidden => hidden.policyMetaUniqueCode).includes(product.policyMetaUniqueCode);
                                product.isActiveFilters = true;
                            });
                            commit('SET_PRODUCTS', response.data.products);
                            commit('SET_EXIST_PLAN');

                            // set groups
                            if (!state.initialized) {
                                response.data.jsonGroups.forEach(group => {
                                    group.isActive = true;
                                    group.isClosed = false;
                                });
                                commit('SET_GROUPS', response.data.jsonGroups);
                            }

                            // set categories
                            response.data.categories.forEach(category => {
                                category.isClosed = true;
                                category.checked = state.appliedFilters.map(filter => filter.code).includes(category.code);
                                category.selected = state.appliedFilters.map(filter => filter.code).includes(category.code)
                                    ? state.appliedFilters.find(filter => filter.code === category.code).selected
                                    : '-1';
                                category.counter = category.countAfterEnabled;
                                category.isUpsale = state.products.map(product => product.categoryValues)
                                    .map(categories => categories[category.code])
                                    .filter(info => category.type !== 'CATALOG'
                                        && (info.value === 'Optional' || info.value.includes('-') && info.value.length > 1))
                                    .length > 0;
                            });
                            commit('SET_CATEGORIES', response.data.categories);

                            commit('APPLY_FILTERS');
                            commit('CHANGE_COUNTERS');
                            commit('SET_FAKE_BEST');
                            commit('SET_CLEAN_REQUEST');
                            dispatch('changeOrder', {order: state.order});

                            commit('SET_ZERO_COST', state.zeroCost);
                            commit('SET_PLAN_TYPE', state.planType);

                            commit('SET_SEND_FILTERS', true);
                            commit('SET_INITIALIZED', true);
                            commit('SET_FINISHED', response.data.finished);
                            if (!response.data.finished) {
                                dispatch({type: 'fetchProducts', action: payload.action});
                            }
                        }
                    })
                    .then(() => {
                        if (state.finished && (payload.action === 'load' || payload.action === 'changePlanType')) {
                            sessionStorage.setItem(`products_${state.currentQuote}_${state.planType}`, JSON.stringify(state.products));
                            sessionStorage.setItem(`categories_${state.currentQuote}_${state.planType}`, JSON.stringify(state.categories));
                            sessionStorage.setItem(`groups_${state.currentQuote}_${state.planType}`, JSON.stringify(state.groups));
                        }
                    })
            },

            changePlanType({commit, dispatch, state}, payload) {
                commit('RESET_CATEGORIES');
                commit('SET_STOP_FETCHING', false);
                commit('RESET');
                commit('RESTORE_ALL_HIDDEN');
                commit('RESET_PRODUCTS');

                commit('SET_DISABLE_CANCELLATION', true);
                commit('SET_INITIALIZED', false);
                document.getElementById('editQuote').classList.add('disabled-link');

                axios.post(context + 'results/stopRequest/' + state.requestId)
                    .then(response => {
                        commit('SET_PLAN_TYPE', payload.value);
                        axios.post(context + 'results/changePlanType/' + state.currentQuote,
                            null, {params: {requestId: state.requestId, planType: state.planType}})
                            .then(response => {
                                commit('SET_REQUEST_ID', response.data.requestId);
                                document.getElementById('requestId').value = response.data.requestId;
                                document.getElementById('editQuote').classList.remove('disabled-link');
                                if (sessionStorage.getItem(`products_${state.currentQuote}_${state.planType}`) !== null
                                    && sessionStorage.getItem(`categories_${state.currentQuote}_${state.planType}`) !== null
                                    && sessionStorage.getItem(`groups_${state.currentQuote}_${state.planType}`) !== null) {
                                    dispatch('restoreFromStorage');
                                } else {
                                    dispatch({type: 'fetchProducts', action: 'changePlanType'});
                                }
                            })
                    })
            },

            updateCategories({commit, dispatch, state}, payload) {
                commit('RESTORE_ALL_HIDDEN');
                commit('SET_STOP_FETCHING', false);
                if (payload.action !== 'reset') {
                    commit('RESET_PRODUCTS');
                    commit('RESET_COUNTERS');

                    commit('SET_DISABLE_CANCELLATION', true);
                    commit('SET_SEND_FILTERS', false);
                    document.getElementById('editQuote').classList.add('disabled-link');
                }

                axios.post(context + 'results/stopRequest/' + state.requestId)
                    .then(response => {
                        let params = {plans: state.plans, bestPlanCode: state.bestPlanCode};
                        state.appliedFilters.forEach(category => {
                            category.type === 'CATALOG' ? params[category.code] = category.selected : params[category.code] = category.name;
                        });
                        axios.post(context + 'comparePlans/updateCategories/' + state.currentQuote,
                            null, {params: params})
                            .then(response => {
                                commit('SET_REQUEST_ID', response.data.requestId);
                                document.getElementById('requestId').value = response.data.requestId;
                                document.getElementById('editQuote').classList.remove('disabled-link');
                                if (payload.action === 'reset' && sessionStorage.getItem(`products_${state.currentQuote}_${state.planType}`) !== null
                                    && sessionStorage.getItem(`categories_${state.currentQuote}_${state.planType}`) !== null
                                    && sessionStorage.getItem(`groups_${state.currentQuote}_${state.planType}`) !== null) {
                                    commit('SET_STOP_FETCHING', true);
                                    dispatch('restoreFromStorage');
                                } else {
                                    dispatch({type: 'fetchProducts', action: 'updateCategories'});
                                }
                            })
                    })
            },

            restoreFromStorage({commit, state, dispatch}) {
                commit('SET_DISABLE_CANCELLATION', false);
                commit('SET_PRODUCTS_FROM_STORAGE');
                commit('SET_CATEGORIES_FROM_STORAGE');
                commit('SET_GROUPS_FROM_STORAGE');
                commit('APPLY_FILTERS');
                commit('CHANGE_COUNTERS');
                commit('SET_FAKE_BEST');
                commit('SET_CLEAN_REQUEST');
                dispatch('changeOrder', {order: state.order});

                commit('SET_ZERO_COST', state.zeroCost);
                commit('SET_PLAN_TYPE', state.planType);
                commit('SET_EXIST_PLAN');

                commit('SET_SEND_FILTERS', true);
                commit('SET_INITIALIZED', true);
                commit('SET_FINISHED', true);
            },

            hideProduct({commit, state}, payload) {
                commit('ADD_HIDDEN', state.products.find(product => product.policyMetaUniqueCode === payload.policyCode));
                commit('CHANGE_COUNTERS');
            },

            restoreAllHidden({commit}) {
                commit('RESTORE_ALL_HIDDEN');
                commit('CHANGE_COUNTERS');
            },

            restoreLastHidden({commit}) {
                commit('RESTORE_LAST_HIDDEN');
                commit('CHANGE_COUNTERS');
            },

            changeOrder({commit, state}, payload) {
                commit('SET_ORDER', payload.order);
                const best = state.products.filter(product => product.policyMetaUniqueCode === state.fakeBest.policyMetaUniqueCode);
                switch (payload.order) {
                    case "LTH":
                        commit('SET_PRODUCTS', best.concat(state.products.filter(product => product.policyMetaUniqueCode !== state.fakeBest.policyMetaUniqueCode).sort((a, b) => a.totalPrice - b.totalPrice)));
                        break;
                    case "HTL":
                        commit('SET_PRODUCTS', best.concat(state.products.filter(product => product.policyMetaUniqueCode !== state.fakeBest.policyMetaUniqueCode).sort((a, b) => b.totalPrice - a.totalPrice)));
                        break;
                    case "PROVIDER":
                        const bestVendorCode = state.products.find(product => product.policyMetaUniqueCode === state.fakeBest.policyMetaUniqueCode).vendorCode;
                        const products = state.products.filter(product => !product.fakeBest).sort((a, b) => a.totalPrice - b.totalPrice);
                        const bestVendors = products.filter(product => product.vendorCode === bestVendorCode && product.policyMetaUniqueCode !== state.fakeBest.policyMetaUniqueCode);
                        const otherVendors = products.filter(product => product.vendorCode !== bestVendorCode)
                            .sort((a, b) => {
                                if (a.vendorName < b.vendorName) {
                                    return -1;
                                }
                                if (a.vendorName > b.vendorName) {
                                    return 1;
                                }
                                return 0;
                            });
                        commit('SET_PRODUCTS', best.concat(bestVendors.concat(otherVendors)));
                        break;
                }
            },

            expandAll({commit}) {
                commit('EXPAND_ALL');
            },

            wrapAll({commit}) {
                commit('WRAP_ALL');
            },

            reset({commit, dispatch, state}) {
                commit('RESET_CATEGORIES');
                if (state.appliedFilters.filter(category => category.type === 'CATALOG' || category.isUpsale).length > 0) {
                    dispatch({type: 'updateCategories', action: 'reset'});
                }
                commit('RESET');
                commit('CHANGE_COUNTERS');
                commit('SET_FAKE_BEST');
                commit('SET_CLEAN_REQUEST');
                dispatch('changeOrder', {order: state.order});
            },

            resultsHeight({commit, dispatch}, payload) {
                commit('SET_RESULTS_HEIGHT', payload.height);
                dispatch('filtersHeight');
            },

            tableHeight({commit, dispatch}, payload) {
                commit('SET_TABLE_HEIGHT', payload.height);
                dispatch('filtersHeight')
            },

            presetFiltersHeight({commit, dispatch}, payload) {
                commit('SET_PRESET_FILTERS_HEIGHT', payload.height);
                dispatch('filtersHeight')
            },

            filtersWidth({commit, dispatch}, payload) {
                commit('SET_FILTERS_WIDTH', payload.width);
            },

            filtersHeight({commit, state}) {
                const height = state.heights.results + state.heights.table;
                state.heights.presetFilters < height ? commit('SET_FILTERS_HEIGHT', height) : commit('SET_FILTERS_HEIGHT', state.heights.presetFilters);
            },

            offsetLeft({commit, dispatch}, payload) {
                commit('SET_WINDOW_OFFSET_LEFT', payload.windowOffsetLeft);
                commit('SET_TABLE_OFFSET_LEFT', payload.tableOffsetLeft);
            },

            changeView({commit, dispatch, state}) {
                commit('TOGGLE_VIEW');
                dispatch('restoreAllHidden');
                commit('SET_FAKE_BEST');
                dispatch('changeOrder', {order: state.order});
            },

            changeCompareList({commit}, payload) {
                payload.status ? commit('REMOVE_PLAN_FROM_COMPARE', payload.product) : commit('ADD_PLAN_TO_COMPARE', payload.product);
            },

            clearCompare({commit}) {
                commit('CLEAR_COMPARE_PLANS');
                commit('CHANGE_COUNTERS');
            },

            setComparePlans({commit, dispatch, state}) {
                commit('SET_COMPARE_PLANS');
                commit('CHANGE_COUNTERS');
                commit('SET_FAKE_BEST');
                dispatch('changeOrder', {order: state.order});
            },

            showCompareBar({commit}, payload) {
                commit('SET_COMPARE_BAR_STATUS', payload.status);
            },

            applyFilter({commit, dispatch, state}, payload) {
                let category = state.categories.find(category => {
                    return category.code === payload.code
                });
                commit('CHANGE_FILTERS', {
                    category: category,
                    status: category.type === 'CATALOG' ? category.selected !== "-1" : !category.checked
                });
                if (category.isUpsale) {
                    dispatch({type: 'updateCategories', action: 'applyFilter'});
                } else {
                    commit('APPLY_FILTERS');
                    commit('CHANGE_COUNTERS');
                    commit('SET_FAKE_BEST');
                    commit('SET_CLEAN_REQUEST');
                    dispatch('changeOrder', {order: state.order});
                }
            },

            closeFilter({commit, dispatch, state}, payload) {
                let category = state.categories.find(category => {
                    return category.code === payload.code
                });
                commit('RESET_CATEGORY', category);
                if (state.appliedFilters.filter(category => category.type === 'CATALOG').length > 0 || category.isUpsale) {
                    dispatch({type: 'updateCategories', action: 'closeFilter'});
                }
                commit('CHANGE_FILTERS', {category: category, status: false});
                commit('APPLY_FILTERS');
                commit('CHANGE_COUNTERS');
                commit('SET_FAKE_BEST');
                commit('SET_CLEAN_REQUEST');
                dispatch('changeOrder', {order: state.order});
            },


            applyFilterCatalog({commit, dispatch, state}, payload) {
                let category = state.categories.find(category => {
                    return category.code === payload.code
                });
                commit('CHANGE_FILTERS', {
                    category: category,
                    status: category.type === 'CATALOG' ? category.selected !== '-1' : !category.checked
                });
                dispatch({type: 'updateCategories', action: 'applyFilterCatalog'});
            },

        },

        mutations: {
            SET_STOP_FETCHING(state, status) {
                state.stopFetching = status;
            },

            SET_CATEGORIES_FROM_STORAGE(state) {
                state.categories = JSON.parse(sessionStorage.getItem(`categories_${state.currentQuote}_${state.planType}`));
            },

            SET_PRODUCTS_FROM_STORAGE(state) {
                state.products = JSON.parse(sessionStorage.getItem(`products_${state.currentQuote}_${state.planType}`));
            },

            SET_GROUPS_FROM_STORAGE(state) {
                state.groups = JSON.parse(sessionStorage.getItem(`groups_${state.currentQuote}_${state.planType}`));
            },

            RESET_CATEGORY(state, closedCategory) {
                state.categories.find(category => category.code === closedCategory.code).selected = '-1';
            },

            SET_SEND_FILTERS(state, status) {
                state.sendFilters = status;
            },

            SET_COMPARE_BAR_STATUS(state, status) {
                state.compareBarStatus = status;
            },

            SET_COMPARE_PLANS(state) {
                state.products.filter(product => !state.comparePlans.includes(product)).forEach(product => {
                    product.isActive = false;
                });
            },

            ADD_PLAN_TO_COMPARE(state, product) {
                state.comparePlans.push(product);
            },

            REMOVE_PLAN_FROM_COMPARE(state, product) {
                state.comparePlans = state.comparePlans.filter(plan => plan.policyMetaUniqueCode !== product.policyMetaUniqueCode);
            },

            CLEAR_COMPARE_PLANS(state) {
                state.products.forEach(product => {
                    product.isActive = true;
                });
                state.comparePlans = [];
            },

            SET_EXIST_PLAN(state) {
                state.existPlan = state.products.length > 0;
            },

            SET_CLEAN_REQUEST(state) {
                state.cleanRequest = state.appliedFilters.length === 0;
            },

            TOGGLE_VIEW(state) {
                state.compare = !state.compare;
            },

            SET_VIEW(state, status){
                state.compare = status;
            },

            SET_WINDOW_OFFSET_LEFT(state, windowOffsetLeft) {
                state.windowOffsetLeft = windowOffsetLeft;
            },

            SET_TABLE_OFFSET_LEFT(state, tableOffsetLeft) {
                state.tableOffsetLeft = tableOffsetLeft;
            },

            SET_FILTERS_WIDTH(state, width) {
                state.filtersWidth = width;
            },

            SET_RESULTS_HEIGHT(state, height) {
                state.heights.results = height;
            },

            SET_TABLE_HEIGHT(state, height) {
                state.heights.table = height;
            },

            SET_PRESET_FILTERS_HEIGHT(state, height) {
                state.heights.presetFilters = height;
            },

            SET_FILTERS_HEIGHT(state, height) {
                state.heights.filters = `${height}px`;
            },

            SET_QUOTE(state, quote) {
                state.currentQuote = quote;
            },

            SET_BEST_PLAN_CODE(state, bestPlanCode) {
                state.bestPlanCode = bestPlanCode;
            },

            SET_PLANS(state, plans) {
                state.plans = plans;
            },

            SET_REQUEST_ID(state, requestId) {
                state.requestId = requestId;
            },

            SET_PRODUCTS(state, products) {
                state.products = products;
            },

            SET_GROUPS(state, groups) {
                state.groups = groups;
            },

            SET_CATEGORIES(state, categories) {
                state.categories = categories;
            },

            SET_ZERO_COST(state, zeroCost) {
                state.zeroCost = zeroCost;
            },

            SET_PLAN_TYPE(state, planType) {
                state.planType = planType;
            },

            SET_INITIALIZED(state, initialized) {
                state.initialized = initialized;
            },

            SET_FINISHED(state, finished) {
                state.finished = finished;
            },

            ADD_HIDDEN(state, hidden) {
                hidden.isActive = false;
                state.hiddenProducts.push(hidden);
            },

            RESTORE_ALL_HIDDEN(state) {
                const hiddenCodes = state.hiddenProducts.map(product => product.policyMetaUniqueCode);
                state.products.filter(product => hiddenCodes.includes(product.policyMetaUniqueCode)).forEach(product => {
                    product.isActive = true
                });
                state.hiddenProducts = [];
            },

            RESTORE_LAST_HIDDEN(state) {
                let last = state.hiddenProducts.pop();
                state.products.find(product => product.policyMetaUniqueCode === last.policyMetaUniqueCode).isActive = true;
            },

            EXPAND_ALL(state) {
                state.groups.forEach(group => {
                    group.isClosed = false;
                })
            },

            WRAP_ALL(state) {
                state.groups.forEach(group => {
                    group.isClosed = true;
                })
            },

            RESET(state) {
                state.appliedFilters = [];
                state.products.forEach(product => {
                    product.isActiveFilters = true;
                });
            },

            RESET_CATEGORIES(state) {
                state.categories.forEach(category => {
                    category.isClosed = true;
                    category.checked = false;
                    category.selected = '-1';
                    category.counter = 0;
                });
            },

            CHANGE_FILTERS(state, payload) {
                let category = payload.category;
                category.checked = payload.status;

                if (category.checked) {
                    if (state.appliedFilters.map(filter => filter.code).includes(category.code)) {
                        state.appliedFilters.find(filter => filter.code === category.code).selected = category.selected;
                    } else {
                        state.appliedFilters.push(category);
                    }
                } else {
                    state.appliedFilters = state.appliedFilters.filter(filter => filter.code !== category.code);
                }
            },

            APPLY_FILTERS(state) {
                state.products.forEach(product => {
                    product.isActiveFilters = true;
                });

                if (state.appliedFilters.length !== 0) {
                    state.products.forEach(product => {
                            state.appliedFilters.forEach(category => {
                                const productValue = product.categoryValues[category.code].value;
                                if (category.type !== 'CATALOG' && (productValue === '-' || productValue === 'n/a')) {
                                    product.isActiveFilters = false;
                                }
                            })
                        }
                    );
                }
            },

            CHANGE_COUNTERS(state) {
                state.categories.forEach(category => {
                    category.counter = state.products.filter(product =>
                        product.isActive &&
                        product.isActiveFilters &&
                        (product.categoryValues[category.code].value !== '-' || category.selected === '0')
                    ).length;
                });
            },

            RESET_COUNTERS(state) {
                state.categories.forEach(category => {
                    category.counter = 0;
                });
            },

            SET_FAKE_BEST(state) {
                const best = state.products
                    .filter(product => product.categoryValues['trip-cancellation'].value === '100% Trip Cost' && product.categoryValues['primary-medical'].value === 'Yes' && product.isActive && product.isActiveFilters)
                    .sort((a, b) => a.totalPrice - b.totalPrice)[0];
                state.fakeBest = best !== undefined
                    ? best
                    : state.products
                        .filter(product => product.isActive && product.isActiveFilters)
                        .sort((a, b) => a.totalPrice - b.totalPrice)[0];


            },

            SET_ORDER(state, order) {
                state.order = order;
            },

            SET_DISABLE_CANCELLATION(state, disableCancellation) {
                state.disableCancellation = disableCancellation;
            },

            RESET_PRODUCTS(state) {
                state.products = [];
            }
        }
    })
;

export {store}