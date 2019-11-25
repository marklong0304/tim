$(document).ready(function () {
    $('#contentCategories').DataTable({
        stateSave: true,
        info: false,
        "bPaginate": false,
        "order": [[ 5, "asc" ]],
        "aoColumns": [
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": false, "visible": false}
        ]
    });
    var lastModified = $("#lastModified").val();
    if (lastModified != undefined) {
        $('#page-' + lastModified).css("background-color", "rgb(209, 255, 209)");
    }
    $("#sort").on("click", function () {
        $.ajax({
            url: context + "cms/category_content/",
            type: "POST",
            data: "sort",
            success: function (response) {
                location.reload();
            }
        }).fail(function () {
            alert("Error");
        });
    })
});

function openChooseCategoryDialog() {
    $('#chooseCategoryDialog').modal('show');
}

function createPolicyMetaCategory() {
    window.location.href = context + "cms/category_content/create/" + $('#categoryCode').val();
}

function deleteCategoryContent(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "cms/category_content/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#categoryContent-' + id).hide();
                location.reload();
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

