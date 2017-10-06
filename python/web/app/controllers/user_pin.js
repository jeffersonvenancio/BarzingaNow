app.controller('UserPinCtrl', ['$rootScope', '$scope', '$timeout', 'UserService', function($rootScope, $scope, $timeout, service) {

    $scope.new = function() {
        service.putPin($scope.user, function() {
            $rootScope.showAlertMessage('Pin cadastrado com sucesso!', 'success');
        });
    };
}]);
