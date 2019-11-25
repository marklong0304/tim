var quoteId = $("#quoteId").val();
var quoteUpdateUrl = context + 'results/purchasePage/updateQuoteRequest';
var ajaxTestUrl = context + 'results/ajaxTest';
var currentDp, currentRealAge, currentBirthdayAge, currentTraveler, currentAgeOrBirthday;
var upsaleUrl = context + 'details/detailsUpsale';
var updatePackagesUrl = context + 'upsale/package/enable';
var tooltipsUrl = context + 'upsale/tooltips';
var validatePurchaseUrl = context + 'results/validatePurchase/' + quoteId;
var checkedUpsales;

var purchaseDepositDatePicker;

$(document).ready(function () {
    initPurchaseDatePicker();
    initBirthdayDatePickers();
    initPurchaseUpsales();
    initRentalCarDatePickers();
    sameAsHome();
    $('.timezoneOffset').val(new Date().getTimezoneOffset());
    $('#creditCard\\.ccCode').mask('0000');
    $('.ageInput').mask('000');
    $('.dateInput').mask("00/00/0000", {placeholder: "mm/dd/yyyy"});
    $('#customRadio1').attr('checked', 'checked');
    $('.selectpicker').selectpicker({liveSearch: false, liveSearchStyle: 'begins'});

    maskCreditCard('41');

    $('#cName').focus(function() {
      $('#cName').attr('name', 'ffffsls');
    });
    $('#cName').blur(function() {
      $('#cName').attr('name', 'creditCard.ccName');
    });


    $('#bNumber').focus(function() {
      $('#bNumber').attr('name', 'fdsfffsdf');
    });
    $('#bNumber').blur(function() {
      $('#bNumber').attr('name', 'creditCard.ccNumber');
    });

    if ($("#cardType").val() == "") {
        $('.bootstrap-select [data-id=cardType] .filter-option').text('Choose Card Type');
    }

    $("input[id*=firstName]").change(function () {
        var firstNameIndex = getElementIndex($(this));
        var firstName = $(this).val();
        if (firstName == '') {
            showError(this, '#firstNameError' + firstNameIndex, 'First name is required!')
        } else {
            if (!/^[a-zA-Z]*([ \'-][a-zA-Z]+)*$/.test(firstName.trim())) {
                showError(this, '#firstNameError' + firstNameIndex, 'First name can only contain Latin letters')
            } else {
                removeError(this, '#firstNameError' + firstNameIndex);
            }
        }
    });

    $("input[id*=lastName]").change(function () {
        var lastName = $(this).val();
        var lastNameIndex = getElementIndex($(this));
        if (lastName == '') {
            showError(this, '#lastNameError' + lastNameIndex, 'Last name is required!')
        } else {
            if (!/^[a-zA-Z]*([ \'-][a-zA-Z]+)*$/.test(lastName.trim())) {
                showError(this, '#lastNameError' + lastNameIndex, 'Last name can only contain Latin letters')
            } else {
                removeError(this, '#lastNameError' + lastNameIndex);
            }
        }
    });

    $("input[id*=beneficiaryName]").change(function () {
        var x1 = $(".benef-label").text();
        var beneficiaryNameIndex = getElementIndex($(this));
        var beneficiaryName = $(this).val();
        if (beneficiaryName == '') {
            if(x1.indexOf("*") !== -1) {
                showError(this, '#beneficiaryError' + beneficiaryNameIndex, 'Beneficiary name is required!');
            } else {
                removeError(this, '#beneficiaryError' + beneficiaryNameIndex);
            }
        } else if (!/^[a-zA-Z]*([ \'-][a-zA-Z]+)*$/.test(beneficiaryName.trim())) {
            showError(this, '#beneficiaryError' + beneficiaryNameIndex, 'Beneficiary name can only contain Latin letters and whitespace!')
        } else if (!/^\b[a-zA-Z]+\s[a-zA-Z]+\b$/.test(beneficiaryName.trim()) ) {
            showError(this, '#beneficiaryError' + beneficiaryNameIndex, 'Please enter valid first and last name, e.g. James Smith')
        } else {
            removeError(this, '#beneficiaryError' + beneficiaryNameIndex);
        }
    });

    if(document.getElementById('creditCard.ccName') !==null) {
      $("#creditCard\\.ccName").change(function () {
        var ccName = $(this).val();
        if (ccName == '') {
          showError(this, '#ccNameError', 'Cardholder name is required!');
        } else if (!/^[a-zA-Z]*([ \'-][a-zA-Z]+)*$/.test(ccName.trim())) {
          showError(this, '#ccNameError', 'Cardholder can only contain Latin letters and whitespace!')
        } else if (!/^\b[a-zA-Z]+\s[a-zA-Z]+\b$/.test(ccName.trim()) ) {
          showError(this, '#ccNameError', 'Please enter valid first and last name, e.g. James Smith')
        } else {
          removeError(this, '#ccNameError');
        }
      });
    } else if(document.getElementById('cName') !==null) {
      $("#cName").change(function () {
        var ccName = $(this).val();
        if (ccName == '') {
          showError(this, '#ccNameError', 'Cardholder name is required!');
        } else if (!/^[a-zA-Z]*([ \'-][a-zA-Z]+)*$/.test(ccName.trim())) {
          showError(this, '#ccNameError', 'Cardholder can only contain Latin letters and whitespace!')
        } else if (!/^\b[a-zA-Z]+\s[a-zA-Z]+\b$/.test(ccName.trim()) ) {
          showError(this, '#ccNameError', 'Please enter valid first and last name, e.g. James Smith')
        } else {
          removeError(this, '#ccNameError');
        }
      });
    }

    var x1 = $(".benef-label").text();
     // alert(x1);
    if(x1.indexOf("*") !== -1) {

        $("[id*=beneficiaryRelation]").change(function () {
            var beneficiaryRelationIndex = getElementIndex($(this));
            if ($(this).val() == '') {
                $(this).selectpicker('setStyle', 'invalid', 'add');
                $("#beneficiaryRelError" + beneficiaryRelationIndex).text("Beneficiary relation is required!");
            } else {
                $(this).selectpicker('setStyle', 'invalid', 'remove');
                $("#beneficiaryRelError" + beneficiaryRelationIndex).text("");
            }
        });

    }

    $("input[name='tripTypes']").change(function () {
        if ($("input[name='tripTypes']:checked").length == 0) {
            showError("#triptype", '#tripTypesError', 'Trip type is required!')
        } else {
            removeError("#triptype", '#tripTypesError');
        }
    });

    $("#phone").change(function () {
        var phoneInput = $(this).val();
        if (phoneInput == '') {
            showError(this, '#phoneError', 'Phone is required!');
        } else {
            var phoneRegex = /^\+?[\+\d\(\) \-\.]{5,30}$/;
            if (!phoneRegex.test(phoneInput)) {
                showError(this, '#phoneError', 'Phone has incorrect format!')
            } else {
                removeError(this, '#phoneError');
            }
        }
    });

    $("#address").change(function () {
        validateAddress(this, '#addressError');
    });

    $("#city").change(function () {
      validateCity(this, '#cityError');
    });


    if(document.getElementById('ccNumber') !== null) {
      $("#ccNumber").change(function () {
        setCcType($(this));
        validateCreditCardNumber($(this));
      });
    } else if(document.getElementById('bNumber') !== null) {
      $("#bNumber").change(function () {
        setCcType($(this));
        validateCreditCardNumber($(this));
      });
    }

    if(document.getElementById('ccNumber') !==null) {
      $("#ccNumber").keydown(function(e) {
        if ((event.keyCode >= 48 && event.keyCode <= 57) || event.keyCode == 8 || event.keyCode == 46) {
          maskCreditCard($(this).val().replace(/ /g, ''));
          document.getElementById('ccNumber').style.backgroundImage = '';
          removeError($(this), '#ccTypeError');
          removeError($(this), '#ccNumberError');
        } else if(event.keyCode == 13){
          maskCreditCard($(this).val().replace(/ /g, ''));

          $(this).blur();
        }
      });
    } else if(document.getElementById('bNumber') !==null) {
      $("#bNumber").keydown(function(e) {
        if ((event.keyCode >= 48 && event.keyCode <= 57) || event.keyCode == 8 || event.keyCode == 46) {
          maskCreditCard($(this).val().replace(/ /g, ''));
          document.getElementById('bNumber').style.backgroundImage = '';
          removeError($(this), '#ccTypeError');
          removeError($(this), '#ccNumberError');
        } else if(event.keyCode == 13){
          maskCreditCard($(this).val().replace(/ /g, ''));

          $(this).blur();
        }
      });
    }

    if(document.getElementById('ccNumber') !==null) {
      $("#ccNumber").on('paste', function(e) {
        maskCreditCard(e.originalEvent.clipboardData.getData('text'));

        document.getElementById('ccNumber').style.backgroundImage = '';
        removeError($(this), '#ccTypeError');
        removeError($(this), '#ccNumberError');
      });
    } else if(document.getElementById('bNumber') !==null) {
      $("#bNumber").on('paste', function(e) {
        maskCreditCard(e.originalEvent.clipboardData.getData('text'));

        document.getElementById('bNumber').style.backgroundImage = '';
        removeError($(this), '#ccTypeError');
        removeError($(this), '#ccNumberError');
      });
    }

    $("#creditCard\\.ccCode").change(function () {
        validateIsEmptyElement('#creditCard\\.ccCode', '#ccCodeError', 'Credit card code is required!');
        validateCcCode($(this));
    });

    $("#postalCode").change(function () {
        validateIsEmptyElement(this, '#postalCodeError', 'Zip code is required!');
    });

    $("#expMonth").change(function () {
        validateSelect('#expMonth', '#ccExpMonthError', 'Expiration month');
    });

    $("#expYear").change(function () {
        validateSelect('#expYear', '#ccExpYearError', 'Expiration year');
    });

    $("#creditCard\\.ccAddress").change(function () {
      validateAddress(this, '#ccAddressError');
    });

    $("#creditCard\\.ccCity").change(function () {
      validateCity(this, '#ccCityError');
    });

    $("#creditCard\\.ccCountry").change(function () {
        if ($("#creditCard\\.ccCountry option:selected").val() == '') {
            $('#creditCard\\.ccCountry').selectpicker('setStyle', 'invalid', 'add');
            $('#ccCountryError').text("Country is required!");
        } else {
            $('#creditCard\\.ccCountry').selectpicker('setStyle', 'invalid', 'remove');
            $('#ccCountryError').text("");
        }
    });

    $("#email").change(function () {
        if ($(this).val() == '') {
            showError(this, '#emailError', 'Email is required!');
        } else {
            var emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
            if (!emailRegex.test($(this).val())) {
                showError(this, '#emailError', 'Email has incorrect format!');
            } else {
                removeError(this, '#emailError');
            }
        }
    });

    $("#clientEmail").change(function () {
        if ($(this).val() == '') {
            showError(this, '#emailError', 'Email is required!');
        } else {
            var emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
            if (!emailRegex.test($(this).val())) {
                showError(this, '#emailError', 'Email has incorrect format!');
            } else {
                removeError(this, '#emailError');
            }
        }
    });

    $("#creditCard\\.ccZipCode").change(function () {
        validateIsEmptyElement(this, '#ccZipCodeError', 'Credit card zip code is required!');
    });

    $("#creditCard\\.ccEmail").change(function () {
      if ($(this).val() == '') {
        showError(this, '#ccEmailError', 'Billing email is required!');
      } else {
        var emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
        if (!emailRegex.test($(this).val())) {
          showError(this, '#ccEmailError', 'Billing email has incorrect format!');
        } else {
          removeError(this, '#ccEmailError');
        }
      }
    });

    $("#creditCard\\.ccPhone").change(function () {
      var phoneInput = $(this).val();
      if (phoneInput == '') {
        showError(this, '#ccPhoneError', 'Billing phone is required!');
      } else {
        var phoneRegex = /^\+?[\+\d\(\) \-\.]{5,30}$/;
        if (!phoneRegex.test(phoneInput)) {
          showError(this, '#ccPhoneError', 'Billing phone has incorrect format!')
        } else {
          removeError(this, '#ccPhoneError');
        }
      }
    });

    $("#validatePurchase").on('click', function () {
        setCookies();
        resetValidPurchase()

        $("input[id*=firstName]").each(function () {
            if ($(this).val() == '') {
                var firstNameIndex = getElementIndex($(this));
                showError(this, '#firstNameError' + firstNameIndex, 'First Name is required');
            }
        });
        $("input[id*=lastName]").each(function () {
            if ($(this).val() == '') {
                var lastNameIndex = getElementIndex($(this));
                showError(this, '#lastNameError' + lastNameIndex, 'Last Name is required');
            }
        });
        $(".traveler").each(function () {
            var traveler = $(this);
            traveler.find('.ageOrBirthday').val(traveler.find('.birthday').val());
        });

        var x1 = $(".benef-label").text();
         // alert(x1);
        if(x1.indexOf("*") !== -1) {
            $("[id*=beneficiaryRelation]").each(function () {
                if ($(this).val() == '') {
                    $(this).selectpicker('setStyle', 'invalid', 'add');
                    var beneficiaryRelationIndex = getElementIndex($(this));
                    $("#beneficiaryRelError" + beneficiaryRelationIndex).text("Beneficiary relation is required!");
                }
            });

        }

        if ($("input[name='tripTypes']:checked").length == 0) {
            showError("#triptype", '#tripTypesError', 'Trip type is required!')
        } else {
            removeError("#triptype", '#tripTypesError');
        }

        validateAddress($('#address'), '#addressError');
        validateCity($('#city'), '#cityError');
        validateIsEmptyElement('#postalCode', '#postalCodeError', 'Zip code is required!');
        validateIsEmptyElement('#email', '#emailError', 'Email is required!');
        validateIsEmptyElement('#phone', '#phoneError', 'Phone is required!');

        if (!$('#homeAdress').is(':checked')) {
            validateAddress($('#creditCard\\.ccAddress'), '#ccAddressError');
            validateCity($('#creditCard\\.ccCity'), '#ccCityError');
            validateCountryState($("#creditCard\\.ccCountry"), $('#creditCard\\.ccStateCode'), '#ccStateError');
            validateIsEmptyElement('#creditCard\\.ccZipCode', '#ccZipCodeError', 'Credit card zip code is required!');
            validateIsEmptyElement('#creditCard\\.ccEmail', '#ccEmailError', 'Credit card email is required!');
            validateIsEmptyElement('#creditCard\\.ccPhone', '#ccPhoneError', 'Credit card phone is required!');
        }
        if (document.getElementById('creditCard.ccName') !==null) {
          var ccName = $("#creditCard\\.ccName").val();
          if (ccName == '') {
            showError(ccName, '#ccNameError', 'Cardholder name is required!');
            $("#creditCard.ccName").addClass("invalid");
          }
        }
        if (document.getElementById('cName') !==null) {
          var ccName = $("#cName").val();
          if (ccName == '') {
            showError(ccName, '#ccNameError', 'Cardholder name is required!');
            $("#cName").addClass("invalid");
          }
        }
        if(document.getElementById('ccNumber') !==null) {
          $('#ccNumber:visible').each(function() { validateCreditCardNumber('#ccNumber') });
          validateCardType();
          validateIsEmptyElement('#creditCard\\.ccCode', '#ccCodeError', 'Credit card code is required!');
          validateSelect('#expMonth', '#ccExpMonthError', 'Expiration month');
          validateSelect('#expYear', '#ccExpYearError', 'Expiration year');
        }
        if(document.getElementById('bNumber') !==null) {
          $('#bNumber:visible').each(function () { validateCreditCardNumber('#bNumber') });
          validateCardType();
          validateIsEmptyElement('#creditCard\\.ccCode', '#ccCodeError', 'Credit card code is required!');
          validateSelect('#expMonth', '#ccExpMonthError', 'Expiration month');
          validateSelect('#expYear', '#ccExpYearError', 'Expiration year');
        }

        if ($('.invalid').length > 0) {
              if ($($('.invalid')[0]).is("input")) {
                  $('.invalid')[0].focus();
              } else {
                  $($('.invalid')[0]).find("input").focus();
              }
              return;
          } else {
              var fe = $('#fieldErrors');
              var agreeTerms = $("#agreeTerms").is(':checked') && $("#agreeVendorTerms").is(':checked');

              if (!agreeTerms) {
                  var $agreeTermsError = $('#agreeTermsError');
                  $agreeTermsError.show();
                  if (fe.length == 0 && $('.invalid').length == 0) {
                      $('html, body').animate({
                          scrollTop: $agreeTermsError.offset().top
                      }, 400);
                  } else if (fe.find('.terms-error').length == 0) {
                      fe.append("<div class=\"terms-error\"><span>" + $agreeTermsError.text() + "</span></div>");
                  }
                  return;
              }
              $(".purchaseBtn").addClass("disabled-pointer");
              validatePurchaseRequest();
          }
      });

    function validatePurchaseRequest() {
        $.ajax({
            url: validatePurchaseUrl,
            data: $("#purchaseForm").serialize(),
            method: "POST",
            success: function (data) {
                if (data.errors) {
                    $(".purchaseBtn").removeClass("disabled-pointer");
                    printErrors(data.errors);
                } else {
                    setValidPurchase();
                    submitPurchasePage();
                }
            }
        });
    }

    $(function(){
        $("input[id*=firstName], input[id*=lastName]").bind('input', function(){
            $(this).val(function(_, v){
                return v.replace(/\s+/g, '').toLowerCase();
            });
        });
        if(document.getElementById('ccName') !==null) {
          $("input[id*=beneficiary], input[id*=ccName]").bind('input', function(){
            $(this).val(function(_, v){
              return v.toLowerCase();
            });
          });
        } else if (document.getElementById('cName') !==null) {
          $("input[id*=beneficiary], input[id*=cName]").bind('input', function(){
            $(this).val(function(_, v){
              return v.toLowerCase();
            });
          });
        }
    });

    if(document.getElementById('ccName') !==null) {
      $("input[id*=beneficiary], input[id*=ccName]").change(function () {
        $(this).val(function(_, v){
          return v.trim().replace(/\s\s+/g, ' ');
        });
      });
    } else if(document.getElementById('cName') !==null) {
      $("input[id*=beneficiary], input[id*=cName]").change(function () {
        $(this).val(function(_, v){
          return v.trim().replace(/\s\s+/g, ' ');
        });
      });
    }

    $("#homeAdress").change(function () {
        sameAsHome();
        $('#creditCard\\.ccAddress').removeClass('invalid');
        $('.ccCountryStatePair').selectpicker('setStyle', 'invalid', 'remove');
        $('#creditCard\\.ccCity').removeClass('invalid');
        $('#creditCard\\.ccZipCode').removeClass('invalid');
        $('#ccAddressError').text("");
        $('#ccCountryStatePairError').text("");
        $('#ccCityError').text("");
        $('#ccZipCodeError').text("");
        removeError('#creditCard\\.ccEmail', '#ccEmailError');
        removeError('#creditCard\\.ccPhone', '#ccPhoneError');
    });

    sameAsHome();

    var ccState = $('#creditCard\\.ccStateCode');
    var ccCountry = $('#creditCard\\.ccCountry');

    ccState.on("change", function () {
        if ($(this).val() != '') {
            var country = ccState.find(":selected").attr("data");
            ccCountry.selectpicker('val', country);
        }
    });

    ccCountry.on("change", function () {
        if ($(this).val() != 'US' && $(this).val() != "CA") {
            ccState.addClass("disabled-filter");
            ccState.selectpicker('val', '');
        } else {
            ccState.removeClass("disabled-filter");
        }
    });
    /*
     updatePurchaseButton();
     $("#agreeTerms").on('change', function () {
     updatePurchaseButton();
     })
     */
    $(".input-group-addon").on('click', function () {
        $(this).prev('input').datepicker('show');
    });

    _.each($(".tripTypesParam"), function (val) {
        $("input[value=" + val.value + "]").attr("checked", "checked")
    });
    getTooltips();
    getCookies();
});


function setCcType(element){
    var ccType = getCcType($(element).val());

    if(ccType) {
        ccTypeSelectAndValidate(ccType, element);
    } else {
        $('input:radio[name=creditCard\\.ccType]:checked').prop('checked', false);
        if($(element).val().length > 0) {
            showError(element, '#ccTypeError', 'Unrecognized credit card type');
        }
        if(document.getElementById('ccNumber') !==null) {
          document.getElementById('ccNumber').style.backgroundImage = '';
        } else if(document.getElementById('bNumber') !==null) {
          document.getElementById('bNumber').style.backgroundImage = '';
        }
        var cardCode = $("#creditCard\\.ccCode");
        cardCode.attr("placeholder", "");
        removeError('#creditCard\\.ccCode', '#ccCodeError');
    }
}

function getCcType(ccTypeData){
    var ccType = ccTypeData.replace(/ /g, '');
    var typeLength = ccType.length;
    var firstTwo = ccType.substr(0,2);
    var firstThree = ccType.substr(0,3);
    var firstSix = ccType.substr(0,6);

    if(ccType.charAt(0) == '4') {
        return 'VISA';
    }
    else if(typeLength >= 2 && (firstTwo == '34' || firstTwo == '37') ) {
        return 'AmericanExpress';
    }
    else if(typeLength >= 2 && firstTwo > 50 && firstTwo < 56) {
        return 'MasterCard';
    }
    else if(typeLength >= 6 && firstSix >= 222100 && firstSix <= 272099) {
        return 'MasterCard';
    }
    else if(typeLength >= 2 && (firstTwo == '36' || firstTwo == '54' || firstTwo == '55' || firstTwo > 37 && firstTwo < 40)) {
        return 'Diners';
    }
    else if(typeLength >= 3 && (firstThree == '309' || firstThree > 299 && firstThree < 306)) {
        return 'Diners';
    }
    else if(typeLength >= 4 && ccType.substr(0,4) == '6011') {
        return 'Discover';
    }
    else if(typeLength >= 3 && firstThree > 643 && firstThree < 650) {
        return 'Discover';
    }
    else if(typeLength >= 6 && firstSix > 622125 && firstSix < 622926) {
        return 'Discover';
    }
    else if(typeLength >= 2 && firstTwo == '65') {
        return 'Discover';
    } else {
        return null;
    }
}

function ccTypeSelectAndValidate(ccType, element){
    $("input[name='creditCard\\.ccType'][value=" + ccType + "]").prop('checked', true);

    if($("input[name='creditCard\\.ccType']:checked").val() != ccType){
        $('input:radio[name=creditCard\\.ccType]:checked').prop('checked', false);
        showError(element, '#ccTypeError', 'This credit card type is not supported by provider!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
    } else {
        removeError(element, '#ccTypeError');
        setCCStyle(ccType.toLowerCase());
        var cardCode = $("#creditCard\\.ccCode");

        if (ccType == 'AmericanExpress') {
            cardCode.attr("placeholder", "4-digit on the front of your card");
        } else {
            cardCode.attr("placeholder", "3-digit on the back of your card");
        }
        validateCcCode($("#creditCard\\.ccCode"));
    }
}

function setCCStyle(ccType) {
  if(document.getElementById('ccNumber') !==null) {
    document.getElementById('ccNumber').style.backgroundImage = 'url(\'https://d3u70gwj4bg98w.cloudfront.net/images/newdesign/ccards/' + ccType + '.png\')';
    document.getElementById('ccNumber').style.backgroundRepeat = 'no-repeat';
    document.getElementById('ccNumber').style.backgroundPosition = 'right 0.25rem top 0.2rem';
    document.getElementById('ccNumber').style.backgroundSize = '55px 34px';
    document.getElementById('ccNumber').style.padding = '5px 45px 5px 9px';
  } else if(document.getElementById('bNumber') !==null) {
    document.getElementById('bNumber').style.backgroundImage = 'url(\'https://d3u70gwj4bg98w.cloudfront.net/images/newdesign/ccards/' + ccType + '.png\')';
    document.getElementById('bNumber').style.backgroundRepeat = 'no-repeat';
    document.getElementById('bNumber').style.backgroundPosition = 'right 0.25rem top 0.2rem';
    document.getElementById('bNumber').style.backgroundSize = '55px 34px';
    document.getElementById('bNumber').style.padding = '5px 45px 5px 9px';
  }
}

function validateCcCode(element) {
  if(document.getElementById('ccNumber') !==null) {
    var ccType = getCcType(document.getElementById('ccNumber').value);
        if(ccType && !$('#ccNumber').is('.invalid') && element.val().length > 0)
        {
            if (ccType == 'AmericanExpress' && element.val().length != 4) {
                showError('#creditCard\\.ccCode', '#ccCodeError', 'Code must be 4-digit');
            } else if (ccType != 'AmericanExpress' && element.val().length != 3) {
                showError('#creditCard\\.ccCode', '#ccCodeError', 'Code must be 3-digit');
            } else {
                removeError('#creditCard\\.ccCode', '#ccCodeError');
            }
      }
  } else if(document.getElementById('bNumber') !==null) {
      var ccType = getCcType(document.getElementById('bNumber').value);
      if(ccType && !$('#bNumber').is('.invalid') && element.val().length > 0)
      {
          if (ccType == 'AmericanExpress' && element.val().length != 4) {
              showError('#creditCard\\.ccCode', '#ccCodeError', 'Code must be 4-digit');
          } else if (ccType != 'AmericanExpress' && element.val().length != 3) {
              showError('#creditCard\\.ccCode', '#ccCodeError', 'Code must be 3-digit');
          } else {
              removeError('#creditCard\\.ccCode', '#ccCodeError');
          }
      }
  }
}

function validateCreditCardNumber(element) {
    var cardTypeVal = $("input[name='creditCard\\.ccType']:checked").val();
    var cardNumberLength = $(element).val().replace(/ /g, '').length;
    if(document.getElementById('ccNumber') !==null) {
      if ($(element).val() === '') {
        document.getElementById('ccNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is required!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else if (cardTypeVal === 'VISA' && cardNumberLength !== 16 && cardNumberLength !== 13 && cardNumberLength !== 19) {
        document.getElementById('ccNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is invalid!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else if (cardTypeVal === 'MasterCard' && cardNumberLength !== 16) {
        document.getElementById('ccNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is invalid!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else if (cardTypeVal === 'AmericanExpress' && cardNumberLength !== 15) {
        document.getElementById('ccNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is invalid!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else if (cardTypeVal === 'Discover' && cardNumberLength !== 16 && cardNumberLength !== 19) {
        document.getElementById('ccNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is invalid!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else if (cardTypeVal === 'Diners' && (cardNumberLength < 14 || cardNumberLength > 16 )) {
        document.getElementById('ccNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is invalid!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else {
        setCcType(element);
      }
    } else if(document.getElementById('bNumber') !==null) {
      if ($(element).val() === '') {
        document.getElementById('bNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is required!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else if (cardTypeVal === 'VISA' && cardNumberLength !== 16 && cardNumberLength !== 13 && cardNumberLength !== 19) {
        document.getElementById('bNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is invalid!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else if (cardTypeVal === 'MasterCard' && cardNumberLength !== 16) {
        document.getElementById('bNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is invalid!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else if (cardTypeVal === 'AmericanExpress' && cardNumberLength !== 15) {
        document.getElementById('bNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is invalid!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else if (cardTypeVal === 'Discover' && cardNumberLength !== 16 && cardNumberLength !== 19) {
        document.getElementById('bNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is invalid!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else if (cardTypeVal === 'Diners' && (cardNumberLength < 14 || cardNumberLength > 16 )) {
        document.getElementById('bNumber').style.backgroundImage = '';
        showError(element, '#ccNumberError', 'Credit card number is invalid!');
        removeError('#creditCard\\.ccCode', '#ccCodeError');
      } else {
        setCcType(element);
      }
    }
}

function validateCardType() {
    if ($("input[name='creditCard\\.ccType']:checked").length === 0) {
        showError(".ccTypeGroup", '#ccTypeError', 'Credit card type is required!');
    } else {
        removeError(".ccTypeGroup", '#ccTypeError');
    }
}

function submitPurchasePage() {
    // ajax test for session expired
    $.post(ajaxTestUrl).error(function (x, status, error) {
        if (x.status == 403) {
            var resultsUrl = context + 'results/' + $('#quoteId').val();
            $('#expiredSessionReloadBtn').attr('onclick', 'window.location.replace("' + resultsUrl + '")');
            $('#serverErrorModal').modal('show');
        }
    }).done(function () {
        $('#purchaseForm').submit();
    });
}

function validateAddress(element, errorSpan) {
    if ($(element).val() == '') {
        showError(element, errorSpan, 'Address is required!');
    } else if (!/^[a-zA-Z0-9 '\.,\-/\(\)#]*$/.test($(element).val())) {
        showError(element, errorSpan, 'Address can only contain Latin letters and allowed symbols!');
    } else {
        removeError(element, errorSpan);
    }
}

function validateCity(element, errorSpan) {
    if ($(element).val() == '') {
        showError(element, errorSpan, 'City is required!');
    } else {
      $('#city').val(function(_, v) {
        return v.trim().replace(/[^a-zA-Z éóúíáöüëïä]/g, "");
      });
      $('#creditCard\\.ccCity').val(function(_, v) {
        return v.trim().replace(/[^a-zA-Z éóúíáöüëïä]/g, "");
      });
        removeError(element, errorSpan);
    }
}

function validateSelect(element, errorSpan, message) {
    if ($(element).selectpicker('val').length == 0) {
        $(element).selectpicker('setStyle', 'invalid', 'add');
        $(errorSpan).text(message + ' is required!');
    } else {
        removeError(element, errorSpan);
        $(element).selectpicker('setStyle', 'invalid', 'remove');
    }
}

function validateCountryState(country, state, errorSpan) {
    if (($(country).val() == "US" || $(country).val() == "CA") && $(state).val() == "") {
        $(state).selectpicker('setStyle', 'invalid', 'add');
        $(errorSpan).text('State for ' + $(country).val() + ' is required!');
    } else {
        removeError(state, errorSpan);
        $(state).selectpicker('setStyle', 'invalid', 'remove');
    }
}

function getElementIndex(element) {
    return element.attr('id').replace(/\D/g, '');
}

function maskCreditCard(value) {
    if(document.getElementById('ccNumber') !==null) {
      var cardType = getCcType(value);
      var cardNumber = $("#ccNumber:visible");
    } else if(document.getElementById('bNumber') !==null) {
      var cardType = getCcType(value);
      var cardNumber = $("#bNumber:visible");
    }
      if (cardType == 'Discover' || cardType == 'VISA') {
        cardNumber.mask('0000 0000 0000 0000 000');
      } else if (cardType == 'MasterCard') {
        cardNumber.mask('0000 0000 0000 0000');
      } else if (cardType == 'Diners') {
        cardNumber.mask('0000 000000 0000');
      } else if (cardType == 'AmericanExpress') {
        cardNumber.mask('0000 000000 00000');
      }
}

function validateIsEmptyElement(element, errorSpan, errorText) {
    if ($(element).val() == '') {
        showError(element, errorSpan, errorText);
    } else {
        removeError(element, errorSpan);
    }
}

function showError(element, errorSpan, errorText) {
    $(element).addClass("invalid");
    $(errorSpan).text(errorText);
}

function removeError(element, errorSpan) {
    $(element).removeClass("invalid");
    $(errorSpan).text("");
}

function isValidPurchase() { return !!(parseInt($('#isValidPurchase:hidden').val())) }
function updateValidPurchase(value) { $('#isValidPurchase:hidden').val(value) }
function setValidPurchase() { updateValidPurchase('1') }
function resetValidPurchase() { updateValidPurchase('0') }

function sameAsHome() {
    var arr = [
        $('#creditCard\\.ccAddress'),
        $('.ccCountry'),
        $('#creditCard\\.ccCity'),
        $('.ccState'),
        $('#creditCard\\.ccZipCode'),
        $('#creditCard\\.ccEmail'),
        $('#creditCard\\.ccPhone')
    ];
    var checked = $("#homeAdress").is(':checked');
    /*$.each(arr, function (index, value) {
        value.prop('disabled', checked);
    });*/
    $('#billingAddr').toggle(!checked);
}

function initPurchaseDatePicker() {
    purchaseDepositDatePicker = $('.purchaseInitDepositDate').datepicker({
        orientation: "bottom auto",
        autoclose: true,
        endDate: "new Date()"
    });
}

function initBirthdayDatePickers() {
    var travelers = $(".traveler");
    $.each(travelers, function (key, value) {
        var birthday = $(value).children().find('.birthday');
        var realAge = $(value).children().find('.ageInput').val();
        var dp = $(birthday).datepicker({
            orientation: "bottom auto",
            autoclose: true,
            defaultViewDate: {year:new Date().getFullYear() - realAge, month:new Date().getMonth() - 6, day:new Date().getDay()}
        });

        dp.on('focusout hide', function () {

            // alert(1);
            var val = $(this).val();
            if (val !== "") {

            var ageFromDepartDate = $("#ageFromDepartDate").val();
            var realAge = $(this).parents().eq(3).find(".ageInput").val();
            var momentBirthday = moment(val, "MM/DD/YYYY");
            if (momentBirthday.isAfter()) {
                $(this).addClass("invalid");
                $(this).parent().parent().find('#dobError').text("Date cannot be in future!");
                return;
            }
            var birthdayAge;
            var birthdayDay;
            if (ageFromDepartDate == "true") {
                var departMoment = moment($("#departDate").val(), "MM/DD/YYYY");
                birthdayAge = departMoment.diff(momentBirthday, 'years');
                birthdayDay = departMoment.diff(momentBirthday, 'days');
            } else {
                birthdayAge = moment().diff(momentBirthday, 'years');
                birthdayDay = moment().diff(momentBirthday, 'days');
            }


            // alert('realAge=' + realAge + ' birthdayAge=' + birthdayAge);

            if (birthdayDay < 1 || birthdayAge >= 120) {
                $(this).addClass("invalid");
                $(this).parent().parent().find('#dobError').text("Age should be in range from 1 to 119");
            } else if (realAge != birthdayAge) {
                $(this).removeClass("invalid");
                $(this).parent().parent().find('#dobError').text("");
                var r = /\d+/;
                currentTraveler = this.id.match(r);
                currentDp = dp;
                currentRealAge = realAge;
                currentBirthdayAge = birthdayAge;
                currentAgeOrBirthday = val;
                $('#confirmModal').modal('show');

                // alert(3);

            } else {
                $(this).removeClass("invalid");
                $(this).parent().parent().find('#dobError').text("");
            }

            }
        });
    });
}

var doNotSendPurchaseUpsaleRequest = false;

function initRentalCarDatePickers() {
    var rentalCarStartDatePicker = $('#rentalCarStartDatePicker').datepicker({
        orientation: "bottom auto",
        autoclose: true,
        startDate: $('#departDate').val(),
        endDate: $('#returnDate').val()
    }).on('show', function () {
        closeBootstrapSelect();
    }).on('changeDate', function () {
        $('#purchaseRentalCarStartDate').val(this.value);
        var dateMoment = moment(new Date(this.value));
        dateMoment.add(1, 'd');
        rentalCarEndDatePicker.datepicker('setStartDate', dateMoment.toDate());
        rentalCarStartDatePicker.datepicker('hide');
        if(!doNotSendPurchaseUpsaleRequest) {
            sendPurchaseUpsaleRequest($('input[name=rental-car]:checked', '#purchaseUpsaleForm'));
        }
    });

    var rentalCarEndDatePicker = $('#rentalCarEndDatePicker').datepicker({
        orientation: "bottom auto",
        autoclose: true,
        startDate: $('#departDate').val(),
        endDate: $('#returnDate').val()
    }).on('show', function () {
        closeBootstrapSelect();
    }).on('changeDate', function () {
        $('#purchaseRentalCarEndDate').val(this.value);
        var dateMoment = moment(new Date(this.value));
        dateMoment.subtract(1, 'd');
        rentalCarStartDatePicker.datepicker('setEndDate', dateMoment.toDate());
        rentalCarStartDatePicker.datepicker('hide');
        if(!doNotSendPurchaseUpsaleRequest) {
            sendPurchaseUpsaleRequest($('input[name=rental-car]:checked', '#purchaseUpsaleForm'));
        }
    });
}

function updateAge() {
    var endBirthdayMoment = moment().subtract(currentRealAge, 'years');
    var quoteRequest = JSON.parse($("#quoteRequestJson").val());
    quoteRequest.travelers[currentTraveler].age = currentBirthdayAge;
    quoteRequest.travelers[currentTraveler].ageOrBirthday = currentAgeOrBirthday;
    $.post(quoteUpdateUrl, {
        uniqueCode: $("#product\\.policyMeta\\.uniqueCode").val(),
        quoteRequestJson: JSON.stringify(quoteRequest)
    }, function (data) {
        if (data.success == true) {
            $('.sum').text("$" + parseFloat(data.price).toFixed(2));
            currentDp.parents().eq(3).find(".ageInput").val(currentBirthdayAge);
            $("#quoteRequestJson").val(JSON.stringify(quoteRequest));
            $('#confirmModal').modal('hide');
            var topTravelerAge = $('.topTraveler-' + currentTraveler);
            if (topTravelerAge.text().indexOf(",") > 0) {
                topTravelerAge.text(currentBirthdayAge + ', ');
            } else {
                topTravelerAge.text(currentBirthdayAge);
            }
            updateTotalPrice(data.price);
        } else {
            if (data.error != null) {
                $('#errorModalLabel').text(data.error)
            }
            $('#errorModal').modal('show');
            currentDp.datepicker('update', endBirthdayMoment.format("MM/DD/YYYY"));
            $('#confirmModal').modal('hide');
        }
    }).fail(function () {
        $('#errorModal').modal('show');
        currentDp.datepicker('update', endBirthdayMoment.format("MM/DD/YYYY"));
        $('#confirmModal').modal('hide');
    });

}

function cancelUpdateAge() {
    var endBirthdayMoment = moment().subtract(currentRealAge, 'years');
    currentDp.datepicker('setDate', null);
    $('#confirmModal').modal('hide');
}

function myaffix() {
    $(window).scroll(function () {
        var pricesDistance = $('.plans-order').offset().top - $('.entry-price').offset().top;
        var viewportHeight = $(window).innerHeight() - 2 * ($('.entry-price').height());
        if (pricesDistance < viewportHeight) {
            $('.affix').css('margin-top', '-100px');
        }
        else {
            $('.affix').css('margin-top', '0px');
        }
    });
}

function initPurchaseUpsales() {
    var upsales = $("input.upsale");
    $.each(upsales, function (index, value) {
        $(value).on("change", function () {
            var el = $(this);
            if(el.attr('name') == 'rental-car') {
                //Show and fill rental car start and end dates if a non-zero rental car upsale has been chosen
                var rentalCarDivs = $('#rentalCarStartDateDiv, #rentalCarEndDateDiv');
                if(el.val() > 0) {
                    if($('#showRentalCarDates').val() == 'true') rentalCarDivs.removeClass('hidden');
                    var departDate = $('#departDate').val();
                    var rentalCarStartDatePicker = $('#rentalCarStartDatePicker');
                    doNotSendPurchaseUpsaleRequest = true;
                    rentalCarStartDatePicker.datepicker('setDate', departDate);
                    doNotSendPurchaseUpsaleRequest = false;
                    rentalCarStartDatePicker.datepicker('hide');
                    $('#purchaseRentalCarStartDate').val(departDate);
                    var returnDate = $('#returnDate').val();
                    var rentalCarEndDatePicker = $('#rentalCarEndDatePicker');
                    rentalCarEndDatePicker.datepicker('setDate', returnDate);
                    rentalCarEndDatePicker.datepicker('hide');
                    $('#purchaseRentalCarEndDate').val(returnDate);
                } else {
                    rentalCarDivs.addClass('hidden');
                    $('#purchaseRentalCarStartDate').val(null);
                    $('#purchaseRentalCarEndDate').val(null);
                    sendPurchaseUpsaleRequest(this);
                }
            } else {
                sendPurchaseUpsaleRequest(this);
            }
        });
    });
    checkedUpsales = $("input.upsale:checked");
}

function sendPurchaseUpsaleRequest(elem) {
    var $elem = $(elem);
    var upsaleName = ('data-' + $elem.attr('name').replace(/[^a-zA-Z0-9]/g,'-'));
    var upsaleFixedCost = parseFloat($elem.data('fixed-cost'));

    var price = $(".sum");
    var oldPrice = $(price[0]).text();
    var oldPriceAsNumber = parseFloat(oldPrice.substring(1));
    var oldPriceAddedUpsaleCost = (parseFloat(price.attr(upsaleName)) || 0);

    var isJSPriceUpdate = !!(upsaleFixedCost || oldPriceAddedUpsaleCost);

    if (isJSPriceUpdate) {
        if (upsaleFixedCost) {
            price.attr(upsaleName, upsaleFixedCost);
            price.text('$' + (oldPriceAsNumber - oldPriceAddedUpsaleCost + upsaleFixedCost).toFixed(2));
        } else {
            price.removeAttr(upsaleName);
            price.text('$' + (oldPriceAsNumber - oldPriceAddedUpsaleCost).toFixed(2));
        }
        updateTotalPrice();
    } else {
        price.text("loading...");
        $(".plans-additional").addClass("disabled-link");
    }

    var changed = $("#purchaseUpsaleForm input[name='changed']");
    if (changed.length == 0) {
        changed = $("<input/>").attr("type", "hidden").attr("name", "changed").appendTo($("#purchaseUpsaleForm"));
    }
    changed.val(elem.name);
    $.post(upsaleUrl, $("#purchaseUpsaleForm").serialize(), function (data) {
        if (!isJSPriceUpdate) {
            updatePurchasePageAfterUpsale(data, oldPrice);
        }
    });
}

function sendUpdatePackagesRequestOnPurchase(element) {
    var $elem = $(element);
    var upsaleFixedCost = parseFloat($elem.data('fixed-cost'));

    var price = $(".sum");
    var oldPrice = $(price[0]).text();
    var oldPriceAsNumber = parseFloat(oldPrice.substring(1));

    var isJSPriceUpdate = !!(upsaleFixedCost);

    if (isJSPriceUpdate) {
        price.text('$' + (oldPriceAsNumber + ($elem.prop('checked') ? upsaleFixedCost : -upsaleFixedCost)).toFixed(2));
        updateTotalPrice();
    } else {
        price.text("loading...");
        $(".plans-additional").addClass("disabled-link");
    }

    var paramsObj = {
        packageCode: element.name,
        policyUniqueCode: $("#product\\.policyMeta\\.uniqueCode").val(),
        quoteRequestJson: $("#quoteRequestJson").val(),
        enabled: $(element).is(':checked')
    };
    var paramsStr = $.param(paramsObj);
    $.post(updatePackagesUrl, paramsStr, function (data) {
        if (!isJSPriceUpdate) {
            updatePurchasePageAfterUpsale(data, oldPrice);
        }
    });
}

function updatePurchasePageAfterUpsale(data, oldPrice) {
    var price = $(".sum");
    var response = jQuery.parseJSON(data);
    if (response.error != "") {
        clearErrors();
        printError(null, response.error);
        restoreUpsales();
        price.text(oldPrice);
        updateTotalPrice();
    } else {
        price.text('$' + response.price.toFixed(2));
        if (response.planDescriptionCategoryCaption1 != null) {
            $("#pex1").text(response.planDescriptionCategoryCaption1);
        }
        if (response.planDescriptionCategoryCaption2 != null) {
            $("#pex2").text(response.planDescriptionCategoryCaption2);
        }
        if (response.planDescriptionCategoryCaption3 != null) {
            $("#pex3").text(response.planDescriptionCategoryCaption3);
        }
        if (response.planDescriptionCategoryCaption4 != null) {
            $("#pex4").text(response.planDescriptionCategoryCaption4);
        }
        if (response.planDescriptionCategoryCaption5 != null) {
            $("#pex5").text(response.planDescriptionCategoryCaption5);
        }
        if (response.planDescriptionCategoryCaption6 != null) {
            $("#pex6").text(response.planDescriptionCategoryCaption6);
        }
        $(".quoteRequestJson").val(JSON.stringify(response.quoteRequest));
        checkedUpsales = $("input.upsale:checked");
        for (var name in response.quoteRequest.categories) {
            var val = response.quoteRequest.categories[name];
            $("[name='" + name + "'][value='" + val.toUpperCase() + "']").prop("checked", true);
            var pval = $("#purchaseUpsaleForm input[name='p-val." + name + "']");
            if (pval.length == 0) {
                pval = $("<input/>").attr("type", "hidden").attr("name", "p-val." + name).appendTo($("#purchaseUpsaleForm"));
            }
            pval.val(val);
        }
        /** for packages with not selected values */
        var upsalesFromServer = [];
        var upsalesFromPage = [];

        $.each(response.quoteRequest.categories, function (value) {
            upsalesFromServer.push(value)
        });

        $.each(checkedUpsales, function (index, value) {
            upsalesFromPage.push(value.name)
        });

        var q = _.differenceWith(upsalesFromPage, upsalesFromServer, _.isEqual);

        _.forEach(q, function (value) {
            $("[name='" + value + "']").filter(function () {
                return $(this).val() == "";
            }).prop("checked", true);
        });

        // update packages
        var allPackages = _.map($("#packageBlock").find(".checkbox-custom"), 'id');

        _.forEach(allPackages, function (packageCode) {
            var currentElement = $('#' + packageCode);
            var packageChecked = currentElement.prop('checked');
            var enabledOnRequest = !_.isNil(_.find(response.quoteRequest.enabledPackages, function (o) {
                return _.eq(o, packageCode);
            }));
            if (!packageChecked && enabledOnRequest){
                currentElement.prop('checked', true);
            } else if (packageChecked && !enabledOnRequest){
                currentElement.prop('checked', false);
            }
        });

        updateTotalPrice(response.price);

        /*
        _.each(response.notifications, function (notification) {
            var enabled = _.eq(notification.color, 'GREEN');
            showModal({
                title: notification.message,
                text: notification.message + (enabled ? ' was enabled' : ' was disabled'),
                type: enabled ? 'info' : 'error',
                fontSize: 16
            });
        });
        */

        updateTooltips(response.tooltips);
    }
    $(".plans-additional").removeClass("disabled-link");
}

function getTooltips(){
    var paramsObj = {
        policyUniqueCode: $("#product\\.policyMeta\\.uniqueCode").val(),
        quoteRequestJson: $("#quoteRequestJson").val()
    };
    if (_.isEmpty(paramsObj.policyUniqueCode) || _.isEmpty(paramsObj.quoteRequestJson)){
        return;
    }
    var paramsStr = $.param(paramsObj);
    $.post(tooltipsUrl, paramsStr, function (data) {
        updateTooltips(jQuery.parseJSON(data).tooltips);
    });
}

function updateTooltips(tooltips) {
    if (_.isEmpty(tooltips)){
        return;
    }
    $("[data-toggle-cus=upsale-tooltip]").tooltip('destroy');
    setTimeout(printTooltips, 300, tooltips);
}

function printTooltips(tooltips){
    _.each(tooltips, function (tooltip) {
        $("[data-toggle-cus=upsale-tooltip][tdata="+tooltip.upsaleCategoryId+"-"+_.toUpper(tooltip.upsaleCategoryValue)+"]").tooltip({ trigger: 'hover', placement: 'right auto', html: true, title: tooltip.message});
    });
}

function restoreUpsales() {
    $.each(checkedUpsales, function (index, value) {
        if (!$(value).prop("checked")) {
            $(value).prop("checked", true);
        }
    });
}

function updatePurchaseButton() {
    if (!$("#agreeTerms").prop('checked') && !$("#agreeVendorTerms").prop('checked')) {
        $(".purchaseBtn").addClass("disabled-pointer");
    } else {
        $(".purchaseBtn").removeClass("disabled-pointer");
    }

}

$(".purchaseDetails").on('click', function () {
    var detailModal = $("#plandetailsModal");
    var policyMetaUniqueCode = $(this).attr("data");
    $.post(context + 'details?quoteId=' + quoteId + '&quoteRequestJson=' + encodeURIComponent($("#quoteRequestJson").val()) + '&plan=' + policyMetaUniqueCode, function (result) {
        detailModal.html(result);
        detailModal.modal({
                keyboard: false,
                show: true
            }
        );
        registryDetailsExpand();
        initUpsales();
        registryDialogs();
        registryStikcy();
        $("html").css({"overflow-y": "hidden"});
        $(".purchaseDetail").hide();
        $('.entry-price.affix').hide();
    });
    detailModal.on('hidden.bs.modal', function () {
        $('.entry-price').show();
    })
});

function printErrors(errors) {
    var allErrors = '';
    var isAddedTravelersError;
    $.each(errors, function (key, value) {
        if (value.defaultMessage) {
            if (value.field && value.field.indexOf("travelers") == -1 || (value.field.indexOf("travelers") != -1 && !isAddedTravelersError)) {
                if (allErrors != '') {
                    allErrors = allErrors + "<br/>";
                }
                allErrors = allErrors + value.defaultMessage;
            }
        }
        if (value.field == "travelersString") {
            $(".ageField").first().addClass("invalid");
        } else if (value.field.indexOf("travelers") != -1) {
            isAddedTravelersError = true;
            var inputs = $(".ageField");
            var index = value.field.match(/\d+/);
            $(inputs[index]).addClass("invalid");
        } else if (value.field == "destinationCountry") {
            destinationElement.parent().find(":button").addClass("invalid");
        } else if (value.field == "residentCountry") {
            residenceSelect.parent().find(":button").addClass("invalid");
        } else if (value.field == "citizenCountry") {
            citizenshipSelect.parent().find(":button").addClass("invalid");
        }
        else if (value.field == "creditCard.ccNumber") {
          if (document.getElementById('ccNumber') !== null) {
            if ($("#ccNumber").val() == '') {
              $("#ccNumber").addClass("invalid");
            }
          }
          if (document.getElementById('bNumber') !== null) {
            if ($("#bNumber").val() == '') {
              $("#bNumber").addClass("invalid");
            }
          }
        }
        else if (value.field == "creditCard.ccName") {
          if (document.getElementById('creditCard.ccName') !== null) {
            if ($("#creditCard.ccName").val() == "") {
              $("#creditCard.ccName").addClass("invalid");
            }
          }
          if (document.getElementById('cName') !== null) {
            if ($("#cName").val() == "") {
              $("#cName").addClass("invalid");
            }
          }
        }
        else {
          $("input[name=" + value.field + "]").addClass("invalid");
        }
    });
    printError(allErrors, defErrMsg);
}

function clearErrors() {
    $(".invalid").removeClass("invalid");
    $("#errors").html("");
}

function showTermsAndConditions() {
    $("#termsAndConditionsModal").modal('show')
}

function updateTotalPrice(responsePrice) {
    $("#product\\.totalPrice").val(!!responsePrice ? responsePrice : $('.sum:first').text().substring(1));
}