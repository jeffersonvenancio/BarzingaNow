app.service('ReportService', ['$http', function($http) {
    return {
        doReport: function(successCallback) {
        	var start = '2017-01-01';
        	var end = '2017-03-31';

        	$http.get('/api/transaction/transactions_all/'+start+'/'+end).then(function(transactions_all) {
        		$http.get('/api/credit/credits_all/'+start+'/'+end).then(function(credits_all){
        			console.info(transactions_all, credits_all);

        			successCallback(transactions_all, credits_all);
        		});
        		}
        	);
        }
    };
}]);