app.controller('TransactionCtrl', ['$rootScope', '$scope', '$timeout', 'TransactionService', function($rootScope, $scope, $timeout, service) {

    $scope.listExtract = function() {
        service.extract(function(response) {
            $scope.extracts = response.data;
        });
    };

    $scope.listExtract();
}]);
