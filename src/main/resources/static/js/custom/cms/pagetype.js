$(document).ready(function () {
    $('#pagetypes').DataTable({
        stateSave: true,
        info: false,
        "aoColumns": [
            null,
            null,
            {"bSortable": false}
        ]
    });
    var lastModified = $("#lastModified").val();
    if (lastModified != undefined) {
        $('#type-' + lastModified).css("background-color", "rgb(209, 255, 209)");
    }
});

function deletePageType(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "cms/pagetype/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#type-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

