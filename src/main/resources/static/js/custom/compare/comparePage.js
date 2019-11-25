/**
 * Created by artur on 29.08.2016.
 */
var currentPage = 'compare';
var baseCompareResultsUrl = context + 'comparePlans/baseResults';
var preparedCompareResultsUrl = context + 'comparePlans/preparedResults';
var updateCompareCategoriesUrl = context + 'comparePlans/updateCategories';
var updateSortOrderUrl = context + 'comparePlans/updateSortOrder';
var stopRequest = context + 'comparePlans/stopRequest';
var resetUrl = context + 'comparePlans/reset';
var changePlanType = context + 'comparePlans/changePlanType';
var quoteId = $("#quoteId").val();
var hiddenColumns = [];
var totalPlans;

var sortOrderInput = $("#sortOrderInput");
var sortOrderSelect = $("#sortOrderSelect");


(function ($) {
    $.fn.hasScrollBar = function () {
        return this.get(0).scrollHeight > this.height();
    }
})(jQuery);

$(document).ready(function () {
    totalPlans = $("#plansCount").text();

    // alert(totalPlans);

    $(".restoreLast").on('click', function (e) {
        e.preventDefault();
        var lastColumn = hiddenColumns.pop();
        $('.' + lastColumn).removeClass('hidden-col');
        refreshCounter();
        updateFS();
    });

    $(".restoreAll").on('click', function (e) {
        e.preventDefault();
        restoreAll();
        updateFS();
    });
    refreshCounter();

    $(sortOrderSelect).val($(sortOrderInput).val());
    sortOrderSelect.change(function (event) {
        var currentOrder = $(this).val();
        if (currentOrder == "LTH") {
            sortOrderInput.val("LTH");
        } else if (currentOrder == "HTL") {
            sortOrderInput.val("HTL");
        } else if (currentOrder == "PROVIDER") {
            sortOrderInput.val("PROVIDER");
        }
        sendUpdateCompareSortOrderRequest($(this).closest('form'))
    });

    sendBaseCompareResultsRequest();
});

function restoreAll() {
    $.each(hiddenColumns, function (index, value) {
        $('.' + value).removeClass('hidden-col');
    });
    hiddenColumns.length = 0;
    refreshCounter()
}

function refreshCounter() {
    var count = hiddenColumns.length;
    if (count == 0) {
        $(".restoreLast").addClass("disabled-filter");
        $(".restoreAll").addClass("disabled-filter");
    } else {
        $(".restoreLast").removeClass("disabled-filter");
        $(".restoreAll").removeClass("disabled-filter");
    }
    $('.counter').html(count);
    var showedPlans = totalPlans - count;
    $("#plansCount").text(showedPlans);
    if (showedPlans <= 2) {
        $(".columnClose").hide();
    } else {
        $(".columnClose").show();
    }
    if (sTable) {
        sTable.refreshWidths();
        filters.height($(".table-container").height() + $(".fake-results").height());
        sticky = false;
    }
}

/* Send different actions requests */

function sendResetRequest() {
    sendCompareRequestForAsyncResults(null, resetUrl, false);
}

function sendBaseCompareResultsRequest() {
    sendCompareRequestForAsyncResults(null, baseCompareResultsUrl, true);
}

function sendUpdateCompareCategoriesRequest(element) {
    sendCompareRequestForAsyncResults(element, updateCompareCategoriesUrl, false);
}

function sendUpdateCompareSortOrderRequest(element) {
    sendCompareRequestForAsyncResults(element, updateSortOrderUrl, false);
}

function sendChangePlanTypeRequest(element) {
    sendCompareRequestForAsyncResults(element, changePlanType, false, true);
}

function sendStopRequest() {
    $.post(stopRequest + '/' + requestId);
}

var currentProcess = null;

function sendCompareRequestForAsyncResults(msgElement, url, isBase, changeRequest) {
    if (!isBase && requestId != null) {
        sendStopRequest();
    }

    var current = {};
    currentProcess = current;

    //resetCompare();
    var bestPlanCode = $("#bestPlanCode").val();
    var plans = $("#plans").val();
    var msg = $(msgElement).serialize() + '&bestPlanCode=' + bestPlanCode + "&plans=" + plans;
    disableFilters();
    sticky = false;

    $.post(url + '/' + quoteId, msg, function (data) {
        if (changeRequest) {
            window.location.href = context + 'comparePlans/?quoteId=' + quoteId;
        }
        if (currentProcess != current) {
            return;
        }
        var response = jQuery.parseJSON(data);
        depositAndPaymentDates = response.depositAndPaymentDates;
        requestId = response.requestId;
        $("#requestId").val(requestId);

        var compareAll = response.includedPolicies == null || response.includedPolicies == '';
        var params =  'bestPlanCode=' + bestPlanCode + (!compareAll ?  '&includedPolicies=' + response.includedPolicies : '');
        function getCompareProducts() {
            $.get(preparedCompareResultsUrl + '/' + quoteId + '/' + requestId, params, function (data) {
                if (currentProcess != current) {
                    return;
                }
                var response = jQuery.parseJSON(data);
                if (response.finished || response.products.length > 0) {
                    updateProductOrder(response);
                    //If compare all then the first plan after ordering the response is the best plan
                    if(compareAll) {
                        setFirstProductAsBest(response);
                    }
                    responseType = response.type;
                    zeroCost = response.zeroCost;
                    planType = response.planType;
                    if (responseType == 'ORIGINAL' && originalCategories == undefined) {
                        originalCategories = response.categories;
                    }
                    showCountAfterEnabledFilter = response.showCountAfterEnabledFilter;
                    updateCategories(response.categories, showCountAfterEnabledFilter);
                    updateCompareProducts(response);
                    enableFilters();
                    updateCancellationGroup();
                    $("#quoteRequestJson").val(response.quoteRequestJson);
                }
                if (!response.finished) {
                    setTimeout(getCompareProducts, 500);
                }
                updateSticky();
            }).fail(function (jqXHR, textStatus) {
                console.log("fail");
                currentProcess = null;
            });
        }

        //getCompareProducts();
    }).fail(function () {
        printError(null, defErrMsg);
        enableFilters();
    });
}

function updateProductOrder(data) {
    var products = data.products;
    var order = $(sortOrderInput).val();
    if (order == 'PROVIDER') {
        var bestVendorCode = _.find(products, function (p) {
            return p.vendorCode;
        });
        var bestVendors = _.filter(products, function (p) {
            return p.vendorCode == bestVendorCode;
        });
        var otherVendors = _.filter(products, function (p) {
            return p.vendorCode != bestVendorCode;
        });
        var updatedVendors = _.concat(bestVendors, otherVendors);
        data.products = updatedVendors;
    } else if (order == 'HTL') {
        var best = _.last(products);
        data.products = _.concat(best, _.slice(products, 0, products.length - 1));
    }
}

function setFirstProductAsBest(response) {
    for(var i = 0; i < response.products.length; i++) {
        if(i === 0) {
            response.products[i].best = true;
            $("#bestPlanCode").val(response.products[i].policyMetaUniqueCode);
        } else {
            if(response.products[i].best) {
                response.products[i].best = false;
                break;
            }
        }
    }
}

function updateCompareProducts(data) {
    $("#plansCount").text(data.products.length);
    totalPlans = data.products.length;
    insertProducts(data);
    initTable();
    registryActions();
    restoreAll();
    if(data.finished) {
        $(".msg-original-plan").show();
    }
}

function insertProducts(data) {
    var compiledProductsTemplate = Handlebars.getTemplate('compareProducts');
    var templateData = {
        products: data.products,
        jsonGroups: data.jsonGroups,
        requestId: requestId
    }
    var productsHtml = compiledProductsTemplate(templateData);
    $('.table-container').html(productsHtml);

    setTimeout(function () {
        updateFS();
    }, 100);

}

function initTable() {
    //Sticky Table
    if ($(window).width() > 767) {
        var table = $('.table-compare').sticky({
            cellWidth: 175,
            cellHeight: 48,
            columnCount: 1,
            offset: {top: 0, left: 42}
        });
    } else {
        $('body').css('overflow-x', 'hidden');
        $('.table-compare-mob').sticky('unstick');
    }
    updateFS();
}

function registryActions() {
    $(".columnClose").on('click', function (e) {
        e.preventDefault();
        var cNum = $(this).attr('data');
        $('.' + cNum).addClass('hidden-col');
        hiddenColumns.push(cNum);
        refreshCounter();
        updateFS();
    });
    $('.table-compare tbody tr:not(.tr-header) td:not(:first-child)>span, .table-compare-mob tbody tr:not(.tr-header) td').on('click', function () {
        openCompareChooseCategoryDialog($(this).attr("data-policy-meta-id"), $(this).attr("data-category-id"));
    });

    $('#certificateDialogCompare').on('hidden.bs.modal', function () {
        $("html").css({"overflow-y": "auto"});
    });

    $('.simpleCell').parent().mouseenter(function () {
        $(this).css("color", "#333");
        $(this).css("text-decoration", "none");
    });
    registryDetails();
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

function togg(num) {
    var e = $("[arrow='" + num + "']");
    if (e.hasClass('expand')) {
        $("[child='" + num + "']").addClass('hidden-row')
    } else {
        $("[child='" + num + "']").removeClass('hidden-row');
    }
    e.toggleClass('expand');
}

function registryDetails() {
    $(".details").on('click', function () {
        var policyMetaUniqueCode = $(this).attr("data");
        $.post(context + 'details?quoteId=' + quoteId + '&plan=' + policyMetaUniqueCode, function (result) {
            var detailModal = $("#plandetailsModal");
            detailModal.html(result);
            detailModal.modal({
                    keyboard: false,
                    show: true
                }
            );
            registryDetailsExpand();
            initUpsales();
            registryDialogs();
            registryStikcy();
            $("html").css({"overflow-y": "hidden"});
        });
    });
}