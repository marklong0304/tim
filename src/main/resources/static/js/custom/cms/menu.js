$(document).ready(function () {
    $('#menus').DataTable({
        stateSave: true,
        info: false,
        "bPaginate": false,
        "aoColumns": [
            null,
            null,
            {"bSortable": false}
        ]
    });
    var lastModified = $("#lastModified").val();
    if (lastModified != undefined) {
        $('#menu-' + lastModified).css("background-color", "rgb(209, 255, 209)");
    }
});

function deleteMenu(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "cms/menu/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#menu-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

