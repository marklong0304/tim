//draw Policies
function drawPolicies(vendors = filterData.vendors.map(a => a.id.toString())) {
    $('#policies').html('');

    if (filterData.policies.length < selectedPolicies.length){
        filterData.policies = selectedPolicies;
        $.each(filterData.policies, function (key, value) {
            $('#policies')
                .append($('<option>', {value: value.id})
                    .text(value.name));
        });
    } else {
        $.each(filterData.policies, function (key, value) {
            if (vendors.indexOf(value.vendorId.toString()) !== -1) {
                $('#policies')
                    .append($('<option>', {value: value.id})
                        .text(value.name).attr('data-vendor', value.vendorId));
            }
        });
    }



    $('#policies').val(selectedPolicies.map(el => el.id.toString()));

    $('#policies').multiselect('rebuild');
}

//Init Policies input
function initPolicies(dataTableId) {
    filterData.policies = $(dataTableId).DataTable().ajax.json().filterOptions.policies.map(function (el) {
        if (el.policy !== null) {
            return {
                name: el.policy,
                id: el.policyId,
                vendorId: el.vendorId,
            };
        }
    }).filter(el => el !== undefined);
}

//Init vendors input
function initVendors(dataTableId) {
    filterData.vendors = $(dataTableId).DataTable().ajax.json().filterOptions.vendors.map(function (el) {
        if (el.vendor !== null) {
            return {
                name: el.vendor,
                id: el.vendorId,
            };
        }
    }).filter(el => el !== undefined);

    if (filterData.vendors.length < selectedVendors.length){
        filterData.vendors = selectedVendors;
    }

    $.each(filterData.vendors, function (key, value) {
        $('#vendors')
            .append($('<option>', {value: value.id})
                .text(value.name));
    });

    $('#vendors').val(selectedVendors.map(el => el.id.toString()));

    $('#vendors').multiselect('rebuild');

    $('#vendors').change(function (e) {
        var selectedVendors = $(e.target).val();
        if (selectedVendors === null) {
            selectedVendors = filterData.vendors.map(a => a.id.toString());
        }
        drawPolicies(selectedVendors);
    });
}