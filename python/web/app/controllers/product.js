app.controller('ProductCtrl', ['$scope', 'ProductService', function($scope, service) {
    $scope.products = [];

    service.getAll(function(response) {
        $scope.products = response.data;
    });
}]);