var lastValue;
$('document').ready(function () {
    var percentType = $("#percentType");
    var linkPercentType = $("#linkPercentType");
    var quoteLinkPercentType = $("#quoteLinkPercentType");
    var userType = $("#userType");
    var company = $("#company");

    userType.bind("focus", function (e) {
        lastValue = $(this).val();
    }).bind("change", function () {
        var changeConfirmation = confirm("Settings will be permanently deleted. Confirm?");
        if (!changeConfirmation) {
            $(this).val(lastValue);
        } else {
            $("#percentInfo").html("");
            $("#updateUserType").click();
        }
    });

    company.bind("focus", function (e) {
        lastValue = $(this).val();
    }).bind("change", function () {
        var changeConfirmation = confirm("Settings will be permanently deleted. Confirm?");
        if (!changeConfirmation) {
            $(this).val(lastValue);
        } else {
            $("#percentInfo").html("");
            $("#updateUserType").click();
        }
    });

    percentType.bind("focus", function (e) {
        lastValue = $(this).val();
    }).bind("change", function () {
        var changeConfirmation;
        if ((lastValue == 'PERCENT' || lastValue == 'FIXED') && $('#percentValue0').val() != "") {
             changeConfirmation = confirm("Settings will be permanently deleted. Confirm?");
        } else if ((lastValue == 'RANGE_PERCENT' || lastValue == 'RANGE_FIXED') &&
                ($('#percentValue0').val() != "" || $('#valueFrom0').val() != "" || $('#valueTo0').val() != "")) {
            changeConfirmation = confirm("Settings will be permanently deleted. Confirm?");
        } else if (lastValue == 'CALCULATED' && $('#textValue0').val() != "") {
            changeConfirmation = confirm("Settings will be permanently deleted. Confirm?");
        } else {
            changeConfirmation = true;
        }
        if (!changeConfirmation) {
            $(this).val(lastValue);
        } else {
            $("#percentInfo").html("");
            $("#updateValues").click();
        }
    });

    linkPercentType.bind("focus", function (e) {
        lastValue = $(this).val();
    }).bind("change", function () {
        var changeConfirmation = confirm("Settings will be permanently deleted. Confirm?");
        if (!changeConfirmation) {
            $(this).val(lastValue);
        } else {
            $("#linkPercentInfo").html("");
            $("#updateLinkValues").click();
        }
    });

    quoteLinkPercentType.bind("change", function () {
        $("#quoteLinkPercentInfo").html("");
        $("#updateQuoteLinkValues").click();
    });

    if (percentType.val() == 'PERCENT' || percentType.val() == 'FIXED') {
        $('#valueFrom0').parent().hide();
        $('#valueTo0').parent().hide();
        $('#addRange').parent().hide();
        $('#addRange').prop('disabled', true);
        $('#valueFrom0').prop("disabled", true);
        $('#valueTo0').prop("disabled", true);
    } else if (percentType.val() == 'NONE') {
        $('#percentPanel0').hide();
        $('#addRange').parent().hide();
        $('#percentValue0').prop("disabled", true);
        $('#valueFrom0').prop("disabled", true);
        $('#valueTo0').prop("disabled", true);
    } else if (percentType.val() == 'CALCULATED') {
        $('#addRange').parent().hide();
        $('#percentValue0').parent().hide();
        $('#valueFrom0').parent().hide();
        $('#valueTo0').parent().hide();
        $('#percentValue0').prop("disabled", true);
        $('#valueFrom0').prop("disabled", true);
        $('#valueTo0').prop("disabled", true);
        $('#textValue0').parent().show();
        $('#textValue0').prop("disabled", false);
    }

    if (linkPercentType.val() == 'PERCENT' || linkPercentType.val() == 'FIXED') {
        $('#linkValueFrom0').parent().hide();
        $('#linkValueTo0').parent().hide();
        $('#addLinkRange').parent().hide();
        $('#linkValueFrom0').prop("disabled", true);
        $('#linkValueTo0').prop("disabled", true);
    } else if (linkPercentType.val() == 'NONE') {
        $('#linkPercentPanel0').hide();
        $('#addLinkRange').parent().hide();
        $('#linkPercentValue0').prop("disabled", true);
        $('#linkValueFrom0').prop("disabled", true);
        $('#linkValueTo0').prop("disabled", true);
    } else if (linkPercentType.val() == 'CALCULATED') {
        $('#addLinkRange').parent().hide();
        $('#linkPercentValue0').parent().hide();
        $('#linkValueFrom0').parent().hide();
        $('#linkValueTo0').parent().hide();
        $('#linkPercentValue0').prop("disabled", true);
        $('#linkValueFrom0').prop("disabled", true);
        $('#linkValueTo0').prop("disabled", true);
        $('#linkTextValue0').parent().show();
        $('#linkTextValue0').prop("disabled", false);
    }

    if (quoteLinkPercentType.val() == 'PERCENT' || quoteLinkPercentType.val() == 'FIXED') {
        $('#quoteLinkValueFrom0').parent().hide();
        $('#quoteLinkValueTo0').parent().hide();
        $('#addQuoteLinkRange').parent().hide();
        $('#quoteLinkValueFrom0').prop("disabled", true);
        $('#quoteLinkValueTo0').prop("disabled", true);
    } else if (quoteLinkPercentType.val() == 'NONE') {
        $('#quoteLinkPercentPanel0').hide();
        $('#addQuoteLinkRange').parent().hide();
        $('#quoteLinkPercentValue0').prop("disabled", true);
        $('#quoteLinkValueFrom0').prop("disabled", true);
        $('#quoteLinkValueTo0').prop("disabled", true);
    } else if (quoteLinkPercentType.val() == 'CALCULATED') {
        $('#addQuoteLinkRange').parent().hide();
        $('#quoteLinkPercentValue0').parent().hide();
        $('#quoteLinkValueFrom0').parent().hide();
        $('#quoteLinkValueTo0').parent().hide();
        $('#quoteLinkPercentValue0').prop("disabled", true);
        $('#quoteLinkValueFrom0').prop("disabled", true);
        $('#quoteLinkValueTo0').prop("disabled", true);
        $('#quoteLinkTextValue0').parent().show();
        $('#quoteLinkTextValue0').prop("disabled", false);
    }
});

function confirmRemove() {
    return confirm("Settings will be permanently deleted. Confirm?");
}