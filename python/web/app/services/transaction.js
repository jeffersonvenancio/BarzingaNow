app.service('TransactionService', ['$http', function($http) {
    return {
        add: function(products, successCallback) {
            console.info(JSON.stringify(products));
            $http.post('/api/transaction/', { products: JSON.stringify(products) }).then(successCallback);
        },
        extract: function(successCallback) {
            $http.get('/api/transaction/extract').then(successCallback);
        }
    };
}]);
