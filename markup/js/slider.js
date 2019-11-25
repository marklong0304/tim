(function ($) {
    $('.nextForm').click(function () {
        $('.firstSlide').find('.firstFormPart').fadeOut(0).parent().find('.secondFormPart').fadeIn(1000);
    });
    $('.prevForm').click(function () {
        $('.firstSlide').find('.secondFormPart').fadeOut(0).parent().find('.firstFormPart').fadeIn(1000);
    });
    var windowWidth = $(document).width();
    $('#customRadio4').click(function () {
        if (windowWidth > 1600) {
            $('.thirdSlide h5').animate({
                'margin-top': 5,
                'margin-bottom': 0
            }, {
                duration: 500,
                complete: function () {
                    $('.hiddenContent.dateChoice').fadeIn(500);
                }
            });
            $('.thirdSlide h2').animate({
                'margin-bottom': 0,
            }, {
                duration: 500
            });
        }
        if (windowWidth <= 1600) {
            $('.thirdSlide h5').animate({
                'margin-top': 0,
                'margin-bottom': 0
            }, {
                duration: 500,
                complete: function () {
                    $('.hiddenContent.dateChoice').fadeIn(500);
                }
            });
            $('.thirdSlide h2').animate({
                'margin-bottom': 0
            }, {
                duration: 500
            });
        }
    })
    $('#customRadio3').click(function () {
        $('.hiddenContent.dateChoice').fadeOut(500);
        $('#animationLoader .spinner').animate({opacity: "1"}, 300);
        //$('#animationLoader').fadeOut(500);
        if (windowWidth > 1600) {
            $('.thirdSlide h5').animate({
                'margin-top': 40,
                'margin-bottom': 30
            }, {
                duration: 500
            });
            $('.thirdSlide h2').animate({
                'margin-bottom': 40
            }, {
                duration: 500
            });
        }
        if (windowWidth <= 1600) {
            $('.thirdSlide h5').animate({
                'margin-top': 20,
                'margin-bottom': 30
            }, {
                duration: 500
            });
            $('.thirdSlide h2').animate({
                'margin-bottom': 15
            }, {
                duration: 500
            });
        }

    })
})(jQuery);

