app.service('UserService', ['$http', function($http) {
    return {
        getLogged: function(successCallback, errorCallback) {
            $http.get('/api/user/logged').then(successCallback, errorCallback);
        }
    };
}]);