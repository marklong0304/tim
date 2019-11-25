var comparePage = false;
var mainPage = false;
var purchasingPage = false;
var mainFormPage = 1;
var mainFormTravelers = 1;
var finalPaymentEnabled = true;
var confirmObj = {};
var currentConfirmEL;
var errors;
var modalConfirmed = false;

if ($('main').hasClass('comparePage')) {
    comparePage = true;
}
if ($('main').hasClass('mainPage')) {
    mainPage = true;
}
if ($('main').hasClass('purchasing')) {
    purchasingPage = true;
}


// text massages
var mAgeNotSet = 'Please enter all travelers ages as of today. For infants under the age of 1 you may enter 0';
var mServerNotRespond = 'Server communication failure. Please refresh your browser and try again';
var mTripCostNotNumber = 'Wrong "Trip Cost" value<br>Only numbers are allowed';
var mTripCostTooBig = 'Trip cost exceeds maximum possible coverage';
var mAgeNotNumber = 'Wrong "Age" value<br>Only numbers are allowed';
var mAgeTooBig = 'Wrong "Age" value<br>No more than a three-digit number allowed';

// Travelform hero section and form heights
window.onresize = function (event) {
    resizeDiv();
    if (comparePage) {
        updateFS();
    }
};

window.onchange = (event) => {
    if (comparePage) {
        updateFS();
    }
};

function resizeDiv() {
    const windWidth = window.innerWidth;
    const formHeight = $(".forminfo").outerHeight();

    $('.travelform').css({'height': formHeight + 'px'});
    $('.travelform .pattern').css({'height': formHeight + 'px'});
    $('#hero-inner').css({'height': formHeight + 'px'});
    $('.slogan-box').css({'height': formHeight + 'px'});
    $('.forminfo-box').css({'height': formHeight + 'px'});

    if (windWidth < 992) {
        $(".benefits").css({'margin-top': (formHeight + 10) + 'px'});
    } else {
        $(".benefits").css({'margin-top': '0px'});
    }
}

$('.forminfo').on('change', () => {
    setTimeout(resizeDiv);
});

//Burger Navigation
$('.burgerbtn').click(function () {
    $('.side-menu').fadeIn('fast');

});
$('.crossbtn').click(function () {
    $('.side-menu').fadeOut('fast');
});

//Purchasing Page - Age field tooltip
var originalLeave = $.fn.tooltip.Constructor.prototype.leave;
$.fn.tooltip.Constructor.prototype.leave = function (obj) {
    var self = obj instanceof this.constructor ?
        obj : $(obj.currentTarget)[this.type](this.getDelegateOptions()).data('bs.' + this.type)
    var container, timeout;

    originalLeave.call(this, obj);

    if (obj.currentTarget) {
        container = $(obj.currentTarget).siblings('.tooltip')
        timeout = self.timeout;
        container.one('mouseenter', function () {
            //We entered the actual popover – call off the dogs
            clearTimeout(timeout);
            //Let's monitor popover content instead
            container.one('mouseleave', function () {
                $.fn.tooltip.Constructor.prototype.leave.call(self, self);
            });
        })
    }
};

$('body .plans-travelers').tooltip({
    selector: '[data-toggle]',
    trigger: 'hover',
    placement: 'top auto',
    delay: {show: 50, hide: 400},
    html: true,
    title: 'This is the age that was entered on the quote form. If this is incorrect, please go back and quote again'
});


// Purchasing Page - Change Placeholder Text on Small Screens

if ($(window).width() < 992) {
    $(".state").attr("title", "Select a State…");
    $(".expir-mth").attr("title", "Month...");
    $(".expir-y").attr("title", "Year...");
    $
} else {
    $(".state").attr("title", "Select a State/Province/Territory");
    $(".expir-mth").attr("title", "Month");
    $(".expir-y").attr("title", "Year");

}

//-----------------Compare Plans - Filters Buttons--------------//


function updTopBlockSize() {
    // fake-results
    if ($(window).width() > 900) {
        var newH = $('.summary .results').height();
        $('.summary .results').css({bottom: -newH});
        $('.fake-results').css({height: newH});
    }
}

//Filter Expand

$(".filter-name").click(function () {

    var data_block = $(this).attr('data-block');

    if ($(this).attr("closed") == 'false') {
        $(".filter-expand[data-block='" + data_block + "']").slideToggle("fast");
        $(".filter-expand[data-block='" + data_block + "']").attr('closed', true);

        $(".chevron-open[data-block='" + data_block + "']").attr('closed', true);
        $(".chevron-open[data-block='" + data_block + "']").removeClass("chevron-open").addClass("chevron-close");


        $(this).attr("closed", true);
    } else {
        $(".filter-expand[data-block='" + data_block + "']").slideToggle("fast");
        $(".filter-expand[data-block='" + data_block + "']").attr('closed', false);

        $(".chevron-close[data-block='" + data_block + "']").attr('closed', false);
        $(".chevron-close[data-block='" + data_block + "']").removeClass("chevron-close").addClass("chevron-open");


        $(this).attr("closed", false);
    }
    updTopBlockSize();
    setTimeout(function () {
        updateSticky(false);
    }, 210);

});

//Filter script

function updateSelectedCheckBox(e) {
    var text = e.attr('data-text');
    var name = e.attr('data-name');
    var counter = $('.checkbox-filter:checkbox:checked').length;
    //alert(counter);

    if (e.prop('checked') == true) {
        var div = '<div class="filter-tag alert fade in" name="' + name + '"><span>' + text + '</span><a class="close" name="' + name + '" onclick="remove_tag(\'' + name + '\');" >×</a></div>';
        $(".counter_div").append(div);
    } else {
        $(".filter-tag[name='" + name + "']").remove();
    }
    //alert(counter);
    $('#counter_text').text(counter);
    if (counter == 0) {
        $('#selected-checkboxes').hide();
    } else {
        $('#selected-checkboxes').show();
    }
    updTopBlockSize();
}

function updateCounter() {
    var counter = $('.checkbox-filter:checkbox:checked').length;
    $('#counter_text').text(counter);
    if (counter == 0) {
        $('#selected-checkboxes').hide();
    } else {
        $('#selected-checkboxes').show();
    }
}

function remove_tag(name) {
    var current = $("input[data-name=\"" + name + "\"]");
    current.click();
    updTopBlockSize();
    // updateStorage();
}


//-----------------Compare Plans - Table--------------//

//Filter block height stay the same
/*
if ($(window).width() > 600) {
  var rt = $('.results').outerHeight() + $('.table-container').outerHeight();

  if (1500 < rt) {

    $('#fcompare').css('height', rt + 'px');

  }
  else {
    $('#fcompare').css('height', 'auto');
  }
} */


//Expand Row in Modal Table
function registryDetailsExpand() {
    $('.table-subhead').click(function () {
        $(this).toggleClass('expand').nextUntil('tr.table-subhead').slideToggle(100);
    });
}

registryDetailsExpand();


//Open Details popup window

$('#btn-collapse-wrapper').css('visibility', 'hidden');

if ($(window).width() > 767) {

    var visible = false;

    if ($('.divfixed-wrapper').hasClass('divfixed')) {
        $('.divfixed-wrapper').removeClass('divfixed');
    }

    var timeoutDrag;
    var sidebarW;
    var filtersButtonW;
    var fakeScroll = $('.fScrollCont');


    $('.scrlCont').scroll(function (e) {
        // console.log(e);
        var distanceLeft = $('.scrlCont').scrollLeft();
        clearTimeout(timeoutDrag);

        timeoutDrag = setTimeout(function () {

            var lOffset = $('.scrlCont').scrollLeft();
            console.log('horizontal scroll');
            fakeScroll.scrollLeft(lOffset);
            if (true) {
                if (sTable.$stickyTableColumn.is(':hidden')) {
                    stickyItem.css("margin-left", $(".fContFake").offset().left);
                } else {
                    stickyItem.css("margin-left", 0)
                }
            }
        }, 100);

        if (distanceLeft > (sidebarW - filtersButtonW) && !visible) {
            $('#btn-collapse-wrapper').css('visibility', 'visible').addClass('showed');
            visible = true;
        }

        if (distanceLeft < (sidebarW - filtersButtonW) && visible) {
            $('#btn-collapse-wrapper').css('visibility', 'hidden').removeClass('showed');
            visible = false;
        }
    });

    $('.btn-collapse').click(function () {
        $('.scrlCont').animate({
            scrollLeft: 0
        });

        $('#mCSB_1_dragger_horizontal, #mCSB_1_container').animate({left: 0});
    });


    $(fakeScroll).scroll(function (e) {
        var lOffset = fakeScroll.scrollLeft();
        $('.scrlCont').scrollLeft(lOffset);
    });

}

//filters save
var formValues = JSON.parse(localStorage.getItem('formValues')) || {};
var $checkboxes = $("#fcompare :checkbox");

function handleButtonClick() {
//  $checkboxes.prop("checked", allChecked()? false : true)
    $checkboxes.prop("checked", false);
}

/*
function updateStorage(){
  $checkboxes.each(function(){
    formValues[this.id] = this.checked;
  });

  formValues["buttonText"] = $button.text();
  localStorage.setItem("formValues", JSON.stringify(formValues));
}

$checkboxes.on("change", function(){
  updateStorage();
});

*/

$(document).ready(function () {
    if (comparePage) {
        //$('.scrlCont').doubleScroll();
        setTimeout(function () {
            $(".pTotal").html($('.table-non-sticky .price p.total').html());

            $('.sticky-header .alt-provname, .alt-plan-name').ellipsis({setTitle: 'onEllipsis', live: false});
        }, 200);

        // upd filter checkboxes state
        $.each(formValues, function (key, value) {
            $("#" + key).prop('checked', value);
            var el = $("#" + key);
            if (value === true) {
                var text = el.attr('text');
                var name = el.attr('name');
                var counter = $('.checkbox-filter:checkbox:checked').length;

                var div = '<div class="filter-tag alert fade in" name="' + name + '"><span>' + text + '</span><a class="close" name="' + name + '" onclick="remove_tag(\'' + name + '\');" >×</a></div>';
                $(".counter_div").append(div);

                $('#counter_text').text(counter);
                if (counter == 0) {
                    $('#selected-checkboxes').hide();
                } else {
                    $('#selected-checkboxes').show();
                }
            }

            updTopBlockSize();
        });
    }

});

function updateFS() {
    sidebarW = $('.filters').outerWidth();
    filtersButtonW = $('#btn-collapse-wrapper').width();
    var contW = $('.scrlCont')[0].scrollWidth;
    $('.fContFake').width(contW);
    //$('.fScrollCont').css('left', `${sidebarW}px`).width($('.scrlCont')[0].clientWidth);
    //$('.fScrollCont').width(document.getElementsByClassName('scrlCont')[0].clientWidth);
}

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}


function setNotValid(el) {
    el.parents('.input-group').addClass('notValid');
    el.parents('.btn-group').find('.btn').addClass('notValid');
}

function ValidatePurchasingForm() {
    errors = false;
    var formFields = $('.plans').find('select, input');
    $('.plans .notValid').removeClass('notValid');
//  console.log(formFields);
    formFields.each(function (n, element) {  // first pass - mark error fields
        var el = $(element);
        //  console.log(n, el.val());
        if (el.parents('.form-group:first').find('label req').length === 0) {
            return
        }

        if (el.val().length === 0 && el.attr('disabled') != 'disabled') {

            el.parents('.input-group').addClass('notValid');
            el.parents('.btn-group').find('.btn').addClass('notValid');
            if (el.attr('type') == 'text' && el.parents('.input-group').length === 0) {
                el.addClass('notValid');
            }

        }

    });

    if (!validateEmail($('#clientEmail').val())) {
        $('#clientEmail').addClass('notValid');
        errors = true;
    }

    var tripTypeNotSet = true;
    $('#triptype input').each(function (n, element) {
        if (element.checked === true) {
            tripTypeNotSet = false
        }
    });
    if (tripTypeNotSet) {
        $('#triptype').addClass('notValid');
        errors = true
    }

    // Exceptions
    if ($('#state').val().length != 0) {
        $('#state').parents('.form-group').find('.notValid').removeClass('notValid');
    }
    if ($('#country').val().length != 0) {
        $('#country').parents('.form-group').find('.notValid').removeClass('notValid');
    }
    if ($('#state2').val().length != 0) {
        $('#state2').parents('.form-group').find('.notValid').removeClass('notValid');
    }
    if ($('#country2').val().length != 0) {
        $('#country2').parents('.form-group').find('.notValid').removeClass('notValid');
    }
    if ($('#expMonth').val().length != 0) {
        $('#expMonth').parents('.form-group').find('.notValid').removeClass('notValid');
    }
    if ($('#expYear').val().length != 0) {
        $('#expYear').parents('.form-group').find('.notValid').removeClass('notValid');
    }

    if (!$('#agreeTerms')[0].checked) {
        $('.final-price label').addClass('notValid');
    }


    if (errors || $('.plans .notValid').length > 0) {
        var fEl = $('.plans .notValid:first');
        var body = $("html, body");
        body.stop().animate({scrollTop: fEl.offset().top - 90}, '500', 'swing', function () {
            showModal({
                title: 'Check fields',
                text: 'Please complete all required fields!',
                type: 'error',
                fontSize: 19
            });
        });


    } else {
        // request to server here
        showModal({title: 'Validation complete', text: 'Sending data to server, wait for confirmation', type: 'info'});
    }

}


function ValidateMainForm(nextPageN) {
    errors = false;
    var cErrors = false;

    var numbers = /^[0-9]+$/;

    if (mainFormPage > nextPageN) {
        setMainFormPage(nextPageN);
        return;
    }
    var formFields = $('form.active').find('select, input');
    $('form.active .notValid').removeClass('notValid');
//  console.log(formFields);
    formFields.each(function (n, element) {  // first pass - mark error fields
        var el = $(element);
        if (el.attr('id') === undefined) {
            return
        }

        if (el.val().length === 0 && el.attr('disabled') != 'disabled') {
            if (el.attr('id') == 'tripCost') {
                return
            }
            el.parents('.input-group').addClass('notValid');
            el.parents('.btn-group').find('.btn').addClass('notValid');
            errors = true;
        }

    });

    formFields.each(function (n, element) {  // second pass - custom events
        var el = $(element);
        if (el.attr('id') === undefined) {
            return
        }
        //  if ( !(el.parents('.notValid').length > 0 || el.parents('.btn-group').find('.notValid').length > 0) ) {return}
        console.log(el);

        if (el.hasClass('ddown') && el.val().length === 0 && el.attr('disabled') != 'disabled') { //field not selected, open dd
            el.parents('.btn-group').find('.dropdown-toggle').dropdown('toggle');
            return false;
        }

        if (el.hasClass('dateSel') && el.val().length === 0) {
            el.parent().data("DateTimePicker").show();
            return false;
        }


        if (el.hasClass('numeric')) {

            if (el.attr('id') == 'tripCost') {
                if (el.val().length === 0 || el.val() == 0) {
                    currentConfirmEL = el;
                    if (confirmObj[el.attr('id')] != undefined) {
                        return
                    }
                    showModal({
                        title: 'Check fields',
                        text: mTripCostNotSet,
                        type: 'error',
                        fontSize: 18,
                        confirm: true
                    });
                    cErrors = true;
                    return false;
                }

                if (el.val() > 1000000) {
                    showModal({title: 'Check fields', text: mTripCostTooBig, type: 'error', fontSize: 18});
                    errors = true;
                    setNotValid(el);
                    return false;
                }

            }

            if (el.attr('id').split('-')[0] == 'age') {
                if (el.val().length === 0) {
                    showModal({title: 'Check fields', text: mAgeNotSet, type: 'error', fontSize: 18});
                    errors = true;
                    return false;
                }

                if (el.val().length > 3) {
                    showModal({title: 'Check fields', text: mAgeTooBig, type: 'error', fontSize: 18});
                    errors = true;
                    setNotValid(el);
                    return false;
                }


            }

        }

    });

    var dDate = $('#dapartureDate').val();
    var rDate = $('#returnDate').val();


    if (!errors && !cErrors && (moment(rDate, "DD-MM-YYYY").unix() - moment(dDate, "DD-MM-YYYY").unix()) == 86400) {

        currentConfirmEL = $('#dapartureDate');
        if (confirmObj[currentConfirmEL.attr('id')] == undefined) {
            showModal({title: 'Check fields', text: mOneDayDiff, type: 'error', fontSize: 18, confirm: true});
            cErrors = true;
        }
    } else if (!errors && cErrors && (moment(rDate, "DD-MM-YYYY").unix() - moment(dDate, "DD-MM-YYYY").unix()) == 86400) {
        errors = true;
    }
    if (!errors && !cErrors) {
        setMainFormPage(nextPageN);
    }
}


$(window).on("load", function () {

    resizeDiv();


    $(document).on('click', 'dropdown-menu', function (e) {
       e.stopPropagation();
    });

    if (comparePage) {
        updateFS();
    }

//  showModal({title:'Check fields', text:'enter age for all travelers', type:'error', fontSize:19});

    if (mainPage) {


        $('.bootstrap-select').on('hidden.bs.dropdown', function (e) {
            var closedEl = $(e.target).children().attr('data-id');
            console.log(closedEl);


            switch (closedEl) {

                case 'travelers':
                    updateTravelers();
                    break;
            }


        });
    }

    // $(document).on('click', '.loginBtn', function (e) {
    //     $('#loginMenu').hide();
    //     $('#logoutMenu').show();
    // });

    $(document).on('click', '#notfback .close', function () {
        resizeDiv();
    });

    $(document).on('keydown', 'input.numeric', function (e) {
        // Allow: backspace, delete, tab, escape, enter and .
        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
            // Allow: Ctrl+A
            (e.keyCode == 65 && e.ctrlKey === true) ||
            // Allow: Ctrl+C
            (e.keyCode == 67 && e.ctrlKey === true) ||
            // Allow: Ctrl+X
            (e.keyCode == 88 && e.ctrlKey === true) ||
            // Allow: home, end, left, right
            (e.keyCode >= 35 && e.keyCode <= 39)) {
            // let it happen, don't do anything
            return;
        }
        // Ensure that it is a number and stop the keypress
        if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
            e.preventDefault();
        }
    });

    $(document).on('keydown', 'input.ageordate', function (e) {
        // Allow: backspace, delete, tab, escape, enter
        var keyCode = e.keyCode;
        if ($.inArray(keyCode, [46, 8, 9, 27, 13, 110]) !== -1 ||
            // Allow: Ctrl+A
            (keyCode == 65 && e.ctrlKey === true) ||
            // Allow: Ctrl+C
            (keyCode == 86 && e.ctrlKey === true) ||
            // Allow: Ctrl+V
            (keyCode == 67 && e.ctrlKey === true) ||
            // Allow: Ctrl+X
            (keyCode == 88 && e.ctrlKey === true) ||
            // Allow: home, end, left, right
            ((keyCode >= 35 && keyCode <= 39) || keyCode === 191)) {
            // let it happen, don't do anything
            return;
        }
        // Ensure that it is a number, slash or dot and stop the keypress
        var key = e.key;
        if (!(key === '1' ||
            key === '2' ||
            key === '3' ||
            key === '4' ||
            key === '5' ||
            key === '6' ||
            key === '7' ||
            key === '8' ||
            key === '9' ||
            key === '0' ||
            key === '/' ||
            key === '.')) {
            e.preventDefault();
        }
    });

    $(document).on('change keyup paste', 'input.ageordate', function () {
        var full = this.value;

        if (!full.match(/^[0-9./\\]*$/)) {
            this.value = '';
            return;
        }

        full = full.split('.').join('/');
        this.value = full;

        var fullLength = full.length;

        if (fullLength > 0) {
            var key = full.substr(fullLength - 1);
            var value = full.substring(0, fullLength - 1);
            var length = value.length;
            if (length > 0) {
                var first = value.substring(0, 1);
                var last = value.substr(length - 1);
            }
            var containsSlash = value.includes("/");

            if (key === "/") {
                if (length === 0 || value === "0" || last === "/" || length > 5
                    || (length === 3 && !containsSlash)
                    || (length === 4 && !containsSlash && last === 0)) {
                    this.value = value;
                } else if (length === 1) {
                    this.value = "0" + full;
                } else if (length === 4 && containsSlash) {
                    this.value = value.substring(0, 3) + "0" + last + key;
                } else {
                    //  do nothing
                }
            } else if (key === "0") {
                if (value === "0" || length === 5 || length === 6
                    || (length === 3 && !containsSlash && last === "0")
                    || (length === 4 && containsSlash && last === "0")) {
                    this.value = value;
                } else if ((length === 2 && first === "0")) {
                    this.value = value + "/" + key;
                } else if ((length === 3 && !containsSlash)) {
                    this.value = full.substring(0, 2) + "/" + full.substring(2);
                } else if ((length === 4 && containsSlash)) {
                    this.value = full;
                } else {
                    // do nothing
                }
            } else {
                if ((length === 3 && !containsSlash)) {
                    this.value = full.substring(0, 2) + "/" + full.substring(2);
                } else if ((length === 7 && !containsSlash)) {
                    this.value = full.substring(0, 2) + "/" + full.substring(2, 4) + "/" + full.substring(4);
                } else {
                    if ((length === 2 && first === "0")
                        || (length === 5)) {
                        this.value = value + "/" + key;
                    } else {
                        // do nothing
                    }
                }
            }
        }
    });

});


