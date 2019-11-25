// add CSRF token to all ajax requests
$(function () {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    }).ajaxError(function (e, xhr) {
        if (xhr.status == 403) {
            $('#serverErrorModal').modal('show');
        }
    });
});


//STATUS_CHECK
$(function () {
    setTimeout(function () {
        testStatus();
    }, STATUS_CHECK_INTERVAL);

    function testStatus() {
        var url = context + 'api/test.json';
        $.post(url, null, function (data) {
            if (data && data.status && data.status == 'ok') {
                setTimeout(function () {
                    testStatus();
                }, STATUS_CHECK_INTERVAL);
            } else {
                $('#serverErrorModal').modal('show');
            }
        }).fail(function (jqXHR, textStatus) {
            if (jqXHR.status == 500) {
                $('#serverErrorModal').modal('show');
            } else {
                setTimeout(function () {
                    testStatus();
                }, STATUS_CHECK_INTERVAL);
            }
        });
    }
});

function checkAuthentication(callback) {
    var url = context + 'api/checkAuthentication.json';
    $.post(url, null, function (data) {
        if (data && data.status && data.status == 'ok') {
            callback();
        } else {
            $('#serverErrorModal').modal('show');
        }
    }).fail(function (jqXHR, textStatus) {
        if (jqXHR.status == 403) {
            $('#serverErrorModal').modal('show');
        } else {
            testStatus();
        }
    });
}

function topCheck() {
    if ($(window).scrollTop() > 0) {
        $('.goTopLink').fadeIn();
    } else {
        $('.goTopLink').fadeOut();
    }
}

function maxHeightHeader() {
    var height = $(window).height() - $('.navbar-header').height() - 35;
    $('ul.navbar-nav').css('max-height', height + 'px')
}

function topCheckPlanTable() {
    if ($('.plansCompareHeight').length > 0) {
        if ($('.plansCompareHeight').scrollTop() > 0) {
            $('.goTopLink').fadeIn();
        } else {
            $('.goTopLink').fadeOut();
        }
    }
}

function footerBottom() {
    $('.mainWrapper').css('min-height', $('html').height());
}

function heightHeader() {
    $('.emptyHeader').height($('#header').height());
}

function checkGrayBg() {
    if ($('.mainWrapper').children('.bgF6F6F6').length > 0) {
        $('.mainWrapper').addClass('hasBgF6F6F6');
    }
}


$(document).ready(function () {
    PointerEventsPolyfill.initialize({});

    if (navigator.platform == 'iPad' || navigator.platform == 'iPhone' || navigator.platform == 'iPod') {
        $('html').addClass('ios');
    }

    checkGrayBg();
    initMenu();
    heightHeader();
    topCheck();
    footerBottom();
    maxHeightHeader();
    $(window).resize(function () {
        heightHeader();
        footerBottom();
        maxHeightHeader();
    })
    $(window).scroll(function () {
        topCheck();
    });
    $('.plansCompareHeight').scroll(function () {
        topCheckPlanTable();
    });
    $('.goTopLink').click(function () {
        $('body,html, .plansCompareHeight').animate({
            scrollTop: 0
        }, 400);
        return false;
    });
    function imgTopMargin(img) {
        var imgHeight = img.height();
        if (imgHeight != 44 && imgHeight != 0) {
            img.css('margin-top', -imgHeight / 2);
        }
    }
    $(function () {
        $('[data-toggle="popover"]').popover({
            placement: 'bottom',
            html: true,
            content: function() {
                return $('#popover-content').html();
            }
        })
    })

    setTimeout(function () {
        imgTopMargin($('.logo.visible-xs.visible-sm a img'));
        imgTopMargin($('.hidden-sm.hidden-xs .logo a img'));
    }, 100);
    $(window).resize(function () {
        imgTopMargin($('.logo.visible-xs.visible-sm a img'));
        imgTopMargin($('.hidden-sm.hidden-xs .logo a img'));
    });
    var errorInfo = $('.errorInfo');
    var errorHeight = errorInfo.outerHeight();
    errorInfo.css('margin-top', -errorHeight / 2);
    getCurrentQuote();
    $(".longNumericValue").mask('0000000', {reverse: true});
    $(".negativeNumericValue").mask("S###", {
        translation: {
            'S': {
                pattern: /-/,
                optional: true
            }
        }
    });
    $('.modal').on('shown.bs.modal', function () {
        $('.modal-body').scrollTop(0);
    });
});

function printError(msg, defaultMsg) {
    if (!msg) {
        msg = defaultMsg;
    }
    showModal({title:'Check fields', text:msg, type:'error', fontSize:18});
    /*$("#errors").append('<div class="alert alert-danger alert-dismissible"  role="alert"> \
        <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span \
        class="sr-only">Close</span></button> \
        <span>' + msg + '</span></div>');*/
}

/*function isMobileDevice() {
    return (typeof window.orientation !== "undefined") || (navigator.userAgent.indexOf('IEMobile') !== -1);
}*/

function getCurrentQuote() {
    var currentQuoteUrl = context + 'results/currentQuote';
    $("#currentQuote").hide();
    $.get(currentQuoteUrl, null, function (data) {
        var currentQuote = $("#currentQuote");
        if (data == "") {
            currentQuote.hide();
        } else {
            var currentQuoteLink = context + "comparePlans/?quoteId=" + data;
            /*if(window.location.href.indexOf("comparePlans") > -1) {
                currentQuoteLink = context + "comparePlans/?quoteId=" + data;
            } else if(window.location.href.indexOf("results") > -1 && window.location.href.indexOf("purchase") < 0) {
               currentQuoteLink = context + "results/" + data;
            } else if(isMobileDevice()) {
               currentQuoteLink = context + "results/" + data;
            } else {
                currentQuoteLink = context + "comparePlans/?quoteId=" + data;
            }*/
            currentQuote.attr("href", currentQuoteLink);
            currentQuote.show();
        }
    }).fail(function () {
        printError(null, defErrMsg);
    });
}

function fixCheckBox(selector) {
    var ch1 = $(selector);
    if (ch1.length != 0) {
        ch1.next().insertBefore(ch1);
    }
}

function clearBootstrapMultiselect(multiselectElement) {
    $(multiselectElement).multiselect("deselectAll", false);
    $(multiselectElement).multiselect("updateButtonText");
}
function clearSelectizeInput(elementToClear) {
    $(elementToClear)[0].selectize.clear();
}

function initMenu() {

    var event;
    if (navigator.platform == 'iPad' || navigator.platform == 'iPhone' || navigator.platform == 'iPod') {
        event = "click";
    }
    else {
        event = "mouseenter"
    }

    $('.hasMenu > span').on(event, function (e) {
        $('.lvl-2').removeClass('open');
        $(this).toggleClass('open');
        e.preventDefault();
        return false;
    });
    $('.dropdown-menu > li').on(event, function (e) {
        $('.lvl-2').removeClass('open');
    });
}

$(".disabled-link").on("click", function(event){
    event.preventDefault();
});