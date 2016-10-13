app.controller('SideMenuCtrl', ['$scope', '$location', function($scope, $location) {
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
}]);