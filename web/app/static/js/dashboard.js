angular.module('ChronoLogger', []).config(function($interpolateProvider){
        $interpolateProvider.startSymbol('{[{').endSymbol('}]}');
    }
).directive('timeLine', function() {
	// this function renders the entire Google Chart containter. Trigger it using attribute: 'time-line="<data array>"'
	return function(scope, element, attrs, MonitoringService) {
		// Log element
		var chart = new google.visualization.Timeline(element[0]);
		scope.$watch(attrs.timeLine, function(value) {
			//alert('New Array: '+value);
			var dataTable = new google.visualization.DataTable();

			dataTable.addColumn({ type: 'string', id: 'Member' });
			dataTable.addColumn({ type: 'string', id: 'Location' });
			dataTable.addColumn({ type: 'date', id: 'Start' });
			dataTable.addColumn({ type: 'date', id: 'End' });
			dataTable.addRows(value);

			chart.draw(dataTable);
		});
	}
});

function DashboardCtrl($scope, $http) {
	$scope.test = "hello world";
	$scope.mode = "main" // main for main page, users for user dashboard, member for member details

	$scope.scheduleData = [
			  [ 'Tim Pei',          'Conference Room', new Date(0,0,0,9,0,0), new Date(0,0,0,11,0,0)],
			  [ 'Tim Pei',          'Cafeteria', new Date(0,0,0,11,45,0), new Date(0,0,0,12,50,0)],
			  [ 'Tim Pei',          'Conference Room', new Date(0,0,0,13,0,0), new Date(0,0,0,18,0,0)],
			  [ 'Brett Sun',     	'Conference Room', new Date(0,0,0,9,0,0), new Date(0,0,0,11,0,0)],
			  [ 'Brett Sun',        'Cafeteria', new Date(0,0,0,11,45,0), new Date(0,0,0,12,50,0)],
			  [ 'Brett Sun',        'Conference Room', new Date(0,0,0,13,0,0), new Date(0,0,0,18,0,0)]];

	$scope.members = ['Tim Pei', 'Brett Sun'];

	$scope.toggleView = function(newView) {
		$scope.mode = newView;
	}

	$scope.viewMember = function(id) {
		$scope.mode = 'member';
	}

	$scope.request = function(url, type, data) {
		console.log("Sending " + type + " request to " + url + " (data: " + data + ")");
		$http({method: type, url: url, data: data})
			.success(function(data, status, headers, config) {
				console.log("Respnse: " + data);
				return data;
			})
			.error(function(data, status, headers, config) {
				alert("Error getting data. Check console for details.");
				console.log(status);
			});
	}
}