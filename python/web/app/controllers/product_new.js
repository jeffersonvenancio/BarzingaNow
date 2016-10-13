app.controller('ProductNewCtrl', ['$scope', 'ProductService', function($scope, service) {
    $scope.product = {};

    $scope.new = function() {
        service.add($scope.product, $scope.clear);
    };

    $scope.clear = function() {
        $scope.product = {};
    };
}]);