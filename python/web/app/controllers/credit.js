app.controller('CreditCtrl', ['$scope', '$timeout', 'CreditService', function($scope, $timeout, service) {
    $scope.credit = {};

    $scope.new = function() {
        service.add($scope.credit, $scope.clear);
    };

    $scope.clear = function(response) {
        $scope.credit = {};
    };

}]);