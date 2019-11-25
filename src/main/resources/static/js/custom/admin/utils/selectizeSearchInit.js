function selectizeAffiliatesInit() {
    $('#affiliates').selectize({
        valueField: 'affiliateId',
        searchField: ['affiliateEmail', 'affiliateName'],
        searchConjunction: 'or',
        placeholder: 'Affiliate',
        preload: 'focus',
        maxItems: 1,
        options: [],
        create: false,
        onChange: function() {
            if(!isReseted) {
                $("#doFilter").trigger('click');
            }
        },
        render: {
            item: function (item) {
                return '<div>' +
                    (item.affiliateName ? '<span class="selectedZName">' + item.affiliateName + '</span>' : '') +
                    '</div>';
            },
            option: function (item) {
                var label = item.affiliateName || '';
                var caption = item.affiliateEmail;
                return '<div>' +
                    '<span class="selectizeName">' + label + '</span>' +
                    (caption ? '<span class="selectizeEmail">' + caption + '</span>' : '') +
                    '</div>';
            },
        },
        load: function (query, callback) {
            if (!query.length || query.length < 3) return callback();
            $.ajax({
                url: `/api/admin/filters/affiliates?filterString=${query}`,
                type: 'GET',
                error: function (e) {
                    console.log(e);
                },
                success: function (res) {
                    if (res.length !== 0) $('.affiliates .initial').removeClass('initial');
                    callback(res);
                },
            });
        },

    });
}

function selectizeAffiliatesCorrectionInit() {
    $('#affiliates-correction').selectize({
        valueField: 'affiliateId',
        searchField: ['affiliateEmail', 'affiliateName'],
        searchConjunction: 'or',
        preload: 'focus',
        maxItems: 1,
        options: [],
        create: false,
        render: {
            item: function (item) {
                return '<div>' +
                    (item.affiliateName ? '<span class="selectedZName">' + item.affiliateName + '</span>' : '') +
                    '</div>';
            },
            option: function (item) {
                var label = item.affiliateName || '';
                var caption = item.affiliateEmail;
                return '<div>' +
                    '<span class="selectizeName">' + label + '</span>' +
                    (caption ? '<span class="selectizeEmail">' + caption + '</span>' : '') +
                    '</div>';
            },
        },
        load: function (query, callback) {
            if (!query.length || query.length < 3) return callback();
            $.ajax({
                url: `/api/admin/filters/affiliates?filterString=${query}`,
                type: 'GET',
                error: function (e) {
                    console.log(e);
                },
                success: function (res) {
                    if (res.length !== 0) $('.affiliates-correction .initial').removeClass('initial');
                    callback(res);
                },
            });
        },

    });
}

function selectizeCompaniesInit() {
    $('#companies').selectize({
        valueField: 'companyId',
        searchField: 'companyName',
        searchConjunction: 'or',
        placeholder: 'Company',
        preload: 'focus',
        maxItems: 1,
        options: [],
        create: false,
        onChange: function() {
            if(!isReseted) {
                $("#doFilter").trigger('click');
            }
        },
        render: {
            item: function (item) {
                return '<div>' + (item.companyName ? '<span class="selectedZName">' + item.companyName + '</span>' : '') + '</div>';
            },
            option: function (item) {
                var label = item.companyName || '';
                return '<div><span class="selectizeName">' + label + '</span></div>';
            },
        },
        load: function (query, callback) {
            if (!query.length || query.length < 3) return callback();
            $.ajax({
                url: `/api/admin/filters/companies?filterString=${query}`,
                type: 'GET',
                error: function (e) {
                    console.log(e);
                },
                success: function (res) {
                    if (res.length !== 0) $('.companies .initial').removeClass('initial');
                    callback(res);
                },
            });
        }
    });
}

function selectizeUsersInit() {
    var options = [];
    var userId = $('#userId').val();
    if (userId !== ''){
        options = [{userId: userId, userName: $('#fullName').val(), userEmail: $('#email').val()}];
    }
    var $select = $('#users').selectize({
        valueField: 'userId',
        searchField: ['userEmail', 'userName'],
        searchConjunction: 'or',
        placeholder: 'User',
        preload: 'focus',
        maxItems: 1,
        options: options,
        create: false,
        onChange: function() {
            if(!isReseted) {
                $("#doFilter").trigger('click');
            }
        },
        render: {
            item: function (item) {
                return '<div>' +
                    (item.userName ? '<span class="selectedZName">' + item.userName + '</span>' : '') +
                    '</div>';
            },
            option: function (item) {
                var label = item.userName || '';
                var caption = item.userEmail;
                return '<div>' +
                    '<span class="selectizeName">' + label + '</span>' +
                    (caption ? '<span class="selectizeEmail">' + caption + '</span>' : '') +
                    '</div>';
            },
        },
        load: function (query, callback) {
            if (!query.length || query.length < 3) return callback();
            $.ajax({
                url: `/api/admin/filters/users?filterString=${query}`,
                type: 'GET',
                error: function (e) {
                    console.log(e);
                },
                success: function (res) {
                    if (res.length !== 0) $('.users .initial').removeClass('initial');
                    callback(res);
                },
            });
        },
    });
    if (userId !== '' && userId !== undefined) {
        $select[0].selectize.setValue(userId);
        $('.users .initial').removeClass('initial');
    }
}

function selectizeTravellersInit() {
    $('#travellers').selectize({
        valueField: 'value',
        searchField: 'name',
        searchConjunction: 'or',
        placeholder: 'Traveller',
        preload: 'focus',
        maxItems: 1,
        options: [],
        create: false,
        onChange: function() {
            if(!isReseted) {
                $("#doFilter").trigger('click');
            }
        },
        render: {
            item: function (item) {
                return '<div>' +
                    (item.name ? '<span class="selectedTravellerName">' + item.name + '</span>' : '') +
                    '</div>';
            },
            option: function (item) {
                var label = item.name || '';
                return '<div>' +
                    '<span class="travellerName">' + label + '</span>' +
                    '</div>';
            },
        },
        load: function (query, callback) {
            if (!query.length || query.length < 3) return callback();
            $.ajax({
                url: `/api/admin/filters/travelers?filterString=${query}`,
                type: 'GET',
                error: function (e) {
                    console.log(e);
                },
                success: function (res) {
                    if (res.length !== 0) $('.travellers .initial').removeClass('initial');
                    const items = res.map(el => {
                        return {
                            name: el.replace('|', ' '),
                            value: el,
                        };
                    });
                    callback(items);
                },
            });
        },
    });
}