app.controller('SideMenuCtrl', ['$scope', '$location', 'UserService', function($scope, $location, userService) {
    var $menuArrow = jQuery('nav .nav-arrow');

    var animateMenu = function($elementMenu) {
        $menuArrow.css('display', 'block');

        $menuArrow.animate({
            top: $elementMenu.offsetTop
        }, 300, function() {
            jQuery('nav ul li a.active').removeClass('active');
            jQuery($elementMenu).addClass('active');
        });
    }

    $scope.selectCategory = function(category, $event) {
        animateMenu($event.target);
        $location.path('/product').search({ category: category });
    };

    $scope.openPageProduct = function($event) {
        animateMenu($event.target);
        $location.path('/product/new').search({});
    };

    $scope.openPageCredit = function($event) {
        animateMenu($event.target);
        $location.path('/credit/add').search({});
    };


    $scope.user = {};
    $scope.user.photo_url = '#';
    $scope.user.admin = false;

    $scope.refreshUser = function() {
        userService.getLogged(function(user) {
            $scope.user = user.data;
        });
    };

    $scope.refreshUser();
}]);