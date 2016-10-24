app.controller('HomeCtrl', ['$rootScope', '$scope', '$timeout', '$location', function($rootScope, $scope, $timeout, $location) {
    $location.path('/product');

    $rootScope.alert = {
        message: '',
        type: '',
        visible: false
    };

    $scope.$watch('alert.visible', function(newValue) {
        if (newValue) {
            $timeout(function() {
                $rootScope.alert.visible = false;
            }, 5000);
        }
    });

    $rootScope.showAlertMessage = function(message, type) {
        $rootScope.alert.message = message;
        $rootScope.alert.type = type;
        $rootScope.alert.visible = true;
    }
}]);