var loginUrl = context + "api/login.json";
var adminMenuUrl = context + "users/getAdminMenu";

$(document).ready(function () {
    $('.loginForm').on('submit', function (e) {
        var submBtn = $('#loginSubmit');
        if (!submBtn.hasClass('disabled-pointer')) {
            submBtn.addClass('disabled-pointer');
            getLogin(this);
        }
        e.preventDefault();
    });
});

var loginErrorMsg = "Invalid email or password.";
function getLogin(form) {
    var msg = $(form).serialize();
    clearErrors();
    $.post(loginUrl, msg, function (data) {
        if (data.loggedIn == true) {
            rebuildLoginPanel(true, data.username);
            if (data.role.indexOf('ROLE_ADMIN') != -1 || data.role.indexOf('ROLE_CONTENT_MANAGER') != -1 || data.role.indexOf('ROLE_AFFILIATE') != -1
                || data.role.indexOf('ROLE_CUSTOMER_SERVICE') != -1 || data.role.indexOf('ROLE_ACCOUNTANT') != -1) {
                getAdminMenu();
            }
            updateSaveQuoteLink();
            window.location.reload();
        } else {
            printError(null, loginErrorMsg);
            $("#loginSubmit").removeClass('disabled-pointer');
        }
    }).fail(function () {
        printError(null, defErrMsg);
        $("#loginSubmit").removeClass("disabled-pointer");
    });
}

function doLogout() {
    rebuildLoginPanel(false);
    $("#admin-menu").remove();
    $("#loginSubmit").removeClass('disabled-pointer');
}

function rebuildLoginPanel(isLogedin, username) {
    if (!isLogedin) {
        $(".login.log").hide();
        $(".login").not(".log").show();
    } else {
        $(".login").not(".log").hide();
        $(".login.log").show();
        $('.username').text(username);
        getCurrentQuote();
    }
}

function getAdminMenu() {
    $.post(adminMenuUrl, null, function (data) {
        if ($(data).is("li")) {
            $(".nav.navbar-nav > li:last-child").before(data);
            initMenu();
        }
    }).fail(function () {
        //printError(null, defErrMsg);
    });
}

function updateSaveQuoteLink(){
    $("#saveQuote,#save").removeClass("disabled-link");
}


