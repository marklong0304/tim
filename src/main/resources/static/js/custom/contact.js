$(document).ready(function () {
  $("#provider").on('changed.bs.select', function () {
    $('.policy').css('display', 'block');
    $('#policy').val('Select').change().parent().addClass('open');
    filterPolicies();
    $('.contactInfo').find(`.contactL`).css('display', 'none');
  });

  function filterPolicies() {
    $('#policy option').each(function () {
      $(this).val() === $("#provider option:selected").val()
        ? $(this).parent().siblings('.open').find(`li[data-original-index=${parseInt($(this).attr('data-index')) + 2}]`).css('display', 'block')
        : $(this).parent().siblings('.open').find(`li[data-original-index=${parseInt($(this).attr('data-index')) + 2}]`).css('display', 'none');
    });
  }

  $('#policy').on('change.bs.select', function () {
    filterContacts();
  });

  function filterContacts() {
    $('.contactInfo div').each(function () {
      if ($("#provider option:selected").text() === $(this).attr('value')) {
        $('.contactInfo').find(`.contactL[value="${($('#provider option:selected').text())}"]`).css('display', 'block');

      } else {
        $('.contactInfo').find(`.contactL[value!="${($('#provider option:selected').text())}"]`).css('display', 'none');
      }
    });
  }
});