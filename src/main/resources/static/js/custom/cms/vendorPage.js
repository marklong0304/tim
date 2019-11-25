$(document).ready(function () {
    $('#vendorPages').DataTable({
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
            null,
            {"bSortable": false}
        ]
    });
    $('#vendorPages').wrap('<div class="overflowX"></div>');
    var lastModified = $("#lastModified").val();
    if (lastModified != undefined) {
        $('#vendorPage-' + lastModified).css("background-color", "rgb(209, 255, 209)");
    }
});

