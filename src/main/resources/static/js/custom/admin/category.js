$(document).ready(function () {
    var lastModified = $("#lastModified").val();
    if (lastModified != undefined) {
        $('#category-' + lastModified).css("background-color", "rgb(209, 255, 209)");
    }
});

function deleteCategory(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "admin/categories/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#category-' + id).hide()
                $('#alert-success div').remove();
                $('#alert-success').append('<div class="alert alert-success alert-dismissable">\
                <button type="button" class="close" data-dismiss="alert" aria-hidden="true"> &times; </button> \
                Success! Well done its submitted. </div>');
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

function subcategoryUp(id) {
    $.ajax({
        url: context + "admin/categories/subcategory/up/" + id,
        type: "POST",
        success: function (response) {
            location.reload();
        }
    }).fail(function () {
        alert("Error");
    });
}


function subcategoryDown(id) {
    $.ajax({
        url: context + "admin/categories/subcategory/down/" + id,
        type: "POST",
        success: function (response) {
            location.reload();
        }
    }).fail(function () {
        alert("Error");
    });
}

