app.service('UserService', ['$http', function($http) {
    return {
        getLogged: function(successCallback) {
            $http.get('/api/user/logged').then(successCallback);
        },

        putPin: function(user, successCallback) {
            $http.post('/api/user/pin/', user).then(successCallback);
        }
    };
}]);