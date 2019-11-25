$(document).ready(function () {
    $('#companies').DataTable({
        stateSave: true,
        info: false,
        "aoColumns": [
            null,
            null,
            null,
            null,
            null,
            {"bSortable": false}
        ]
    });
});

function deleteCompany(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "companies/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#company-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}