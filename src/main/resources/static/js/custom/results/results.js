var currentPage = 'results';
var baseResultsUrl = context + 'results/baseResults';
var preparedResultsUrl = context + 'results/preparedResults';
var updateCategoriesUrl = context + 'results/updateCategories';
var updateSortOrderUrl = context + 'results/updateSortOrder';
var originalUrl = context + 'results/original';
var resetUrl = context + 'results/reset';
var stopRequest = context + 'results/stopRequest';
var changePlanType = context + 'results/changePlanType';
var quoteId = $("#quoteId").val();
var systemSaveQuoteUrl = context + 'results/systemSaveQuote/' + quoteId;

var requestId = null;
var base = false;
var responseType;
var originalCategories;
var originalMessage;
var showCountAfterEnabledFilter;
var lastProductSize = 0;
var lastCategories = null;


$(document).ready(function () {

    var products = $("#products");

    var sortOrderInput = $("#sortOrderInput");
    var sortOrderSelect = $("#sortOrderSelect");
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
        sendUpdateSortOrderRequest($(this).closest('form'));
    });

    $("#original").click(function () {
        sendOriginalRequest();
    });

    $('#editQuote').click(function () {
        sendStopRequest();
    });

    sendBaseResultsRequest();


});



function clearFilters(event) {
    event.preventDefault();
    $('input:checkbox').removeAttr('checked');
    $(".catalog-select").hide().val("-1");
}

function updateMessage(msg, type) {
    if (type == 'ORIGINAL') {
        var textPlace = $(".original");
        if (originalMessage == undefined) {
            originalMessage = msg;
            if (originalMessage.indexOf("Pre-Ex Waiver not eligible") > -1) {
                originalMessage = "Pre-Ex Waiver not eligible";
                $(".original").wrap('<span class="wrapPreEx"><span data-toggle="modal" data-target="#preExModal"></span></span>');
            }
        }
        textPlace.text(originalMessage);
    }
}

function insertProducts(data, requestId) {
    var compiledProductsTemplate = Handlebars.getTemplate('products');
    var templateData = {
        products: data,
        requestId: requestId
    }
    var productsHtml = compiledProductsTemplate(templateData);
    $('#products-content').html(productsHtml);
}

function updateProducts(data, requestId, finished, categories) {
    var existPlan = data.length;
    var cleanRequest = true;
    $.each(categories, function (i, category) {
        if (category.checked) {
            cleanRequest = false;
        }
    });
    $("#plansCount").text('PLANS (' + data.length + '): ');
    $.each(data, function (index, value) {
        if (value.best == true) {
            $('#bestPlanCode').val(value.policyMetaUniqueCode);
        }
    });
    insertProducts(data, requestId);

    if (finished) {
        hideAllMessages();
        if (existPlan == 0) {
            if (cleanRequest) {
                $("#msg-empty-original-plan").show();
            } else {
                $("#msg-empty-filtered-plan").show();
            }
        } else {
            if (cleanRequest) {
                $("#msg-original-plan").show();
            } else {
                $("#msg-filtered-plan").show();
            }
        }
    }
    registryCompare();
}

function submitPurchase(element) {
    $(element).addClass("disabled");
    $(element).closest('form').submit();
}

function synchronizedCatalogCategoryValues(element) {
    var selects = $("select[name='" + element.name + "']");
    $.each(selects, function (index, select) {
        $(select).val(element.value);
    });
}

/* Send different actions requests */

function sendBaseResultsRequest() {
    sendRequestForAsyncResults(null, baseResultsUrl, true);
}

function sendOriginalRequest() {
    sendRequestForAsyncResults(null, originalUrl, false);
}

function sendResetRequest() {
    sendRequestForAsyncResults(null, resetUrl, false);
}

function sendStopRequest() {
    $.post(stopRequest + '/' + requestId);
}

function sendUpdateCategoriesRequest(element) {
    sendRequestForAsyncResults(element, updateCategoriesUrl, false);
}

function sendUpdateSortOrderRequest(element) {
    sendRequestForAsyncResults(element, updateSortOrderUrl, false);
}

function sendChangePlanTypeRequest(element) {
    sendRequestForAsyncResults(element, changePlanType, false);
}

var currentProcess = null;

function sendRequestForAsyncResults(msgElement, url, isBase) {
    if (!isBase && requestId != null) {
        sendStopRequest();
    }

    var current = new Object();
    currentProcess = current;

    lastProductSize = 0;
    resetCompare();
    var msg = $(msgElement).serialize();
    disableFilters();
    sticky = false;

    $.post(url + '/' + quoteId, msg, function (response) {
        if (currentProcess != current) {
            return;
        }

        depositAndPaymentDates = response.depositAndPaymentDates;
        requestId = response.requestId;
        $("#requestId").val(requestId);

        function getProducts() {
            $.get(preparedResultsUrl + '/' + quoteId + '/' + requestId, null, function (response) {
                if (currentProcess != current) {
                    return;
                }
                if (response.finished || response.products.length > lastProductSize) {
                    responseType = response.type;
                    zeroCost = response.zeroCost;
                    planType = response.planType;
                    if (responseType == 'ORIGINAL' && originalCategories == undefined) {
                        originalCategories = response.categories;
                    }
                    showCountAfterEnabledFilter = response.showCountAfterEnabledFilter;
                    updateProducts(response.products, requestId, response.finished, response.categories);
                    if (!_.isEqual(lastCategories, response.categories, _.isEqual)) {
                        updateCategories(response.categories, showCountAfterEnabledFilter);
                        lastCategories = response.categories;
                    }
                    updateMessage(response.categoryReducedMessage, responseType);
                    enableFilters();
                    updateCancellationGroup();
                    $("#quoteRequestJson").val(response.quoteRequestJson);
                }
                lastProductSize = response.products.length;
                if (!response.finished) {
                    setTimeout(getProducts, 500);
                }
                updateSticky();

            }).fail(function (jqXHR, textStatus) {
                console.log("fail");
                currentProcess = null;
            });
        }

        getProducts();
    }).fail(function () {
        printError(null, defErrMsg);
        enableFilters();
    });
}