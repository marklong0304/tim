var lastValue;
$(document).ready(function () {

    $('#policyMetas').DataTable({
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
            {"bSortable": false}
        ]
    });

});

