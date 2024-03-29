var quoteId = $("#quoteId").val();
var upsaleUrl = context + 'details/detailsUpsale';
var updatePackagesUrl = context + 'upsale/package/enable';
var tooltipsUrl = context + 'upsale/tooltips';
var systemSaveQuoteUrl = context + 'results/systemSaveQuote/' + $('#detailsQuoteId').val();

var zeroCostGroup = ["trip-cancellation", "hurricane-&-weather", "terrorism", "financial-default", "pre-ex-waiver", 'look-back-period',
    "employment-layoff", "cancel-for-medical", "cancel-for-work-reasons", "cancel-for-any-reason", "trip-interruption"];
var depositAndPaymentGroup = ["pre-ex-waiver", "cancel-for-any-reason", "cancel-for-work-reasons", "pre-ex-waiver-on-trip"];


function getDetails(e, policyMetaUniqueCode, quoteIdParam, requestId) {
    var planCode = policyMetaUniqueCode.id ? policyMetaUniqueCode.id : policyMetaUniqueCode;
    e.preventDefault();
    $.post(context + 'details?quoteId=' + (quoteIdParam ? quoteIdParam : quoteId) + '&plan=' + planCode + '&requestId=' + requestId, function (result) {
        var detailModal = $("#plandetailsModal");
        detailModal.html(result);

        detailModal.modal({
                //     backdrop: 'static',
                keyboard: false,
                show: true
            }
        );
        registryDetailsExpand();
        initUpsales();
        registryDialogs();
        registryStikcy();
        $("html").css({"overflow-y": "hidden"});
        getTooltipsOnDetails();
    });
}

$(document).on('click', '.plan-details', function () {
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

$(document).on('hidden.bs.modal', '#plandetailsModal', function (event) {
    if (event.target.id === 'plandetailsModal') {
        $("html").css({"overflow-y": "auto"});
    }
});

function parseLink(certificateLink) {
    if (certificateLink.match(/travelinsured/g) !== null) {
        let target = event.target;
        target.style.display = 'none';
        target.nextElementSibling.style.display = 'block';
        axios.get(certificateLink,
            {
                responseType: 'blob',
                headers: {
                    'Accept-Language': 'en-US,en;q=0.9',
                    'Accept': 'application/pdf',
                }
            })
            .then((response) => {
                target.style.display = 'block';
                target.nextElementSibling.style.display = 'none';
                const url = window.URL.createObjectURL(response.data);
                window.open(url);
            })
            .catch((error) =>
                console.log(error)
            );
    } else {
        window.open(certificateLink);
    }
}

function sendUpsaleRequest(elem, lastVal) {
    var oldPrice = $("#totalPriceTxt").text();
    $("#totalPriceTxt").text("loading...");
    $(".purchaseDetail").addClass("disabled-pointer");
    clearErrors();
    var changed = $("#upsaleForm input[name='changed']");
    if (changed.length == 0) {
        changed = $("<input/>").attr("type", "hidden").attr("name", "changed").appendTo($("#upsaleForm"));
    }
    changed.val(elem.name);
    $.post(upsaleUrl, $("#upsaleForm").serialize(), function (data) {
        updatePageAfterUpsale(data, oldPrice, elem, lastVal)
    });
    $(".upsale").prop('disabled', true);
}

function sendUpdatePackagesRequest(element) {
    var oldPrice = $("#totalPriceTxt").text();
    var enabledPackage = $(element).is(':checked');
    var paramsObj = {
        packageCode: element.name,
        policyUniqueCode: $('#purchaseUniqueCode').val(),
        quoteRequestJson: $(".quoteRequestJson").val(),
        enabled: enabledPackage
    };
    var paramsStr = $.param(paramsObj);
    $.post(updatePackagesUrl, paramsStr, function (data) {
        updatePageAfterUpsale(data, oldPrice, element, !enabledPackage);
    });
    $(".upsale").prop('disabled', true);
}

function getTooltipsOnDetails() {
    var paramsObj = {
        policyUniqueCode: $('#purchaseUniqueCode').val(),
        quoteRequestJson: $(".quoteRequestJson").val(),
    };
    var paramsStr = $.param(paramsObj);
    $.post(tooltipsUrl, paramsStr, function (data) {
        updateTooltipsOnDetails(jQuery.parseJSON(data).tooltips);
    });
}

function updateTooltipsOnDetails(tooltips) {
    if (_.isEmpty(tooltips)) {
        return;
    }
    $(".uspTooltip").tooltip('destroy');
    setTimeout(printTooltipsOnDetails, 300, tooltips);
}

function printTooltipsOnDetails(tooltips) {
    _.each(tooltips, function (tooltip) {
        $("a.uspTooltip." + tooltip.upsaleCategoryId + "-" + tooltip.upsaleCategoryValue).tooltip({
            trigger: 'hover',
            placement: 'right auto',
            html: true,
            title: tooltip.message,
            container: 'body'
        });
    });
}

function updatePageAfterUpsale(data, oldPrice, elem, lastVal) {
    var response = jQuery.parseJSON(data);
    if (response.error != "") {
        $("html, body").animate({scrollTop: 0}, "slow");
        printError(null, response.error);
        $("#totalPriceTxt").text(oldPrice);
        $("#totalPriceTxt2").text(oldPrice);
        $("#totalPrice").val(oldPrice);
        $(elem).selectpicker('val', lastVal);
    } else {
        $("#totalPriceTxt").text('$' + response.price.toFixed(2));
        if (response.planDescriptionCategoryCaption1 != null) {
            $("#ex1").text(response.planDescriptionCategoryCaption1);
        }
        if (response.planDescriptionCategoryCaption2 != null) {
            $("#ex2").text(response.planDescriptionCategoryCaption2);
        }
        if (response.planDescriptionCategoryCaption3 != null) {
            $("#ex3").text(response.planDescriptionCategoryCaption3);
        }
        if (response.planDescriptionCategoryCaption4 != null) {
            $("#ex4").text(response.planDescriptionCategoryCaption4);
        }
        if (response.planDescriptionCategoryCaption5 != null) {
            $("#ex5").text(response.planDescriptionCategoryCaption5);
        }
        if (response.planDescriptionCategoryCaption6 != null) {
            $("#ex6").text(response.planDescriptionCategoryCaption6);
        }
        $(".quoteRequestJson").val(JSON.stringify(response.quoteRequest));
        $('.upsale').prop("selectedIndex", 0);
        for (var name in response.quoteRequest.categories) {
            $(".details[name='" + name + "']").selectpicker('val', (response.quoteRequest.categories[name].toUpperCase()));
            var pval = $("#upsaleForm input[name='p-val." + name + "']");
            if (pval.length == 0) {
                pval = $("<input/>").attr("type", "hidden").attr("name", "p-val." + name).appendTo($("#upsaleForm"));
            }
            pval.val(response.quoteRequest.categories[name]);
        }
        /** for packages with not selected values */
        var upsalesFromServer = [];
        var upsalesFromPage = [];

        $.each(response.quoteRequest.categories, function (value) {
            upsalesFromServer.push(value)
        });

        $.each($('select.upsale'), function (index, value) {
            upsalesFromPage.push(value.name)
        });

        var q = _.differenceWith(upsalesFromPage, upsalesFromServer, _.isEqual);

        _.forEach(q, function (value) {
            $(".details select[name='" + value + "']").selectpicker('val', '');
        });

        // update packages
        var allPackages = _.map($("#package .checkbox-custom"), 'id');

        _.forEach(allPackages, function (packageCode) {
            var currentElement = $('#' + packageCode);
            var packageChecked = currentElement.prop('checked');
            var enabledOnRequest = !_.isNil(_.find(response.quoteRequest.enabledPackages, function (o) {
                return _.eq(o, packageCode);
            }));
            if (!packageChecked && enabledOnRequest) {
                currentElement.prop('checked', true);
            } else if (packageChecked && !enabledOnRequest) {
                currentElement.prop('checked', false);
            }
        });

        // show package notifications
        _.each(response.notifications, function (notification) {
            var enabled = _.eq(notification.color, 'GREEN');
            showModal({
                title: notification.message,
                text: notification.message + (enabled ? ' was enabled' : ' was disabled'),
                type: enabled ? 'info' : 'error',
                fontSize: 16
            });
        });

        updateTooltipsOnDetails(response.tooltips);
    }
    $(".upsale").prop('disabled', false);
    $(".purchaseDetail").removeClass("disabled-pointer");
    $.get(systemSaveQuoteUrl, {
        policyUniqueCode: $('#policyUniqueCode').val(),
        quoteRequestJson: $('#quoteRequestJson').val()
    });
}

function displaySecondaryModalLink(element) {
    if ($('option:selected', $(element)).attr('data-secondary') == 'true') {
        $(element).parent().find('.secondaryModalUpsale').show();
    } else {
        $(element).parent().find('.secondaryModalUpsale').hide();
    }
}

function submitPurchase(element) {
    // ajax test for session expired
    var ajaxTestUrl = context + 'results/ajaxTest';
    $.post(ajaxTestUrl).error(function (x, status, error) {
        if (x.status == 403) {
            $('#serverErrorModal').modal('show');
        }
    }).done(function () {
        getPurchase($("#purchaseUniqueCode").val(), $('.quoteRequestJson').val(), 'purchaseFormDetails', $('#purchaseRequestId').val());
    });
}

function validateSave() {
    checkAuthentication(submitSave);
}

function submitSave() {
    $("#saveQuoteForm").submit();
}

function checkZeroCostDetail(code) {
    if ($("#zeroCost").val() == 'true' && ($.inArray(code, zeroCostGroup) != -1)) {
        if ($("#depositAndPayment").val() == 'false' && ($.inArray(code, depositAndPaymentGroup) != -1)) {
            $("#zeroCostMessage").text("For Trip Cancellation benefit the Trip Cost must be more than zero and Initial Deposit Date and Final Payment Date filled. Enter Trip Cost?")
        } else {
            $("#zeroCostMessage").text("For Trip Cancellation benefit the Trip Cost must be more than zero. Enter Trip Cost?")
        }
        $("#linkModalDetails").modal({
                backdrop: 'static',
                keyboard: false,
                show: true
            }
        );
        return false;
    }
    return true;
}

function checkPaymentAndDepositDatesDetail(code) {
    if ($("#depositAndPayment").val() == 'false' && ($.inArray(code, depositAndPaymentGroup) != -1)) {
        $("#depositAndPaymentDetailsModal").modal({
                backdrop: 'static',
                keyboard: false,
                show: true
            }
        );
        return false;
    }
    return true;
}

function clearErrors() {
    $("#errors").html("");
}

$(document).ready(function () {
    initUpsales();
});

function initUpsales() {
    var upsaleSelect = $('select.details.upsale');
    upsaleSelect.each(function () {
        $(this).data('lastValue', $(this).val());
    });
    upsaleSelect.change(function () {
        var lastVal = $(this).data('lastValue');
        if (!checkZeroCostDetail(this.name) || !checkPaymentAndDepositDatesDetail(this.name)) {
            event.preventDefault();
            $(this).val(lastVal);
        } else {
            sendUpsaleRequest(this, lastVal);
        }
    });
    upsaleSelect.selectpicker({
        actionsBox: true,
        dropupAuto: false
    });
}

function openCmsRestrictionDialog(e) {
    $.post(context + 'page/provider/' + vendorPageName + '/' + policyMetaPageName + '/' + $(e).attr('id'), null, function (data) {
        $("#modalCategory").text('').append(data.category);
        $("#modalContent").text('').append(data.content);
        $('#certificateDialog').modal('show');
        $('#certificateDialog').css('z-index', 1060);
    });
}

function openCmsPackageDialog(e) {
    $.post(context + 'page/provider/' + vendorPageName + '/' + policyMetaPageName + '/packages/' + $(e).attr('id'), null, function (data) {
        $("#modalCategory").text('').append(data.category);
        $("#modalContent").text('').append(data.content);
        $('#certificateDialog').modal('show');
        $('#certificateDialog').css('z-index', 1060);
    });
}

function openChooseCategoryDialog(e) {
    $.post(context + 'details/' + policyMetaId + '/' + $(e).attr('id'), null, function (data) {
        $("#modalCategory").text('').append(data.category);
        $("#modalContent").text('').append(data.content);
        $('#certificateDialog').modal('show');
        $('#certificateDialog').css('z-index', 1060);
    });
}

function registryDialogs() {
    $('.modal').on('hidden.bs.modal', function (e) {
        if ($('.modal').hasClass('in')) {
            $('body').addClass('modal-open');
            $("html").css({"overflow-y": "hidden"});
        }
    });
    $("#closeCertificateDialog").on('click', function () {
        $('#certificateDialog').modal('hide');
    });
    $("#closeLinkModalDetails").on('click', function () {
        $('#linkModalDetails').modal('hide');
    });
    $("#closeDepositAndPaymentDetailsModal").on('click', function () {
        $('#depositAndPaymentDetailsModal').modal('hide');
    });
    $(".closeSecondaryModal").on('click', function () {
        $('#secondaryModal').modal('hide');
    });
}

function registryStikcy() {
    var sticky = 'no';
    var target = $(".row-outer");
    var modalWindow = $('#plandetailsModal');
    var openCardDetails = $('#openCardDetails');
    var h = 100;
    let detailRow = $(".row-outer .plans-row");

    scrollHandler = function () {
        if (modalWindow.scrollTop() > 160) {
            let a = modalWindow.scrollTop() - h;
            if (sticky === 'no') {
                sticky = 'yes';
                target.addClass('fixedHeader');
                if (window.outerWidth < 768) {
                    for (let i = 1; i < 4; ++i){
                        detailRow.children().eq(i).css('display', 'none');
                    }
                }
                if ($('.fixedHeader')[0] !== undefined) {
                    openCardDetails.css('height', $('.fixedHeader')[0].offsetHeight);
                }
                if (version != false) {
                    target.addClass('fixedHeaderIE');
                    openCardDetails.css('height', $('.fixedHeaderIE')[0].offsetHeight - 50);
                }
            }
            if (version === false) {
                // for good browsers
                $('.fixedHeader').css('top', a + 'px');
            } else {
                // for IE
                $('.fixedHeader').css('top', 0);
            }
        } else {
            if (sticky === 'yes') {
                sticky = 'no';
                target.removeClass('fixedHeader');
                if (window.outerWidth < 768) {
                    for (let i = 1; i < 4; ++i){
                        detailRow.children().eq(i).css('display', 'block');
                    }
                }
                openCardDetails.css('height', 0);
                if (version != false) {
                    target.addClass('fixedHeaderIE');
                    openCardDetails.css('height', 0);
                }
            }
        }
    };

    modalWindow.on('scroll', this, scrollHandler);
}

