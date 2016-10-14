app.controller('HeaderCtrl', ['$scope', 'UserService', function($scope, userService) {
    $scope.refreshUser = function() {
        userService.getLogged(function(user) {
            console.log(user.data);
            $scope.user = user.data;
        });
    };

    $scope.refreshUser();
}]);