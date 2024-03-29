const FiltersTemplate = `
<div>
<div v-if="compare" id="btn-collapse-wrapper" :style="filtersBtnStyle"  :class="showFilterBtn() ? 'showed' : ''" @click="goToFilters()">
    <button class="btn btn-collapse">Filters <i class="chevron-collapse"></i></button>
</div>
<div id="fcompare" class="col-sm-4 col-md-4 col-lg-3 filters" :style="[initialized? {'height': filtersHeight} : {'height': 'auto'}]" ref="filters">
    <div class="filters-inner">
        <form id="planTypeForm">
            <div class="filter-plan">
                <h1>Plan Type</h1>
                <div class="form-group">
                    <input @click="changePlanType($event)" id="radio-14" class="radio-custom radio-filter" :disabled="zeroCost || disableCancellation" name="planType" value="COMPREHENSIVE" 
                        type="radio" :checked="planType === 'COMPREHENSIVE'"/>
                    <label for="radio-14" class="radio-custom-label filter-label"
                        :title="zeroCost ? 'Not available when trip cost is $0. To enable click \\'Edit Quote\\' and enter real trip cost.':''">
                        Trip Cancellation
                    </label>
                                        
                                        
                    <input @click="changePlanType($event)" id="radio-15" class="radio-custom radio-filter" :disabled="disableCancellation" :checked="planType === 'LIMITED'"
                        name="planType" value="LIMITED" type="radio"/>
                    <label for="radio-15" class="radio-custom-label filter-label"
                        title="Does not include Trip Cancellation option. It will reset Trip cost to $0.00.">
                        No Trip Cancellation
                    </label>
                </div>
                <input type="hidden" name="requestId" id="requestId"/>
            </div>
        </form>
        
        <form id="updateCategoriesForm" action="#"  method="post">
            <div class="filter-box" v-for="(group, index) in groups">
            <template v-if="group.categories.length !== 0">
                <h1 v-if="index === 0">Filters</h1>
                <div v-if="index === 0" class="actions">
                    <button type="button" @click="expandAll()" class="btn" id="openf">Expand All</button> |
                    <button type="button" @click="wrapAll()" class="btn" id="closef">Wrap All</button> |
                    <button type="button" @click="reset()" class="btn reset" id="resetf">Reset</button>
                </div>
                
                <div class="filter-name" :closed="group.isClosed" :data-block="index"  :data-group-name="group.name" @click="group.isClosed = !group.isClosed;">
                    <h2 :text="group.name">{{group.name}}</h2><i :class="group.isClosed ? 'chevron-close' : 'chevron-open'" :closed="group.isClosed" :data-block="index"></i>
                </div>
                
                <template v-for="category in group.categories">
                <div v-if="getCategory(category.code) !== undefined" class="form-group filter-expand" :closed="group.isClosed" :data-block="index" :class="group.isClosed ? 'hidden-category' : ''">
                        <template v-if="getCategory(category.code).type === 'SIMPLE' || getCategory(category.code).type == 'CONDITIONAL'">
                            <div class="inputWrap">
                                        <input :checked="getCategory(category.code).checked" :id="'category-' + getCategory(category.code).id"
                                               class="checkbox-custom checkbox-filter simple" type="checkbox"
                                               :value="category.name"
                                               :name="category.code"
                                               :data-text = "category.name" :data-name = "category.code"
                                               :disabled="getCategory(category.code).counter == 0"
                                               @click="apply(category.code)"/>
                                        <label :for="'category-' + getCategory(category.code).id" :disabled="getCategory(category.code).counter == 0"
                                               class="checkbox-custom-label filter-label" :class="getCategory(category.code).counter == 0 ? 'disabled-link': ''">
                                               <span :text="category.name">{{category.name}} ({{getCategory(category.code).counter}})</span>&nbsp;
                                               <span :id="'category-count-' + getCategory(category.code).id"></span>
                                        </label>
                                        
                            </div>
                        </template>
                        <template v-if="getCategory(category.code).type === 'CATALOG'">
                            <div class="inputWrap catalog" @click.prevent="getCategory(category.code).isClosed = !getCategory(category.code).isClosed">
                                        <input type="checkbox" :checked="getCategory(category.code).checked"
                                               :id="'category-' + getCategory(category.code).id"
                                               :data-text = "category.name" :data-name = "category.code"
                                               class="checkbox-custom checkbox-filter catalog"
                                               :disabled="getCategory(category.code).counter == 0"/>
                                        <label :for="'category-' + getCategory(category.code).id" :disabled="getCategory(category.code).counter == 0"
                                               class="checkbox-custom-label filter-label" :class="getCategory(category.code).counter == 0 ? 'disabled-link': ''">
                                            <span :text="category.name">{{category.name}} ({{getCategory(category.code).counter}})</span>&nbsp;
                                            <span :id="'category-count-' + getCategory(category.code).id"></span>&nbsp;
                                            <span class="glyphicon glyphicon-chevron-down"/>
                                            <select v-model="getCategory(category.code).selected" @change="applyCatalog(category.code)" @click.stop class="form-control catalog-select" :name="category.code" :class="getCategory(category.code).isClosed ? 'hidden-category' : ''">
                                                <option value="-1">Not selected</option>
                                                <template v-for="value in getCategory(category.code).values">
                                                <option  :value="value.value">{{value.caption}}</option>
                                                </template>
                                            </select>
                                        </label>
                            </div>
                        </template>
                </div>
                </template>
            </template>
            </div>
        </form>          

    </div>
</div>
</div>
`;

export {FiltersTemplate}