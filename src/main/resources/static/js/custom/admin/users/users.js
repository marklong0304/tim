$(document).ready(function () {
    $('#users').DataTable({
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

function deleteUser(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "users/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#user-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}