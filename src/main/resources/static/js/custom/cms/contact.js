var lastValue;
$(document).ready(function () {

    $('#contacts').DataTable({
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

});

function deleteContactPage(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "cms/filing_claim_page/contact/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#policyPage-' + id).hide();
                location.reload();
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

