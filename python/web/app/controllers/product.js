app.controller('ProductCtrl', ['$rootScope', '$scope', '$timeout', 'ProductService', 'TransactionService',
        function($rootScope, $scope, $timeout, productService, transactionService, recommenderService) {
    $scope.selectedProducts = [];

    $scope.cartTotal = 0;

    var refreshCart = function() {
        if ($scope.selectedProducts.length) {
            $scope.cartTotal = $scope.selectedProducts.map(function(e) {
                return e.price * e.quantity
            }).reduce(function(prev, curr) {
                return prev + curr;
            });
        } else {
            $scope.cartTotal = 0;
        }
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
            $rootScope.showAlertMessage('Compra efetuada com sucesso!', 'success');
            $timeout($scope.listProducts, 1000);
            $timeout($rootScope.refreshUser, 1000);
            $scope.cleaningCart();
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
