var commentManager = (function () {
    var getAll = function (obj, callback) {
        console.log("get All....");

        $.getJSON('/comment/' + obj, callback);
    };
});