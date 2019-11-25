const CompareTemplate = `
<div>
<div class="col-sm-8 col-md-8 col-lg-9 fake-results" :style="resultsStyle"></div>
<div v-show="!initialized || !sendFilters" id="loading" class="container-fluid notf">
    <div class="notf-loading text-center">
        <img src="https://d3u70gwj4bg98w.cloudfront.net/images/newdesign/logo-gray.svg" alt="Logo Notification"/>
            <h4>LOADING</h4>
        <hr/>
        <p>
            We are loading your insurance quotes.
            <br>
            We will also recommend the best plan for you!
        </p>
    </div>
</div>
<div v-show="initialized && sendFilters" id="load" :class="compare ? 'table-container table-container-desktop' : 'recommended-plan'" ref="tableContainer">
    
    <div v-show="compare">
    <table class="sticky-corner table table-bordered table-hover table-compare" :style="stickyCornerStyles" v-show="showHeader() && showColumn()">
        <tbody>
            <tr class="compare-tr">
                <th></th>
            </tr>
            <tr class="compare-tr2">
                <th class="pPlans">
                    PROVIDER PLANS
                    <span class="pMessage">(All coverages are per person, unless otherwise noted)</span>
                </th>
            </tr>
    </tbody>
    </table>
    <table class="sticky-column table table-bordered table-hover table-compare" :style="stickyColumnStyles" v-show="showColumn()">
        <tbody>
            <tr class="compare-tr">
                <th></th>
            </tr>
            <tr>
                <th class="compare-th">
                    <p v-if="finished" class="msg-original-plan description">
                        Recommended plan will give you maximum coverage for your money.
                    </p>
                </th>
            </tr>
            <tr class="compare-tr3">
                <th class="pPlans">
                    PROVIDER PLANS
                    <span class="pMessage">(All coverages are per person, unless otherwise noted)</span>
                </th>
            </tr>
            <template v-for="group in groups">
            <tr :class="['tr-header', group.isActive ? 'expand': '']" @click="group.isActive = !group.isActive" :arrow="'par-' + group.name">
                <td  class="tr-header ">
                    <div class="tr-header-inner">{{group.name}}<i class="chevron-filter"></i></div>
                </td>
            </tr>
            <tr v-for="category in group.categories" :child="'par-' + group.name" v-show="group.isActive">
                <td >{{category.name}}</td>
            </tr>
            </template>
    </tbody>
    </table>
    <table class="sticky-header table table-bordered table-hover table-compare" :style="stickyHeaderStyles" v-show="showHeader()">
        <thead id="sticky-header">
            <tr>
                <th></th>
                <th v-for="(product, index) in products" :class="['tdclose col-' + index, isBest(product) ? 'recomm-column' : '', product.isActive && product.isActiveFilters ? '' : 'hidden-col']">
                    <template v-if="isBest(product)">RECOMMENDED PLAN</template>
                    <template v-else><a v-if="canHide()" class="columnClose" :data="'col-' + index" @click="hideProduct(product.policyMetaUniqueCode)">×</a></template>
                </th>
                
            </tr>
            <tr class="compare-tr4">
            <th class="pPlans">
                PROVIDER PLANS
                <span class="pMessage">(All coverages are per person, unless otherwise noted)</span>
            </th>
                <th v-for="(product, index) in products" :class="['col-' + index, isBest(product) ? 'recomm-column' : '', product.isActive && product.isActiveFilters ? '' : 'hidden-col']">
                    <div :class="['price', isBest(product) ? 'recom-price' : '']">
                        <span class="alt-provname">{{product.vendorName}}</span>
                        <p class="alt-plan-name">{{product.policyMetaName}}</p>
                        <p class="sum">{{formatPrice(product.totalPrice)}}</p>
                        <a @click="getPurchase(product.policyMetaUniqueCode, null)" class="btn btnplan details">Buy Plan</a>
                    </div>
                </th>
        </tr>
        </thead>
    </table :>
    <table class="table table-bordered table-hover table-compare" id="my-table" :style="tableStyles">
        <thead>
            <tr>
                <th></th>
                
                <th v-for="(product, index) in products" :class="['tdclose col-' + index, isBest(product) ? 'recomm-column' : '', product.isActive && product.isActiveFilters ? '' : 'hidden-col']">
                    <template v-if="isBest(product)">RECOMMENDED PLAN</template>
                    <template v-else><a v-if="canHide()" class="columnClose" :data="'col-' + index" @click="hideProduct(product.policyMetaUniqueCode)">×</a></template>
                </th>
                
            </tr>
            <tr>
                <th class="compare-th2">
                    <p v-if="finished" class="msg-original-plan description">
                        Recommended plan will give you maximum coverage for your money.
                    </p>
                </th>
                <th v-for="(product, index) in products" :class="['col-' + index, isBest(product) ? 'recomm-column' : '', product.isActive && product.isActiveFilters ? '' : 'hidden-col']">
                    <div class="provider">
                        <div class="img-box">
                            <img :src="'https://d3u70gwj4bg98w.cloudfront.net/vendorLogo/get/' + product.vendorCode + '.png'" :alt="product.vendorName" class="img-responsive"/>
                        </div>
                        <p class="plan-name">{{product.policyMetaName}}</p>
                    </div>
                </th>
            </tr>
            <tr class="compare-tr5">
            <th class="pPlans">
                PROVIDER PLANS
                <span class="pMessage">(All coverages are per person, unless otherwise noted)</span>
            </th>
                <th v-for="(product, index) in products" :class="['col-' + index, isBest(product) ? 'recomm-column' : '', product.isActive && product.isActiveFilters ? '' : 'hidden-col']">
                    <div :class="['price', isBest(product) ? 'recom-price' : '']">
                        <span class="alt-provname">{{product.vendorName}}</span>
                        <p class="alt-plan-name">{{product.policyMetaName}}</p>
                        <p class="sum">{{formatPrice(product.totalPrice)}}</p>
                        <p class="total">
                            <template v-if="product.countOfTravelers">
                                <span class="forPeople"> total for {{product.countOfTravelers}} person</span>
                            </template>
                            <template v-else>
                                <span class="forPeople"> total for {{product.countOfTravelers}} people</span>
                            </template>
                        </p>
                        <a :data="product.policyMetaUniqueCode" class="certificate details plan-details">Plan Details</a>
                        <a @click="getPurchase(product.policyMetaUniqueCode, null)" class="btn btnplan details">Buy Plan</a>
                        <template v-if="product.certificateLink">
                            <a @click="parseLink(product.certificateLink, $event)" target="_blank" class="certificate">View Certificate</a>
                            <div class="loader"></div>
                        </template>
                    </div>
                </th>
        </tr>
        </thead>
        
        <tbody>
            <template v-for="group in groups">
            <tr :class="['tr-header', group.isActive ? 'expand': '']" @click="group.isActive = !group.isActive" :arrow="'par-' + group.name">
                <td  class="tr-header ">
                    <div class="tr-header-inner">{{group.name}}<i class="chevron-filter"></i></div>
                </td>
                <td v-for="(product, index) in products" :class="['col-' + index, product.isActive && product.isActiveFilters ? '': 'hidden-col']"></td>
            </tr>
            <tr v-for="category in group.categories" :child="'par-' + group.name" v-show="group.isActive">
                <td>{{category.name}}</td>
                <td v-for="(product, index) in products" :class="['col-' + index, isBest(product)? 'recomm-column' : '', product.isActive && product.isActiveFilters ? '' : 'hidden-col', product.categoryValues[category.code].showCertificate? '': 'dash']" >
                    <template v-if="product.categoryValues[category.code].showCertificate">
                        <span @click="openCategoryDetails(product.categoryValues[category.code].dataPolicyMetaId, product.categoryValues[category.code].dataCategoryId)" 
                        :data-category-id="product.categoryValues[category.code].dataCategoryId" 
                        :data-policy-meta-id="product.categoryValues[category.code].dataPolicyMetaId"
                        data-toggle="modal" data-target="#certificateDialogCompare" >{{product.categoryValues[category.code].value}}</span>
                            <template v-if="product.categoryValues[category.code].secondary">
                                <span data-toggle="modal" data-target="#secondaryModal"> (secondary)</span>
                            </template>
                    </template>
                    <template v-else>
                        <span class="simpleCell">{{product.categoryValues[category.code].value}}</span>
                    </template>
                </td>
            </tr>
            </template>
    </tbody>
    </table>
    </div>
    
    <div v-show="!compare">
        <p class="heading">YOUR RECOMMENDED PLAN</p>
        <p id="msg-original-plan" class="description" v-show="existPlan && cleanRequest && finished">
            This plan will give you maximum coverage for your money.
            <span class="original"></span><br/>
            <span>* All coverages are per person, unless otherwise noted.</span>
        </p>
        <p id="msg-filtered-plan" class="description" v-show="existPlan && !cleanRequest && finished">
            This plan is recommended based on your new selection.<br/>
            <span>* All coverages are per person, unless otherwise noted.</span>
        </p>
        <p id="msg-empty-filtered-plan" class="description" v-show="!existPlan && !cleanRequest && finished">
            Sorry, your quote has changed or expired. Please start over or edit quote.<br/>
            <span>* All coverages are per person, unless otherwise noted.</span>
        </p>
        <p id="msg-empty-original-plan" class="description" v-show="!existPlan && cleanRequest && finished">
            There are no plans available for requested parameters.<br/>
            <span>* All coverages are per person, unless otherwise noted.</span>
        </p>
        <div id="products-content" class="products-content">
            <section v-for="(product, index) in products" :class="['plans-plans', 'plans-box', isBest(product) ? 'recommended': '' ]" :style="{'display': product.isActiveFilters ? 'block' : 'none'}">
                <div class="row plans-row no-gutter">
                    <div :class="['col-md-3 col-lg-2 col-lg-push-9 col-md-push-9', 'price', isBest(product) ? 'recom-price': '' ]">
                        <p class="sum">{{formatPrice(product.totalPrice)}}</p>
                        <p class="total">
                            <template v-if="product.countOfTravelers">
                                <span class="forPeople"> total for {{product.countOfTravelers}} person</span>
                            </template>
                            <template v-else>
                                <span class="forPeople"> total for {{product.countOfTravelers}} people</span>
                            </template>
                        </p>
                        <a @click="getPurchase(product.policyMetaUniqueCode, null)" class="btn btnplan details">Buy Plan</a>

                        <div class="form-group">
                            <input :id="product.policyMetaUniqueCode" class="checkbox-custom checkbox-recomm"
                                :name="product.policyMetaUniqueCode" type="checkbox"/>
                            <label :for="product.policyMetaUniqueCode" class="checkbox-custom-label checkbox-custom-recomm"
                                @click="changeCompareList(product.policyMetaUniqueCode)">Add to Compare</label>
                        </div>
                    </div>

                    <div class="col-md-3 col-lg-3 col-lg-pull-2 col-md-pull-3 ">
                        <div class="provider">
                            <div class="img-box">
                                <img :src="'https://d3u70gwj4bg98w.cloudfront.net/vendorLogo/get/' + product.vendorCode + '.png'" :alt="product.vendorName" class="img-responsive"/>
                            </div>
                            <p class="plan-name">{{product.policyMetaName}}</p>
                            <a :data="product.policyMetaUniqueCode" id="plandetails" class="plan-details" >Plan Details</a>
                        </div>
                    </div>

        <div class="col-md-3 col-lg-3 col-lg-pull-2 col-md-pull-3 ">
            <ul class="plan-features">
                <template v-if="product.countOfTravelers">
                <li v-for="(category, index) in product.categories.slice(0, 3)">
                    <h6>{{category.categoryName}}</h6>
                    <p>{{category.categoryCaption}}</p>
                </li>
                </template>
            </ul>
        </div>
        <div class="col-md-3 col-lg-3 col-lg-pull-2 col-md-pull-3 ">
            <ul class="plan-features">
                <template v-if="product.countOfTravelers">
                <li v-for="(category, index) in product.categories.slice(3)">
                    <h6>{{category.categoryName}}</h6>
                    <p>{{category.categoryCaption}}</p>
                </li>
                </template>
            </ul>
        </div>
        <div class="col-lg-2 col-md-2 col-sm-4 col-xs-12 otherPlansPrice">
            <div class="costTrip clearfix"></div>
        </div>
                </div>
            </section>
        </div>
    </div>
</div>
</div>
`;

export {CompareTemplate}