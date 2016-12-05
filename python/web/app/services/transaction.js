app.service('TransactionService', ['$http', function($http) {
    return {
        add: function(products, successCallback) {
            $http.post('/api/transaction/', { products: JSON.stringify(products) }).then(successCallback);
        },
        extract: function(successCallback) {
            $http.get('/api/transaction/extract').then(successCallback);
        }

    };
}]);