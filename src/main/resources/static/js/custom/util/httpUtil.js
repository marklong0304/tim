var HttpUtils = (function () {

    return {
        addParamToUrl: addParamToUrl,
        addParameterToForm: addParameterToForm,
        addParams: addParams
    };

    function addParamToUrl(base, key, value) {
        var sep = (base.indexOf('?') > -1) ? '&' : '?';
        return base + sep + key + '=' + value;
    }

    function addParams(base, params){
        return base + '&' + $.param(params);
    }

    function addParameterToForm(serializedForm, key, value) {
        return serializedForm + "&" + key + '=' + value;
    }



}());