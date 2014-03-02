angular.module('ChronoLogger', []).config(function($interpolateProvider){
        $interpolateProvider.startSymbol('{[{').endSymbol('}]}');
    }
).directive('timeLine', function() {
	// this function renders the entire Google Chart containter. Trigger it using attribute: 'time-line="<data array>"'
	return function(scope, element, attrs, MonitoringService) {
		// Log element
		var chart = new google.visualization.Timeline(element[0]);
		scope.$watch(attrs.timeLine, function(data) {
			//alert('New Array: '+value);
			var dataTable = new google.visualization.DataTable();

			for (var i = 0; i < data.column.length; i++) {
				dataTable.addColumn(data.column[i]);
			}
			dataTable.addRows(data.value);

			chart.draw(dataTable);
		});
	}
});

function DashboardCtrl($scope, $http, $location) {
	$scope.test = "hello world";
	$scope.mode = "main" // main for main page, users for user dashboard, member for member details

	$scope.dashboard = {};	// variables used in dashboard
	$scope.member = {};		// variables used in member
	$scope.locations = {};		// variables used in locations

	$scope.dashboard.scheduleData = {
		column: [{ type: 'string', id: 'Member' },
				  { type: 'string', id: 'Location' },
				  { type: 'date', id: 'Start' },
				  { type: 'date', id: 'End' }],
		value: [[ 'Tim Pei',          'Conference Room', new Date(0,0,0,9,0,0), new Date(0,0,0,11,0,0)],
				  [ 'Tim Pei',          'Cafeteria', new Date(0,0,0,11,45,0), new Date(0,0,0,12,50,0)],
				  [ 'Tim Pei',          'Conference Room', new Date(0,0,0,13,0,0), new Date(0,0,0,18,0,0)],
				  [ 'Brett Sun',     	'Conference Room', new Date(0,0,0,9,0,0), new Date(0,0,0,11,0,0)],
				  [ 'Brett Sun',        'Cafeteria', new Date(0,0,0,11,45,0), new Date(0,0,0,12,50,0)],
				  [ 'Brett Sun',        'Conference Room', new Date(0,0,0,13,0,0), new Date(0,0,0,18,0,0)]],
	};

	$scope.dashboard.members = [{id: 1, name: 'Tim Pei'}, {id: 2, name: 'Brett Sun'}];

	$scope.member.member = {
		id: 1,
		name: 'Tim Pei',
		events: [{
			timeEntered: 1393654219,
			timeLeft: 1393704219,
			location: "Conference Room", 
		},{
			timeEntered: 1393709219,
			timeLeft: 1393719219,
			location: "Conference Room", 
		},],
	}

	$scope.member.mode = "day"; // day, month

	$scope.member.tableData = $scope.member.member.events;

	$scope.viewMember = function(id) {
		$scope.request('/get_for_user/' + id, 'GET').then(function(result) {
			$scope.member.member = result.data;
			$scope.member.scheduleData = {
				column: [{ type: 'string', id: 'Date' },
						  { type: 'string', id: 'Location' },
						  { type: 'date', id: 'Start' },
						  { type: 'date', id: 'End' }],
				value: [],
			};

			for (var i = 0; i < result.data.visits.length; i++) {
				var start = new Date(result.data.visits[i].time_entered * 1000);
				var end = new Date(result.data.visits[i].time_left * 1000);
				$scope.member.scheduleData.value.push([
					moment(start).format('dddd, MMMM Do YYYY'), 
					result.data.visits[i].location, 
					new Date(0, 0, 0, start.getHours(), start.getMinutes()),
					new Date(0, 0, 0, end.getHours(), end.getMinutes()),
				]);

			}
			console.log($scope.member.scheduleData);
			
			$scope.mode = 'member';
		});
	}

	$scope.request = function(url, type, data) {
		console.log("Sending " + type + " request to " + url + " (data: " + data + ")");
		return $http({method: type, url: url, data: data})
			.success(function(data, status, headers, config) {
				console.log("Respnse: ", data);
				return data;
			})
			.error(function(data, status, headers, config) {
				alert("Error getting data. Check console for details.");
				console.log(status);
			});
	}

	$scope.toggleView = function(newView) {
		if (newView === 'location') {
			$scope.request('/locations', 'GET').then(function(result) {
				$scope.locations = result.data;
				$scope.mode = newView;
			});
		} else {
			$scope.mode = newView;
		}
	}

}