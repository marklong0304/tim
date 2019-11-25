/**
 * Created by artur on 26.09.2016.
 */

// messages
var mTripCostNotSet = 'Zero Trip Cost means NO Trip Cancellation coverage for all plans and NO Trip Interruption coverage for most plans.<br> You will still be covered during the trip, but the cost of your trip itself will not be protected. For best coverage enter full amount of your prepaid and non-refundable travel expenses!';
var mOneDayDiff = 'Are you traveling for one day only?';

var modalObj;

function showModal(options) {
    modalObj = $('#infoModal');
    var optionsDef = {
        title: 'Title',
        text: 'message text',
        type: 'info',
        fontSize: 19,
        confirm: false,
        confirmClass: 'confirm',
        callback: ""
    };
    var settings = $.extend({}, optionsDef, options);

    modalObj.find('.confirmCont').remove();

    modalObj.find('.modal-title').html(settings.title);
    modalObj.find('.modal-message').html(settings.text); // css({fontSize: settings.fontSize});

    modalObj.removeClass("info error");
    if (settings.type === "error") {
        modalObj.addClass('error');
    } else {
        modalObj.addClass('info');
    }

    if (settings.confirm) {
        modalObj.find('.modal-body').after('<div class="confirmCont"><button type="button" class="btn btn-success btn-md '
            + settings.confirmClass +'">Confirm</button><button type="button" class="btn btn-info btn-md change">Change</button></div>')
    }

    registerModalActions(settings.confirmClass, settings.callback);

    initModal(modalObj);
}

function registerModalActions(confirmClass, callback) {
    $(document).on('click', '.confirmCont .' + confirmClass, function () {
        modalObj.modal('hide');
        fixModalbackdrop();
        modalObj.unbind();
        $('#' + confirmClass + 'Val').val(true);
        callback();
    });

    $(document).on('click', '.confirmCont .change', function () {
        modalObj.modal('hide');
        fixModalbackdrop();
        $('#infoModal').unbind();
    });
}

function initModal(modalObj) {
    modalObj.show();
    if ($('.modal-message').height() > 210) {
        $('.modal-body').height($('.modal-message').height() + 30);
    }
    modalObj.hide();
    modalObj.modal();
    modalObj.on('shown.bs.modal', function () {
        modalObj.find('button').focus();
    })
}

function fixModalbackdrop() {
    $('body').removeClass('modal-open');
    $('.modal-backdrop').remove();
    modalObj.removeAttr( 'style' );
}

function closeModal(modalObj) {
    modalObj.modal('hide');
    fixModalbackdrop();
    $('#infoModal').unbind();
}

function showIncludesUSModal(callback) {
    modalObj = $('#infoModal');
    modalObj.find('.modal-title').html('Trip Details');
    modalObj.find('.modal-message').addClass('confirmCont').html('<span style="font-size: 16px; font-weight: bold;">Does your trip include USA?</span>');
    modalObj.find('.modal-body').next().remove();
    modalObj.find('.modal-body').after(
        '<div class="confirmCont">'
        + '<button type="button" class="btn btn-success btn-md" id="includesUSModalYes">Yes</button>'
        + '<button type="button" class="btn btn-info btn-md change" id="includesUSModalNo">No</button>'
        + '</div>'
    );
    initModal(modalObj);
    $("#includesUSModalYes").on("click", function() {
        callback(true);
        closeModal(modalObj);
    });
    $("#includesUSModalNo").on("click", function() {
        callback(false);
        closeModal(modalObj);
    });
}