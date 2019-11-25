var dataTable;
var isFirstDraw = true;
var idColumn = 5;
var isReseted = false;
var filterData = {
    vendors: [],
    policies: [],
};

var selectedPolicies = [];
var selectedVendors = [];

$(function () {
    dataTable = $('#affiliatePayments').DataTable({
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
        "scrollY": 620,
        "scrollCollapse": true,
        'ajax': {
            'url': context + 'reports/affiliatePayments/get.json',
            'contentType': 'application/json; charset=utf-8',
            dataType: 'json',
            'type': 'POST',
            'data': function (d) {
                var affiliatesFilter = $('#affiliates').val();
                var companiesFilter = $('#companies').val();
                var usersFilter = $('#users').val();
                d.affiliates = affiliatesFilter ? [affiliatesFilter] : null;
                d.companies = companiesFilter ? [companiesFilter] : null;
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
                d.paymentDate = {
                    from: $('#paymentDateFrom').val(),
                    to: $('#paymentDateTo').val(),
                };
                d.paymentTotal = {
                    from: $('#paymentTotalFrom').val().replace(/\./g, ''),
                    to: $('#paymentTotalTo').val().replace(/\./g, ''),
                };
                return JSON.stringify(d);
            },
        },
        'order': [[3, 'desc'], [5, 'desc']],
        'aoColumns': [
            {'mData': 'affiliateUser'},
            {'mData': 'affiliateCompany'},
            {'mData': 'paymentOption'},
            {'mData': 'paymentDate'},
            {'mData': 'total', 'bSortable': false},
            {'mData': 'id', 'bSortable': false}
        ],
        'createdRow': function (row, data, index) {
            var affiliatePaymentId = $('td', row).eq(idColumn).text();
            var purchaseLink = '<a class="text-center" href="' + context + 'reports/MyPayment/' + affiliatePaymentId + ' "> \
                    <span class="glyphicon glyphicon-chevron-right"></span></a>';
            $('td', row).eq(idColumn).text('').append(purchaseLink);
        },
        'drawCallback': function () {
            isReseted = false;

            createTooltip('#payments');

            $('#vendors').empty();
            $('#policies').empty();

            initVendors('#affiliatePayments');
            initPolicies('#affiliatePayments');

            drawPolicies();

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
        clearSelectizeInput('#companies');
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
        $('#paymentDateFrom').val(null);
        $('#paymentDateTo').val(null);
        $('#paymentTotalFrom').val(null);
        $('#paymentTotalTo').val(null);
        selectedPolicies = [];
        selectedVendors = [];
        dataTable.search("").draw();
    });

    bootstrapMultiselectInit();
    selectizeAffiliatesInit();
    selectizeCompaniesInit();
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