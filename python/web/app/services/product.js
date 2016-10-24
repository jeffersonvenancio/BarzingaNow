app.service('ProductService', ['$http', function($http) {
    return {
        getAll: function(successCallback, errorCallback) {
            var type = location.hash.split('=')[1] || '';
            $http.get('/api/product/category/' + type).then(successCallback);
        },
        add: function(product, successCallback, errorCallback) {
            $http.post('/api/product/', product).then(successCallback);
        }
    };
}]);