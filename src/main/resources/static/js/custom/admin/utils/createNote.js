function createNote(noteColumn, editable = false, purchaseId, cellData, row) {
    var blockClick = editable || cellData? '' : 'disable ';
    $('td', row).eq(noteColumn).html(`<a href='#showNote' class='${blockClick} ${cellData ? '' : 'empty'}' rel='modal:open' data-uid=${purchaseId}><i class=\"fas fa-book-open\"></i></a>`)
    $('td', row).eq(noteColumn).click(function () {
        $('#showNote').html(`<div class='modalContent'>
                    <a id="undoNote"> <i class="fas fa-undo"></i>Undo</a>
                    <p>Edit Note</p>
                    <div contenteditable=${editable.toString()} class="modalData" data-uid=${purchaseId}>${cellData}</div>
                    </div>`);
        if (editable) {
            $('#showNote .modalContent').append(
                '</div>' +
                '<a id="saveBtn" class="btn btn-primary from-control pull-right" style="margin-top: 10px; padding: 5px 20px;" role="button">Save</a>' +
                '<div class="save-message"></div>' +
                '</div>');

            $('#undoNote').click(function () {
                $('.modalData').text(cellData)
            });
            $('#saveBtn').click(function () {
                var noteText = $('.modalData').text();
                if (noteText === cellData) return;
                $.ajax({
                    url: `/api/admin/commissions/note`,
                    dataType: 'json',
                    type: 'post',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        'id': purchaseId,
                        'note': noteText
                    }),
                    processData: false
                }).done(function (data) {
                    if (data.status === true) {
                        cellData = noteText;
                        var uid = $('.modalData').attr('data-uid');
                        cellData === '' ? $(`a[data-uid='${uid}']`).addClass('empty') : $(`a[data-uid='${uid}']`).removeClass('empty');

                        $('.save-message').text('Note saved!').css('display', 'inline').delay(2000).fadeOut('slow');
                    }
                }).fail(function () {
                    alert('error!');
                });

            });
        }
    })
}