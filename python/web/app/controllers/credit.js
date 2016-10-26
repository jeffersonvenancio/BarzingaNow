app.controller('CreditCtrl', ['$rootScope', '$scope', '$timeout', 'CreditService', function($rootScope, $scope, $timeout, service) {
    $scope.credit = {};

    $scope.new = function() {
        service.add($scope.credit, function() {
            $rootScope.showAlertMessage('Valor creditado com sucesso!', 'success');
            $scope.clear();
        });
    };

    $scope.clear = function(response) {
        $scope.credit = {};
    };

}]);
