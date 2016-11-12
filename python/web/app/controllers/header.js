app.controller('HeaderCtrl', ['$rootScope', 'UserService', '$location', function($rootScope, userService, $location) {
    $rootScope.user = {};
    $rootScope.user.photo_url = '#';

    $rootScope.goHome = function() {
        $location.path('/product').search({});
    };

    $rootScope.refreshUser = function() {
        userService.getLogged(function(user) {
            $rootScope.user = user.data;

            jQuery('.avatar img').attr('src', $rootScope.user.photo_url);
        });
    };

    $rootScope.refreshUser();
}]);
