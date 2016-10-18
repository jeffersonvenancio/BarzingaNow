app.controller('ProductCtrl', ['$scope', 'ProductService', function($scope, service) {
    service.getAll(function(response) {
        $scope.products = response.data;
    });
}]);