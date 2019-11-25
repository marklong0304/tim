$(document).ready(function () {
    var valueM = $('.valueMask');
    _.each(valueM, function (vm) {
        var id = $(vm).attr('data');
        var valueType = $('.valueType' + id);
        initValueMask(valueType);
    });

    function initValueMaskHandler(event) {
        initValueMask($(event.currentTarget), event.data.sc);
    }

    function syncCaptionHandler(event) {
        syncCaption($(event.currentTarget));
    }

    $('.valueType').on('change', {sc: true}, initValueMaskHandler);
    valueM.on('keyup', {}, syncCaptionHandler);

    function initValueMask(el, sc) {
        var id = el.attr('data');
        var valueMask = $('.valueMask' + id);
        if (el.val() === 'FIX') {
            valueMask.mask('000.000.000.000.000', {reverse: true});
        } else if (el.val() === 'PERCENT') {
            valueMask.mask('000.000', {reverse: true});
        } else {
            valueMask.unmask();
        }
        if (sc) {
            syncCaption(valueMask);
        }
    }

    function syncCaption(el) {
        var id = el.attr('data');
        var valueType = $('.valueType' + id);
        var caption = $('.caption' + id);
        if (valueType.val() === 'NAN') {
            caption.val(el.val());
        }
        if (valueType.val() === 'FIX') {
            if (!_.isEmpty(el.val())) {
                caption.val('$' + el.val().replace(".", ","));
            } else {
                caption.val(el.val());
            }
        } else if (valueType.val() === 'PERCENT') {
            if (!_.isEmpty(el.val())) {
                caption.val(el.val().replace(".", ",") + '%');
            } else {
                caption.val(el.val());
            }
        }
    }
});

function policyMetaCategoryValueUp(id) {
    $.ajax({
        url: context + "admin/vendors/policy/values/up/" + id,
        type: "POST",
        success: function (response) {
            location.reload();
        }
    }).fail(function () {
        alert("Error");
    });
}


function policyMetaCategoryValueDown(id) {
    $.ajax({
        url: context + "admin/vendors/policy/values/down/" + id,
        type: "POST",
        success: function (response) {
            location.reload();
        }
    }).fail(function () {
        alert("Error");
    });
}

