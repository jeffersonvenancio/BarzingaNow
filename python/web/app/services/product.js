app.service('ProductService', ['$http', function($http) {
    return {
        getAll: function(successCallback, errorCallback) {
            $http.get('http://localhost:8080/api/product').then(successCallback, errorCallback);
        }
    };
}]);