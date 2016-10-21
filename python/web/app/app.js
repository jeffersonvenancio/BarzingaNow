app = angular.module('app', ['ngRoute']);

app.config(function($routeProvider, $locationProvider, $httpProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'web/app/views/home.html',
            controller: 'HomeCtrl'
        })
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