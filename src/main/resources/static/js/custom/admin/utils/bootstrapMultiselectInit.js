
function bootstrapMultiselectInit() {
    $('#vendors,#policies').multiselect({
        enableFiltering: true,
        buttonWidth: 550,
        maxHeight: 372,
        enableCaseInsensitiveFiltering: true,
        includeSelectAllOption: true,
        filterStyle: 'beginWith',
        includeResetOption: true,
        resetText: "Reset"
    });
}