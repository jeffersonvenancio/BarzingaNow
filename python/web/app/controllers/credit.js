app.controller('CreditCtrl', ['$scope', '$timeout', 'CreditService', function($scope, $timeout, service) {
    $scope.credit = {};

    $scope.new = function() {
        service.add($scope.credit, $scope.clear, $scope.fail);
    };

    $scope.clear = function(response) {
        jQuery('.msg span').show().text('Sucesso');
        $timeout(function(){jQuery('.msg span').hide(1000);}, 3000);
        $scope.credit = {};
    };

    $scope.fail = function(response) {
        jQuery('.msg span').show().text(response.data);
        $timeout(function(){jQuery('.msg span').hide(1000);}, 3000);
    };

}]);