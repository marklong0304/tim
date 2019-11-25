/**
 * Created by artur on 16.09.2016.
 */

var cancellationGroup = ["hurricane-&-weather", "terrorism", "financial-default", "pre-ex-waiver", 'look-back-period',
    "employment-layoff", "cancel-for-medical", "cancel-for-work-reasons", "cancel-for-any-reason", "trip-interruption"];
var depositAndPaymentGroup = ["pre-ex-waiver", "cancel-for-any-reason", "cancel-for-work-reasons", "pre-ex-waiver-on-trip"];
var zeroCostGroup = ["trip-cancellation", "hurricane-&-weather", "terrorism", "financial-default", "pre-ex-waiver", 'look-back-period',
    "employment-layoff", "cancel-for-medical", "cancel-for-work-reasons", "cancel-for-any-reason", "trip-interruption"];
var limitedGroup = ["look-back-period"];
var interruptionAirOnly = 'trip-interruption-return-air-only';

var depositAndPaymentDates;
var zeroCost;
var planType;

var filters = $(".filters");
var filtersInner = $(".filters-inner2");

var sticky = false;
var stickyItem;
var isStick = false;

function init() {
    loadCancellationGroup();
}

init();

function loadCancellationGroup() {
    var cancellationGroupUrl = context + 'results/cancellationGroup';
    $.ajax({
        url: cancellationGroupUrl,
        method: "GET",
        success: function (data) {
            zeroCostGroup = data;
        }
    });
}

$("input.simple:checkbox").on('click', function (event) {
    if (!checkZeroCost(this.name) || !checkPaymentAndDepositDates(this.name)) {
        event.preventDefault();
    } else {
        updateLookBackPeriod();
        updateCancellationGroup();
        if (currentPage == 'results') {
            sendUpdateCategoriesRequest($("#updateCategoriesForm"));
        } else {
            sendUpdateCompareCategoriesRequest($("#updateCategoriesForm"));
        }
        $.get(systemSaveQuoteUrl);
    }
});

$('.catalog input[type=checkbox]').click(function (e) {
    e.preventDefault();
    var sel = $(e.target).parent().parent().find('select');
    sel.toggle();
    sel.val('-1');
    if (!this.checked) {
        updateCancellationGroup();
        if (currentPage == 'results') {
            sendUpdateCategoriesRequest($("#updateCategoriesForm"));
        } else {
            sendUpdateCompareCategoriesRequest($("#updateCategoriesForm"));
        }
        $.get(systemSaveQuoteUrl);
    }
    updateSticky(false);
});

$('.catalog select').click(function (e) {
    e.preventDefault();
});

$(".catalog-select").change(function (e) {
    if (!checkZeroCost(this.name) || !checkPaymentAndDepositDates(this.name)) {
        $(this).val('-1');
        e.preventDefault();
    } else {
        updateCancellationGroup();
        if (currentPage == 'results') {
            sendUpdateCategoriesRequest($("#updateCategoriesForm"));
        } else {
            sendUpdateCompareCategoriesRequest($("#updateCategoriesForm"));
        }
    }
    updateSticky(false);
});

$(".reset").click(function (event) {
    event.preventDefault();
    $('input:checkbox').removeAttr('checked');
    $(".catalog-select").hide().val("-1");
    clearCategoriesInTopFilter();
    sendResetRequest();
});

$("input:radio[name ='planType']").on('click', function () {
    sendChangePlanTypeRequest($("#planTypeForm"));
});

function updateCategories(categories, showCountAfterEnabledFilter) {
    // clear all categories in top filter
    clearCategoriesInTopFilter();
    $.each(categories, function (index, category) {
        var select = $("select[name='" + category.code + "']");
        var categoryElem = $("#category-" + category.id);
        if (category.disabled == true && category.checked == false) {
            categoryElem.prop("disabled", true);
            categoryElem.prop("checked", false);
            if (showCountAfterEnabledFilter && category.type != 'CONDITIONAL') {
                $("#category-count-" + category.id).text('(' + category.countAfterEnabled + ')').addClass('disabled-link').attr("disabled", "disabled").on("click", function () {
                    return false;
                });
            }
            if (category.type == "CATALOG") {
                if (select != undefined) {
                    if ($(select).is(":visible")) {
                        $(select).toggle();
                        $(select).val("-1");
                    }
                }
            }
        } else {
            categoryElem.prop("disabled", false);
            if (showCountAfterEnabledFilter && category.type != 'CONDITIONAL') {
                $("#category-count-" + category.id).text('(' + category.countAfterEnabled + ')').removeClass('disabled-link').removeAttr("disabled").off("click");
            }
            if (category.checked == true) {
                categoryElem.prop("checked", "checked");
                updateSelectedCheckBox(categoryElem);
            } else {
                categoryElem.removeProp("checked");
            }
            if (category.type == "CATALOG") {
                if (select != undefined) {
                    if (!category.checked) {
                        // $(select).hide();
                        var firstOption = $('<option></option>').attr("value", "-1").text("Not selected");
                        $(select).empty().append(firstOption);
                    } else {
                        $(select).empty();
                    }
                    $.each(category.values, function (index, value) {
                        var caption = value.caption;
                        if (value.valueType == "FIX") {
                            caption = "$" + value.value;
                        } else if (value.valueType == "PERCENT") {
                            caption = value.value + "%";
                        }
                        var option = $('<option></option>').attr("value", value.value).text(caption);
                        $(select).append(option);
                    });
                    if (category.currentValue == undefined) {
                        $(select).val(-1);
                    } else {
                        var currentVal = category.currentValue;
                        var isFindLastVal = _.find(category.values, function (o) {
                            return o.intValue == currentVal;
                        });
                        if (!isFindLastVal) {
                            var newCategoryVal = _.find(category.values, function (o) {
                                if (category.code == 'look-back-period') {
                                    return o.intValue <= currentVal;
                                } else {
                                    return o.intValue >= currentVal;
                                }
                            });
                        }
                        if (newCategoryVal) {
                            currentVal = newCategoryVal.intValue;
                        }
                        $(select).val(currentVal)
                    }
                }
            }
            if (responseType == 'ORIGINAL' && category.checked == true) {
                categoryElem.prop("checked", 'checked');
            }
        }
    });
    updateCounter();
}

function disableFilters() {
    $(".summary").addClass("disabled-filter");
    $(".filters").addClass("disabled-filter");
    $(".results-block").addClass("disabled-filter");

    hideAllMessages();

    $("#loading").show();
    $("#load").hide();
    $("#plan-msg").hide();
}

function enableFilters() {
    $(".summary").removeClass("disabled-filter");
    $(".filters").removeClass("disabled-filter");
    $(".results-block").removeClass("disabled-filter");

    var load = $("#load");
    if (!load.is(":visible")) {
        load.show();
        $("#loading").hide();
    }

    $("#plan-msg").show();
}

function hideAllMessages() {
    $("#loading, #msg-original-plan, #msg-filtered-plan, #msg-empty-filtered-plan, #msg-empty-original-plan").hide();
}

function updateCancellationGroup() {
    updateCancellationGroupVisibility();
    var cancellation = $("input[name='trip-cancellation']");
    var cancellationTop = $(".filter-tag[name='trip-cancellation']");
    var cancellationCheckboxChecked = cancellation.prop("checked");
    var cancellationGroupChecked = false;
    $.each(cancellationGroup, function (index, val) {
        if (!cancellationGroupChecked) {
            var current = $("input[name=\"" + val + "\"]");
            if (current.length == 0) {
                current = $("select[name=\"" + val + "\"]");
                cancellationGroupChecked = (current.val() > 0);
            } else {
                if (!current.hasClass("ex")) {
                    cancellationGroupChecked = current.prop("checked");
                } else {
                    cancellationGroupChecked = (current.val() > 0);
                }
            }

        }
    });
    if (cancellationGroupChecked) {
        cancellation.parent().addClass('disabled-filter');
        cancellationTop.addClass('disabled-pointer');
        if (!cancellationCheckboxChecked) {
            cancellation.prop("checked", true);
        }
    } else if (!cancellationGroupChecked) {
        cancellation.parent().removeClass('disabled-filter');
        cancellationTop.removeClass('disabled-pointer');
    }
}

function updateCancellationGroupVisibility() {
    var cancellationHeader = $("[data-group-name='Cancellation']");
    var medicalHeader = $("[data-group-name='Medical']");
    var closedMedical = medicalHeader.find('i').attr('closed');
    var closedCancellation = cancellationHeader.find('i').attr('closed');
    //var cancellationTableHeader = $("[arrow='par-Cancellation']");
    //var cancellationTableRows = $("[child='par-Cancellation']");

    if (planType == "LIMITED") {
        _.each(zeroCostGroup, function (value) {
            var current = $("[data-name=\"" + value + "\"]").parent();
            var found = $.inArray(value, limitedGroup) > -1;
            if (found) {
                current.parent().attr("data-block", 2);
                medicalHeader.parent().append(current.parent().detach());
                if (closedMedical == 'true') {
                    current.parent().hide();
                } else {
                    current.parent().show();
                }
            } else {
                current.hide();
            }
        });
        $("[data-name=\"" + interruptionAirOnly + "\"]").parent().parent().removeClass("hidden");
        if ($('[data-group-name="Loss or Delay"]').attr("closed") == 'true') {
            $("[data-name=\"" + interruptionAirOnly + "\"]").parent().parent().hide();
        }
        cancellationHeader.hide();
        medicalHeader.parent().css("padding-top", "0px");
        //cancellationTableHeader.hide();
        //cancellationTableRows.hide();
    } else {
        _.each(zeroCostGroup, function (value) {
            var current = $("[data-name=\"" + value + "\"]").parent();
            var found = $.inArray(value, limitedGroup) > -1;
            if (found) {
                current.parent().attr("data-block", 1);
                cancellationHeader.parent().append(current.parent().detach());
                if (closedCancellation == 'true') {
                    current.parent().hide();
                } else {
                    current.parent().show();
                }
            } else {
                current.show();
            }
        });
        $("[data-name=\"" + interruptionAirOnly + "\"]").parent().parent().addClass("hidden");
        if ($('[data-group-name="Loss or Delay"]').attr("closed") == 'true') {
            $("[data-name=\"" + interruptionAirOnly + "\"]").parent().parent().hide();
        }
        cancellationHeader.show();
        medicalHeader.parent().css("padding-top", "25px");
        //cancellationTableHeader.show();
        //cancellationTableRows.show();
    }
    //updateSticky(true);
}

function checkZeroCost(code) {
    if (zeroCost && ($.inArray(code, zeroCostGroup) != -1)) {
        if (!depositAndPaymentDates && ($.inArray(code, depositAndPaymentGroup) != -1)) {
            $("#zeroCostMessage").text("For Trip Cancellation benefit the Trip Cost must be more than zero and Initial Deposit Date and Final Payment Date filled. Enter Trip Cost?")
        } else {
            $("#zeroCostMessage").text("For Trip Cancellation benefit the Trip Cost must be more than zero. Enter Trip Cost?")
        }
        $("#linkModal").modal({
                backdrop: 'static',
                keyboard: false,
                show: true
            }
        );
        return false;
    }
    return true;
}

function checkPaymentAndDepositDates(code) {
    if (!depositAndPaymentDates && ($.inArray(code, depositAndPaymentGroup) != -1)) {
        $("#depositAndPaymentModal").modal({
                backdrop: 'static',
                keyboard: false,
                show: true
            }
        );
        return false;
    }
    return true;
}


function updateLookBackPeriod() {
    var lookBack = $("select[name='look-back-period']");
    var preExWaiver = $("input[name='pre-ex-waiver']");
    if (preExWaiver.prop('checked')) {
        if (lookBack.is(":visible")) {
            lookBack.toggle();
        }
        lookBack.val("-1");
    }
}

function updateSticky(status) {
    if (status != undefined) {
        sticky = status;
    }
    if (filters.height() > filtersInner.height() + 500) {
        stickyItem = filtersInner.stick_in_parent()
            .on("sticky_kit:stick", function (e) {
                isStick = true;
            })
            .on("sticky_kit:unstick", function (e) {
                isStick = false;
                stickyItem.show();
                stickyItem.css("margin-left", 0);
            })
            .on("sticky_kit:bottom", function (e) {
                isStick = false;
                stickyItem.show();
                stickyItem.css("margin-left", 0);
            })
            .on("sticky_kit:unbottom", function (e) {
                isStick = true;
            });
    }
    if (!sticky && stickyItem != null) {
        stickyItem.trigger("sticky_kit:recalc");
        sticky = true;
    }
}

//Filter Open All

$("#openf").click(function (e) {
    e.preventDefault();
    $(".filter-expand[closed='true']").slideToggle("fast", function () {
        updateSticky(false);
    });
    $(".filter-expand[closed='true']").attr('closed', false);
    $(".chevron-close[closed='true']").removeClass("chevron-close").addClass("chevron-open");
    $(".chevron-open[closed='true']").attr('closed', false);
    $(".filter-name[closed='true']").attr('closed', false);
    updTopBlockSize();
    updateSticky(false);
});

//Filter Close All

$("#closef").click(function (e) {
    e.preventDefault();
    $(".filter-expand[closed='false']").slideToggle("fast", function () {
        updateSticky(false);
    });
    $(".filter-expand[closed='false']").attr('closed', true);
    $(".chevron-open[closed='false']").removeClass("chevron-open").addClass("chevron-close");
    $(".chevron-close[closed='false']").attr('closed', true);
    $(".filter-name[closed='false']").attr('closed', true);
    updTopBlockSize();
});

//Filter Reset All
$("#resetf").click(function (e) {
    e.preventDefault();
    $(".checkbox-filter").prop("checked", false);
    $('.counter_div').empty();
    $('#counter_text').text('0');
    $('#selected-checkboxes').hide();
    updTopBlockSize();
    updateSticky(false);
});

//Filter Clear All
function clearCategoriesInTopFilter() {
    $('.counter_div').empty();
    $('#counter_text').text('0');
    $('#selected-checkboxes').hide();
    updTopBlockSize();
}


