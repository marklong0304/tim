const ResultsTemplate = `
<div class="results" ref="results" :style="resultsStyle">
     <div class="selected-filters">
        <div v-if="appliedFilters.length > 0" id='selected-checkboxes' >
            <h3>SELECTED FILTERS ({{appliedFilters.length}}):</h3>
            <div v-for="filter in appliedFilters" class="filter-tag alert fade in" :name="filter.code">
                <span>{{filter.name}}</span>
                <a class="close closeFilter" :name="filter.code" @click="closeFilter(filter.code)">×</a>
            </div>
            <a @click="closeAll()" class="clearbtn reset" id="clearbtn">Clear All</a>
        </div>

        <div class="row no-gutter views">
            <div class="col-md-8">
                <h3>PLANS ({{activeProductsSize}}): </h3>
                <a class="changeView changeView-pointer" @click="changeView()" >
                    <img src="https://d3u70gwj4bg98w.cloudfront.net/images/newdesign/comparsion.svg" class="comparsion-img" alt="Comparsion">
                    
                    <template v-if="compare">
                        <img src="https://d3u70gwj4bg98w.cloudfront.net/images/newdesign/list-view.svg" alt="List View" class="listView-img"/> 
                        <u>List View</u>
                    </template>
                    <template v-else>
                        <img src="https://d3u70gwj4bg98w.cloudfront.net/images/newdesign/comparsion.svg" alt="Comparsion Icon">
                        <u>Compare All</u>
                    </template>
                </a>
                <span v-if="compare" class="span-vue"> | </span>
                <a v-if="compare" @click="restoreHiddenProducts()" class="restoreAll">
                    <img src="https://d3u70gwj4bg98w.cloudfront.net/images/newdesign/restore.svg" alt="Restore" class="restore-img"/> 
                    <u>Restore All Hidden (<span class="counter">{{hiddenProducts.length}}</span>)</u>
                </a>
                <span v-if="compare" class="span-vue"> | </span>
                <a v-if="compare" @click="restoreLastHidden()" class="restoreLast">
                    <img src="https://d3u70gwj4bg98w.cloudfront.net/images/newdesign/restore.svg" alt="Restore" class="restore-img"/> 
                    <u>Restore Last Hidden (<span class="counter">{{hiddenProducts.length}}</span>)</u>
                </a>
            </div>
            <div class="col-md-4">
                <div class="row no-gutter sortby text-right">
                    <div class="col-lg-10">
                        <label class="control-label" for="sortOrderSelect">SORT BY:</label>
                        <select id="sortOrderSelect" class="selectpicker form-control" @change="changeOrder()" v-model="sortOrder">
                            <option value="LTH">Price - Low to High</option>
                            <option value="HTL">Price - High to Low</option>
                            <option value="PROVIDER">Provider</option>
                        </select>
                    </div>
                    <div v-if="(compare && resultsW) || (appliedFilters.length > 0 && resultsW)" class="row no-gutter">
                        <button id="morePlans" @click="showMorePlans()" type="button" class="btn btn-more-plans">More Plans</button>
                    </div>
                </div>
            </div>
        </div>
     </div>
</div>
`;

export {ResultsTemplate}