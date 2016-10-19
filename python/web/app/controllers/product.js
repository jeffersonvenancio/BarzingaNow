app.controller('ProductCtrl', ['$scope', 'ProductService', function($scope, service) {
    $scope.addProductToCart = function() {

    };

    service.getAll(function(response) {
        $scope.products = response.data;
    });
}]);