app.controller('ProductCtrl', ['$scope', '$timeout', 'ProductService', 'TransactionService', function($scope, $timeout, productService, transactionService) {
    $scope.selectedProducts = [];
    
    $scope.cartTotal = 0;

    var refreshCart = function() {
        $scope.cartTotal = $scope.selectedProducts.map(function(e) {
            return e.price * e.quantity
        }).reduce(function(prev, curr) {
            return prev + curr;
        });
    };

    $scope.addProductToCart = function(product) {
        var product = {
            id: product.id,
            description: product.description,
            price: product.price,
            quantity: 1
        };

        var index = $scope.selectedProducts.indexOfById(product.id);

        if (index != -1) {
            $scope.selectedProducts[index].quantity++;
        } else {
            $scope.selectedProducts.push(product);
        }

        refreshCart();
    };

    $scope.removeProductFromCart = function(product) {
        var index = $scope.selectedProducts.indexOfById(product.id);

        if ($scope.selectedProducts[index].quantity > 1) {
            $scope.selectedProducts[index].quantity--;
        } else {
            $scope.selectedProducts.removeById(product.id);
        }

        refreshCart();
    };

    $scope.finalize = function() {
        transactionService.add($scope.selectedProducts, function() {
            $timeout($scope.listProducts, 1000);
            $scope.cleaningCart();
        }, function(e) {
            console.log(e);
        });
    };

    $scope.cleaningCart = function() {
        $scope.selectedProducts = [];
    };

    $scope.listProducts = function() {
        productService.getAll(function(response) {
            $scope.products = response.data;
        });
    };

    $scope.listProducts();

}]);