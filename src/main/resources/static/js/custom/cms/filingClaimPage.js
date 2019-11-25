$(document).ready(function () {
    $('#filingClaimPages').DataTable({
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
            null,
            null,
            {"bSortable": false},
            {"bSortable": false}
        ]
    });
    $('#filingClaimPages').wrap('<div class="overflowX"></div>');
    var lastModified = $("#lastModified").val();
    if (lastModified != undefined) {
        $('#filingClaimPage-' + lastModified).css("background-color", "rgb(209, 255, 209)");
    }
});

function deleteFilingClaimPage(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "cms/filing_claim_page/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#filingClaimPage-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

