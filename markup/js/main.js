$(document).ready(function () {
    $(window).scroll(function () {
        if ($(this).scrollTop() > 0) {
            $('.goTopLink').fadeIn();
        } else {
            $('.goTopLink').fadeOut();
        }
    });
    $('.goTopLink').click(function () {
        $('body,html').animate({
            scrollTop: 0
        }, 400);
        return false;
    });
});
