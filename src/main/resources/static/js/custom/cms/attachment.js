$(document).ready(function () {
    $('#attachments').DataTable({
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
            {"bSortable": false},
            {"bSortable": false}
        ]
    });
    var lastModified = $("#lastModified").val();
    if (lastModified != undefined) {
        $('#menu-' + lastModified).css("background-color", "rgb(209, 255, 209)");
    }

    $('#linkModal').on('shown.bs.modal', function () {
        $('#myInput').focus()
    });

    $('.viewLink').on('click', function (e) {
        e.preventDefault();
        var uid = this.getAttribute('data');
        $("#link").val(location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '') + context +'cms/attachment/' + uid);
        $("#linkModal").modal('show');
    });
});

function deleteAttachment(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "cms/attachment/delete/" + id,
            type: "POST",
            success: function (response) {
                location.reload();
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

