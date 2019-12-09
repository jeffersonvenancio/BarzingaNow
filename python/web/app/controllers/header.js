app.controller('HeaderCtrl', ['$rootScope', 'UserService', '$location', function($rootScope, userService, $location) {
    $rootScope.user = {};
    $rootScope.user.photo_url = '#';

    var $menuArrow = jQuery('nav .nav-arrow');

    var animateMenu = function($elementMenu) {
        if (window.matchMedia("(max-device-width: 480px)").matches) {
            jQuery('nav').hide();
        }

        $menuArrow.css('display', 'none');
    }

    $rootScope.goHome = function($event) {
        $location.path('/product').search({});
        animateMenu($event.target);
    };

    $rootScope.refreshUser = function() {
        userService.getLogged(function(user) {
            $rootScope.user = user.data;

            jQuery('.avatar img').attr('src', $rootScope.user.photo_url);
        });
    };

    $rootScope.toggleMenu = function () {
        jQuery('nav').toggle();
    };

    $rootScope.toggleCredit = function () {
        jQuery('.money span').toggle();
    };


    $rootScope.refreshUser();
}]);