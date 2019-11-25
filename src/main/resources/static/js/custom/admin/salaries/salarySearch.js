var payDateColumn = 0;
var agentColumn = 1;
var salaryColumn = 2;
var idColumn = 3;
var isReseted = false;

var dataTable;

$(document).ready(function () {
    dataTable = $('#salarySearch').DataTable({
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
            {"width": "10%", "targets": 0},
            {"width": "10%", "targets": 2},
            {"width": "5%", "targets": 3}
        ],
        "sScrollY": 600,
        "scrollCollapse": true,
        "paging": false,
        "ajax": {
            "url": context + "commissions/salary/search/get.json",
            "contentType": "application/json; charset=utf-8",
            dataType: "json",
            "type": "POST",
            "data": function (d) {
                var affiliatesFilter = $('#affiliates').val();
                d.affiliates = affiliatesFilter ? [affiliatesFilter] : null;
                d.payDate = {
                    from: $('#payDateFrom').val(),
                    to: $('#payDateTo').val()
                };
                d.salary = {
                    from: $("#salaryFrom").val().replace(/\./g, ''),
                    to: $("#salaryTo").val().replace(/\./g, '')
                };
                d.search = {
                    value: $('div.dataTables_filter input').val(),
                    regex: false
                };
                return JSON.stringify(d);
            }
        },
        "order": [0, "desc"],
        "aoColumns": [
            {"mData": "payDate", "searchable": false},
            {"mData": "affiliate"},
            {"mData": "salary"},
            {"mData": "id", "bSortable": false, "searchable": false}
        ],
        "createdRow": function (row, data, index) {

            var salary = $('td', row).eq(salaryColumn).text();
            if (salary !== '') {
                salary = '$' + salary;
            }

            var agent = data.company !== '' ? data.company : data.affiliate;
            var agentId = data.companyId !== '' ? data.companyId : data.affiliateId;

            var agentSpan = '<span id="' + agentId + '" class="agentField">' + agent + '</span>';

            var purchaseId = $('td', row).eq(idColumn).text();
            var disabled = '';
            if (purchaseId === '') {
                disabled = 'disabled';
            }
            var purchaseLink = '<a id="' + purchaseId + '" class="text-center ' + disabled + '" href="' + context + 'commissions/purchase/' + 'salary/' + purchaseId + ' " style="text-decoration:none;">'
                + '<span class="glyphicon glyphicon-chevron-right"></span></a>';

            $('td', row).eq(agentColumn).text("").append(agentSpan);
            $('td', row).eq(salaryColumn).text("").append(salary);
            $('td', row).eq(idColumn).text("").append(purchaseLink);

        },
        "footerCallback": function (row, data, start, end, display) {
            var api = this.api(), data;

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

            createTooltip('#salarySearch');
        }
    });

    dataTable.on('draw', function () {
        $('#salarySearch').DataTable().columns.adjust();
        $('<p></p>').insertAfter('.dtrg-end');
    });

    $('#doFilter').on('click', function () {
        dataTable.draw();
    });

    $('#reset').on("click", function () {
        isReseted = true;
        clearSelectizeInput('#affiliates');
        $('#payDateFrom').val(null);
        $('#payDateTo').val(null);
        $('#salaryFrom').val(null);
        $('#salaryTo').val(null);
        dataTable.search('').draw();
    });

    selectizeAffiliatesInit();

    $('#payDate').datepicker({
        orientation: 'bottom auto',
        autoclose: true,
        endDate: new Date()
    });

    $('.dateInput').mask('00/00/0000', {placeholder: '  /  /    '});
    $('.dateInput').datepicker({
        orientation: 'bottom auto',
        autoclose: true
    });

    $('#salarySearch').on('length.dt', function (e, settings, len) {
        '<%Session["length"] = "' + len + '"; %>';
    });

    $('#salarySearch td').css('white-space', 'initial');
    $('#salarySearch td').css('overflow', 'hidden');
    $('#salarySearch td').css('text-overflow', 'ellipsis');

    $('.adminDataTables .formBlockLine .col-lg-6 div').css('margin', '0')
});
