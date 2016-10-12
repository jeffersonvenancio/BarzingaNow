app.controller('ProductCtrl', ['$scope', 'ProductService', function($scope, service) {
    $scope.products = [
        {
            description: 'Refrigerante Coca-Cola 250ml',
            price: 1.5,
            quantity: 10
        },
        {
            description: 'Refrigerante Coca-Cola 350ml',
            price: 2.5,
            quantity: 10
        },
        {
            description: 'Toddynho',
            price: 1.5,
            quantity: 10
        },
        {
            description: 'Suco LIV 200ml',
            price: 2,
            quantity: 10
        },
        {
            description: 'Cerveja Budweiser 335ml',
            price: 2.5,
            quantity: 10
        },
        {
            description: 'Cerveja Amstel 335ml',
            price: 2.5,
            quantity: 10
        },
    ];

    $scope.selectCategory = function(category, $event) {
        var $menuArrow = jQuery('nav .nav-arrow');

        $menuArrow.animate({
            top: $event.target.offsetTop
        }, 300, function() {
            jQuery('nav ul li a.active').removeClass('active');
            jQuery($event.target).addClass('active');
        });

        return false;
    };
}]);