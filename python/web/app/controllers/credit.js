app.controller('CreditCtrl', ['$scope', 'CreditService', function($scope, service) {
    $scope.credit = {};

    $scope.new = function() {
        service.add($scope.credit, $scope.clear);
    };

    $scope.clear = function() {
        $scope.credit = {};
    };

}]);