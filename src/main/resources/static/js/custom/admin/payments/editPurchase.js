$(function () {
    var departDatePicker = $('#departDatePicker').datepicker({
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

    var returnDatePicker = $('#returnDatePicker').datepicker({
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

    var depositDatePicker = $('#depositDatePicker').datepicker({
        orientation: "auto",
        autoclose: true,
        endDate: "new Date()"
    }).on('changeDate', function () {
        if (!moment(depositDatePicker.val(), ["MM/DD/YYYY"], true).isValid()) {
            return;
        }
        paymentDatePicker.datepicker('setStartDate', this.value);
        if (paymentDatePicker.datepicker('getDate') == null) {
            paymentDatePicker.datepicker('setDate', depositDatePicker.val());
        }
        depositDatePicker.datepicker('hide');
    });

    var paymentDatePicker = $('#paymentDatePicker').datepicker({
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

    var purchaseDatePicker = $('#purchaseDatePicker').datepicker({
        orientation: "auto",
        autoclose: true
    }).on('changeDate', function () {
        if (!moment(purchaseDatePicker.val(), ["MM/DD/YYYY"], true).isValid()) {
            return;
        }
        purchaseDatePicker.datepicker('hide');
    });

    $('#countrySelect').change(function () {
        var selectedCode = $('#countrySelect').val();
        if (selectedCode == 'US' || selectedCode == 'CA') {
            $('#stateDiv').show();
        } else {
            $('#stateDiv').hide();
        }
    });

    var commissionReceiveDatePicker = $('#commissionReceiveDatePicker').datepicker({
        orientation: "auto",
        autoclose: true
    }).on('changeDate', function () {
        if (!moment(commissionReceiveDatePicker.val(), ["MM/DD/YYYY"], true).isValid()) {
            return;
        }
        commissionReceiveDatePicker.datepicker('hide');
    });

    initDatePickers('.dobDatePicker');

    var money = $('.money');
    money.autoNumeric('init', {aSep: ',', aPad: false, dGroup: '3', aDec: '.', vMin: '0.00', vMax: '9999999.99'});
    money.keyup(function () {
        var val = money.val();
        if (/\.00$/.test(val)) {
            tripCost.val(val.replace(/\.00$/, ''));
        }
    });
});

function initDatePickers(datePickerSelector) {
    var datePicker = $(datePickerSelector).datepicker({
        orientation: "auto",
        autoclose: true
    }).on('changeDate', function () {
        if (!moment(datePicker.val(), ["MM/DD/YYYY"], true).isValid()) {
            return;
        }
        datePicker.datepicker('hide');
    });
}

function addTravelerRow() {
    var travelerCount = parseInt($('#travelerCount').val());
    var newRow = '<div class="row" id="row-' + travelerCount + '">'
        + $('#newTravelerRow').html().replace(/template-/g, '').replace(/{n}/g, travelerCount)
        + '</div>';
    console.log(newRow);
    $(newRow).insertBefore('#newTravelerRow');
    initDatePickers('#birthday-' + travelerCount);
    $('#travelerCount').val(travelerCount + 1);
}

function closeBootstrapSelect() {
    // prevent bootstrap-select open when clicking and pressing tab at once
    setTimeout(function () {
        $('.bootstrap-select').removeClass('open');
    }, 150);
}

function deleteTravelerRow(rowNum) {
    $('#row-' + rowNum).remove();
}

function cleanUpMoneyFields() {
    $('.money').each(function() {
        var el = $(this);
        el.val(el.val().replace(',', ''));
    });
}