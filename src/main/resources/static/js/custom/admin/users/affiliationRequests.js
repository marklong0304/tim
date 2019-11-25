$(document).ready(function () {
    $('#affiliationRequests').DataTable({
        stateSave: true,
        info: false,
        "aoColumns": [
            null,
            null,
            null,
            null,
            {"bSortable": false}
        ]
    });
});

function approveAffiliationRequest(id) {
    $.ajax({
        url: context + "users/approveAffiliationRequest/" + id,
        type: "POST",
        success: function (response) {
            $('#user-' + id).hide()
        }
    }).fail(function () {
        alert("Error");
    });
}

function removeAffiliationRequest(id) {
    if (confirm("Confirm affiliation request removal?")) {
        $.ajax({
            url: context + "users/removeAffiliationRequest/" + id,
            type: "POST",
            success: function (response) {
                $('#user-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}