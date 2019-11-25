/**
 * Created by ritchie on 5/16/16.
 */
var isUpdating = false;
var editCategoryValueUrl = context + 'vendors/policyMatrix/editCategoryValue/';
$(document).ready(function () {
    clearErrors();
    var firstInput = $("input[id*=category-]").first();
    if (firstInput.length != 0) {
        $('#rightBlock').show();
        firstInput.click();
        changeFocusedInput(firstInput);
        setValueProperties(firstInput);
        $('#restrictions div').remove();
        getRestrictions();
    } else {
        $('#rightBlock').hide();
    }

    $(document).on('click', 'input[id*=category-]', function (event, isAddedNow) {
        clearErrors();
        if (!isAddedNow) {
            var selectedInputValue = $(this).val();
            if (selectedInputValue == '') {
                showErrors('Empty category value structure');
            }
        }
        setValueProperties($(this), true);
        var focusedInput = getFocusedInput();
        if ($(this).prop('id') != $(focusedInput).prop('id') && !policyRestrictionChbx()) {
            $('#restrictions div').remove();
            getRestrictions();
        }
        changeFocusedInput(this);
        if ($('#residence option:selected').size() != 0 ||
            $('#citizenshipCountry option:selected').size() != 0 ||
            $('#destinationCountry option:selected').size() != 0) {
            $('#restrictionsBlock input').prop("readonly", true);
            $('#restrictionsBlock select').prop("disabled", true);
            $('#rightBlock button').prop('disabled', true);
            $('#saveAll').prop('disabled', true);
        }
        $(this).focus();
    });

    $(document).on('focusout', 'input[id*=category-]', function () {
        var selectedInputValue = $(this).val();
        if (selectedInputValue == '') {
            showErrors('Empty category value structure');
            $(this).addClass('invalid');
            $(this).parent().find('.categoryValueProperiesErrors').val('true');
        }
    });

    $('#categoryValuePropertiesForm .protected').focusout(function () {
        if ($(this).is('input')) {
            $(this).prop('readonly', 'readonly');
        } else {
            $(this).prop('disabled', 'disabled');
        }
    });

    $(document).on('click', '.editRestriction', function () {
        clearRestrictionFields();
        $('#addRestriction').attr('name', policyRestrictionChbx() ? 'addRestrictionToPlan' : 'addRestrictionToMatrix');
        changeDialogTitle();
        getRestriction(this);
        $("#restrictionType").addClass('disabled-pointer');
        $("#restrictionPermit").addClass('disabled-pointer');
    });

    $(document).on('change', 'input[id*=category-]', function () {
        enableCancelBtn();
        changeFocusedInput(this);
        clearErrors();
        autofillProperties(this, false);
        updateValues(this, true);
    });

    $(document).on('change', '#restrictions select', function () {
        enableCancelBtn();
        updateRestrictionsSelect(this);
    });

    $(document).on('change', 'input[id*=subcategoryValue-]', function () {
        enableCancelBtn();
        var subcategoryValueId = $(this).prop('id').split('-')[1];
        var editSubcategoryValueUrl = context + 'vendors/policyMatrix/editSubcategoryValue/' + $('#policyMetaId').val();
        $.post(editSubcategoryValueUrl, {
            selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
            selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
            selectedCategoryValueIndex: $('#selectedCategoryValueIndex').val(),
            subcategoryValueId: subcategoryValueId,
            subcategoryValue: $(this).val(),
            sessionId: $('#sessionId').val()
        });
    });

    $(document).on('change', '#restrictions input.minValue', function () {
        var categoryBlockInputs = getCategoryBlockInputs();
        if ($(this).val() == '' && $(this).closest('.form-inline').find('.maxValue').val() == '') {
            addRestrictionRangeErrors(this, categoryBlockInputs, 'Min or Max value should be filled');
        } else if ($(this).closest('.form-inline').find('.maxValue').val() != '' &&
            parseInt($(this).val()) > parseInt($(this).closest('.form-inline').find('.maxValue').val())) {
            addRestrictionRangeErrors(this, categoryBlockInputs, 'Min value must be less than or equal to Max value');
        } else {
            $(this).closest('.rangeValuesBlock').find('.errorRed').text('');
            removeInvalidIfNoErrors(categoryBlockInputs);
        }
        enableCancelBtn();
        updateMinValue(this);
    });

    $(document).on('change', '#restrictions input.maxValue', function () {
        var categoryBlockInputs = getCategoryBlockInputs();
        if ($(this).val() == '' && $(this).closest('.form-inline').find('.minValue').val() == '') {
            addRestrictionRangeErrors(this, categoryBlockInputs, 'Min or Max value should be filled')
        } else if ($(this).closest('.form-inline').find('.minValue').val() != '' &&
            parseInt($(this).val()) < parseInt($(this).closest('.form-inline').find('.minValue').val())) {
            addRestrictionRangeErrors(this, categoryBlockInputs, 'Min value must be less than or equal to Max value');
        } else {
            $(this).closest('.rangeValuesBlock').find('.errorRed').text('');
            removeInvalidIfNoErrors(categoryBlockInputs);
        }
        enableCancelBtn();
        updateMaxValue(this);
    });

    $(document).on('change', '#restrictions input.calculatedRestrictionValue', function () {
        var that = $(this);
        var restrictionIndex = getRestrictionIndex(this);
        var updateCalculatedRestrictionValueUrl;
        if (policyRestrictionChbx()) {
            updateCalculatedRestrictionValueUrl = context + 'vendors/policyMatrix/updatePlanCalculatedRestrictionValue/' + $('#policyMetaId').val();
        } else {
            updateCalculatedRestrictionValueUrl = context + 'vendors/policyMatrix/updateCalculatedRestrictionValue/' + $('#policyMetaId').val();
        }
        $.post(updateCalculatedRestrictionValueUrl, {
            selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
            selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
            restrictionIndex: restrictionIndex,
            calculatedRestrictionValue: that.val(),
            sessionId: $('#sessionId').val()
        }).done(function (data) {
            var categoryBlockInputs = getCategoryBlockInputs();
            that.parent().find('span.errorRed').text('');
            removeInvalidIfNoErrors(categoryBlockInputs);
            if (data.status == true) {
                enableCancelBtn();
            } else {
                $.each(data.fieldErrorsList, function (key, responseError) {
                    $("[id='" + responseError.field + "']").find('span.errorRed').text(responseError.defaultMessage);
                });
                $.each(categoryBlockInputs, function (key, categoryBlockInput) {
                    addInvalidClass(categoryBlockInput);
                });
            }
        });
    });

    $('#policyCategoryRestriction').on('change', function () {
        $('#restrictions div').remove();
        getRestrictions(policyRestrictionChbx());
        $('#createRestrictionBtn').click(function() {
            clearRestrictionFields();
            $('#actionName').text('Create');
            $('#addRestriction').attr('name', policyRestrictionChbx() ? 'addRestrictionToPlan' : 'addRestrictionToMatrix');
        });
    });

    $('#categoryValuePropertiesForm .form-control').change(function () {
        clearErrors();
        enableCancelBtn();
        if ($(this).attr('id') == 'value') {
            $('.focusedInput').parent().find('.categoryValue').val($(this).val());
        }
        updateValues($('.focusedInput'), true);
    });

    $('#caption').change(function () {
        var selectedCategoryValueInput = $('#category-' + $('#selectedPolicyMetaCategoryId').val() + '-' + $('#selectedPolicyMetaCategoryIndex').val() + '-' + $('#selectedCategoryValueIndex').val());
        selectedCategoryValueInput.val($(this).val());
        // autofillProperties(this, true);
    });

    $('a[id*=addCategoryBlock-]').off().click(function () {
        var $el = $(this);
        if($el.data('clicked')){
            e.preventDefault();
            e.stopPropagation();
        } else {
            $el.data('clicked', true);
            window.setTimeout(function(){
                $el.removeData('clicked');
            }, 1000);
            enableCancelBtn();
            var addCategoryBlockUrl = context + 'vendors/policyMatrix/addCategoryBlock/' + $('#policyMetaId').val();
            var categoryRow = $(this).closest('.row');
            var categoryId = $(this).attr('id').replace('addCategoryBlock-', '');
            var policyMetaCategoryIndex = categoryRow.find('div[id*=categoryBlock-]').length;
            var categoryBlocksOnPage = $('#categories div[id*=categoryBlock-]');
            if (categoryBlocksOnPage.length == 0) {
                $('#rightBlock').show();
            }
            $.post(addCategoryBlockUrl, {
                categoryId: categoryId,
                policyMetaId: $('#policyMetaId').val(),
                sessionId: $('#sessionId').val()
            }).done(function (data) {
                var categoryBlocks = categoryRow.find('div[id*=categoryBlock-]').length;
                categoryRow.find('a[id*=addCategoryBlock-]').parent().before('<div id="categoryBlock-' + categoryId + '-' + policyMetaCategoryIndex + '" class="col-md-3"> \
                    <input class="policyMetaCategoryIndex" type="hidden" value="' + categoryBlocks + '"/> \
                    <div> \
                    <input id="category-' + categoryId + '-' + policyMetaCategoryIndex + '-0" style="margin-bottom: 3px;" type="text" class="form-control" /> \
                    <input type="hidden" class="categoryValue"/> \
                    <input type="hidden" class="categoryValueIndex" value="0"/> \
                    <input type="hidden" class="valueDuplicatesError" value="false"/> \
                    <input type="hidden" class="categoryValueProperiesErrors" value="false"/> \
                    </div> \
                    </div>');
                var addedCategoryValue = categoryRow.find('input[id*=category-]').last();
                addedCategoryValue.trigger('click', true);
                changeFocusedInput(addedCategoryValue);
            });
        }
    });
    $('#saveStateCountriesChk').click(function () {
        var statesArray = new Array();
        var countriesArray = new Array();
        $("input[id*=state-]:checked").each(function () {
            statesArray.push($(this).val());
        });
        $("input[id*=country-]:checked").each(function () {
            countriesArray.push($(this).val());
        });
        var updateStatesCountriesUrl;
        enableCancelBtn();
        if (policyRestrictionChbx()) {
            updateStatesCountriesUrl = context + 'vendors/policyMatrix/updatePlanStatesCountries/' + $('#policyMetaId').val();
        } else {
            updateStatesCountriesUrl = context + 'vendors/policyMatrix/updateStatesCountries/' + $('#policyMetaId').val();
        }
        var restrictionIndex = $('#restrictionIndex').val();
        $.post(updateStatesCountriesUrl, {
            selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
            selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
            restrictionIndex: restrictionIndex,
            statesArray: statesArray,
            countriesArray: countriesArray,
            sessionId: $('#sessionId').val()
        }).done(function (data) {
            $('#restrictions div').remove();
            if (data != false) {
                getRestrictions(policyRestrictionChbx());
            }
            $('#editRestrictionModal').modal('hide');
        });
    });
    $('#saveAll').click(function (e) {
        setTimeout(function (){
            if (!isUpdating) {
                saveAll(e);
            } else {
                setTimeout(saveAll(e), 100);
            }
        }, 100);
    });
    $('#cancelBtn').click(function () {
        location.reload();
    });
    $('#selectAllUsStates').click(function () {
        $('#usStatesBlock').find(':checkbox').prop('checked', true);
    });
    $('#deselectAllUsStates').click(function () {
        $('#usStatesBlock').find(':checkbox').prop('checked', false);
    });
    $('#selectAllCaStates').click(function () {
        $('#caStatesBlock').find(':checkbox').prop('checked', true);
    });
    $('#deselectAllCaStates').click(function () {
        $('#caStatesBlock').find(':checkbox').prop('checked', false);
    });
    $('#selectAllCountries').click(function () {
        $('#allCountriesBlock').find(':checkbox').prop('checked', true);
    });
    $('#deselectAllCountries').click(function () {
        $('#allCountriesBlock').find(':checkbox').prop('checked', false);
    });
    $('.editValueProperties').click(function () {
        var formControl = $(this).closest('.form-group').find('.form-control');
        if ($(formControl)[0].hasAttribute('readonly')) {
            $(formControl).removeAttr('readonly');
        } else {
            $(formControl).removeAttr('disabled');
        }
        formControl.focus();
    });
    $('.alertClose').click(function () {
        $(this).parent().hide();
    });
    $(".smallNumericInput").mask('000');
    setTimeout(function (){
        disableCategoryValueInputs();
    }, 50);
});

function getCategoryBlockInputs () {
    return $('.focusedInput').closest('div[id*=categoryBlock-]').find('input[id*=category-]');
}

function getFocusedInput () {
    return $('#categories .focusedInput');
}

function addRestrictionRangeErrors (changedInput, categoryBlockInputs, errorText) {
    $(changedInput).closest('.rangeValuesBlock').find('.errorRed').text(errorText);
    $.each(categoryBlockInputs, function (key, categoryBlockInput) {
        addInvalidClass(categoryBlockInput);
    });
}

function checkRestrictionsErrors () {
    var hasRestrictionsErrors = false;
    var restrictionsErrors = $('#restrictions .errorRed');
    $.each(restrictionsErrors, function (key, restrictionError) {
        if ($(restrictionError).text() != '') {
            hasRestrictionsErrors = true;
            return false;
        }
    });
    return hasRestrictionsErrors;
}

function removeInvalidIfNoErrors (categoryBlockInputs) {
    var hasRestrictionsError = checkRestrictionsErrors();
    if (!hasRestrictionsError) {
        removeInvalidIfNoPropertiesErrors(categoryBlockInputs);
    }
}

function removeInvalidIfNoPropertiesErrors (categoryBlockInputs) {
    $.each(categoryBlockInputs, function (key, categoryBlockInput) {
        var isValueDuplicatesError = $(categoryBlockInput).parent().find('.valueDuplicatesError').val();
        var isCategoryValueProperiesErrors = $(categoryBlockInput).parent().find('.categoryValueProperiesErrors').val();
        if (isValueDuplicatesError == 'false' && isCategoryValueProperiesErrors == 'false') {
            $(categoryBlockInput).removeClass('invalid');
        }
    });
}

function addInvalidClass (element) {
    if (!$(element).hasClass('invalid')) {
        $(element).addClass('invalid');
    }
}

function autofillProperties (element, isCaptionEdited) {
    var changedValue = $(element).val();
    if (/(\d+)%/.test(changedValue)) {
        if (!isCaptionEdited) {
            $('#caption').val($(element).val());
        }
        $('#valueType').val('PERCENT');
        $('#value').val(changedValue.match(/(\d+)%/)[1]);
        $(element).parent().find('.categoryValue').val(changedValue.match(/(\d+)%/)[1]);
    } else if (/\$(\d+[,.]*\d+)/.test(changedValue)) {
        var dollarFormatPatter = /(\d)(?=(\d{3})+(?!\d))/g;
        var formattedValue = changedValue.replace(dollarFormatPatter, "$1,");
        if (!isCaptionEdited) {
            $('#caption').val(formattedValue);
        }
        $(element).val(formattedValue);
        $('#valueType').val('FIX');
        $('#value').val(changedValue.match(/\$(\d+[,.]*\d+)/)[1].replace(/[.,]/, ''));
        $(element).parent().find('.categoryValue').val(changedValue.match(/\$(\d+[,.]*\d+)/)[1].replace(/[.,]/, ''));
    } else {
        if (!isCaptionEdited) {
            $('#caption').val($(element).val());
        }
        $('#value').val('0');
        $(element).parent().find('.categoryValue').val('0');
        $('#valueType').val('NAN');
    }
}

function policyRestrictionChbx () {
    return document.getElementById('policyCategoryRestriction').checked;
}

function saveAll(event) {
    if ($('.invalid').length > 0) {
        showAlerts('block', 'none');
        event.preventDefault();
    } else {
        disableCancelBtn();
        var saveAllUrl = context + 'vendors/policyMatrix/saveAll';
        $.post(saveAllUrl, {
            policyMetaId: $('#policyMetaId').val(),
            sessionId: $('#sessionId').val()
        }).done(function () {
            showAlerts('none', 'block');
        });
    }
}

function enableCancelBtn() {
    if ($('#cancelBtn')[0].hasAttribute('disabled')) {
        $('#cancelBtn').removeAttr('disabled');
    }
}

function disableCancelBtn() {
    if (!$('#cancelBtn')[0].hasAttribute('disabled')) {
        $('#cancelBtn').attr('disabled', 'disabled');
    }
}

function disableCategoryValueInputs() {
    if ($('#residence option:selected').size() != 0 ||
        $('#citizenshipCountry option:selected').size() != 0 ||
        $('#destinationCountry option:selected').size() != 0) {
        $('#categories input').prop("readonly", true);
        $('#rightBlock input').prop("readonly", true);
        $('#restrictionsBlock select').prop("disabled", true);
        $('a[id*=addCategoryBlock-]').hide();
        $('#rightBlock button').prop('disabled', true);
        $('#saveAll').prop('disabled', true);
    } else {
        $('#categories input').prop("readonly", false);
        $('#restrictionsBlock input').prop("readonly", false);
        $('#restrictionsBlock select').prop("disabled", false);
        $('a[id*=addCategoryBlock-]').show();
        $('#rightBlock button').prop('disabled', false);
        $('#saveAll').prop('disabled', false);
    }
}

function changeFocusedInput (input) {
    $('#categories input').removeClass('focusedInput');
    $(input).addClass('focusedInput');
}

function getRestriction(element) {
    var getRestrictionUrl;
    if (policyRestrictionChbx()) {
        getRestrictionUrl = context + 'vendors/policyMatrix/getPlanRestriction/' + $('#policyMetaId').val();
    } else {
        getRestrictionUrl = context + 'vendors/policyMatrix/getCategoryRestriction/' + $('#policyMetaId').val();
    }
    var restrictionIndex = getRestrictionIndex(element);
    $.post(getRestrictionUrl, {
        selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
        selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
        restrictionIndex: restrictionIndex,
        sessionId: $('#sessionId').val()
    }).done(function (data) {
        $('#editRestrictionModal input:checkbox').prop('checked', false);
        if (data.states) {
            $.each(data.states, function (key, value) {
                $('#state-' + value).prop('checked', true);
            });
        }
        if (data.countries) {
            $.each(data.countries, function (key, value) {
                $('#country-' + value).prop('checked', true);
            });
        }
        $('#restrictionIndex').val(restrictionIndex);
        //$('#editRestrictionModal').modal('show');

        clearRestrictionFields();
        $('#actionName').text('Edit');
        $('#restrictionId').val(data.id);
        $('#restrictionType').val(data.restrictionType);
        $('#restrictionPermit').val(data.restrictionPermit);
        $('#countries').multiselect('select', data.countries);
        $('#states').multiselect('select', data.states);
        $('#minValue').val(data.minValue);
        $('#maxValue').val(data.maxValue);
        $('#calculatedRestrictions').val(data.calculatedRestrictions);
        $('#tempId').val(data.tempId);
        $('#addRestrictionModal').modal('show');
    });
}

function enableSelects() {
    $("#restrictionType").removeClass('disabled-pointer');
    $("#restrictionPermit").removeClass('disabled-pointer');
}

function showAlerts(errorAlertVisibility, successAlertVisibility) {
    $('#pageError').css('display', errorAlertVisibility);
    $('#pageSuccess').css('display', successAlertVisibility);
}

function getRestrictions(plan) {
    var policyMetaRestrictions = $('#policyMetaRestrictions').val();
    /*<![CDATA[*/
    var getRestrictionsUrl;
    if (plan) {
        getRestrictionsUrl = context + 'vendors/policyMatrix/getPlanRestrictions/' + $('#policyMetaId').val();
    } else {
        getRestrictionsUrl = context + 'vendors/policyMatrix/getCategoryRestrictions/' + $('#policyMetaId').val();
    }
    $.post(getRestrictionsUrl, {
        selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
        selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
        sessionId: $('#sessionId').val()
    }).done(function (data) {
        $.each(data.restrictionsList, function(key, value) {
            var content = '';
            if (value.restrictionType == 'CITIZEN' || value.restrictionType == 'RESIDENT' || value.restrictionType == 'DESTINATION') {
                content = '<div class="row" id="restriction-' + key + '"> \
                            <div class="col-md-4"> \
                                <select class="form-control">';
                if (value.restrictionType == 'CITIZEN') {
                    content += '<option' + (value.restrictionPermit == 'ENABLED' ? ' selected' : '') + ' value="ENABLED">Citizen of</option> \
                                    <option' + (value.restrictionPermit == 'DISABLED' ? ' selected' : '') + ' value="DISABLED">Citizen is not</option>';
                }
                if (value.restrictionType == 'RESIDENT') {
                    content += '<option' + (value.restrictionPermit == 'ENABLED' ? ' selected' : '') + ' value="ENABLED">Resident of</option> \
                                    <option' + (value.restrictionPermit == 'DISABLED' ? ' selected' : '') + ' value="DISABLED">Resident is not</option>';
                }
                if (value.restrictionType == 'DESTINATION') {
                    content += '<option' + (value.restrictionPermit == 'ENABLED' ? ' selected' : '') + ' value="ENABLED">Destination of</option> \
                                    <option' + (value.restrictionPermit == 'DISABLED' ? ' selected' : '') + ' value="DISABLED">Destination is not</option>';
                }
                content += '</select> \
                            </div> \
                            <div class="col-md-5"> \
                                <label id="residentRestrictionLabel">';
                if (value.countries && value.countries.length > 0) {
                    content += '<strong>Countries:</strong> ';
                    var countriesLength = value.countries.length;
                    $.each(value.countries, function (key, value) {
                        content += value;
                        if (countriesLength - 1 != key) {
                            content += ', ';
                        }
                    });
                }
                if (value.states && value.states.length > 0) {
                    if (value.countries && value.countries.length > 0 ) {
                        content += '; '
                    }
                    content += '<strong>States:</strong> ';
                    var statesLength = value.states.length;
                    $.each(value.states, function (key, value) {
                        content += value;
                        if (statesLength - 1 != key) {
                            content += ', ';
                        }
                    });
                }
                content += '</label> \
                            </div> \
                            <div class="col-md-3 text-right"> \
                                <button class="btn btn-primary editRestriction" type="button" title="Edit"> \
                                    <span class="glyphicon glyphicon-edit"></span> \
                                </button> \
                                <button onclick="deleteRestriction(this)" class="btn btn-danger" type="button" title="Remove"> \
                                    <span class="glyphicon glyphicon-trash"></span> \
                                </button> \
                            </div> \
                        </div>';
            } else if (value.restrictionType == 'CALCULATE') {
                content += '<div class="row" id="restriction-' + key + '"> \
                            <label class="col-md-4">' + value.restrictionType + '</label> \
                            <div class="col-md-6"> \
                            <input type="text" value="' + (value.calculatedRestrictions != null ? value.calculatedRestrictions : '') + '" class="form-control calculatedRestrictionValue" style="width: 265px;"/> \
                            <span class="errorRed"></span> \
                            </div> \
                            <div class="col-md-2 text-right"> \
                                <button onclick="deleteRestriction(this)" class="btn btn-danger" type="button" title="Remove"> \
                                    <span class="glyphicon glyphicon-trash"></span> \
                                </button> \
                            </div> \
                            </div> \
                            </div>';
            } else {
                content += '<div class="row" id="restriction-' + key + '"> \
                            <label class="col-md-4">' + value.restrictionType + '</label> \
                            <div class="col-md-6 rangeValuesBlock"> \
                                <div class="form-inline"> \
                                    <input type="text" value="' + (value.minValue != null ? value.minValue : '') + '" class="form-control minValue longNumericValue" style="width: 65px;"/>- \
                                    <input type="text" value="' + (value.maxValue != null ? value.maxValue : '') + '" class="form-control maxValue longNumericValue" style="width: 65px;"/> \
                                    <select class="form-control"> \
                                        <option' + (value.restrictionPermit == 'ENABLED' ? ' selected' : '') + ' value="ENABLED">Allowed</option> \
                                        <option' + (value.restrictionPermit == 'DISABLED' ? ' selected' : '') + ' value="DISABLED">Disallowed</option> \
                                    </select> \
                                </div> \
                                <span class="errorRed"></span> \
                            </div> \
                            <div class="col-md-2 text-right"> \
                                <button onclick="deleteRestriction(this)" class="btn btn-danger" type="button" title="Remove"> \
                                    <span class="glyphicon glyphicon-trash"></span> \
                                </button> \
                            </div> \
                        </div>';
            }
            $('#restrictions').append(content);
        });
        $.each(data.fieldErrorsList, function (key, value) {
            $('#' + value.field).find('.errorRed').text(value.defaultMessage);
        });
        if (data.fieldErrorsList.length == 0) {
            var categoryBlockInputs = getCategoryBlockInputs();
            removeInvalidIfNoPropertiesErrors(categoryBlockInputs);
        }
        disableCategoryValueInputs();
    });
    /*]]>*/
}

function addInvalidToDuplicatesCategoryValue (categoryBlockInputs, focusedCategoryValue) {
    $.each(categoryBlockInputs, function (key, categoryBlockInput) {
        var blockCategoryValue = $(categoryBlockInput).parent().find('.categoryValue').val();
        if (blockCategoryValue == focusedCategoryValue) {
            $(categoryBlockInput).parent().find('.valueDuplicatesError').val("true");
            addInvalidClass(categoryBlockInput);
        }
    });
}

function getDuplicatesCount (categoryBlockInputs) {
    var duplicatesCount = {};
    $.each(categoryBlockInputs, function (key, categoryBlockInput) {
        var blockCategoryValue = $(categoryBlockInput).parent().find('.categoryValue').val();
        duplicatesCount[blockCategoryValue] = (duplicatesCount[blockCategoryValue]||0) + 1;
    });
    return duplicatesCount;
}

function updateValues(selectedInput, update) {
    showAlerts('none', 'none');
    var inputsAmount = $(selectedInput).parent().parent().find('input[id*=category-]').length;
    var params = $('#categoryValuePropertiesForm').serializeArray();
    params.push({name: 'sortOrder', value: $('#sortOrder').val()});
    params.push({name: 'inputsAmount', value: inputsAmount});
    params.push({name: 'update', value: update});
    params.push({name: 'valueType', value: $('#valueType').val()});
    params.push({name: 'sessionId', value: $('#sessionId').val()});
    isUpdating = true;
    $.post(editCategoryValueUrl  + $('#policyMetaId').val(), params).done(function (data) {
        var focusedCategoryValue = $(selectedInput).parent().find('.categoryValue').val();
        var categoryBlockInputs = $(selectedInput).closest('div[id*=categoryBlock-]').find('input[id*=category-]');
        if (data.status != true) {
            var isUniqueError = false;
            $.each(data.fieldErrorsList, function(key, responseError) {
                $("[id='" + responseError.field + "']").parent().find('span').text(responseError.defaultMessage);
                if (responseError.defaultMessage == 'Value must be unique!') {
                    isUniqueError = true;
                }
            });
            // categoryValueProperiesErrors is set to true only if field or global errors are presented,
            // duplicates checked in a separate method and valueDuplicatesError flag is used..
            if (isUniqueError == false ||
                    (isUniqueError == true && (data.fieldErrorsList.length > 1 || data.globalErrors.length > 0))) {
                $(selectedInput).parent().find('.categoryValueProperiesErrors').val('true');
            }
            // collect global errors
            var errors = '';
            $.each(data.globalErrors, function(key, globalError) {
                errors += globalError + '<br/>';
            });
            if (errors != '') {
                showErrors(errors);
            }
            addInvalidToDuplicatesCategoryValue(categoryBlockInputs, focusedCategoryValue);
            removeInvalidIfNoDuplicatesAndRestrictionsErrors(categoryBlockInputs, selectedInput, focusedCategoryValue);
        } else {
            $(selectedInput).parent().find('.categoryValueProperiesErrors').val('false');
            removeInvalidIfNoDuplicatesAndRestrictionsErrors(categoryBlockInputs, selectedInput, focusedCategoryValue);
        }
    }).always(function () {
        isUpdating = false;
    });
}

function removeInvalidIfNoDuplicatesAndRestrictionsErrors (categoryBlockInputs, selectedInput, focusedCategoryValue) {
    var hasRestrictionsErrors = checkRestrictionsErrors();
    var count = 0;
    var duplicatesCount = getDuplicatesCount(categoryBlockInputs);
    $.each(categoryBlockInputs, function (key, categoryBlockInput) {
        var categoryValuePropertiesErrors = $(categoryBlockInput).parent().find('.categoryValueProperiesErrors').val();
        var blockCategoryValue = $(categoryBlockInput).parent().find('.categoryValue').val();
        if (blockCategoryValue == focusedCategoryValue) {
            count++;
        }
        if (blockCategoryValue != focusedCategoryValue && duplicatesCount[blockCategoryValue] == 1 && categoryValuePropertiesErrors == 'false') {
            if (!hasRestrictionsErrors) {
                $(categoryBlockInput).removeClass('invalid');
            }
            $(categoryBlockInput).parent().find('.valueDuplicatesError').val("false");
        }
    });
    var categoryValuePropertiesErrors = $(selectedInput).parent().find('.categoryValueProperiesErrors').val();
    if (count == 1 && categoryValuePropertiesErrors == 'false') {
        if (!hasRestrictionsErrors) {
            $(selectedInput).removeClass('invalid');
        }
        $(selectedInput).parent().find('.valueDuplicatesError').val("false");
    }
}

function setValueProperties(element) {
    return setValueProperties(element, false);
}

function setValueProperties(element, executeValidation) {
    $('#categoryName').text(element.closest('.row').find('.categoryName').val());
    $('#selectedPolicyMetaCategoryId').val(element.closest('.row').find('a[id*=addCategoryBlock-]').attr('id').replace('addCategoryBlock-', ''));
    $('#selectedPolicyMetaCategoryIndex').val(element.parent().parent().find('.policyMetaCategoryIndex').val());
    $('#selectedCategoryValueIndex').val(element.parent().find('.categoryValueIndex').val());
    var getCategoryValueUrl = context + 'vendors/policyMatrix/getCategoryValue/' + $('#policyMetaId').val();
    $.post(getCategoryValueUrl, {
        selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
        selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
        selectedCategoryValueIndex: $('#selectedCategoryValueIndex').val(),
        sessionId: $('#sessionId').val()
    }).done(function (data) {
        var policyMetaCategoryValue = data.policyMetaCategoryValue;
        $('#caption').val(policyMetaCategoryValue.caption);
        $('#value').val(policyMetaCategoryValue.value);
        element.parent().find('.categoryValue').val(policyMetaCategoryValue.value);
        $('#valueType').val(policyMetaCategoryValue.valueType);
        $('#apiValue').val(policyMetaCategoryValue.apiValue);
        $('#daysAfterInitialDeposit').val(policyMetaCategoryValue.daysAfterInitialDeposit);
        $('#daysAfterFinalPayment').val(policyMetaCategoryValue.daysAfterFinalPayment);
        $('#minAge').val(policyMetaCategoryValue.minAge);
        $('#maxAge').val(policyMetaCategoryValue.maxAge);
        $('#sortOrder').val(policyMetaCategoryValue.sortOrder);
        $('#secondary option[value=' + policyMetaCategoryValue.secondary + ']').prop('selected', 'selected');
        showSubcategories(data.subcategoryValueMap);
        if (executeValidation) {
            updateValues(this, false);
        }
    });
}

function showSubcategories(subcategoryValueMap) {
    $('#subcategoryValues div').remove();
    var content = '<div class="form-horizontal">';
    $.each(subcategoryValueMap, function (key, value) {
        content += '<div class="form-group"> \
        <label class="col-md-5 control-label">' + key + '</label> \
        <div class="col-md-3"> \
        <input id="subcategoryValue-' + value.id + '"type="text" value="' + value.subcategoryValue + '" class="form-control"/> \
        </div> \
        </div>';
    });
    if ($.isEmptyObject(subcategoryValueMap)) {
        content += 'No subcategories';
    }
    content += '</div>';
    $('#subcategoryValues').append(content);
}

function addCategoryValue() {
    clearErrors();
    enableCancelBtn();
    showAlerts('none', 'none');
    var addCategoryValueUrl = context + 'vendors/policyMatrix/addCategoryValue/' + $('#policyMetaId').val();
    var selectedCategoryValueInput = $('#category-' + $('#selectedPolicyMetaCategoryId').val() + '-' + $('#selectedPolicyMetaCategoryIndex').val() + '-' + $('#selectedCategoryValueIndex').val());
    var categoryValueIndex = selectedCategoryValueInput.parent().parent().find('input[id*=category-]').length;
    $.post(addCategoryValueUrl, {
        selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
        selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
        sessionId: $('#sessionId').val()
    }).done(function (data) {
        if (data == 'success') {
            selectedCategoryValueInput.parent().parent().append('<div> \
                    <input id="category-' + $('#selectedPolicyMetaCategoryId').val() + '-' + $('#selectedPolicyMetaCategoryIndex').val() + '-' + categoryValueIndex + '" style="margin-bottom: 3px;" type="text" class="form-control" /> \
                    <input type="hidden" class="categoryValue"/> \
                    <input type="hidden" class="categoryValueIndex" value="' + categoryValueIndex + '"/> \
                    <input type="hidden" class="valueDuplicatesError" value="false"/> \
                    <input type="hidden" class="categoryValueProperiesErrors" value="false"/> \
                    </div>');
            var addedCategoryValueInput = $('#category-' + $('#selectedPolicyMetaCategoryId').val() + '-' + $('#selectedPolicyMetaCategoryIndex').val() + '-' + categoryValueIndex);
            addedCategoryValueInput.trigger('click', true);
            var blockInputs = addedCategoryValueInput.closest('div[id*=categoryBlock-]').find('input[id*=category-]');
            if (blockInputs.length == 2) {
                if ($(blockInputs[0]).parent().find('.categoryValue').val() == '') {
                    $(blockInputs[0]).removeClass('invalid');
                }
            }
            changeFocusedInput(addedCategoryValueInput);
            $('#valueType').val('PERCENT');
        } else {
            selectedCategoryValueInput.click();
            showErrors(data);
        }
    });
}

function clearErrors() {
    $('#valuePropertiesError').text('');
    $('#valuePropertiesError').hide();
    $('#categoryValuePropertiesForm span.errorRed').text('');
    $('#categoryValuePropertiesForm').find('input').removeClass("invalid");
}

function showErrors(errorText) {
    $('#valuePropertiesError').html(errorText);
    $('#valuePropertiesError').show();
}

function deleteCategoryValue() {
    if (confirm("Confirm Delete?")) {
        enableCancelBtn();
        var deleteCategoryValueUrl = context + 'vendors/policyMatrix/removeCategoryValue/' + $('#policyMetaId').val();
        $.post(deleteCategoryValueUrl, {
            selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
            selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
            selectedCategoryValueIndex: $('#selectedCategoryValueIndex').val(),
            sessionId: $('#sessionId').val()
        }).done(function (data) {
            var categoryToHide = $('#category-' + $('#selectedPolicyMetaCategoryId').val() + '-' + $('#selectedPolicyMetaCategoryIndex').val() + '-' + $('#selectedCategoryValueIndex').val());
            categoryToHide.attr('id', categoryToHide.attr('id').replace('category-', 'hidden-'));
            var valuesCount = categoryToHide.parent().parent().find('input[id*=category-]').length;
            if (valuesCount >= 1) {
                var existedBlockCategoryInputs = categoryToHide.closest('div[id*=categoryBlock-]').find('input[id*=category-]');
                var count = 0;
                existedBlockCategoryInputs.each(function () {
                    if ($(this).val() == categoryToHide.val()) {
                        count++;
                    }
                });
                /*<![CDATA[*/
                if (count <= 1) {
                    $.each(existedBlockCategoryInputs, function (key, existedBlockCategoryInput) {
                        var hasRestrictionsErrors = checkRestrictionsErrors();
                        var existedCategoryValue = $(existedBlockCategoryInput).parent().find('.categoryValue').val();
                        var categoryToHideValue = $(categoryToHide).parent().find('.categoryValue').val();
                        if (existedCategoryValue == categoryToHideValue) {
                            $(existedBlockCategoryInput).parent().find('.valueDuplicatesError').val("false");
                            if (!hasRestrictionsErrors) {
                                $(existedBlockCategoryInput).removeClass('invalid');
                            }
                        }
                    });
                }
                /*]]>*/
                categoryToHide.removeClass('invalid');
                categoryToHide.hide();
                var nextCategory = $('#category-' + $('#selectedPolicyMetaCategoryId').val() + '-' + $('#selectedPolicyMetaCategoryIndex').val() + '-' + (parseInt($('#selectedCategoryValueIndex').val()) + 1));
                var categoryToClick;
                if (nextCategory.length != 0) {
                    categoryToClick = nextCategory;
                } else {
                    categoryToClick = categoryToHide.parent().parent().find('input[id*=category-]').first();
                }
                var nextCategoryValues = categoryToHide.parent().nextAll('div:has(input[id*=category-])');
                nextCategoryValues.each(function () {
                    var nextInput = $(this).find('input[id*=category-]');
                    var valueIndex = nextInput.attr('id').match(/\d+$/);
                    nextInput.attr('id', nextInput.attr('id').replace(/\d+$/, valueIndex - 1));
                    nextInput.parent().find('.categoryValueIndex').val(valueIndex - 1);
                });
            } else {
                var categoryBlock = categoryToHide.closest('div[id*=categoryBlock-]');
                categoryToHide.removeClass('invalid');
                categoryBlock.attr('id', categoryBlock.attr('id').replace('categoryBlock-', 'hiddenBlock-'));
                categoryBlock.hide();
                var inputs = categoryToHide.closest('.row').find('input[id*=category-]');
                if (inputs.length != 0) {
                    categoryToClick = categoryToHide.closest('.row').find('input[id*=category-]').first();
                } else {
                    var lastRowElement = categoryToHide.closest('.categoryRow').nextAll().find('input[id*=category-]');
                    if (lastRowElement.length != 0) {
                        categoryToClick = categoryToHide.closest('.categoryRow').nextAll().find('input[id*=category-]').first();
                    } else {
                        categoryToClick = categoryToHide.closest('.categoryRow').prevAll().find('div[id*=categoryBlock-]').last().find('input[id*=category-]').first();
                    }
                }
                var nextCategoryBlocks = categoryBlock.nextAll('div[id*=categoryBlock-]');
                nextCategoryBlocks.each(function () {
                    var valueIndex = $(this).attr('id').match(/\d+$/);
                    $(this).attr('id', $(this).attr('id').replace(/\d+$/, valueIndex - 1));
                    var policyMetaCategoryIndex = $(this).find('.policyMetaCategoryIndex');
                    policyMetaCategoryIndex.val(parseInt(policyMetaCategoryIndex.val()) - 1);
                    var nextInputs = $(this).find('input[id*=category-]');
                    nextInputs.each(function () {
                        var inputId = $(this).attr('id');
                        var idParts = inputId.split('-');
                        var categoryIndex = idParts[2] - 1;
                        $(this).attr('id', idParts[0] + '-' + idParts[1] + '-' + categoryIndex + '-' + idParts[3]);
                        $(this).closest('div[id*=categoryBlock-]').find('.policyMetaCategoryIndex').val(categoryIndex);
                    });
                });
            }
            if (categoryToClick.length != 0) {
                categoryToClick.click();
            } else {
                $('#rightBlock').hide();
            }
        });
    }
}

function updateMinValue(element) {
    var restrictionIndex = getRestrictionIndex(element);
    var updateMinValueUrl;
    if (policyRestrictionChbx()) {
        updateMinValueUrl = context + 'vendors/policyMatrix/updatePlanMinValue/' + $('#policyMetaId').val();
    } else {
        updateMinValueUrl = context + 'vendors/policyMatrix/updateMinValue/' + $('#policyMetaId').val();
    }
    $.post(updateMinValueUrl, {
        selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
        selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
        restrictionIndex: restrictionIndex,
        minValue: $(element).val(),
        sessionId: $('#sessionId').val()
    });
}

function updateMaxValue(element) {
    var restrictionIndex = getRestrictionIndex(element);
    var updateMaxValueUrl;
    if (policyRestrictionChbx()) {
        updateMaxValueUrl = context + 'vendors/policyMatrix/updatePlanMaxValue/' + $('#policyMetaId').val();
    } else {
        updateMaxValueUrl = context + 'vendors/policyMatrix/updateMaxValue/' + $('#policyMetaId').val();
    }
    $.post(updateMaxValueUrl, {
        selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
        selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
        restrictionIndex: restrictionIndex,
        maxValue: $(element).val(),
        sessionId: $('#sessionId').val()
    });
}

function updateRestrictionsSelect(element) {
    var restrictionIndex = getRestrictionIndex(element);
    var updateRestrictionsSelectUrl;
    enableCancelBtn();
    if (policyRestrictionChbx()) {
        updateRestrictionsSelectUrl = context + 'vendors/policyMatrix/updatePlanRestrictionsSelect/' + $('#policyMetaId').val();
    } else {
        updateRestrictionsSelectUrl = context + 'vendors/policyMatrix/updateRestrictionsSelect/' + $('#policyMetaId').val();
    }
    $.post(updateRestrictionsSelectUrl, {
        selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
        selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
        restrictionIndex: restrictionIndex,
        selectValue: $(element).val(),
        sessionId: $('#sessionId').val()
    });
}

function deleteRestriction(element) {
    if (confirm("Confirm Delete?")) {
        var restrictionIndex = getRestrictionIndex(element);
        var removeRestrictionUrl;
        enableCancelBtn();
        if (policyRestrictionChbx()) {
            removeRestrictionUrl = context + 'vendors/policyMatrix/removePlanRestriction/' + $('#policyMetaId').val();
        } else {
            removeRestrictionUrl = context + 'vendors/policyMatrix/removeRestriction/' + $('#policyMetaId').val();
        }
        $.post(removeRestrictionUrl, {
            selectedPolicyMetaCategoryId: $('#selectedPolicyMetaCategoryId').val(),
            selectedPolicyMetaCategoryIndex: $('#selectedPolicyMetaCategoryIndex').val(),
            restrictionIndex: restrictionIndex,
            sessionId: $('#sessionId').val()
        }).done(function (data) {
            $('#restrictions div').remove();
            if (data != false) {
                getRestrictions(policyRestrictionChbx());
            }
        });
    }
}

function getRestrictionIndex(element) {
    return $(element).closest('.row').attr('id').match(/\d+$/)[0];
}