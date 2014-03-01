angular.module('ChronoLogger', []).config(function($interpolateProvider){
        $interpolateProvider.startSymbol('{[{').endSymbol('}]}');
    }
);

function DashboardCtrl($scope, $http) {
	$scope.test = "hello world";

	/*
	$http.get("/api/getAllSamples")
		.success(function(data, status, headers, config) {
			$scope.sampleData = data.sample;
		})
		.error(function(data, status, headers, config) {
			alert("Error getting data. Check console for details.");
			console.log(status);
		});

*/

}