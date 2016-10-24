app.service('CreditService', ['$http', function($http) {
    return {
        add: function(credit, successCallback) {
            $http.post('/api/credit/add', credit).then(successCallback);
        }
    };
}]);