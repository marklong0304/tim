var step1Url = context + "api/step1.json";
var step2Url = context + "api/step2.json";
var step3Url = context + "api/step3.json";
var resultPageUrl = location.protocol + '//' + location.host + context;
var updateBaseCacheUrl = context + "api/updateBaseCache";
var updateFilteredCacheUrl = context + "api/updateFilteredCache";
var removeFromCacheUrl = context + "api/removeFromCache";
var quoteStorageId;

var depositDatePickerElement = $('#depositDatePicker');
var paymentDatePickerElement = $('#paymentDatePicker');
var destinationElement = $("#destination");
var departDatePickerElement = $("#departDatePicker");
var returnDatePickerElement = $("#returnDatePicker");
var form1Elements = [destinationElement, departDatePickerElement, returnDatePickerElement];
var ageElements = $(".ageField");
var residenceSelect = $("#residence");
var citizenshipSelect = $("#citizenship");
var form2Elements = [ageElements, residenceSelect, citizenshipSelect];
var form3Elements = [depositDatePickerElement, paymentDatePickerElement];
var departDatePicker, returnDatePicker, depositDatePicker, paymentDatePicker;
var confirmZeroCost = false;
var confirmZeroCostClass = 'confirmZeroCost';
var confirmOneDayDiffClass = 'confirmOneDayDiff';

var rb1 = $("#customRadio1");
var rb2 = $("#customRadio2");

var step1 = $(".step1");
var step2 = $(".step2");
var step3 = $(".step3");

var tripCost = $('.tripCost');
var mainFormTravelers = 1;
var globalCurrentSlide = 1;

String.prototype.reverse = function () {
    return this.split("").reverse().join("");
};

$(document).ready(function () {

    $('.timezoneOffset').val(new Date().getTimezoneOffset());
    $('#destination, #citizenship').find('option[value="US"]').attr('data-tokens', 'usa united states');

    departDatePicker = $('#departDatePicker').datepicker({
        orientation: "bottom auto",
        autoclose: true,
        startDate: '+1d'
    }).on('show', function () {
        closeBootstrapSelect();
    }).on('changeDate', function () {
        var dateMoment = moment(new Date(this.value));
        dateMoment.add(1, 'd');
        returnDatePicker.datepicker('setStartDate', dateMoment.toDate());
        var inputs = $(this).closest('form').find(':input');
        departDatePicker.datepicker('hide');
        inputs.eq(inputs.index(this) + 1).focus();
        validateForm1();
    });

    var dateMoment = moment(new Date());
    dateMoment.add(1, 'd');
    departDatePicker.datepicker('setStartDate', dateMoment.toDate());

    returnDatePicker = $('#returnDatePicker').datepicker({
        orientation: "bottom auto",
        autoclose: true,
        startDate: '+1d'
    }).on('show', function () {
        closeBootstrapSelect();
    }).on('changeDate', function () {
        var inputs = $(this).closest('form').find(':input');
        returnDatePicker.datepicker('hide');
        inputs.eq(inputs.index(this) + 1).focus();
        validateForm1();
    });

    var withoutNextFocus = false;
    depositDatePicker = $('#depositDatePicker').datepicker({
        orientation: "bottom auto",
        autoclose: true,
        endDate: "new Date()"
    }).on('changeDate', function () {
        if (!moment(depositDatePicker.val(), ["MM/DD/YYYY"], true).isValid()) {
            return;
        }
        var preExCondVal = $("#preExistingCondition").val();
        if (preExCondVal == "true") {
            paymentDatePicker.datepicker('setStartDate', this.value);
            /*if (paymentDatePicker.datepicker('getDate') == null) {
                paymentDatePicker.datepicker('setDate', depositDatePicker.val());
            }*/
        }
        //var inputs = $(this).closest('form').find(':input');
        depositDatePicker.datepicker('hide');
        /*if (!$("#noPayment").prop("checked")) {
            inputs.eq(inputs.index(this) + 1).focus();
        }*/

    });

    paymentDatePicker = $('#paymentDatePicker').datepicker({
        orientation: "bottom auto",
        autoclose: true,
        startDate: depositDatePicker.val()
    }).on('changeDate', function () {
        if (!moment(paymentDatePicker.val(), ["MM/DD/YYYY"], true).isValid()) {
            return;
        }
        if (depositDatePicker.datepicker('getDate') == null) {
            withoutNextFocus = true;
            depositDatePicker.datepicker('setDate', paymentDatePicker.val());
        }
        var inputs = $(this).closest('form').find(':input');
        paymentDatePicker.datepicker('hide');
        inputs.eq(inputs.index(this) + 1).focus();
    });

    $('.prev').click(function () {
        var slideNum = $(this).attr("data");
        changeSlide(slideNum - 1);
        sendRemoveFromCacheRequest();
    });

    $('.step-line > span, .step-line > .round').click(function (e) {
        e.stopPropagation();
        var slideIndex = $(this).parents('.step-line').index('.step-line');
        if (globalCurrentSlide <= slideIndex) {
            if (slideIndex == 1) {
                checkStep1();
            } else if (slideIndex == 2) {
                checkStep2()
            } else if (slideIndex == 3) {
                checkStep3();
            }
        } else {
            changeSlide(slideIndex + 1);
        }
    });

    $('.selectpicker').selectpicker({liveSearch: true, liveSearchStyle: 'contains', selectOnTab: true});
    $('.ageSelectpicker').selectpicker();

    if (destinationElement.val() == "") {
        $('.bootstrap-select [data-id=destination] .filter-option').text('Choose a Destination');
    }
    if (residenceSelect.val() == "") {
        $('.bootstrap-select [data-id=residence] .filter-option').text('Choose a Residence');
    }
    if (citizenshipSelect.val() == "") {
        $('.bootstrap-select [data-id=citizenship] .filter-option').text('Choose a Citizenship');
    }

    residenceSelect.on('change', function () {
        if ($(this).val().substring(0, 2) == 'US' && citizenshipSelect.val() == '') {
            citizenshipSelect.val("US");
            citizenshipSelect.selectpicker('refresh');
        }
    });

    step1.click(function (e) {
        e.preventDefault();
        doCheckStep1Modals(e);
    });

    function doCheckStep1Modals(){
        var confirmZeroCost = $('#' + confirmZeroCostClass + 'Val').val();
        var confirmOneDayDiff = $('#' + confirmOneDayDiffClass + 'Val').val();
        if (confirmZeroCost == "true" || parseFloat(tripCost.val().replace(/,/g, '')) > 0) {
            if (confirmOneDayDiff == 'true' || !checkOneDayDiff(returnDatePicker.val(), departDatePicker.val())){
                checkStep1();
            } else {
                showModal({title:'Check fields', text:mOneDayDiff, type:'error', fontSize:18, confirm:true, confirmClass: confirmOneDayDiffClass, callback: doCheckStep1Modals});
            }
        } else {
            showModal({title:'Check fields', text:mTripCostNotSet, type:'error', fontSize:18, confirm:true, confirmClass: confirmZeroCostClass, callback: doCheckStep1Modals});
        }
    }

    step2.click(function (e) {
        e.preventDefault();
        if($('#destination').val() == 'US' || $('#residence').val().substring(0, 2) == 'US') {
            $('#includesUS').val(true);
            checkStep2();
        } else {
            showIncludesUSModal(doIncludesUSModal);
        }
    });

    function doIncludesUSModal(includesUS) {
        $('#includesUS').val(includesUS);
        checkStep2();
    }

    step3.click(function (e) {
        step3.addClass('disabled-pointer');
        e.preventDefault();
        checkStep3();
    });

    tripCost.autoNumeric('init', {aSep: ',', aPad: false, dGroup: '3', aDec: '.', vMin: '0.00', vMax: '9999999.99'});
    tripCost.keyup(function () {
        var val = tripCost.val();
        if (/\.00$/.test(val)) {
            tripCost.val(val.replace(/\.00$/, ''));
        }
    });

    if ($("#currentStep").val() == 3) {
        checkStep1();
        checkStep2();
    }

    $("#notificationModal").modal('show');
    $("#registrationCompletedModal").modal('show');

    $('#registrationCompletedModal').on('shown.bs.modal', function () {
        var registrationModal = $(this);
        clearTimeout(registrationModal.data('hideInteval'));
        registrationModal.data('hideInterval', setTimeout(function () {
            registrationModal.modal('hide');
        }, 1000));
    });


    // TAB NAVIGATION

    // slide 1
    rb1.on('keydown', function (e) {
        if (e.keyCode == 9) {
            e.preventDefault();
            rb2.focus();
        }
    });

    rb2.on('keydown', function (e) {
        if (e.keyCode == 9) {
            e.preventDefault();
            step1.focus();
        }
    });

    step1.on('keydown', function (e) {
        if (e.keyCode == 9) {
            e.preventDefault();
            $('[data-id="destination"]').click();
        }
    });

    // slide 2
    $(".citizenship-bootstrap-select").on('keydown', function (e) {
        if (e.keyCode == 9) {
            e.preventDefault();
            step2.focus();
        }
    });

    step2.on('keydown', function (e) {
        if (e.keyCode == 9) {
            e.preventDefault();
            $("#travelers0\\.age").focus();
        }
    });

    // slide 3


    depositDatePickerElement.on('keydown', function (e) {
        if (e.keyCode == 9) {
            e.preventDefault();
            /*if (!$("#noPayment").prop("checked")) {
                paymentDatePicker.focus();
            }*/
        }
    });

    if (typeof(Storage) !== "undefined") {
        if (sessionStorage.getItem("appSessionExpired") == 'true') {
            $('#expiredSessionModal').modal('show');
            sessionStorage.appSessionExpired = false;
        }
    }

    var travelerCount = $("#mainFormTravelersCount").val();
    if (travelerCount > 0) {
        mainFormTravelers = travelerCount;
    }

    $(".input-group-addon").on('click', function(){
        $(this).prev('input').datepicker('show');
    });

    $('#noPayment').change(function (e) {
        if (e.target.checked) {
            paymentDatePicker.addClass("disabled-filter");
            paymentDatePicker.next().addClass('disabled-filter');
            paymentDatePicker.attr({placeholder: ''});
            paymentDatePicker.val("");
            updatePreExistingCondition(false);
        } else {
            paymentDatePicker.removeClass("disabled-filter");
            paymentDatePicker.next().removeClass('disabled-filter');
            paymentDatePicker.attr({placeholder: 'Select a Date'});
            updatePreExistingCondition(true);
        }
        validateForm3();
    });

    $.each(form1Elements, function (i, e) {
        $(e).on('change', function () {
            validateForm1();
        });
    });
    $.each(form2Elements, function (i, e) {
        $(e).on('change', function () {
            validateForm2();
        });
    });
    $.each(ageElements, function (i, e) {
        $(e).on('keyup change paste', function() {
            setTimeout(function() {
                validateForm2();
            }, 0);
        });
    });
    $.each(form3Elements, function (i, e) {
        $(e).on('change', function () {
            validateForm3();
        });
    });
    validateForm1();
    validateForm2();
    validateForm3();
});

function closeBootstrapSelect() {
    // prevent bootstrap-select open when clicking and pressing tab at once
    setTimeout(function () {
        $('.bootstrap-select').removeClass('open');
    }, 150);
}

function updatePreExistingCondition(val) {
    $("#preExistingCondition").val(val);
}

$('.bootstrap-select').on('hidden.bs.dropdown', function (e) {
    var closedEl = $(e.target).children().attr('data-id');
    switch (closedEl) {
        case 'travelers':
            updateTravelers();
            break;
    }
});

function updateTravelers() {
    var travelersQnty = $('button[data-id=travelers]').attr('title');
    if (travelersQnty > mainFormTravelers) {
        for (var i = 0; i < travelersQnty - mainFormTravelers; i++) {
            $('.form-group.ages > .input-group:last').clone().insertAfter(".form-group.ages > .input-group:last");
            $(".form-group.ages > .input-group:last > input").val('');
            var ageN = $(".form-group.ages > .input-group:last > input").attr('id').split('-')[1];
            $(".form-group.ages > .input-group:last > input").attr({id: 'age-' + (parseInt(ageN) + 1)});
            $(".form-group.ages > .input-group:last").children().removeClass('invalid');
        }

    }
    if (travelersQnty < mainFormTravelers) {
        $('.form-group.ages > .input-group').slice(travelersQnty - mainFormTravelers).remove();
    }
    mainFormTravelers = travelersQnty;
    ageElements = $(".ageField");
    $.each(ageElements, function (i, e) {
        $(e).on('keyup change paste', function() {
            setTimeout(function() {
                validateForm2();
            }, 0);
        });
    });
    validateForm2();
}

function fillAges() {
    var agesArr = [];
    var ages = $(".ageField");
    var agesSize = ages.length;
    for (var i = 0; i < agesSize; i++) {
        if (ages[i].value != "") {
            agesArr.push(ages[i].value);
        }
    }
    $("#travelersString").val(JSON.stringify(agesArr));
}

function checkStep1() {
    clearErrors();
    if (validateDate(departDatePicker.val()) && validateDate(returnDatePicker.val())) {
        $('#successVerifiedMailMsg').hide();
        $('#errorVerifiedMailMsg').hide();
        step1.addClass("disabled-pointer");
        var msg = $("#step1Form").serialize();
        $.post(step1Url, msg, function (data) {
            if (data.status == 'success') {
                changeSlide(2);
                if (residenceSelect.val().substring(0, 2) == 'US' && citizenshipSelect.val() == '') {
                    citizenshipSelect.val("US");
                }
                $('.selectpicker').selectpicker('refresh')
                validateForm2();
            } else if (data.status == 'sessionExpired') {
                reloadIfSessionExpired();
            } else {
                clearErrors();
                printErrors(data.errors);
            }
            step1.removeClass("disabled-pointer");
        }).fail(function () {
            printError(null, defErrMsg);
            step1.removeClass("disabled-pointer");
        });
    } else {
        if (!validateDate(departDatePicker.val())) {
            printErrors(JSON.parse('[{"codes":["step1QuoteRequestForm.departDate","departDate","java.util.Date",""],"defaultMessage":"Depart date is invalid","field":"departDate"}]'));
        }
        if (!validateDate(returnDatePicker.val())) {
            printErrors(JSON.parse('[{"codes":["step1QuoteRequestForm.returnDate","returnDate","java.util.Date",""],"defaultMessage":"Return date is invalid","field":"returnDate"}]'));
        }
    }
}

function checkStep2() {
    fillAges();
    step2.addClass("disabled-pointer");
    var msg = $("#step2Form").serialize();
    $.post(step2Url, msg, function (data) {
        clearErrors();
        if (data.status == 'success') {
            $('#quoteStorageId').val(data.quoteStorageId);
            if (data.zeroCost) {
                $("#step2Form").hide();
                $("#searchResult").click();
            } else {
                quoteStorageId = data.quoteStorageId;
                sendUpdateBasedCache();
                changeSlide(3);
                step2.removeClass("disabled-pointer");
                var preExistingCondition = $("#preExistingCondition");
                if (preExistingCondition.val() == "false") {
                    $('#noPayment').attr("checked", true);
                    paymentDatePicker.addClass("disabled-filter");
                    paymentDatePicker.next().addClass('disabled-filter');
                    paymentDatePicker.attr({placeholder: ''});
                    paymentDatePicker.val("");
                    updatePreExistingCondition(false);
                }
                validateForm3();
            }
        } else if (data.status == 'sessionExpired') {
            reloadIfSessionExpired();
        } else {
            printErrors(data.errors);
            step2.removeClass("disabled-pointer");
        }
    }).fail(function () {
        printError(null, defErrMsg);
        step2.removeClass("disabled-pointer");
    });
}

function checkStep3() {
    $('#step3QuoteStorageId').val(quoteStorageId);
    var msg = $("#step3Form").serialize();
    $.post(step3Url, msg, function (data) {
        if (data.status == 'success') {
            window.location.href = resultPageUrl + "comparePlans/?quoteId=" + quoteStorageId;
        } else if (data.status == 'sessionExpired') {
            reloadIfSessionExpired();
        } else {
            printErrors(data.errors);
            step3.removeClass('disabled-pointer');
            $(".form3").show();
        }
    }).fail(function () {
        step3.removeClass('disabled-pointer');
        printError(null, defErrMsg);
    });
}

function reloadIfSessionExpired() {
    if (typeof(Storage) !== "undefined") {
        sessionStorage.appSessionExpired = true;
    }
    location.reload();
}

function sendUpdateBasedCache() {
    $.post(updateBaseCacheUrl, "quoteId=" + quoteStorageId)
        .fail(function () {
            printError(null, defErrMsg);
        });
}

function sendUpdateFilteredCache() {
    $.post(updateFilteredCacheUrl, "quoteId=" + quoteStorageId)
        .fail(function () {
            printError(null, defErrMsg);
        });
}

function sendRemoveFromCacheRequest() {
    if (quoteStorageId) {
        $.post(removeFromCacheUrl, "quoteId=" + quoteStorageId)
            .fail(function () {
                printError(null, defErrMsg);
            });
    }
}

function printErrors(errors) {
    var allErrors = '';
    var isAddedTravelersError;
    $.each(errors, function (key, value) {
        if (value.defaultMessage) {
            if (value.field && value.field.indexOf("travelers") == -1 || (value.field.indexOf("travelers") != -1 && !isAddedTravelersError)) {
                if (allErrors != '') {
                    allErrors = allErrors + "<br/>";
                }
                allErrors = allErrors + value.defaultMessage;
            }
        }
        if (value.field == "travelersString") {
            $(".ageField").first().addClass("invalid");
        } else if (value.field.indexOf("travelers") != -1) {
            isAddedTravelersError = true;
            var inputs = $(".ageField");
            var index = value.field.match(/\d+/);
            $(inputs[index]).addClass("invalid");
        } else if (value.field == "destinationCountry") {
            destinationElement.parent().find(":button").addClass("invalid");
        } else if (value.field == "residentCountry") {
            residenceSelect.parent().find(":button").addClass("invalid");
        } else if (value.field == "citizenCountry") {
            citizenshipSelect.parent().find(":button").addClass("invalid");
        }
        else {
            $("input[name=" + value.field + "]").addClass("invalid");
        }
    });
    printError(allErrors, defErrMsg);
}

function clearErrors() {
    $(".invalid").removeClass("invalid");
    $("#errors").html("");
}

function validateDate(date) {
    return moment(date, ["MM/DD/YYYY"], true).isValid();
}

function checkOneDayDiff(date1, date2) {
    return (moment(date1, ["MM/DD/YYYY"]).diff(moment(date2, ["MM/DD/YYYY"]), 'day') == 1);
}

function changeSlide(currentSlide) {
    globalCurrentSlide = currentSlide;
    var stepLine = $('.steps .step-line');
    stepLine.removeClass('active underline');
    stepLine.slice(0, currentSlide - 1).addClass('underline');
    stepLine.slice(0, currentSlide).addClass('active');
    var form = $('.forminfo > .form');
    form.removeClass('active').hide();
    form.eq(currentSlide - 1).addClass('active').show();
    resizeDiv();
}

function validateForm1() {
    var errors = false;
    $.each(form1Elements, function (i, e) {
        if ($(e).val().length == 0) {
            errors = true;
        }
    });
    if (errors) {
        $(".step1").addClass("disabled-filter");
        disabledStep(2);
        disabledStep(3)
    } else {
        $(".step1").removeClass("disabled-filter");
        enabledStep(2);
    }
}

function validateForm2() {
    var errors = false;
    $.each(form2Elements, function (i, e) {
        if ($(e).val().length == 0) {
            errors = true;
        }
    });
    $.each(ageElements, function (i, e) {
        if ($(e).val().length == 0) {
            errors = true;
        }
    });
    if (errors) {
        $(".step2").addClass("disabled-filter");
        disabledStep(3);
    } else {
        $(".step2").removeClass("disabled-filter");
        enabledStep(3);
    }
}

function validateForm3() {
    var errors = false;

    if ($(depositDatePickerElement).val().length == 0) {
        errors = true;
    }

    if ($(paymentDatePickerElement).val().length == 0 && !$('#noPayment').prop("checked")) {
        errors = true;
    }

    if (errors) {
        $(".step3").addClass("disabled-filter");
    } else {
        $(".step3").removeClass("disabled-filter");
    }
}

function enabledStep(num) {
    $(".step-line-"+num).removeClass("disabled-filter");
}

function disabledStep(num) {
    $(".step-line-"+num).addClass("disabled-filter");
}