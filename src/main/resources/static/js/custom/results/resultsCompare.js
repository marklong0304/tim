/**
 * Created by artur on 18.08.2016.
 */

function registryCompare() {
    $(".checkbox-recomm").click(function () {
        var counter = $('.checkbox-recomm:checkbox:checked').length;
        $('#notf-counter').text(counter);
        if (counter == 0) {
            $('#notfcompare').hide();
        } else {
            $('#notfcompare').show();
        }
        updateCompareButton(counter);
    });
}

function addToCompare(code) {
    sendStopRequest();
    var plans = $("#plans");
    var val = plans.val();
    var checked = $("input[name='" + code + "']").prop("checked");
    if (!checked) {
        plans.val(val + code + ';');
    } else {
        val = val.replace(code + ';', '');
        plans.val(val);
    }
}

function resetCompare() {
    $("#plans").val("");
    $(".checkbox-recomm").prop("checked", false);
    $('#notf-counter').text('0');
    $('#notfcompare').hide();
}

//Compare plans panel notification hide/show

registryCompare();

//Compare plans - Clear compare button

$("#notf-clear").click(function () {
    resetCompare();
});

//Compare plans - Close compare panel
$("#notf-x").click(function () {
    $('#notfcompare').hide();
});

function submitCompare() {
    $("#compareForm").submit();
}

function compareAll() {
    $("#plans").val('');
    $("#compareForm").submit();
}

function updateCompareButton(compareCount) {
    var compareButton = $(".compareButton");
    if (compareCount >= 2) {
        compareButton.removeClass("disabled-filter");
    } else {
        compareButton.addClass("disabled-filter");
    }
}