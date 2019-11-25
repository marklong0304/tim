/**
 * Created by artur on 17.02.2017.
 */

var quoteId = $("#quoteId").val();
var copyOfQuoteRequestUrl = context + 'results/getCopyOfQuoteRequest/' + quoteId;

function getPurchase(policyMetaCode, quoteRequestJson, purchaseFormId, requestId) {
    $.ajax({
        url: copyOfQuoteRequestUrl,
        data: {
            quoteRequestJson: quoteRequestJson
        },
        method: "POST",
        success: function (purchaseQuoteId) {
            redirectToPurchase(policyMetaCode, purchaseQuoteId, purchaseFormId, requestId);
        }
    });
}

function openCompareChooseCategoryDialog(policyMetaId, categoryId) {
    $.post(context + 'details/' + policyMetaId + '/' + categoryId, null, function (data) {
        $("#modalCategory").text('').append(data.category);
        $("#modalContent").text('').append(data.content);
        $("#modalVendorCode").attr("src", 'https://d3u70gwj4bg98w.cloudfront.net/vendorLogo/get/' + data.vendorCode + '.png');
        $("#modalPolicyMetaName").text('').append(data.policyMetaName);
        $('#certificateDialogCompare').modal('show');
        if ($("#certificateDialogCompare").hasScrollBar()) {
            $("html").css({"overflow-y": "hidden"});
        }
    });
}

function redirectToPurchase(policyMetaCode, purchaseQuoteId, purchaseFormId, requestId) {
    if (!purchaseFormId) {
        purchaseFormId = 'purchaseForm';
    }
    $("#" + purchaseFormId + " #purchaseUniqueCode").val(policyMetaCode);
    $("#" + purchaseFormId + " #purchaseQuoteId").val(purchaseQuoteId);
    $("#" + purchaseFormId + " #purchaseRequestId").val(requestId);
    $("#" + purchaseFormId).submit();
}