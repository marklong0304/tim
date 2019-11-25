$(function () {
    $('#searchStr').keypress(function (e) {
        if (e.which == 13) {
            $('#searchForm').submit();
            return false;
        }
    });
    $('#searchBtn').click(function () {
        $('#searchForm').submit();
    });
});