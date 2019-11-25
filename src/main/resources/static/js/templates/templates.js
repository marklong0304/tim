/**
 * Created by Chernov Artur on 23.08.2016.
 */
Handlebars.getTemplate = function (name) {
    if (Handlebars.templates === undefined || Handlebars.templates[name] === undefined) {
        $.ajax({
            url: context + 'js/templates/' + name + '.hbs',
            success: function (data) {
                if (Handlebars.templates === undefined) {
                    Handlebars.templates = {};
                }
                Handlebars.templates[name] = Handlebars.compile(data);
            },
            async: false
        });
    }
    return Handlebars.templates[name];
};

Handlebars.registerHelper({
    fixed: function (price) {
        return price.toFixed(2);
    },
    context: function () {
        return context;
    },
    ifFourth: function (index, options) {
        if (index == 3) {
            return options.fn(this);
        }
        return options.inverse(this);
    },
    addClass: function (condition, className) {
        if (condition) {
            return className;
        }
        return '';
    },
    selected: function (option, value) {
        if (option === value) {
            return ' selected';
        }
        return ''
    },
    checked: function (condition, value) {
        if (condition === value) {
            return ' checked';
        }
        return ''
    },
    selectedWithPref: function (prefix, condition, value) {
        if (prefix+'-'+condition === value) {
            return ' selected';
        }
        return ''
    },
    printOptionIfNotCond: function (value, caption, excludedCountryCodes, sel) {
        var result = _.includes(excludedCountryCodes, value);
        if (result == false) {
            var selected = (value == sel ? 'selected' : '');
            var str = '<option value=\"' + value + '\" ' + selected + '>' + caption + '</option>';
            return new Handlebars.SafeString(str);
        }
    },
    dateFormat: function (value) {
        if (value == undefined){
            return '';
        }
        return moment(value).format("MM/DD/YYYY")
    },
    printOptionWithSelected: function (target) {
        var result = '';
        for (var i = 1; i <= 9; i++) {
            var select = target === i ? ' selected' : '';
            result = result + '<option' + select + '>' + i + '</option>'
        }
        return new Handlebars.SafeString(result);
    },
    ifCond: function(v1, v2, options) {
        if(v1 === v2) {
            return options.fn(this);
        }
        return options.inverse(this);
    }
});