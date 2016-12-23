var recommenderUrlBase = 'https://recomendador-dot-relacionadosites-qa.appspot.com/api/services'

app.service('RecommenderService', ['$http', function($http) {
    return {
        getRecommendations: function(userId, successCallback) {
            $http.get(recommenderUrlBase + '/recommendations?app_cod=BZG&idx=' + userId).then(successCallback);
        }
    };
}]);

//idx app_cod kind  