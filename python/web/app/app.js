app = angular.module('app', ['ngRoute']);

app.config(function($routeProvider, $locationProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'web/app/views/home.html',
            controller: 'HomeCtrl'
        })
        .when('/product', {
            templateUrl: 'web/app/views/product.html',
            controller: 'ProductCtrl'
        })

        .otherwise({ redirectTo: '/' });
});
