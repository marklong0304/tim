$(document).click(function () {
  $('.loginEmail').focusout(function () {
    disableName();
  });
  $('.loginPass').focusout(function () {
    disablePass();
  });
});

function disableName () {
  let emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
    if (($('.loginEmail').val() === '' ) || (!emailRegex.test($('.loginEmail').val()))) {
      $('#loginBtn').addClass('disabled-filter');
    } else {
      $('#loginBtn').removeClass('disabled-filter');
    }
}
function disablePass () {
    if ($('.loginPass').val() === '') {
      $('#loginBtn').addClass('disabled-filter');
    } else {
      $('#loginBtn').removeClass('disabled-filter');
    }
}

