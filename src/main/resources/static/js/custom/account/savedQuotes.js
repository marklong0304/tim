var dateFrom, dateTo;

$(document).ready(function () {
    dateFrom = $('#dateFrom').datepicker({
        autoclose: true,
        endDate: new Date()
    }).on('changeDate', function (selected) {
        var startDate = new Date(selected.date);
        $('#dateTo').datepicker('setStartDate', startDate);
    }).on('clearDate', function (selected) {
        $('#dateTo').datepicker('setStartDate', null);
    });


    dateTo = $('#dateTo').datepicker({
        autoclose: true,
        endDate: new Date()
    }).on('changeDate', function (selected) {
        var endDate = new Date(selected.date.valueOf());
        $('#dateFrom').datepicker('setEndDate', endDate);
    }).on('clearDate', function (selected) {
        $('#dateFrom').datepicker('setEndDate', null);
    });


    $('.dateInput').mask("00/00/0000", {placeholder: "  /  /    "});


    if ($('#quotes').find('td').size() > 1) {
        let table = $('#quotes').DataTable({
            stateSave: true,
            info: false,
            "aoColumns": [
                null,
                null,
                null,
                null,
                null,
                {"bSortable": false},
                {"bSortable": false}
            ],
            'order': [[0, 'desc']],
        });


        // Handle click on "Select all" control
        $('#select-all').on('click', function () {
            // Get all rows with search applied
            const rows = table.rows({'search': 'applied'}).nodes();
            // Check/uncheck checkboxes for all rows in the table
            $('input[type="checkbox"]', rows).prop('checked', this.checked);
        });

        // Handle click on checkbox to set state of "Select all" control
        $('#quotes tbody').on('change', 'input[type="checkbox"]', function () {
            // If checkbox is not checked
            if (!this.checked) {
                const el = $('#select-all').get(0);
                // If "Select all" control is checked and has 'indeterminate' property
                if (el && el.checked && ('indeterminate' in el)) {
                    // Set visual state of "Select all" control as 'indeterminate'
                    el.indeterminate = true;
                }
            }
        });
    }
    $('#quotes').wrap('<div class="overflowX"></div>');
});

$('.deleteChecked').click(function (e) {
    const selectedCheckboxes = $('.delete-checkbox:checked');
    if (selectedCheckboxes.length > 0 && confirm("Confirm Delete?")) {
        selectedCheckboxes.each(function () {
            const id = $(this).parent().attr('data-quote-id');
            const deleteQuoteUrl = context + 'planQuotes/delete/' + id;
            $.post(deleteQuoteUrl, function (data) {
                window.location.replace(context + data);
            });
        });
    } else {
        e.preventDefault();
    }
});