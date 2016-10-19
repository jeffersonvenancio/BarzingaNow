app.controller('ProductCtrl', ['$scope', 'ProductService', function($scope, service) {
    $scope.selectedProducts = [];

    $scope.addProductToCart = function(product) {
        console.log(product.id);
        var product = {
            id: product.id,
            description: product.description,
            quantity: 1
        };

        var index = $scope.selectedProducts.indexOfById(product.id);

        if (index != -1) {
            $scope.selectedProducts[index].quantity++;
        } else {
            $scope.selectedProducts.push(product);
        }
    };

    $scope.removeProductFromCart = function(product) {
        var index = $scope.selectedProducts.indexOfById(product.id);

        if ($scope.selectedProducts[index].quantity > 1) {
            $scope.selectedProducts[index].quantity--;
        } else {
            $scope.selectedProducts.removeById(product.id);
        }
    };

    service.getAll(function(response) {
        $scope.products = response.data;
    });
}]);