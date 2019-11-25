$(function () {
    $(document).ready(scrollToForm());
    $('#za').click(scrollToForm());
    $('#companyTab').click(scrollToForm());

});

function scrollToForm() {
    var position = $('#initialScroll').offset().top;

    $("body, html").animate({
        scrollTop: position
    } /* speed */);
};

$( "#individualCountrySelect" ).change(function() {
    var selectedCode = $( "#individualCountrySelect" ).val();
    if (selectedCode == 'US' || selectedCode == 'CA') {
        $("#individualStateDiv").show();
    }else{
        $("#individualStateDiv").hide();
    }
});

$( "#companyCountrySelect" ).change(function() {
    var selectedCode = $( "#companyCountrySelect" ).val();
    if (selectedCode == 'US' || selectedCode == 'CA') {
        $("#companyStateDiv").show();
    }else{
        $("#companyStateDiv").hide();
    }
});