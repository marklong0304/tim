const ScrollTemplate = `
<div class="scrlCont" ref="scrlCont" @scroll="getOffsetLeft()">
    <div v-if="!compare && comparePlans.length > 0 && showCompareBar" class="alert notf affix" id="notfcompare" data-spy="affix">
        <div class="row">
            <div class="col-xs-10">
                <div class="notf-compare-inner">
                    <button class="btn clearbtn compareButton" @click="submitCompare()">Compare Selected Plans ({{comparePlans.length}})</button>
                    <button @click="clearCompare()" class="btn clearbtn" id='notf-clear'>Clear All</button>
                </div>
            </div>
            <div class="col-xs-2"><button @click="closeCompareBar()" class="close btn" id="notf-x">&times;</button></div>
        </div>
    </div>
    <filters></filters>
    <div class="results-block results">
        <compare-plans></compare-plans>
    </div>
</div>
`;

export {ScrollTemplate}