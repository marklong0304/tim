
function addRestriction(id, name, restrictionTypeParam){
    clearRestrictionFields();
    $('#addRestriction').attr('name', 'addToVendorRestrictionMatrix');
    $('#policyMetaId').val(id);
    $('#actionName').text('Create');
    $('#policyMetaName').text(name);
    $('#policyMeta\\.name').val(name);
    $('#policyMeta').val(id);
    restrictionType.val(restrictionTypeParam);
    updateDivs();
    restrictionType.addClass('disabled-pointer');
}

function editRestriction(restrictionId, policyId, policyName) {
    var showRestrictionUrl = context + 'vendors/policy/policyMetaRestriction/edit/' + restrictionId;
    $.get(showRestrictionUrl).done(function (data) {
        clearRestrictionFields();
        $('#policyMetaId').val(policyId);
        $('#policyMetaName').text(policyName);
        $('#policyMeta\\.name').val(policyName);
        $('#policyMeta').val(policyId);
        restrictionType.addClass('disabled-pointer');
        $('#addRestriction').attr('name', 'addToVendorRestrictionMatrix');
        $('#actionName').text('Edit');
        $('#restrictionId').val(data.id);
        $('#restrictionType').val(data.restrictionType);
        $('#restrictionPermit').val(data.restrictionPermit);
        $('#countries').multiselect('select', data.countries);
        $('#states').multiselect('select', data.states);
        $('#minValue').val(data.minValue);
        $('#maxValue').val(data.maxValue);
        $('#calculatedRestrictions').val(data.calculatedRestrictions);
        $('#addRestrictionModal').modal('show');
    });
}