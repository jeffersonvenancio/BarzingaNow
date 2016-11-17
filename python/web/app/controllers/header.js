app.controller('HeaderCtrl', ['$rootScope', 'UserService', function($rootScope, userService) {
    $rootScope.user = {};
    $rootScope.user.photo_url = '#';

    $rootScope.refreshUser = function() {
        userService.getLogged(function(user) {
            $rootScope.user = user.data;

            jQuery('.avatar img').attr('src', $rootScope.user.photo_url);
        });
    };

    $rootScope.toggleMenu = function () {
        jQuery('nav').toggle();
    };

    $rootScope.refreshUser();
}]);
