app.controller('CreditCtrl', ['$rootScope','$scope', '$timeout', 'CreditService', function($rootScope, $scope, $timeout, service) {
    $scope.credit = {};

    $scope.new = function() {
        service.add($scope.credit, $scope.clear);
    };

    $scope.clear = function(response) {
        $rootScope.showAlertMessage("Sucesso", 'success');
        $scope.credit = {};
    };

}]);