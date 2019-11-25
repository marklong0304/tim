$(document).ready(function () {
    $('#certificates').DataTable({
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
            {"bSortable": false},
            {"bSortable": false}
        ]
    });
});

function deleteCertificate(id, policyMetaId) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "cms/certificate/delete/" + policyMetaId + "/" + id,
            type: "POST",
            success: function (response) {
                location.reload();
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

