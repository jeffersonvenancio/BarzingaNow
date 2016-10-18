app.service('ProductService', ['$http', function($http) {
    return {
        getAll: function(successCallback, errorCallback) {
            $http.get('http://localhost:8080/api/product/').then(successCallback, errorCallback);
        },
        add: function(product, successCallback, errorCallback) {
            $http({
                method: 'POST',
                url: 'http://localhost:8080/api/product/',
                headers: {
                    'Content-Type': undefined
                },
                data: product,
                transformRequest: function (data, headersGetter) {
                    var formData = new FormData();
                    angular.forEach(data, function (value, key) {
                        formData.append(key, value);
                    });

                    var headers = headersGetter();
                    delete headers['Content-Type'];

                    return formData;
                }
            })
            .success(successCallback)
            .error(errorCallback);
        }
    };
}]);