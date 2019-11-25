function createTooltip(dataTableId) {
    $(dataTableId+' tbody tr td').hover(function () {
        //Add title attr if text doesn't fit to the cell
        if (this.offsetWidth < this.scrollWidth) {
            $(this).attr('title', $(this).text());
        }
        //On hover add custom tooltip
        if ($(this).attr('title')) {
            var title = $(this).attr('title');
            $(this).data('tipText', title).removeAttr('title');
            $('<p class="tooltipP"></p>')
                .text(title)
                .appendTo('body')
                .fadeIn('slow');
        }
    }, function () {
        // Hover out code
        $(this).attr('title', $(this).data('tipText'));
        $('.tooltipP').remove();
    }).mousemove(function (e) {
        var mousex = e.pageX + 20; //Get X coordinates
        var mousey = e.pageY + 10; //Get Y coordinates
        $('.tooltipP')
            .css({top: mousey, left: mousex})
    });
}