var lastValue;
$(document).ready(function () {

    $('#policyMetas').DataTable({
        stateSave: true,
        info: false,
        "bPaginate": false,
        "aoColumns": [
            null,
            null,
            null,
            null,
            {"bSortable": false}
        ]
    });

    $('#policyMetaCodesTable').DataTable({
        stateSave: true,
        info: false,
        "bPaginate": false,
        "aoColumns": [
            null,
            null,
            {"bSortable": false}
        ]
    });

    $('#quoteParam').DataTable({
        stateSave: true,
        info: false,
        "bPaginate": false,
        "aoColumns": [
            null,
            null,
            null,
            null,
            null,
            null,
            {"bSortable": false}
        ]
    });

    $('#metaCategory').DataTable({
        stateSave: true,
        info: false,
        "bPaginate": false,
        "aoColumns": [
            null,
            null,
            null,
            null,
            {"bSortable": false}
        ]
    });

    var percentType = $("#percentType");
    if (percentType.val() == 'PERCENT' || percentType.val() == 'RANGE_PERCENT') {
        $(".numericValue").mask('000', {negative: false});
        $('.numericValueFrom, .numericValueTo').mask('0000000', {negative: false});
    } else {
        $('.numericValue, .numericValueFrom, .numericValueTo').mask('0000000', {negative: false});
    }

    $("#planTypes").multiselect({buttonWidth: '250px'});

    let minTripCost = $('#minTripCost').val();
    let showOnQuotes = $('#showOnQuotes').val();

    function supportsZeroCancellation() {
        if ($("#supportsZeroCancellation option:selected").text() === "True") {
            $('#minTripCost').val('0');
            $('#minTripCost').attr('readonly', true);
            $('#showOnQuotes').val('true');
            $('#show').prop('checked', true);
            $('#showOnQuotesPage').css('display', 'none');
            $('#showPageQuotes').css('display', 'none');
        } else {
            if ($('#minTripCost').val() === '0') {
                $('#minTripCost').val('');
            } else {
                $('#minTripCost').val(minTripCost);
            }
            $('#minTripCost').attr('readonly', false);
            $('#showOnQuotes').val(showOnQuotes);
            $('#show').prop('checked', showOnQuotes);
            $('#showOnQuotesPage').css('display', 'none');
            $('#showPageQuotes').css('display', 'inline-flex');
        }
    }

    supportsZeroCancellation();

    $('#show').change(function (e) {
        if (e.target.checked) {
            $('#showOnQuotes').val('true');
        } else {
            $('#showOnQuotes').val('false');
        }
    });

    $('#supportsZeroCancellation').on('change.bs.select', function () {
        supportsZeroCancellation();
    });

    if ($("#showOnQuotesPage option:selected").text() === "True") {
        $('#show').prop('checked', true);
    } else {
        $('#show').prop('checked', false);
    }

    $('#showOnQuotesPage').on('change.bs.select', function () {
        if ($("#showOnQuotesPage option:selected").text() === "True") {
            $('#show').prop('checked', true);
        } else {
            $('#show').prop('checked', false);
        }
    });
});

function deletePolicy(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "vendors/policy/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#policy-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

function deletePolicyMetaCategory(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "vendors/policy/metaCategory/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#policyMetaCategory-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

function deletePolicyMetaCode(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "vendors/policy/policyMetaCode/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#policyMetaCode-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

function deletePolicyMetaPackage(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "vendors/policy/policyMetaPackage/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#policyMetaPackage-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

function deleteQuoteParam(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "vendors/policy/quoteParam/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#policyQuoteParam-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}
