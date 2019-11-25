function getCookies(){

    //Travelers
    $("input[id*=firstName]").each(function () {
        var firstNameIndex = getElementIndex($(this));
        var name = 'firstName-' + firstNameIndex;
        if (Cookies.get(name) !== undefined && Cookies.get(name) !== '') {
            $(this).val(Cookies.get('firstName-' + firstNameIndex));
        }
    });
    $("input[id*=lastName]").each(function () {
        var lastNameIndex = getElementIndex($(this));
        var name = 'lastName-' + lastNameIndex;
        if (Cookies.get(name) !== undefined && Cookies.get(name) !== '') {
            $(this).val(Cookies.get('lastName-' + lastNameIndex));
        }
    });
    $("input[id*=travelerM]").each(function () {
        var travelerMIndex = getElementIndex($(this));
        var name = 'travelerM-' + travelerMIndex;
        if (Cookies.get(name) !== undefined && Cookies.get(name) !== '') {
            $(this).val(Cookies.get(name));
        }
    });
    $("input[id*=travelerB]").each(function () {
        var travelerBIndex = getElementIndex($(this));
        var name = 'travelerB-' + travelerBIndex;
        if (Cookies.get(name) !== undefined && Cookies.get(name) !== '') {
            $(this).val(Cookies.get(name));
        }
    });
    if (Cookies.get('primaryContact') !== undefined && Cookies.get('primaryContact') !== '') {
        var primaryContactId = Cookies.get('primaryContact');
        $(`#${primaryContactId}`).prop('checked', true);
    }

    //Beneficiaries
    $("input[id*=beneficiaryName]").each(function () {
        var beneficiaryNameIndex = getElementIndex($(this));
        var name = 'beneficiaryName' + beneficiaryNameIndex;
        if (Cookies.get(name) !== undefined && Cookies.get(name) !== '') {
            $(this).val(Cookies.get(name));
        }
    });
    $("select[id*=beneficiaryRelation]").each(function () {
        var beneficiaryRelationIndex = getElementIndex($(this));
        var name = "beneficiaryRelation" + beneficiaryRelationIndex;
        if (Cookies.get(name) !== undefined && Cookies.get(name) !== '') {
            $(this).val(Cookies.get(name));
            $(`.selectpicker[id=${name}]`).selectpicker('refresh');
        }
    });

    //Travel Suppliers
    $("input[id*=checkbox]").each(function () {
        var tripTypeId = getElementIndex($(this));
        var name = 'checkbox-' + tripTypeId;
        if (Cookies.get(name) !== undefined && Cookies.get(name) !== '') {
            $(this).prop('checked', Cookies.get('checkbox-' + tripTypeId) === 'true');
        }
    });

    //Home Address of Primary Contact
    if (Cookies.get('homeAdress') !== undefined && Cookies.get('homeAdress') !== '') {
        var checked = Cookies.get('homeAdress') === 'true';
        $('#homeAdress').prop('checked', checked);
        $('#billingAddr').toggle(!checked);
    }
    if (Cookies.get('address') !== undefined && Cookies.get('address') !== '') {
        $("#address").val(Cookies.get('address'));
    }
    if (Cookies.get('address2') !== undefined && Cookies.get('address2') !== '') {
        $("#address2").val(Cookies.get('address2'));
    }
    if (Cookies.get('city') !== undefined && Cookies.get('city') !== '') {
        $("#city").val(Cookies.get('city'));
    }
    if (Cookies.get('postalCode') !== undefined && Cookies.get('postalCode') !== '') {
        $("#postalCode").val(Cookies.get('postalCode'));
    }
    if (Cookies.get('email') !== undefined && Cookies.get('email') !== '') {
        $("#email").val(Cookies.get('email'));
    }
    if (Cookies.get('phone') !== undefined && Cookies.get('phone') !== '') {
        $("#phone").val(Cookies.get('phone'));
    }

    //Billing Address
    if (Cookies.get('ccAddress') !== undefined && Cookies.get('ccAddress') !== '') {
        $("#creditCard\\.ccAddress").val(Cookies.get('ccAddress'));
    }
    if (Cookies.get('ccAddress2') !== undefined && Cookies.get('ccAddress2') !== '') {
        $("#ccAddressLine2").val(Cookies.get('ccAddress2'));
    }
    if (Cookies.get('ccCity') !== undefined && Cookies.get('ccCity') !== '') {
        $("#creditCard\\.ccCity").val(Cookies.get('ccCity'));
    }
    if (Cookies.get('ccStateCode') !== undefined && Cookies.get('ccStateCode') !== '') {
        $("#creditCard\\.ccStateCode").val(Cookies.get('ccStateCode'));
        $(`.selectpicker[id=creditCard\\.ccStateCode]`).selectpicker('refresh');
    }
    if (Cookies.get('ccCountry') !== undefined && Cookies.get('ccCountry') !== '') {
        $("#creditCard\\.ccCountry").val(Cookies.get('ccCountry'));
        $(`.selectpicker[id=creditCard\\.ccCountry]`).selectpicker('refresh');
    }
    if (Cookies.get('ccZipCode') !== undefined && Cookies.get('ccZipCode') !== '') {
        $("#creditCard\\.ccZipCode").val(Cookies.get('ccZipCode'));
    }
    if (Cookies.get('ccEmail') !== undefined && Cookies.get('ccEmail') !== '') {
        $("#creditCard\\.ccEmail").val(Cookies.get('ccEmail'));
    }
    if (Cookies.get('ccPhone') !== undefined && Cookies.get('ccPhone') !== '') {
        $("#creditCard\\.ccPhone").val(Cookies.get('ccPhone'));
    }

    //Payment Information
    if (Cookies.get('ccName') !== undefined && Cookies.get('ccName') !== '' && Cookies.get('ccName') !== "undefined") {
        $("#creditCard\\.ccName").val(Cookies.get('ccName'));
    }
    if (Cookies.get('cName') !== undefined && Cookies.get('cName') !== '' && Cookies.get('cName') !== "undefined") {
        $("#cName").val(Cookies.get('cName'));
    }
    if (Cookies.get('ccType') !== undefined && Cookies.get('ccType') !== '' ) {
        var ccTypeId = Cookies.get('ccType');
        $(`#${ccTypeId}`).prop('checked', true);
    }
    if (Cookies.get('ccNumber') !== undefined && Cookies.get('ccNumber') !== '' && Cookies.get('ccNumber') !== "undefined") {
        $("#ccNumber").val(Cookies.get('ccNumber'));
    }
    if (Cookies.get('bNumber') !== undefined && Cookies.get('bNumber') !== '' && Cookies.get('bNumber') !== "undefined") {
        $("#bNumber").val(Cookies.get('bNumber'));
    }
    if (Cookies.get('expMonth') !== undefined && Cookies.get('expMonth') !== '') {
        $("#expMonth").val(Cookies.get('expMonth'));
        $(`.selectpicker[id=expMonth]`).selectpicker('refresh');
    }
    if (Cookies.get('expYear') !== undefined && Cookies.get('expYear') !== '') {
        $("#expYear").val(Cookies.get('expYear'));
        $(`.selectpicker[id=expYear]`).selectpicker('refresh');
    }
}

function setCookies(){
    var in5Minutes = new Date(new Date().getTime() + 5 * 60 * 1000);

    //Travelers
    $("input[id*=firstName]").each(function () {
        var firstNameIndex = getElementIndex($(this));
        Cookies.set('firstName-' + firstNameIndex, $(this).val(), {expires: in5Minutes});
    });
    $("input[id*=lastName]").each(function () {
        var lastNameIndex = getElementIndex($(this));
        Cookies.set('lastName-' + lastNameIndex, $(this).val(), {expires: in5Minutes});
    });
    $("input[id*=travelerM]").each(function () {
        var travelerMIndex = getElementIndex($(this));
        Cookies.set('travelerM-' + travelerMIndex, $(this).val(), {expires: in5Minutes});
    });
    $("input[id*=travelerB]").each(function () {
        var travelerBIndex = getElementIndex($(this));
        Cookies.set('travelerB-' + travelerBIndex, $(this).val(), {expires: in5Minutes});
    });
    var primaryContact = $('input[id*=customRadio]:checked');
    if (primaryContact !== undefined) Cookies.set('primaryContact', primaryContact[0].id, {expires: in5Minutes});

    //Beneficiaries
    $("input[id*=beneficiaryName]").each(function () {
        var beneficiaryNameIndex = getElementIndex($(this));
        Cookies.set('beneficiaryName' + beneficiaryNameIndex, $(this).val(), {expires: in5Minutes});
    });
    $("select[id*=beneficiaryRelation]").each(function () {
        var beneficiaryRelationIndex = getElementIndex($(this));
        Cookies.set('beneficiaryRelation' + beneficiaryRelationIndex, $(this).val(), {expires: in5Minutes});
    });

    //Travel Suppliers
    $("input[id*=checkbox]").each(function () {
        var tripTypeId = getElementIndex($(this));
        Cookies.set('checkbox-' + tripTypeId, $(this)[0].checked, {expires: in5Minutes});
    });

    //Home Address of Primary Contact
    var homeAdress = $('#homeAdress').prop('checked');
    Cookies.set('homeAdress', homeAdress, {expires: in5Minutes});

    Cookies.set('address', $('#address').val(), {expires: in5Minutes});
    Cookies.set('address2', $('#address2').val(), {expires: in5Minutes});
    Cookies.set('city', $('#city').val(), {expires: in5Minutes});
    Cookies.set('postalCode', $('#postalCode').val(), {expires: in5Minutes});
    Cookies.set('email', $('#email').val(), {expires: in5Minutes});
    Cookies.set('phone', $('#phone').val(), {expires: in5Minutes});

    //Billing Address
    Cookies.set('ccAddress', $('#creditCard\\.ccAddress').val(), {expires: in5Minutes});
    Cookies.set('ccAddress2', $('#ccAddressLine2').val(), {expires: in5Minutes});
    Cookies.set('ccCity', $('#creditCard\\.ccCity').val(), {expires: in5Minutes});
    Cookies.set('ccStateCode', $('#creditCard\\.ccStateCode').val(), {expires: in5Minutes});
    Cookies.set('ccCountry', $('#creditCard\\.ccCountry').val(), {expires: in5Minutes});
    Cookies.set('ccZipCode', $('#creditCard\\.ccZipCode').val(), {expires: in5Minutes});
    Cookies.set('ccEmail', $('#creditCard\\.ccEmail').val(), {expires: in5Minutes});
    Cookies.set('ccPhone', $('#creditCard\\.ccPhone').val(), {expires: in5Minutes});

    //Payment Information
    $("input[id*=ccName]").each(function () {
        var ccNameIndex = getElementIndex($(this));
        Cookies.get('ccName' + ccNameIndex, $(this).val(), {expires: in5Minutes});
    });
    $("input[id*=cName]").each(function () {
        var ccNameIndex = getElementIndex($(this));
        Cookies.get('cName' + ccNameIndex, $(this).val(), {expires: in5Minutes});
    });

    var ccType = $('input[name=creditCard\\.ccType]:checked');
    if (ccType !== undefined) Cookies.set('ccType', ccType[0].id, {expires: in5Minutes});

    var cName = $('#cName').val();
    if (cName !== undefined) Cookies.set('cName', cName, {expires: in5Minutes});

    var bNumber = $('#bNumber').val();
    if (bNumber !== undefined) Cookies.set('bNumber', bNumber, {expires: in5Minutes});

    var ccName = $('#creditCard\\.ccName').val();
    if (ccName !== undefined) Cookies.set('ccName', ccName, {expires: in5Minutes});

    var ccNumber = $('#ccNumber').val();
    if (ccNumber !== undefined) Cookies.set('ccNumber', ccNumber, {expires: in5Minutes});

    Cookies.set('expMonth', $('#expMonth').val(), {expires: in5Minutes});
    Cookies.set('expYear', $('#expYear').val(), {expires: in5Minutes});
}