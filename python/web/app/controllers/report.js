app.controller('ReportCtrl', ['$rootScope', '$scope', '$timeout', 'ReportService', function($rootScope, $scope, $timeout, service) {

    $scope.report = function() {
    	console.info('Chamando para fazer report');
        service.doReport($scope.startDate, $scope.endDate, function(tall, call) {
        	console.info('Chamando para fazer report');
            $scope.transactions = tall.data;
            $scope.credits = call.data;

            $window.location.reload();
            $rootScope.showAlertMessage('Filtrado', 'success');
        });
    };
}]);
