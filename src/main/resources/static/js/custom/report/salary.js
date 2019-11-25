var affiliateColumn = 0;
var vendorColumn = 1;
var policyColumn = 2;
var policyNumberColumn = 3;
var totalPriceColumn = 4;
var purchaseDateColumn = 5;
var expectedSalaryColumn = 6;
var receivedCommissionColumn = 7;
var salaryColumn = 7;
var payColumn = 8;
var idColumn = 10;
var isReseted = false;
var noteColumn = 9;
var companyColumn = 11;

var dataTable;
var purchaseUid;
var affData = {};

var filterData = {
    vendors: [],
    policies: []
};

var selectedPolicies = [];
var selectedVendors = [];

function setTotal(self) {
    var isChecked = $(self)[0].checked;
    var salary = Number($(self).parent().prev().children().eq(0).val());
    var total = $(self).parent().parent().nextAll('.dtrg-end').eq(0).children().eq(1);
    var totalValue = Number(total.text().substr(1));
    totalValue = isChecked ? totalValue + salary : totalValue - salary;
    total.text('$' + totalValue.toFixed(2));
    return totalValue;
}

function setTotalAdjustBalance(self) {
    var totalValue = setTotal(self);
    var startRow = $(self).parent().parent().prevAll('.dtrg-start').eq(0);
    var balanceRow = startRow.nextAll('.balance').eq(0);
    var balance = Number(balanceRow.find('.salaryInput').val());
    var balancePayInput = balanceRow.find('.payInput');
    var totalAfterBalanceCheck = totalValue;
    if(!balancePayInput.is(":checked")) {
        totalAfterBalanceCheck += balance;
    }
    if (totalAfterBalanceCheck > 0) {
        balancePayInput.removeAttr('disabled');
    } else {
        balancePayInput.click();
        balancePayInput.attr('disabled', true);
    }
}

function fillUpdatedFields(id, affiliateId, salary, name, val) {
    const updatedField = {
        id: id,
        affiliateId: affiliateId,
        salary: salary,
        name: name,
        value: val,
    };
    var updatedFieldIndex = updatedFields.findIndex(f => f.id === id && f.affiliateId === affiliateId && f.name === name);
    if(updatedFieldIndex < 0) {
        updatedFields.push(updatedField);
    } else {
        updatedFields[updatedFieldIndex] = updatedField;
    }
}

var updatedFields = [];
$(document).ready(function () {
    dataTable = $('#salaries').DataTable({
        "responsive": true,
        "bProcessing": true,
        "bServerSide": true,
        "sort": "position",
        "length": function () {
            var s = '<%=Session["length"] %>';
            return s;
        },
        "start": 0,
        "lengthMenu": [50, 100, 200],
        "columnDefs": [
            {"orderable": false, "width": "10%", "targets": 0}
        ],
        "sScrollY": 620,
        "scrollCollapse": true,
        "ajax": {
            "url": context + "reports/salary/get.json",
            "contentType": "application/json; charset=utf-8",
            dataType: "json",
            "type": "POST",
            "data": function (d) {
                var affiliatesFilter = $("#affiliates").val();
                d.affiliates = affiliatesFilter ? [affiliatesFilter] : null;
                var fullName = $('#travellers').val();
                d.traveler = {
                    firstName: fullName ? fullName.replace(/\"/g, "").split("|")[0] : "",
                    lastName: fullName ? fullName.replace(/\"/g, "").split("|")[1] : "",
                    middleInitials: $("#middleName").val(),
                    age: {
                        from: $("#ageFrom").val(),
                        to: $("#ageTo").val()
                    }
                };
                d.vendors = $("#vendors").val();
                d.policies = $("#policies").val();
                d.purchaseDate = {
                    from: $("#purchaseDateFrom").val(),
                    to: $("#purchaseDateTo").val()
                };
                d.policyNumber = $("#policyNumber").val();
                d.pay = $('input[name="isPaid"]:checked').val() === "PAID";
                d.note = $("#note").val();
                d.expectedSalary = {
                    from: $("#expectedSalaryFrom").val().replace(/\./g, ''),
                    to: $("#expectedSalaryTo").val().replace(/\./g, '')
                };
                d.totalPrice = {
                    from: $("#policyPriceFrom").val().replace(/\./g, ''),
                    to: $("#policyPriceTo").val().replace(/\./g, '')
                };
                d.salary = {
                    from: $("#salaryFrom").val().replace(/\./g, ''),
                    to: $("#salaryTo").val().replace(/\./g, '')
                };
                d.search = {
                    value: $('div.dataTables_filter input').val(),
                    regex: false
                };
                d.cancellation = $('#cancellation')[0].checked.toString();
                d.updatedFields = updatedFields;
                return JSON.stringify(d);
            }
        },
        "orderFixed": [[11, "asc"], [0, "asc"]],
        "aoColumns": [
            {"mData": "affiliate"},
            {"mData": "vendor"},
            {"mData": "policy"},
            {"mData": "policyNumber"},
            {"mData": "totalPrice"},
            {"mData": "purchaseDate", "searchable": false},
            {"mData": "expectedSalary"},
            {"mData": "salary"},
            {"mData": "pay", 'bSortable': false, "searchable": false},
            {"mData": "note", 'bSortable': false},
            {"mData": "id", "bSortable": false, "searchable": false},
            {"mData": "company", "visible": false, "searchable": false}
        ],
        rowGroup: {
            dataSrc: ["company", "affiliate"],
            startRender: function ( rows, group ) {
                if (group === '') return;
                return $('<tr class="group" />')
                    .append('<td colspan="11">' + group + '</td>');
            },
            endRender: function (rows, group) {
                if (group === '') return;
                var groupName = 'group-' + group.replace(/[^A-Za-z0-9]/g, '');
                var rowNodes = rows.nodes();
                rowNodes.to$().addClass(groupName);
                var checkboxesSelected = $('.payInput:checked', rowNodes);
                var isSelected = (checkboxesSelected.length === rowNodes.length);

                var salary = "$0.00";

                if (isSelected) {
                    salary = rows
                        .data()
                        .pluck("salary")
                        .reduce(function (a, b) {
                            return a + b * 1;
                        }, 0);
                    salary = $.fn.dataTable.render.number(',', '.', 2, '$').display(salary);
                }

                var uid = affData[group][1];
                return $('<tr />')
                    .append('<td colspan="7">Total ('+ group +'):</td>')
                    .append('<td colspan="1">' + salary + '</td>')
                    .append('<td colspan="1"><input class="payAllInput" type="checkbox" name="payAll" uid ="' + uid + '"' + (isSelected ? ' checked' : '') + ' data-group-name="' + groupName + '"' + ' disabled/></td>')
                    .append('<td colspan="2"/>');
            },
        },
        "createdRow": function (row, data, index) {

            var totalPriceInput = $('td', row).eq(totalPriceColumn).text();
            if (totalPriceInput !== '') {
                totalPriceInput = '$' + totalPriceInput;;
            }

            var rowId = $('td', row).eq(idColumn).text();


            var disabled = 'disabled';
            if (policyNumber !== '' ) {
                disabled = '';
            }

            var disabledSecure = data.editable? '' : 'disabled';

            var viewLink;
            if(!isNaN(rowId) && rowId < 0) {
                //Link to balance detail page
                viewLink = '<a id="' + (-rowId) + '" class="text-center" style="text-decoration:none;"'
                    + ' href="' + context + 'reports/salary/balance/' + data.affiliateId + '">'
                    +'<span class="glyphicon glyphicon-chevron-right"></span></a>';
            } else {
                //Link to purchase detail page
                viewLink = '<a id="' + rowId + '" class="text-center ' + disabled + '" style="text-decoration:none;"'
                    + ' href="' + context + 'reports/purchase/MySalary/' + rowId + '">'
                    +'<span class="glyphicon glyphicon-chevron-right"></span></a>';
            }
            viewLink += '<input id="note-' + rowId + '" type="hidden" value="' + data.note + '"/>';

            var pay = $('td', row).eq(payColumn).text();
            var checked = '';
            if (pay == 'true') {
                checked = ' checked'
            }

            var expectedSalary = $('td', row).eq(expectedSalaryColumn).text();
            if (expectedSalary !== '') {
                expectedSalary = expectedSalary < 0 ? ('-$' + (-expectedSalary).toFixed(2)) : ('$' + expectedSalary);
            }

            var salary = $('td', row).eq(salaryColumn).text();
            var salaryInput = '<input class="salaryInput form-control input-sm dollarInput" type="text" style="width: 100px;" name="salary"'
                + (rowId < 0 && salary === '0.00' ? ' readonly="readonly"' : '')
                + ' value="' + salary + '"' + ' uid="' + rowId + '" affiliateId="' + data.affiliateId + '"' + disabledSecure +'/>';

            var payCheckbox = '<input class="payInput"' + ' type="checkbox" name="pay" '
                + ' onclick="' + (rowId < 0 ? 'setTotal(this)' : 'setTotalAdjustBalance(this)') + '"'
                + (rowId < 0 && (salary === '0.00' || salary < 0) ? ' disabled' : '')
                + checked + ' uid="' + rowId + '" affiliateId="' + data.affiliateId + '"' + disabledSecure +'/>';

            var affiliateSpan;
            if(isNaN(rowId) || rowId >= 0) {
                affiliateSpan = '<span id="' + data.affiliateId + '" class="affiliateField">' + data.affiliate + '</span>';
            } else {
                affiliateSpan = '<span id="' + data.affiliateId + '" class="affiliateField"><b><i>Balance</i></b></span>';
            }
            var companySpan = '<span id="' + data.companyId + '" class="companyField">' + data.company + '</span>';
            if(rowId < 0) {
                $(row).addClass('balance');
            }
            $('td', row).eq(affiliateColumn).text("").append(affiliateSpan);
            $('td', row).eq(companyColumn).text("").append(companySpan);
            $('td', row).eq(totalPriceColumn).text("").append(totalPriceInput);
            $('td', row).eq(expectedSalaryColumn).text("").append(expectedSalary);
            $('td', row).eq(salaryColumn).text("").append(salaryInput);
            $('td', row).eq(payColumn).text("").append(payCheckbox);
            $('td', row).eq(idColumn).text("").append(viewLink);
            affData[data.affiliate] = [data.payAll, data.affiliateId];
            affData[data.company] = [data.payAll, data.companyId];

            var cellData = $('td', row).eq(noteColumn).text().trim();

            createNote(noteColumn, data.editable, rowId, cellData, row);
        },
        "footerCallback": function (row, data, start, end, display) {
            var api = this.api(), data;

            var totalPriceTotal = api
                .column(totalPriceColumn, {page: 'current'})
                .data()
                .reduce(function (a, b) {
                    if (isNaN(a) || a === '' || a === null) {
                        a = 0
                    }
                    if (isNaN(b) || b === '' || b === null) {
                        b = 0
                    }
                    return parseFloat(a) + parseFloat(b);
                }, 0);
            if (isNaN(totalPriceTotal)) {
                totalPriceTotal = 0;
            }
            $(api.column(totalPriceColumn).footer()).html(
                '$' + totalPriceTotal.toFixed(2)
            );

            var expectedTotal = api
                .column(expectedSalaryColumn, {page: 'current'})
                .data()
                .reduce(function (a, b) {
                    if (isNaN(a) || a === '' || a === null) {
                        a = 0
                    }
                    if (isNaN(b) || b === '' || b === null) {
                        b = 0
                    }
                    return parseFloat(a) + parseFloat(b);
                }, 0);
            if (isNaN(expectedTotal)) {
                expectedTotal = 0;
            }
            $(api.column(expectedSalaryColumn).footer()).html(
                '$' + expectedTotal.toFixed(2)
            );

            var salaryTotal = api
                .column(salaryColumn, {page: 'current'})
                .data()
                .reduce(function (a, b) {
                    if (isNaN(a) || a === '' || a === null) {
                        a = 0
                    }
                    if (isNaN(b) || b === '' || b === null) {
                        b = 0
                    }
                    return parseFloat(a) + parseFloat(b);
                }, 0);
            if (isNaN(salaryTotal)) {
                salaryTotal = 0;
            }
            $(api.column(salaryColumn).footer()).html(
                '$' + salaryTotal.toFixed(2)
            );
        },
        "drawCallback": function (oSettings, json) {
            isReseted = false;
            var api = this.api();
            var rows = api.rows({page: 'current'}).nodes();
            api.column(0, {page: 'current'}).data().each(function (group, i) {
                $(rows).eq(i).find('span:first').css('color', 'gray');
            });


            $(".salaryInput").on('blur', function () {
                fillUpdatedFields($(this).attr("uid"), $(this).attr("affiliateId"), $(this).parent().parent().find('.salaryInput').val(), this.name, this.value)
            });

            createTooltip('#salaries')

            $('#vendors').empty();
            $('#policies').empty();

            initVendors('#salaries')

            initPolicies('#salaries');
            drawPolicies();
        }
    });

    const scrollArea = this.querySelector(".dataTables_scrollBody");
    scrollArea.addEventListener("wheel", function() {
        const scrollTop = this.scrollTop;
        const maxScroll = this.scrollHeight - this.offsetHeight;
        const deltaY = event.deltaY;
        if ( (scrollTop >= maxScroll && deltaY > 0) || (scrollTop === 0 && deltaY < 0) ) {
            event.preventDefault();
        }
    });

    scrollArea.addEventListener("touchstart", function(event) {
        this.previousClientY = event.touches[0].clientY;
    });

    scrollArea.addEventListener("touchmove", function(event) {
        const scrollTop = this.scrollTop;
        const maxScroll = this.scrollHeight - this.offsetHeight;
        const currentClientY = event.touches[0].clientY;
        const deltaY = this.previousClientY - currentClientY;
        if ( (scrollTop >= maxScroll && deltaY > 0) || (scrollTop === 0 && deltaY < 0) ) {
            event.preventDefault();
        }
        this.previousClientY = currentClientY;
    });

    dataTable.on( 'draw', function () {
        $('#salaries').DataTable().columns.adjust();

        $('.dtrg-start').each(function(){
            var startClassMatch = $(this).next().prop('class').match(/group-/g);
            if (startClassMatch !== null && startClassMatch.length === 2){
                $(this).remove();
            }
        });

        $('.dtrg-end').each(function(){
            var endClassMatch = $(this).prev().prop('class').match(/group-/g);
            if (endClassMatch !== null && endClassMatch.length === 2 && $(this).next().prop('class') !== undefined && !$(this).next().hasClass('dtrg-start')){
                $(this).remove();
            }
        });

        $( '<p></p>' ).insertAfter('.dtrg-end');

        $('.dtrg-group').each(function(){
            $(this).removeClass('dtrg-level-1').addClass('dtrg-level-0');
        });
    });

    $('#cancellation').on('click', function(){
        if(!isReseted) {
            $("#doFilter").trigger('click');
        }
    });

    $('#notPaidRadio').on('click', function(){
        if(!isReseted) {
            $("#doFilter").trigger('click');
        }
    });

    $('#paidRadio').on('click', function(){
        if(!isReseted) {
            $("#doFilter").trigger('click');
        }
    });

    /*$('#salaries').on('click', '#cancellation, #notPaidRadio, #paidRadio', function(){
        if(!isReseted) {
            $("#doFilter").trigger('click');
        }
    });*/

    $('#salaries tbody').on('click', 'tr', function({target}) {
        if (!target.parentElement.classList.contains('dtrg-start')) return;
        $.when($(target.parentElement).nextUntil('.dtrg-start').fadeToggle(200))
            .done(function() {
                $('#salaries').DataTable().columns.adjust();
            });
    });


    $('#salaries').on('click', '.payInput', function () {
        var isChecked = $(this)[0].checked;
        var className = $(this).parent().parent().prop('class').split(' ')[1];
        var payAll = $(this).parent().parent().nextAll('.dtrg-end').eq(0).find('.payAllInput');
        if (!isChecked){
            payAll[0].checked = false;
        } else {
            var checked = true;
            $(this).parent().parent().siblings(`.${className}`).children().children('.payInput').each(function () {
                checked = checked && $(this)[0].checked;
            });
            payAll[0].checked = checked;
        }
        fillUpdatedFields($(this).attr("uid"), $(this).attr("affiliateId"),  $(this).parent().parent().find('.salaryInput').val(), this.name, this.checked);
    });

    // Handle click event on group checkbox
    $('#salaries').on('click', '.payAllInput', function(e){
        // Get group class name
        var groupName = $(this).data('group-name');
        var checked = this.checked;
        var balancePayInput = null;
        //Click pay inputs that don't match the payAllInput checked status in all child rows except the balance row
        $(`.${groupName}`).find('.payInput').each(function() {
            if($(this).parent().parent().hasClass('balance')) {
                balancePayInput = this;
            } else if(checked !== this.checked) {
                $(this).trigger('click');
            }
            fillUpdatedFields($(this).attr("uid"), $(this).attr("affiliateId"), $(this).parent().parent().find('.salaryInput').val(), this.name, this.checked);
        });
        //Click pay inputs that don't match the payAllInput checked status in the balance row
        if(balancePayInput != null && checked !== balancePayInput.checked) {
            $(balancePayInput).trigger('click');
            fillUpdatedFields($(this).attr("uid"), $(this).attr("affiliateId"), $(balancePayInput).parent().parent().find('.salaryInput').val(), balancePayInput.name, balancePayInput.checked);
        };
        $(this).prop('checked', checked);
    });

    $('#addSalaryBtn').click(function () {
        $('.errorRed').text('');
        var addSalaryUrl = context + 'commissions/salary';
        $.ajax({
            url: addSalaryUrl,
            dataType: 'json',
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify({
                'affiliateId': $('#affiliates-correction').val(),
                'note': $('#newNote').val(),
                'salaryToPay': $('#newSalary').val(),
            }),
            processData: false
        }).done(function (data) {
            if (data.status === true) {
                dataTable.draw();
            } else {
                $.each(data.fieldErrorsList, function (key, salaryResponseError) {
                    $("[name='" + salaryResponseError.field + "']").parent().find('span').text(salaryResponseError.defaultMessage);
                });
            }
        }).fail(function () {
            alert('error!');
        });
    });


    $('#saveChangesBtn').click(function () {
        dataTable.draw();
        updatedFields.length = 0;
    });

    $("#doFilter").on("click", function () {
        if (updatedFields.length > 0 && !confirm('All unsaved changes will be lost')) {
            return;
        }
        $(".salaryInput").unbind('blur');

        selectedPolicies = [];
        selectedVendors = [];
        $( '#policies' ).siblings().find('.active').find('input').each(function() {
            if(!isNaN(parseInt($(this).val()))){
                selectedPolicies.push({name: $(this).parent().text().trim(), id: parseInt($(this).val())})
            }
        });
        $( '#vendors' ).siblings().find('.active').find('input').each(function() {
            if(!isNaN(parseInt($(this).val()))){
                selectedVendors.push({name: $(this).parent().text().trim(), id: parseInt($(this).val())})
            }
        });

        updatedFields = [];
        dataTable.draw();
    });

    $("#reset").on("click", function () {
        isReseted = true;
        clearSelectizeInput('#travellers');
        clearBootstrapMultiselect('#vendors');
        clearBootstrapMultiselect('#policies');
        $("#purchaseDateFrom").val(null);
        $("#purchaseDateTo").val(null);
        $("#salaryFrom").val(null);
        $("#salaryTo").val(null);
        $("#policyPriceFrom").val(null);
        $("#policyPriceTo").val(null);
        $("#expectedSalaryFrom").val(null);
        $("#expectedSalaryTo").val(null);
        $("#policyNumber").val(null);
        $("#note").val(null);
        $("#pay").val(null);
        $("#firstName").val(null);
        $("#lastName").val(null);
        $("#middleName").val(null);
        $("#ageFrom").val(null);
        $("#ageTo").val(null);
        $("#checkNumber").val(null);
        $('#cancellation')[0].checked = false;
        $('#notPaidRadio').prop("checked", true);
        $("#newSalary").val(0);
        $("#newNote").val(null);
        selectedPolicies = [];
        selectedVendors = [];
        dataTable.search("").draw();
    });

    bootstrapMultiselectInit();
    selectizeTravellersInit();

    $('#confirm').selectpicker();

    $('#purchaseDate').datepicker({
        orientation: "bottom auto",
        autoclose: true,
        endDate: new Date()
    });
    $('#purchaseDateParam, #returnDate').datepicker({
        orientation: "bottom auto",
        autoclose: true
    });
    $('.dateInput').mask("00/00/0000", {placeholder: "  /  /    "});
    $('.dateInput').datepicker({
        orientation: "bottom auto",
        autoclose: true
    });
    $('.numberInput').mask('000.000.000', {reverse: true});
    //$('.dollarInput').mask('0000000000.00', {reverse: true});
    $('.ageInput').mask('000', {reverse: true});

    $("#export").on("click", function () {
        var d = {};
        var affiliatesFilter = $("#affiliates").val();
        d.affiliates = affiliatesFilter ? [affiliatesFilter] : null;
        d.traveler = {
            firstName: $('#firstName').val(),
            lastName: $('#lastName').val(),
            middleInitials: $('#middleName').val(),
            age: {
                from: $('#ageFrom').val(),
                to: $('#ageTo').val(),
            },
        };
        d.vendors = $("#vendors").val();
        d.policies = $("#policies").val();
        d.purchaseDate = {
            from: $("#purchaseDateFrom").val(),
            to: $("#purchaseDateTo").val()
        };
        d.policyNumber = $("#policyNumber").val();
        d.pay = $('input[name="isPaid"]:checked').val() === "PAID";
        d.note = $("#note").val();
        var fullName = $("#travellers").val();
        d.traveler = {
            firstName: fullName ? fullName.replace(/\"/g, "").split("|")[0] : "",
            lastName: fullName ? fullName.replace(/\"/g, "").split("|")[1] : "",
            middleInitials: $("#middleName").val(),
            age: {
                from: $("#ageFrom").val(),
                to: $("#ageTo").val()
            }
        };
        d.policyPrice = {
            from: $("#policyPriceFrom").val().replace(/\./g, ''),
            to: $("#policyPriceTo").val().replace(/\./g, '')
        };
        d.expectedCommission = {
            from: $("#expectedSalaryFrom").val().replace(/\./g, ''),
            to: $("#expectedSalaryTo").val().replace(/\./g, '')
        };
        d.receivedCommission = {
            from: $("#vendorCommissionFrom").val().replace(/\./g, ''), //Should be removed
            to: $("#vendorCommissionTo").val().replace(/\./g, '')
        };
        d.salary = {
            from: $("#salaryFrom").val().replace(/\./g, ''),
            to: $("#salaryTo").val().replace(/\./g, '')
        };
        d.cancellation = $('#cancellation')[0].checked.toString();
        d.updatedFields = updatedFields;
        d.search = {
            value: $('div.dataTables_filter input').val(),
            regex: false
        };
        $("#filterData").val(JSON.stringify(d));
        $("#exportForm").submit();
    });
    $('#cancellation,#pay').selectpicker();

    $('#salaries').on('length.dt', function (e, settings, len) {
        '<%Session["length"] = "' + len + '"; %>';
    });

    $('#salaries td').css('white-space', 'initial');
    $('#salaries td').css('overflow', 'hidden');
    $('#salaries td').css('text-overflow', 'ellipsis');

    $('.adminDataTables .formBlockLine .col-lg-6 div').css('margin', '0')
    $('.btn-group').css('width', '96%')
    $('.multiselect').css('width', '100%')
    $('.multiselect-container').css('width', 'fit-content');
    $('#salaries_length').addClass('col-sm-6');
});