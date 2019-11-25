import {FiltersTemplate} from "../templates/filtersTemplate.js";

export default {
    template: FiltersTemplate,
    data() {
        return {
            plan: 'COMPREHENSIVE',
            presetCategories: [
                {
                    checked: false,
                    code: "trip-interruption",
                    countAfterEnabled: 0,
                    counter: 0,
                    currentValue: null,
                    disabled: false,
                    id: 2,
                    name: "Trip Interruption",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true
                },
                {
                    checked: false,
                    code: "hurricane-&-weather",
                    countAfterEnabled: 0,
                    counter: 0,
                    currentValue: null,
                    disabled: false,
                    id: 3,
                    name: "Hurricane & Weather",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1'
                },
                {
                    checked: false,
                    code: "terrorism",
                    countAfterEnabled: 0,
                    counter: 0,
                    currentValue: null,
                    disabled: false,
                    id: 4,
                    name: "Terrorism",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1'
                },
                {
                    checked: false,
                    code: "financial-default",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 5,
                    name: "Financial Default",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "employment-layoff",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 6,
                    name: "Employment Layoff",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "cancel-for-medical",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 7,
                    name: "Cancel for medical",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "cancel-for-work-reasons",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 8,
                    name: "Cancel for work reasons",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "cancel-for-any-reason",
                    countAfterEnabled: 5,
                    currentValue: null,
                    disabled: false,
                    id: 9,
                    name: "Cancel for any reason",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "primary-medical",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 10,
                    name: "Primary Medical",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "emergency-medical",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 11,
                    name: "Emergency Medical",
                    type: "SIMPLE",
                    isClosed: true,
                    selected: '-1',
                    counter: 0,
                    values: []
                },
                {
                    checked: false,
                    code: "pre-ex-waiver",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 12,
                    name: "Pre-Ex Coverage",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "medical-evacuation",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 14,
                    name: "Medical Evacuation",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0

                },
                {
                    checked: false,
                    code: "non-medical-evacuation",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 15,
                    name: "Non-Medical Evacuation",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "travel-delay",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 16,
                    name: "Trip Delay",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "baggage-delay",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 17,
                    name: "Baggage Delay",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "baggage-loss",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 18,
                    name: "Baggage Loss",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "missed-connection",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 19,
                    name: "Missed Connection",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "24-hour-ad&d",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 20,
                    name: "24 Hour AD&D",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "flight-only-ad&d",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 21,
                    name: "Flight Only AD&D",
                    type: "SIMPLE",
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "common-carrier-ad&d",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 22,
                    name: "Common Carrier AD&D",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "hazardous-sports",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: true,
                    id: 23,
                    name: "Hazardous Sports",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "amateur-sports",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: true,
                    id: 24,
                    name: "Amateur Sports",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "rental-car",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 25,
                    name: "Rental Car",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "money-back-guarantee",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 26,
                    name: "Plan Review Period",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "24-hour-assistance-service",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 27,
                    name: "24 Hour Assistance Service",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "identity-theft",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 28,
                    name: "Identity Theft",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "renewable-policy",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: true,
                    id: 29,
                    name: "Renewable Policy",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "look-back-period",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 2122,
                    name: "Look back period",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                },
                {
                    checked: false,
                    code: "Schengen Visa",
                    countAfterEnabled: 0,
                    currentValue: null,
                    disabled: false,
                    id: 13268,
                    name: "Schengen Visa",
                    type: "SIMPLE",
                    values: [],
                    isClosed: true,
                    selected: '-1',
                    counter: 0
                }
            ],
            limitedGroups: [
                {
                    categories: [
                        {name: "Primary Medical", code: "primary-medical"},
                        {name: "Emergency Medical", code: "emergency-medical"},
                        {name: "Medical Deductible", code: "medical-deductible"},
                        {name: "Pre-Ex Coverage on Trip", code: "pre-ex-waiver-on-trip"},
                        {name: "Acute Onset of Pre-Ex", code: "Acute Onset of Pre-Existing Condition"},
                        {name: "Look back period", code: "look-back-period"}
                    ],
                    name: "Medical",
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "Medical Evacuation", code: "medical-evacuation"},
                        {name: "Non-Medical Evacuation", code: "non-medical-evacuation"}
                    ],
                    name: "Evacuation",
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "Trip Delay", code: "travel-delay"},
                        {name: "Baggage Delay", code: "baggage-delay"},
                        {name: "Baggage Loss", code: "baggage-loss"},
                        {name: "Missed Connection", code: "missed-connection"},
                        {name: "Trip Interruption (return air only)", code: "trip-interruption-return-air-only"}
                    ],
                    name: "Loss or Delay",
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "24 Hour AD&D", code: "24-hour-ad&d"},
                        {name: "Flight Only AD&D", code: "flight-only-ad&d"},
                        {name: "Common Carrier AD&D", code: "common-carrier-ad&d"}
                    ],
                    name: "Accidental Death",
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "Hazardous Sports", code: "hazardous-sports"},
                        {name: "Amateur Sports", code: "amateur-sports"}
                    ],
                    name: "Sports",
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "Rental Car", code: "rental-car"},
                        {name: "Plan Review Period", code: "money-back-guarantee"},
                        {name: "24 Hour Assistance Service", code: "24-hour-assistance-service"},
                        {name: "Identity Theft", code: "identity-theft"},
                        {name: "Renewable Policy", code: "renewable-policy"},
                        {name: "Schengen Visa", code: "Schengen Visa"},
                        {name: "Home Country/Follow Me Home", code: "home-country-follow-me-home"},
                        {name: "Collision Damage Waiver", code: "collision-damage-waiver"},
                        {name: "Pet Assist", code: "pet-assist"},
                        {name: "Tarmac Delay", code: "personal-property"}
                    ],
                    name: "Other Benefits",
                    isActive: true,
                    isClosed: false,
                }],
            comprehensiveGroups: [
                {
                    name: "Cancellation",
                    categories: [
                        {name: "Trip Cancellation", code: "trip-cancellation"},
                        {name: "Trip Interruption", code: "trip-interruption"},
                        {name: "Hurricane & Weather", code: "hurricane-&-weather"},
                        {name: "Terrorism", code: "terrorism"},
                        {name: "Financial Default", code: "financial-default"},
                        {name: "Employment Layoff", code: "employment-layoff"},
                        {name: "Cancel for medical", code: "cancel-for-medical"},
                        {name: "Cancel for work reasons", code: "cancel-for-work-reasons"},
                        {name: "Cancel for any reason", code: "cancel-for-any-reason"},
                        {name: "Pre-Ex Coverage", code: "pre-ex-waiver"},
                        {name: "Look back period", code: "look-back-period"}
                    ],
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "Primary Medical", code: "primary-medical"},
                        {name: "Emergency Medical", code: "emergency-medical"},
                        {name: "Medical Deductible", code: "medical-deductible"},
                        {name: "Pre-Ex Coverage on Trip", code: "pre-ex-waiver-on-trip"},
                        {name: "Acute Onset of Pre-Ex", code: "Acute Onset of Pre-Existing Condition"},
                    ],
                    name: "Medical",
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "Medical Evacuation", code: "medical-evacuation"},
                        {name: "Non-Medical Evacuation", code: "non-medical-evacuation"}
                    ],
                    name: "Evacuation",
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "Trip Delay", code: "travel-delay"},
                        {name: "Baggage Delay", code: "baggage-delay"},
                        {name: "Baggage Loss", code: "baggage-loss"},
                        {name: "Missed Connection", code: "missed-connection"}
                    ],
                    name: "Loss or Delay",
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "24 Hour AD&D", code: "24-hour-ad&d"},
                        {name: "Flight Only AD&D", code: "flight-only-ad&d"},
                        {name: "Common Carrier AD&D", code: "common-carrier-ad&d"}
                    ],
                    name: "Accidental Death",
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "Hazardous Sports", code: "hazardous-sports"},
                        {name: "Amateur Sports", code: "amateur-sports"}
                    ],
                    name: "Sports",
                    isActive: true,
                    isClosed: false,
                },
                {
                    categories: [
                        {name: "Rental Car", code: "rental-car"},
                        {name: "Plan Review Period", code: "money-back-guarantee"},
                        {name: "24 Hour Assistance Service", code: "24-hour-assistance-service"},
                        {name: "Identity Theft", code: "identity-theft"},
                        {name: "Renewable Policy", code: "renewable-policy"},
                        {name: "Schengen Visa", code: "Schengen Visa"},
                        {name: "Home Country/Follow Me Home", code: "home-country-follow-me-home"},
                        {name: "Collision Damage Waiver", code: "collision-damage-waiver"},
                        {name: "Pet Assist", code: "pet-assist"},
                        {name: "Tarmac Delay", code: "personal-property"}
                    ],
                    name: "Other Benefits",
                    isActive: true,
                    isClosed: false,
                }
            ]
        };
    },

    computed: {

        filtersBtnStyle() {
            return {
                'visibility': this.showFilterBtn() ? 'visible' : 'hidden'
            }
        },

        filtersWidth() {
            return this.$store.getters.filtersWidth;
        },

        filtersHeight() {
            return this.$store.getters.filtersHeight;
        },

        categories() {
            return this.$store.getters.categories;
        },

        groups() {
            return this.$store.getters.groups;
        },

        zeroCost() {
            return this.$store.getters.zeroCost;
        },

        planType() {
            return this.$store.getters.planType;
        },

        appliedFilters() {
            return this.$store.getters.appliedFilters;
        },

        disableCancellation() {
            return this.$store.getters.disableCancellation;
        },

        initialized() {
            return this.$store.getters.initialized;
        },

        products() {
            return this.$store.getters.products;
        },

        tableOffsetLeft() {
            return this.$store.getters.tableOffsetLeft;
        },

        windowOffsetLeft() {
            return this.$store.getters.windowOffsetLeft;
        },

        compare() {
            return this.$store.getters.compare;
        },

        currentQuote() {
            return this.$store.getters.currentQuote;
        },

        sendFilters(){
            return this.$store.getters.sendFilters;
        },
    },

    methods: {

        setFiltersPresets(planType) {
            const presetGroups = planType === 'COMPREHENSIVE' ? this.comprehensiveGroups : this.limitedGroups;
            this.$store.dispatch({
                type: 'setFiltersPresets',
                presetCategories: this.presetCategories,
                presetGroups: presetGroups
            });
        },


        changePlanType(event) {
            if (this.planType !== event.currentTarget.value) {
                this.setFiltersPresets(event.currentTarget.value);
                this.$store.dispatch({type: 'changePlanType', value: event.currentTarget.value});
            }
        },

        getCategory(code) {
            return this.categories.find(category => {
                return category.code === code;
            });
        },

        expandAll() {
            this.$store.dispatch('expandAll');
        },

        wrapAll() {
            this.$store.dispatch('wrapAll');
        },

        reset() {
            this.$store.dispatch('reset');
        },

        apply(code) {
            this.$store.dispatch({type: 'applyFilter', code: code});
            document.getElementById('morePlans').style.display = 'block';
        },

        applyCatalog(code) {
            this.$store.dispatch({type: 'applyFilterCatalog', code: code});
        },

        getPresetFiltersHeight() {
            this.$store.dispatch({type: 'presetFiltersHeight', height: this.$refs.filters.clientHeight});
        },

        // for sticky header offset
        getFiltersWidth() {
            this.$store.dispatch({type: 'filtersWidth', width: this.$refs.filters.clientWidth});
        },

        showFilterBtn() {
            return this.windowOffsetLeft > this.tableOffsetLeft + this.filtersWidth;
        },

        //t = current time, b = start value, c = change in value, d = duration
        easeInOutQuad(t, b, c, d) {
            t /= d / 2;
            if (t < 1) return c / 2 * t * t + b;
            t--;
            return -c / 2 * (t * (t - 2) - 1) + b;
        },

        goToFilters() {
            let start = document.getElementsByClassName('scrlCont')[0].scrollLeft,
                change = 0 - start,
                currentTime = 0,
                increment = 20,
                duration = 200;

            let animateScroll = () => {
                currentTime += increment;
                document.getElementsByClassName('scrlCont')[0].scrollLeft = this.easeInOutQuad(currentTime, start, change, duration);
                document.getElementsByClassName('fScrollCont')[0].scrollLeft = this.easeInOutQuad(currentTime, start, change, duration);
                if (currentTime < duration) {
                    setTimeout(animateScroll, increment);
                }
            };
            animateScroll();
        }
    },

    updated() {
        if (!this.initialized) {
            this.getPresetFiltersHeight()
        }
    },

    mounted() {
        this.getFiltersWidth();
        this.setFiltersPresets(this.planType);

        window.addEventListener('resize', this.getFiltersWidth);
    },

    beforeDestroy() {
        window.removeEventListener('resize', this.getFiltersWidth);
    }
}