app.service('UserService', ['$http', function($http) {
    return {
        getLogged: function(successCallback, errorCallback) {
            $http.get('http://localhost:8080/api/user/logged').then(successCallback, errorCallback);
        }
    };
}]);