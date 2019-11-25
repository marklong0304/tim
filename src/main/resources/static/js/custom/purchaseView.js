$(document).ready(function () {

    $('#policy').on("loaded.bs.select", function () {
            filterPolicies();
            $('#policy').siblings('.open').find('li[data-original-index = 1]').attr('hidden', true);
        });

    $("#cancel").on("click", function () {
        $("#cancelModal").modal({
                backdrop: 'static',
                keyboard: false,
                show: true
            }
        );
    });
    $("#restore").on("click", function () {
        if (confirm('Restore cancelled purchase?')) {
            $('#restoreForm').submit();
        }
    });
    $("#createUpgradedPurchaseButton").on("click", function () {
        if (confirm('Create upgraded purchase?')) {
            $('#purchaseForm').append('<input type="hidden" name="createUpgradePolicy" value="1"/>').submit();
        }
    });
    $("#duplicate").on("click", function () {
        if (confirm('Create duplicate purchase?')) {
            $('#purchaseForm').append('<input type="hidden" name="createUpgradePolicy" value="1"/>').submit();
        }
    });
    $("#showPurchaseResult").on("click", function () {
        var resultPage = $('#resultPage').val();
        if(resultPage) {
            var wnd = window.open("about:blank", "_newtab");
            wnd.document.write(resultPage);
            wnd.document.close();
        }
    });

    $("#provider").on('changed.bs.select', function () {
        filterPolicies();
        $('#policy').val('Select').change().parent().addClass('open');
    });

    function filterPolicies() {
        $('#policy option').each(function () {
            $(this).val() === $("#provider option:selected").val()
                ? $(this).parent().siblings('.open').find(`li[data-original-index=${parseInt($(this).attr('data-index')) + 2}]`).css('display', 'block')
                : $(this).parent().siblings('.open').find(`li[data-original-index=${parseInt($(this).attr('data-index')) + 2}]`).css('display', 'none');
        });
    }

});