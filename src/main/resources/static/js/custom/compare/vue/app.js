import ComparePlans from './components/comparePlans.js';
import Results from './components/results.js';
import Modal from './components/modal.js';
import Filters from './components/filters.js';
import Scroll from './components/scroll.js';
import {store} from './store.js';

const router = new VueRouter({
    mode: 'history',
    routes: []
});

Vue.component('Results', Results);
Vue.component('ComparePlans', ComparePlans);
Vue.component('Filters', Filters);
Vue.component('Scroll', Scroll);

Vue.prototype.$eventHub = new Vue();

new Vue({
    store,
    router,
    el: '#app',
    components: {
        ComparePlans,
        Results,
        Modal,
        Filters,
        Scroll
    },
});