/**
 * Created by artur on 25.10.2016.
 */

var getEditQuoteRequestFormUrl = context + 'results/editQuoteRequest/' + quoteId;
var editQuote = $("#editQuote");
var page = editQuote.attr("data");
var getPageParam = '', postPageParam = '';
var stopRequest = context + 'comparePlans/stopRequest';

function openEditQuote() {
    if (page !== "purchase") {
        if(window.location.href.indexOf("comparePlans") > -1) getPageParam = '?comparePlans=true';
        sendStopRequest();
    } else {
        getPageParam = '?page=purchase&policyUniqueCode=' + $("input[name='policyUniqueCode']").val() + '&purchaseRequestId=' + $("input[name='purchaseRequestId']").val();
        postPageParam = '&page=purchase';
    }
    var deferred = $.Deferred();
    loadEditQuote(deferred);
    deferred.done(function () {
        $("#editQuoteModal").modal({
                backdrop: 'static',
                keyboard: false,
                show: true
            }
        );
        $("html").css({"overflow-y": "hidden"});
    });
}

function sendStopRequest() {
    requestId = $("#requestId").val();
    $.post(stopRequest + '/' + requestId);
}

function loadEditQuote(deferred) {
    var quoteRequest = JSON.parse($("#quoteRequestJson").val());
    $.ajax({
        url: getEditQuoteRequestFormUrl + getPageParam,
        method: "GET",
        data: {quoteRequestJson: JSON.stringify(quoteRequest)},
        success: function (data) {
            var compiledProductsTemplate = Handlebars.getTemplate('editQuoteNewModal');
            var editQuoteModalHtml = compiledProductsTemplate(data);
            $("#editQuoteModal").html(editQuoteModalHtml);
            initEditForm();
            deferred.resolve();
            setAgeFields();
        },
        error: deferred.reject
    });

}

function initEditForm() {
    var departDatePicker, returnDatePicker, depositDatePicker, paymentDatePicker;
    var tripCost = $('.tripCost');
    var mainFormTravelers = 1;

    $('#destination, #citizenship').find('option[value="US"]').attr('data-tokens', 'usa united states');

    $('.timezoneOffset').val(new Date().getTimezoneOffset());

    $('.selectpicker').selectpicker({liveSearch: true, liveSearchStyle: 'contains', selectOnTab: true});

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
    });

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
    });

    var dateMoment = moment(new Date());
    dateMoment.add(1, 'd');
    departDatePicker.datepicker('setStartDate', dateMoment.toDate());

    tripCost = $('.tripCost');
    tripCost.autoNumeric('init', {aSep: ',', aPad: false, dGroup: '3', aDec: '.', vMin: '0.00', vMax: '9999999.99'});
    tripCost.keyup(function () {
        var val = tripCost.val();
        if (/\.00$/.test(val)) {
            tripCost.val(val.replace(/\.00$/, ''));
        }
    });

    $('.ageSelectpicker').selectpicker();

    depositDatePicker = $('#depositDatePicker').datepicker({
        orientation: "auto",
        autoclose: true,
        endDate: "new Date()"
    }).on('changeDate', function () {
        if (!moment(depositDatePicker.val(), ["MM/DD/YYYY"], true).isValid()) {
            return;
        }
        var preExCondVal = $("#preExistingCondition").val();
        if (preExCondVal == "true") {
            paymentDatePicker.datepicker('setStartDate', this.value);
            if (paymentDatePicker.datepicker('getDate') == null) {
                paymentDatePicker.datepicker('setDate', depositDatePicker.val());
            }
        }
        depositDatePicker.datepicker('hide');
    });

    paymentDatePicker = $('#paymentDatePicker').datepicker({
        orientation: "auto",
        autoclose: true,
        startDate: depositDatePicker.val()
    }).on('changeDate', function () {
        if (!moment(paymentDatePicker.val(), ["MM/DD/YYYY"], true).isValid()) {
            return;
        }
        if (depositDatePicker.datepicker('getDate') == null) {
            depositDatePicker.datepicker('setDate', paymentDatePicker.val());
        }
        paymentDatePicker.datepicker('hide');
    });

    var travelerCount = $("#mainFormTravelersCount").val();
    if (travelerCount > 0) {
        mainFormTravelers = travelerCount;
    }

    $('.bootstrap-select').on('hidden.bs.dropdown', function (e) {
        var closedEl = $(e.target).children().attr('data-id');
        switch (closedEl) {
            case 'travelers':
                mainFormTravelers = updateTravelers(mainFormTravelers);
                break;
        }
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
    });

    var preExistingCondition = $("#preExistingCondition");
    if (preExistingCondition.val() == "false") {
        $('#noPayment').attr("checked", true);
        paymentDatePicker.addClass("disabled-filter");
        paymentDatePicker.next().addClass('disabled-filter');
        paymentDatePicker.attr({placeholder: ''});
        paymentDatePicker.val("");
        updatePreExistingCondition(false);
    }

    $('#includesUSCheckbox').change(function (e) {
        $('#includesUS').val(e.target.checked);
    });
    if($('#includesUS').val() == "true") {
        $('#includesUSCheckbox').attr("checked", true);
    }

    adjustIncludesUSVisibility();
    $('#destination, #residence').on('change', function() {
        adjustIncludesUSVisibility();
    });

    $("#saveEditQuote").on("click", function () {
        // prepare request
        fillAges();
        let ageEdit = $('input[name*="travelers.age"]');
        $.each(ageEdit, function (i, e) {
          if (($(e).val() == '') && ($(e).length == 1)) {
            showError($(e));
          } else if (($(e).val().length == 8) || ($(e).val().length == 9) ||
            (($(e).val().length == 10) && (parseInt($(e).val()) > 12))) {
            showError($(e));
          } else {
            removeError($(e));
          }
        });
        // after edit we have a new request
        $('#requestId').val('');
        $.ajax({
            url: getEditQuoteRequestFormUrl,
            method: "POST",
            data: $("#editQuoteForm").serialize() + postPageParam,
            success: function (data) {
                if (data.equals == true){
                    $("#editQuoteModal").modal('hide');
                    return;
                }
                if (data.redirectUrl != undefined) {
                    document.location.href = context + "comparePlans/?quoteId=" + data.redirectUrl;
                } else if (data.quoteId != undefined) {
                    var purchaseUrl = context + 'results/purchasePage/' + data.quoteId;
                    var editPurchaseForm = $("#editPurchaseForm");
                    editPurchaseForm.attr('action', purchaseUrl);
                    $("#editUniqueCode").val(data.policyUniqueCode);
                    $("#editPurchaseQuoteId").val(data.quoteId);
                    $("#editPurchaseRequestId").val(data.requestId);
                    editPurchaseForm.submit();
                } else {
                    var errorText = '';
                    _.forEach(data.errors, function(error) {
                        errorText += error.defaultMessage;
                        errorText += '<br/>'
                    });
                    showModal({title:'Check fields', text:errorText, type:'error', fontSize:18});
                }
            }
        });
    });

    $(".input-group-addon").on('click', function(){
        $(this).prev('input').datepicker('show');
    });

    $( "#editQuoteModal" ).scroll(function() {
        $('.dateInput').datepicker('place')
    });
}

function adjustIncludesUSVisibility() {
    if($('#destination').val() == 'US' || $('#residence').val().substring(0, 2) == 'US') {
        $('#includesUSCheckbox').attr('checked', 'checked');
        $('#includesUS').val(true);
        $('#includesUSCheckbox').hide();
        $('#includesUSCheckboxLabel').hide();
    } else {
        $('#includesUSCheckbox').show();
        $('#includesUSCheckboxLabel').show();
    }
}

function updateTravelers(mainFormTravelers) {
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
    setAgeFields();
    return travelersQnty;
}

function setAgeFields() {
    if ($('#age-3').val() !== undefined && $(window).width() >= '768') {
        $(".form-group.ages").css('margin-left', '80px');
    } else if($(window).width() < '768' && $(window).width() > '594') {
        $(".form-group.ages").css('margin-left', '145px');
    } else if($(window).width() <= '594' && $(window).width() > '553') {
        $(".form-group.ages").css('margin-left', '110px');
    } else if ($(window).width() <= '553' && $(window).width() > '460') {
        $(".form-group.ages").css('margin-left', '100px');
    } else if($(window).width() <= '460') {
        $(".form-group.ages").css('margin-left', '80px');
    }else {
        $(".form-group.ages").css('margin-left', '161px');
    }
}

function showError(element) {
    $(element).addClass("invalid");
}

function removeError(element) {
    $(element).removeClass("invalid");
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

function updatePreExistingCondition(val) {
    $("#preExistingCondition").val(val);
}

function closeBootstrapSelect() {
    // prevent bootstrap-select open when clicking and pressing tab at once
    setTimeout(function () {
        $('.bootstrap-select').removeClass('open');
    }, 150);
}

$('#editQuoteModal').on('hidden.bs.modal', function () {
    $("html").css({"overflow-y": "auto"});
});

