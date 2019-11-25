$(document).ready(function () {
    $('#policyMetaCategoryContentList').DataTable({
        stateSave: true,
        "iDisplayLength": 25,
        "bPaginate": false,
        info: false,
        "order": [[0, "asc"]],
        "aoColumns": [
            null,
            null,
            null,
            null,
            null,
            {"bSortable": false},
        ]
    });

});

function openChooseCategoryDialog() {
    $('#chooseCategoryDialog').modal('show');
}

function createPolicyMetaCategory() {
    window.location.href = context + "cms/vendor_page/policy_page/policy_meta_category/create/" + policyMetaPageId + "/" + $('#categoryCode').val();
}

function deleteCategoryContent(id) {
    if (confirm("Confirm Delete?")) {
        $.ajax({
            url: context + "cms/vendor_page/policy_page/policy_meta_category/delete/" + id,
            type: "POST",
            success: function (response) {
                $('#policyMetaCategory-' + id).hide();
                location.reload();
            }
        }).fail(function () {
            alert("Error");
        });
    }
}

