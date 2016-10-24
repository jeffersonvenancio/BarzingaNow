app.service('TransactionService', ['$http', function($http) {
    return {
        add: function(products, successCallback) {
            $http.post('/api/transaction/', { products: JSON.stringify(products) }).then(successCallback);
        }
    };
}]);