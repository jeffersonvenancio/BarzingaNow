app = angular.module('app', ['ngRoute']);

app.config(function($routeProvider, $locationProvider, $httpProvider) {
    $routeProvider
        .when('/product', {
            templateUrl: 'web/app/views/product.html',
            controller: 'ProductCtrl'
        })
        .when('/product/new', {
            templateUrl: 'web/app/views/product_new.html',
            controller: 'ProductNewCtrl'
        })
        .when('/credit/add', {
            templateUrl: 'web/app/views/credit_add.html',
            controller: 'CreditCtrl'
        })
        .otherwise({ redirectTo: '/' });

    $httpProvider.interceptors.push(['$rootScope', '$q', function($rootScope, $q) {
        return {
            'responseError': function(response) {
                $rootScope.showAlertMessage(response.data, 'error');
                return $q.reject(response);
            }
        };
    }]);

    $httpProvider.defaults.headers.post["Content-Type"] = undefined;
    $httpProvider.defaults.transformRequest.unshift(function (data, headersGetter) {
        var formData = new FormData();
        angular.forEach(data, function (value, key) {
            formData.append(key, value);
        });

        var headers = headersGetter();
        delete headers['Content-Type'];

        return formData;
    });
});

app.directive('file', function () {
    return {
        scope: {
            file: '='
        },
        link: function (scope, el, attrs) {
            el.bind('change', function (event) {
                var file = event.target.files[0];
                scope.file = file ? file : undefined;
                scope.$apply();
            });
        }
    };
});

// app.directive('message', ['$timeout', function($timeout) {
//     return {
//         restrict: 'E',
//         template: '<div class="alert alert-{{ type }}">{{ message }}</div>',
//         scope: {
//             control: '='
//         },
//         link: function($scope, element, attrs) {
//             $scope.message = 'XABLAU';
//             $scope.isVisible = true;

//             $scope.$watch('isVisible', function(newValue) {
//                 if (newValue) {
//                     element[0].style.display = 'block';
//                 } else {
//                     element[0].style.display = 'none';
//                 }
//             });

//             $scope.internalControl = {}
//             $scope.internalControl.showMessage = function(message) {
//                 $scope.isVisible = true;
//                 $scope.message = message.text;
//                 $scope.type = message.type;

//                 $timeout(function() {
//                     $scope.isVisible = false;
//                 }, 5000);
//             }
//         }
//     };
// }]);