app.controller('ReportCtrl', ['$rootScope', '$scope', '$timeout', 'ReportService', function($rootScope, $scope, $timeout, service) {

    $scope.report = function() {
        service.doReport(function(tall, call) {
        	console.info('Chamando para fazer report');
            $scope.transactions = tall.data;
            $scope.credits = call.data;
        });
    };

    $scope.report();
}]);
