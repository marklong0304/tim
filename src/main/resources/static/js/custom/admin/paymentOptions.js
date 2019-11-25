$('document').ready(function () {
    function bindPaymentOptions(parentSelector, selectorNamePrefix) {

        $(parentSelector + ' [name="' + selectorNamePrefix + 'paymentOption"]').bind("change", function () {

            $(parentSelector + ' .bankName').prop('readonly', $(this).val() != 'ACH');
            $(parentSelector + ' .bankName').prop('required', $(this).val() == 'ACH');

            $(parentSelector + ' .bankRouting').prop('readonly', $(this).val() != 'ACH');
            $(parentSelector + ' .bankRouting').prop('required', $(this).val() == 'ACH');

            $(parentSelector + ' .account').prop('readonly', $(this).val() != 'ACH');
            $(parentSelector + ' .account').prop('required', $(this).val() == 'ACH');

            $(parentSelector + ' .paypalEmailAddress').prop('readonly', $(this).val() != 'PAYPAL');
            $(parentSelector + ' .paypalEmailAddress').prop('required', $(this).val() == 'PAYPAL');

            $(parentSelector + ' .checkNumber').prop('readonly', $(this).val() != 'CHECK');
        });
    }

    bindPaymentOptions('#individualTab', 'userInfo.');
    bindPaymentOptions('#companyTab', 'company.');
    bindPaymentOptions('#companyPaymentOption', '');
    bindPaymentOptions('#paymentOption', '');
});