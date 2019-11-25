function removeDuplicated(arr, key = 'id') {
    var map = new Map();
    arr.map(function (el) {
        if (!map.has(el[key])) {
            map.set(el[key], el);
        }
    });
    return [...map.values()];
}

// var randomForTest = function () { //mock exp.commissions API
//     return Math.floor(Math.random() * (+250 - +10) + +10)
// };