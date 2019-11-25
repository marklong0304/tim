var dataTable;
var isFirstDraw = true;
var tripCostColumn = 3;
var vendorColumn = 5;
var policyColumn = 6;
var policyNumberColumn = 7;
var purchaseDateColumn = 8;
var policyPriceColumn = 9;
var noteColumn = 10;
var idColumn = 11;
var delColumn = 12;
var isReseted = false;
var filterData = {
    vendors: [],
    policies: [],
};

var selectedPolicies = [];
var selectedVendors = [];

$(function () {
    var columns = [
        {'mData': 'affiliate'},
        {'mData': 'userName'},
        {'mData': 'traveler'},
        {'mData': 'tripCost'},
        {'mData': 'departDate'},
        {'mData': 'vendor'},
        {'mData': 'policy'},
        {'mData': 'policyNumber'},
        {'mData': 'purchaseDate'},
        {'mData': 'totalPrice'},
        {'mData': 'note'},
        {'mData': 'id', 'bSortable': false}
    ];
    //Add delete column for admin
    if($('#isAdmin').val() == 'true') {
        columns.push({'mData': 'canDelete', 'bSortable': false});
    }
    dataTable = $('#payments').DataTable({
        'responsive': true,
        'bProcessing': true,
        'bServerSide': true,
        'sort': 'position',
        'colResize': true,
        'autoWidth': true,
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
            'url': context + 'commissions/payments/get.json',
            'contentType': 'application/json; charset=utf-8',
            dataType: 'json',
            'type': 'POST',
            'data': function (d) {
                var affiliatesFilter = $('#affiliates').val();
                var usersFilter = $('#users').val();
                var userId = $('#userId').val();
                if (userId !== '' && isFirstDraw) {
                    usersFilter = userId;
                }
                d.affiliates = affiliatesFilter ? [affiliatesFilter] : null;
                d.users = usersFilter ? [usersFilter] : null;
                d.departDate = {
                    from: $('#departDateFrom').val(),
                    to: $('#departDateTo').val(),
                };
                d.purchaseDate = {
                    from: $('#purchaseDateFrom').val(),
                    to: $('#purchaseDateTo').val(),
                };
                d.vendors = $('#vendors').val();
                d.policies = $('#policies').val();
                d.tripCost = {
                    from: $('#tripCostFrom').val().replace(/\./g, ''),
                    to: $('#tripCostTo').val().replace(/\./g, ''),
                };
                d.policyPrice = {
                    from: $('#policyPriceFrom').val().replace(/\./g, ''),
                    to: $('#policyPriceTo').val().replace(/\./g, ''),
                };
                d.policyNumber = $('#policyNumber').val();
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
                // d.cancellation = $("#cancellation")[0].checked ? "CANCELLED" : "ALL"
                d.cancellation = $('input[name="isCancelled"]:checked').val();
                return JSON.stringify(d);
            },
        },
        'order': [[8, 'desc']],
        'aoColumns': columns,
        'createdRow': function (row, data, index) {

            var purchaseId = $('td', row).eq(idColumn).text();
            var purchaseLink = '<a class="text-center" href="' + context + 'commissions/purchase/' + 'payments/' + purchaseId + ' ">' +
                '<span class="glyphicon glyphicon-chevron-right"></span></a>';
            $('td', row).eq(idColumn).text('').append(purchaseLink);

            var delCell = $('td', row).eq(delColumn).text('');
            if($('input[name="isCancelled"]:checked').val() == 'CANCELLED' && data.canDelete) {
                var delPurchaseLink = '<a class="text-center" onclick="deletePurchase(' +
                    '\'' + purchaseId + '\', \'' + $('td', row).eq(policyColumn).text() + '\'' +
                    ', \'' + $('td', row).eq(policyNumberColumn).text() + '\'' +
                    ', \'' + $('td', row).eq(vendorColumn).text() + '\'' +
                    ', \'' + $('td', row).eq(purchaseDateColumn).text() + '\');">' +
                    '<span class="glyphicon glyphicon-trash booking-delete"></span></a>';
                delCell.append(delPurchaseLink);
            }

            $('td', row).eq(tripCostColumn).text('$' + $('td', row).eq(tripCostColumn).text());
            $('td', row).eq(policyPriceColumn).text('$' + $('td', row).eq(policyPriceColumn).text());

            var cellData = $('td', row).eq(10).text();
            createNote(noteColumn, data.editable, purchaseId, cellData, row);
        },
        'footerCallback': function (row, data, start, end, display) {
            var api = this.api(), data;
            var tripCostPageTotal = api
                .column(3, {page: 'current'})
                .data()
                .reduce(function (a, b) {
                    return parseFloat(a) + parseFloat(b);
                }, 0);
            var policyPricePageTotal = api
                .column(9, {page: 'current'})
                .data()
                .reduce(function (a, b) {
                    return parseFloat(a) + parseFloat(b);
                }, 0);
            $(api.column(3).footer()).html(
                '$' + tripCostPageTotal.toFixed(2)
            );
            $(api.column(9).footer()).html(
                '$' + policyPricePageTotal.toFixed(2)
            );
        },
        'drawCallback': function () {
            isReseted = false;

            createTooltip('#payments');

            $('#vendors').empty();
            $('#policies').empty();

            initVendors('#payments');

            initPolicies('#payments');
            drawPolicies();

            $( 'tr' ).siblings().find('a[class=" "]').parent().parent().addClass('cancellation');
            isFirstDraw = false;
        },
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

    $('#allRadio').on('click', function(){
        if(!isReseted) {
            $("#doFilter").trigger('click');
        }
    });

    $('#checkedRadio').on('click', function(){
        if(!isReseted) {
            $("#doFilter").trigger('click');
        }
    });

    $('#cancelledRadio').on('click', function(){
        if(!isReseted) {
            $("#doFilter").trigger('click');
        }
    });

    $('#doFilter').on('click', function () {
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
        $('#policyPriceFrom').val(null);
        $('#policyPriceTo').val(null);
        $('#tripCostFrom').val(null);
        $('#tripCostTo').val(null);
        $('#policyNumber').val(null);
        $('#note').val(null);
        $('#fullName').val(null);
        $('.filter-option').text('');
        $('#middleName').val(null);
        $('#ageFrom').val(null);
        $('#ageTo').val(null);
        //$("#cancellation").val(null);
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
        d.vendors = $('#vendors').val();
        d.policies = $('#policies').val();
        d.tripCost = {
            from: $('#tripCostFrom').val().replace(/\./g, ''),
            to: $('#tripCostTo').val().replace(/\./g, ''),
        };
        d.policyPrice = {
            from: $('#policyPriceFrom').val().replace(/\./g, ''),
            to: $('#policyPriceTo').val().replace(/\./g, ''),
        };
        d.policyNumber = $('#policyNumber').val();
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
        d.cancellation = $('input[name="isCancelled"]:checked').val();
        d.search = {
            value: $('div.dataTables_filter input').val(),
            regex: false,
        };
        $('#filterData').val(JSON.stringify(d));
        $('#exportForm').submit();
    });


    $('#payments td').css('white-space', 'initial');
    $('#payments td').css('overflow', 'hidden');
    $('#payments td').css('text-overflow', 'ellipsis');

    $('.adminDataTables .formBlockLine .col-lg-6 div').css('margin', '0');
    $('.btn-group').css('width', '96%');
    $('.multiselect').css('width', '100%');
    $('.multiselect-container').css('width', 'fit-content');
    $('#payments_length').addClass('col-sm-6');
});

function deletePurchase(purchaseUuid, policy, policyNumber, vendor, purchaseDate) {
    if(confirm('Delete ' + policy + ' #' + policyNumber + ' of ' + vendor + ' bought on ' + purchaseDate + '?')) {
        $.post(context + 'commissions/delete/', { purchaseUuid : purchaseUuid }, function () {
            dataTable.draw();
        });
    }
}