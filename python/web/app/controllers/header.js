app.controller('HeaderCtrl', ['$scope', 'UserService', function($scope, userService) {
    $scope.user = {};
    $scope.user.photo_url = '#';

    $scope.refreshUser = function() {
        userService.getLogged(function(user) {
            $scope.user = user.data;

            jQuery('.avatar img').attr('src', $scope.user.photo_url);
        });
    };

    $scope.refreshUser();
}]);