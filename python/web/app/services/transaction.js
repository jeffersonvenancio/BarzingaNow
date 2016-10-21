app.service('TransactionService', ['$http', function($http) {
    return {
        add: function(products, successCallback, errorCallback) {
            $http.post('/api/transaction/', { products: JSON.stringify(products) }).then(successCallback, errorCallback);
        }
    };
}]);