$(document).ready(function () {
    $('#vendors').DataTable({
        stateSave: true,
        info: false,
        "aoColumns": [
            {bSortable: false},
            null,
            null,
            null,
            null,
            {bSortable: false}
        ],
        "bPaginate": false
    });
});

function deleteVendor(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "vendors/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#vendor-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

