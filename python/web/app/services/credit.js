app.service('CreditService', ['$http', function($http) {
    return {
        add: function(credit, successCallback, errorCallback) {
            $http.post('/api/credit/add', credit).then(successCallback, errorCallback);
        }
    };
}]);