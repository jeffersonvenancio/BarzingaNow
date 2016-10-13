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