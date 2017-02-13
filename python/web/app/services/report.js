app.service('ReportService', ['$http', function($http) {
    return {
        doReport: function(start, end, successCallback) {
            var urlFilter = '';

            console.info(start, end);

            if (start != 'undefined' && end != 'undefined' && start != '' && end != '' && start != null && end != null){
                urlFilter = '/'+start+'/'+end
            }

        	$http.get('/api/transaction/transactions_all'+urlFilter).then(function(transactions_all) {
        		$http.get('/api/credit/credits_all'+urlFilter).then(function(credits_all){
        			successCallback(transactions_all, credits_all);
        		});
        		}
        	);
        }
    };
}]);