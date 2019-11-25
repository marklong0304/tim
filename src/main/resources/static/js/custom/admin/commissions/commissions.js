var dataTable;
var totalPriceColumn = 6;
var expectedCommissionColumn = 8;
var confirmColumn = 9;
var checkNumberColumn = 10;
var receivedCommissionColumn = 11;
var receivedDateColumn = 12;
var noteColumn = 13;
var idColumn = 14;
var defaultInputStyles = {color: '#555', border: '1px solid rgb(204, 204, 204)'};
var isReseted = false;
var filterData = {
    vendors: [],
    policies: [],
};

var selectedPolicies = [];
var selectedVendors = [];

//handler for clicking Confirm checkbox
function setCommission(self, commission) {
    var isChecked = $(self)[0].checked;
    var commInput = $(self).closest('tr').find('input[name="receivedCommission"]');
    commInput.val(isChecked ? commission.toFixed(2) : '');
    //update styles for commissions input if it was changed before
    commInput.css(defaultInputStyles);

    //update Rec Date
    $(self).closest('tr').find('input[name="receivedDate"]').datepicker('setDate', isChecked ? new Date() : '');
}

//handler for updating commission input
function checkCommission(self, commission) {
    var currInnput = $(self.event.target);
    var commissionValue = !commission ? 0 : +commission.toFixed(2);
    var currentValue = +currInnput[0].value;
    if (!currentValue) return;  //do nothing if input is empty

    if (+(currentValue.toFixed(2)) !== commissionValue) {
        currInnput.css({color: 'red', border: '1px solid red'});
    } else {
        currInnput.css(defaultInputStyles);
    }
}

//Add data to updated fields array
function fillUpdatedFields(id, name, val) {
    var updatedField = {
        id: id,
        name: name,
        value: val,
    };
    updatedFields.push(updatedField);
}

var updatedFields = [];
$(function () {
    dataTable = $('#commissions').DataTable({
        'responsive': true,
        'bProcessing': true,
        'bServerSide': true,
        'sort': 'position',
        'dom': 'Rlfrtip',
        'length': function () {
            var s = '<%=Session["length"] %>';
            return s;
        },
        'start': 0,
        'lengthMenu': [50, 100, 200],
        "scrollResize": true,
        "sScrollY": 620,
        "scrollCollapse": true,
        'ajax': {
            'url': context + 'commissions/commission/get.json',
            'contentType': 'application/json; charset=utf-8',
            dataType: 'json',
            'type': 'POST',
            'data': function (d) {
                var affiliatesFilter = $('#affiliates').val();
                var usersFilter = $('#users').val();
                d.affiliates = affiliatesFilter ? [affiliatesFilter] : null;
                d.users = usersFilter ? [usersFilter] : null;
                d.policies = $('#policies').val();
                d.departDate = {
                    from: $('#departDateFrom').val(),
                    to: $('#departDateTo').val(),
                };
                d.purchaseDate = {
                    from: $('#purchaseDateFrom').val(),
                    to: $('#purchaseDateTo').val(),
                };
                d.receivedDate = {
                    from: $('#receivedDateFrom').val(),
                    to: $('#receivedDateTo').val(),
                };
                d.totalPrice = {
                    from: $('#policyPriceFrom').val().replace(/\./g, ''),
                    to: $('#policyPriceTo').val().replace(/\./g, ''),
                };
                d.vendors = $('#vendors').val();
                d.policyNumber = $('#policyNumber').val();
                d.checkNumber = $('#checkNumber').val();
                d.confirm = $('input[name="isConfirm"]:checked').val();
                /*d.confirm = $('#confirm')[0].checked.toString();*/
                d.note = $('#note').val();
                var fullName = $('#travellers').val();
                d.traveler = {
                    firstName: fullName ? fullName.replace(/\"/g, '').split('|')[0] : '',
                    lastName: fullName ? fullName.replace(/\"/g, '').split('|')[1] : '',
                    middleInitials: $('#middleName').val(),
                    age: {
                        from: $('#ageFrom').val(),
                        to: $('#ageTo').val(),
                    },
                };
                d.expectedCommission = {
                    from: $('#expectedCommissionFrom').val().replace(/\./g, ''),
                    to: $('#expectedCommissionTo').val().replace(/\./g, ''),
                };
                d.receivedCommission = {
                    from: $('#receivedCommissionFrom').val().replace(/\./g, ''),
                    to: $('#receivedCommissionTo').val().replace(/\./g, ''),
                };
                d.cancellation = $('#cancellation')[0].checked.toString();
                d.updatedFields = updatedFields;
                return JSON.stringify(d);
            },
        },
        'order': [[7, 'desc']],
        'aoColumns': [
            {'mData': 'affiliate'},
            {'mData': 'userName'},
            {'mData': 'traveler'},
            {'mData': 'vendor'},
            {'mData': 'policy'},
            {'mData': 'policyNumber'},
            {'mData': 'totalPrice'},
            {'mData': 'purchaseDate'},
            {'mData': 'expectedCommission'},
            {'mData': 'confirm', 'orderDataType': 'dom-checkbox'},
            {'mData': 'checkNumber', 'bSortable': false},
            {'mData': 'receivedCommission', 'bSortable': false},
            {'mData': 'receivedDate', 'bSortable': false},
            {'mData': 'note', 'bSortable': false},
            {'mData': 'id', 'bSortable': false},
            {'mData': 'editable', 'visible': false, 'searchable': false},
        ],
        'createdRow': function (row, data, index) {
            var purchaseId = $('td', row).eq(idColumn).text();
            var purchaseLink = '<a id="' + purchaseId + '" class="text-center" href="' + context + 'commissions/purchase/' + 'commission/' + purchaseId + ' "> \
                    <span class="glyphicon glyphicon-chevron-right"></span></a>';
            var confirm = $('td', row).eq(confirmColumn).text();
            var checked = '';
            if (confirm == 'true') {
                checked = 'checked';
            }
            var disabled = '';
            if (!data.editable) {
                disabled = ' disabled ';
            }

            var checkNumber = $('td', row).eq(checkNumberColumn).text();
            var checkNumberInput = '<input class="checkNumberInput form-control input-sm" type="text" style="width: 5vw;" name="checkNumber" value="' + checkNumber + '"' +
                ' uid="' + purchaseId + '" ' + disabled + '/>';

            var totalPrice = $('td', row).eq(totalPriceColumn).text();
            var totalPriceInput = '$' + totalPrice;

            var receivedCommission = $('td', row).eq(receivedCommissionColumn).text();
            var expectedCommission = $('td', row).eq(expectedCommissionColumn).text();

            var confirmCheckbox = '<input class="confirmInput" onclick="setCommission(this, ' + expectedCommission.trim() + ')" type="checkbox" name="confirm" ' + checked + ' uid="' + purchaseId + '" ' + disabled + '/>';

            var receivedCommissionInput = '<input class="receivedCommissionInput form-control input-sm dollarInput" type="text" style="width: 5vw;" ' +
                'name="receivedCommission" oninput="checkCommission(self, ' + expectedCommission + ')"' +
                'onblur="checkCommission(self, ' + expectedCommission + ')"' +
                'value="' + receivedCommission + '"  uid="' + purchaseId + '" ' + disabled + ' />';
            var expectedCommissionInput = '<span>$' + expectedCommission + '</span>';

            var receivedDate = $('td', row).eq(receivedDateColumn).text();
            var receivedDateInput = '<input class="receivedDateInput datapicker form-control input-sm" data-date-format="mm/dd/yyyy" type="text" style="width: 5vw;" name="receivedDate" ' +
                'value="' + receivedDate + '" uid="' + purchaseId + '" ' + disabled + '/>';


            $('td', row).eq(totalPriceColumn).text('').append(totalPriceInput);
            $('td', row).eq(expectedCommissionColumn).text('').append(expectedCommissionInput);
            $('td', row).eq(confirmColumn).text('').append(confirmCheckbox);
            $('td', row).eq(checkNumberColumn).text('').append(checkNumberInput);
            $('td', row).eq(receivedCommissionColumn).text('').append(receivedCommissionInput);
            $('td', row).eq(receivedDateColumn).text('').append(receivedDateInput);
            $('td', row).eq(idColumn).text('').append(purchaseLink);


            //Draw book icon and modal content
            var cellData = $('td', row).eq(noteColumn).text().trim();

            createNote(noteColumn, data.editable, purchaseId, cellData, row);

        },
        'footerCallback': function (row, data, start, end, display) {
            var api = this.api(), data;

            var totalPriceTotal = api
                .column(totalPriceColumn, {page: 'current'})
                .data()
                .reduce(function (a, b) {
                    return parseFloat(a) + parseFloat(b);
                }, 0);
            $(api.column(totalPriceColumn).footer()).html(
                '$' + totalPriceTotal.toFixed(2)
            );

            var expectedTotal = api
                .column(expectedCommissionColumn, {page: 'current'})
                .data()
                .reduce(function (a, b) {
                    return parseFloat(a) + parseFloat(b);
                }, 0);
            $(api.column(expectedCommissionColumn).footer()).html(
                '$' + expectedTotal.toFixed(2)
            );

            var receivedTotal = api
                .column(receivedCommissionColumn, {page: 'current'})
                .data()
                .reduce(function (a, b) {
                    if (isNaN(a) || a == '') {
                        a = 0;
                    }
                    if (isNaN(b) || b == '') {
                        b = 0;
                    }
                    return parseFloat(a) + parseFloat(b);
                }, 0);
            if (isNaN(receivedTotal)) {
                receivedTotal = 0;
            }
            $(api.column(receivedCommissionColumn).footer()).html(
                '$' + receivedTotal.toFixed(2)
            );
        },
        'drawCallback': function () {
            isReseted = false;
            $('.datapicker').datepicker({orientation: 'bottom auto', autoclose: true});

            $('.checkNumberInput').blur(function () {
                fillUpdatedFields($(this).attr('uid'), this.name, this.value);
            });

            $('.receivedCommissionInput').change(function () {
                fillUpdatedFields($(this).attr('uid'), this.name, this.value);
            });

            $('.receivedCommissionInput').blur(function () {
                if ($(this).val() !== ''){
                    $(this).change();
                    $(this).parent().siblings().find('.confirmInput').prop('checked', 'checked').change();
                    $(this).parent().siblings().find('.receivedDateInput').datepicker('setDate', new Date()).change();
                }

            });

            $('.receivedDateInput').on('focus', function () {
                if ($(this).val() === ''){
                    $(this).datepicker('setDate', new Date());
                }
            });
            $('.receivedDateInput').datepicker({
                format: 'dd/mm/yy',
            });
            $('.receivedDateInput').change(function () {
                fillUpdatedFields($(this).attr('uid'), this.name, this.value);
            });

            $('.confirmInput').change(function () {
                fillUpdatedFields($(this).attr('uid'), this.name, this.checked);
                $(this).parent().siblings().find('.receivedCommissionInput').change();
                $(this).parent().siblings().find('.receivedDateInput').change();
            });

            //Tooltip on hover on cell with ellipsis
            createTooltip('#commissions');

            $('#vendors').empty();
            $('#policies').empty();

            //init travellers input
            //add data to vendors and Policies
            initVendors('#commissions');

            initPolicies('#commissions');
            drawPolicies();

            if ($('.checkNumberInput').length) return; // check if all fields was drawn
        },

    });

    /*$('.dataTables_scrollBody').on('scroll', function() {
        window.scrollTo(0, 0);
    });*/

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


    $('#cancellation').on('click', function(){
        $("#doFilter").trigger('click');
    });

    /*$('#confirm').on('click', function(){
        $("#doFilter").trigger('click');
    });*/

    $('#allRadio').on('click', function(){
        if(!isReseted) {
            $("#doFilter").trigger('click');
        }
    });

    $('#confirm').on('click', function(){
        if(!isReseted) {
            $("#doFilter").trigger('click');
        }
    });

    $('#notConfirm').on('click', function(){
        if(!isReseted) {
            $("#doFilter").trigger('click');
        }
    });

    $('#saveChangesBtn').click(function () {
        dataTable.draw();
        updatedFields.length = 0;

    });

    $('#doFilter').on('click', function () {
        if (updatedFields.length > 0 && !confirm('All unsaved changes will be lost')) {
            return;
        }
        $('.checkNumberInput,.receivedCommissionInput,.receivedDateInput').unbind('blur');

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

    $('#reset').on('click', function () {
        isReseted = true;
        clearSelectizeInput('#affiliates');
        clearSelectizeInput('#users');
        clearSelectizeInput('#travellers');
        clearBootstrapMultiselect('#vendors');
        clearBootstrapMultiselect('#policies');
        $('#departDateFrom').val(null);
        $('#departDateTo').val(null);
        $('#purchaseDateFrom').val(null);
        $('#purchaseDateTo').val(null);
        $('#receivedCommissionFrom').val(null);
        $('#receivedCommissionTo').val(null);
        $('#receivedDateFrom').val(null);
        $('#receivedDateTo').val(null);
        $('#policyPriceFrom').val(null);
        $('#policyPriceTo').val(null);
        $('#expectedCommissionFrom').val(null);
        $('#expectedCommissionTo').val(null);
        $('#policyNumber').val(null);
        $('#note').val(null);
        $('#checkNumber').val(null);
        $('#fullName').val(null);
        $('#middleName').val(null);
        $('#ageFrom').val(null);
        $('#ageTo').val(null);
        $('#cancellation')[0].checked = false;
        $('#allRadio').prop('checked', true);
        selectedPolicies = [];
        selectedVendors = [];
        dataTable.search("").draw();
    });

    bootstrapMultiselectInit();
    selectizeAffiliatesInit();
    selectizeUsersInit();
    selectizeTravellersInit();


    $('.dateInput').datepicker({autoclose: true});
    $('.dateInput').mask('00/00/0000', {placeholder: '  /  /    '});
    $('.numberInput').mask('000.000.000', {reverse: true});
    $('.ageInput').mask('000', {reverse: true});

    $('#export').on('click', function () {
        var d = {};
        d.affiliates = $('#affiliates').val();
        d.users = $('#users').val();
        d.departDate = {
            from: $('#departDateFrom').val(),
            to: $('#departDateTo').val(),
        };
        d.purchaseDate = {
            from: $('#purchaseDateFrom').val(),
            to: $('#purchaseDateTo').val(),
        };
        d.receivedDate = {
            from: $('#receivedDateFrom').val(),
            to: $('#receivedDateTo').val(),
        };
        d.policyPrice = {
            from: $('#policyPriceFrom').val().replace(/\./g, ''),
            to: $('#policyPriceTo').val().replace(/\./g, ''),
        };
        d.vendors = $('#vendors').val();
        d.policyNumber = $('#policyNumber').val();
        d.checkNumber = $('#checkNumber').val();
        d.confirm = $('#confirm').val();
        d.note = $('#note').val();
        d.traveler = {
            firstName: $('#firstName').val(),
            lastName: $('#lastName').val(),
            middleInitials: $('#middleName').val(),
            age: {
                from: $('#ageFrom').val(),
                to: $('#ageTo').val(),
            },
        };
        d.expectedCommission = {
            from: $('#expectedCommissionFrom').val().replace(/\./g, ''),
            to: $('#expectedCommissionTo').val().replace(/\./g, ''),
        };
        d.receivedCommission = {
            from: $('#receivedCommissionFrom').val().replace(/\./g, ''),
            to: $('#receivedCommissionTo').val().replace(/\./g, ''),
        };
        d.cancellation = $('#cancellation').val();
        d.updatedFields = updatedFields;
        d.search = {
            value: $('div.dataTables_filter input').val(),
            regex: false,
        };
        $('#filterData').val(JSON.stringify(d));
        $('#exportForm').submit();
    });

    //fields format initiation
    $('#commissions td').css('white-space', 'initial');
    $('#commissions td').css('overflow', 'hidden');
    $('#commissions td').css('text-overflow', 'ellipsis');
    $('.dateInput').datepicker({orientation: 'bottom auto', autoclose: true});
    $('.dateInput').mask('00/00/0000', {placeholder: '  /  /    '});
    $('.numberInput').mask('000.000.000', {reverse: true});
    $('.dollarInput').mask('0000000000.00', {reverse: true});
    $('.ageInput').mask('000', {reverse: true});
    $('#commissions').on('length.dt', function (e, settings, len) {
        '<%Session["CommissionLength"] = "' + len + '"; %>';
    });


    $('.payment .formBlockLine .col-lg-6 div').css('margin', '0');
    $('.btn-group').css('width', '96%');
    $('.multiselect').css('width', '100%');
    $('.multiselect-container').css('width', 'fit-content');
    $('#commissions_length').addClass('col-sm-6');
    $(window).bind('beforeunload', function () {
        if (updatedFields.length > 0) {
            return 'You have unsaved changes. Are you sure you want to leave without saving?';
        }
    });
});
