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
        .when('/self/add', {
            templateUrl: 'web/app/views/self.html',
            controller: 'SelfCtrl'
        })
        .otherwise({ redirectTo: '/' });

    $httpProvider.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
    $httpProvider.defaults.transformRequest.unshift(function (data, headersGetter) {
        var key, result = [];

        if (typeof data === "string")
            return data;

        for (key in data) {
            if (data.hasOwnProperty(key))
                result.push(encodeURIComponent(key) + "=" + encodeURIComponent(data[key]));
        }

        return result.join("&");
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