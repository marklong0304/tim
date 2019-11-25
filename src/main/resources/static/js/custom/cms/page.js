$(document).ready(function () {
    $('#pages').DataTable({
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
            {"bSortable": false}
        ]
    });

    $('#system').DataTable({
        stateSave: true,
        info: false,
        "bPaginate": false,
        "aoColumns": [
            null,
            null,
            null,
            {"bSortable": false}
        ]
    });

    var lastModified = $("#lastModified").val();
    if (lastModified != undefined) {
        $('#page-' + lastModified).css("background-color", "rgb(209, 255, 209)");
    }
});

function deletePage(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "cms/page/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#page-' + id).hide()
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

