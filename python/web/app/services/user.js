app.service('UserService', ['$http', function($http) {
    return {
        getLogged: function(successCallback) {
            $http.get('/api/user/logged').then(successCallback);
        }
    };
}]);