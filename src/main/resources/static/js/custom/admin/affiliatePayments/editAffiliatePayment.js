$(function () {
    initDatePickers('.datePicker');
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

function cleanUpMoneyFields() {
    $('.money').each(function() {
        var el = $(this);
        el.val(el.val().replace(',', ''));
    });
}

function closeBootstrapSelect() {
    // prevent bootstrap-select open when clicking and pressing tab at once
    setTimeout(function () {
        $('.bootstrap-select').removeClass('open');
    }, 150);
}