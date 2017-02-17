app.controller('ReportCtrl', ['$rootScope', '$scope', '$timeout', 'ReportService', function($rootScope, $scope, $timeout, service) {

    $scope.report = function() {
    	service.doReport($scope.startDate, $scope.endDate, function(tall, call) {
            $scope.transactions = tall.data;
            $scope.credits = call.data;

            $window.location.reload();
            $rootScope.showAlertMessage('Filtrado', 'success');
        });
    };
}]);
