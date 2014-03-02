angular.module('ChronoLogger', ['ui.bootstrap']).config(function($interpolateProvider){
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

			var options = {
				height: data.value.length*60,
			};

			chart.draw(dataTable, options);
		});
	}
});

function DashboardCtrl($scope, $http, $location) {
	$scope.test = "hello world";
	$scope.mode = "" // main for main page, users for user dashboard, member for member details

	$scope.dashboard = {};	// variables used in dashboard
	$scope.member = {};		// variables used in member
	$scope.locations = {};		// variables used in locations

	$scope.dashboard.previous = {};
	$scope.dashboard.previous.scheduleData = {
		column: [{ type: 'string', id: 'Member' },
				  { type: 'string', id: 'Location' },
				  { type: 'date', id: 'Start' },
				  { type: 'date', id: 'End' }],
		value: [],
	};

	$scope.dashboard.scheduleData = {
		column: [{ type: 'string', id: 'Member' },
				  { type: 'string', id: 'Location' },
				  { type: 'date', id: 'Start' },
				  { type: 'date', id: 'End' }],
		value: [],
	};

	$scope.dashboard.members = [];
	$scope.dashboard.previous.members = [];

	$scope.member.members = {};

	$scope.member.scheduleData = {
		column: [{ type: 'string', id: 'Date' },
				  { type: 'string', id: 'Location' },
				  { type: 'date', id: 'Start' },
				  { type: 'date', id: 'End' }],
		value: [],
	};

	$scope.$on('datepicker-change', function(event, newDate) {
		$scope.request('/get_for_all/' + newDate, 'GET').then(function(result) {
			$scope.formatToChart(result, $scope.dashboard.previous);
			if ($scope.dashboard.previous.members.length != 0) {
				$scope.showPrevious = true;
			} else {
				$scope.showPrevious = false;
			}
		});
	});

	$scope.viewMember = function(id) {
		$scope.request('/get_for_user/' + id, 'GET').then(function(result) {
			$scope.member.member = result.data;
			$scope.member.scheduleData.value = [];

			for (var i = 0; i < result.data.visits.length; i++) {
				var start = new Date(result.data.visits[i].time_entered * 1000);
				if (result.data.visits[i].time_left == "") {
					var end = new Date();
				} else {
					var end = new Date(result.data.visits[i].time_left * 1000);	
				}
				$scope.member.scheduleData.value.push([
					moment(start).format('dddd, MMMM Do YYYY'), 
					result.data.visits[i].location, 
					new Date(0, 0, 0, start.getHours(), start.getMinutes()),
					new Date(0, 0, 0, end.getHours(), end.getMinutes()),
				]);

			}

			$scope.mode = 'member';
		});
	}

	$scope.viewDashboard = function() {
		$scope.request('/get_for_all_today', 'GET').then(function(result) {
			$scope.formatToChart(result, $scope.dashboard);
			console.log($scope.dashboard)
			$scope.mode = "main";
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

	$scope.formatToChart = function(raw, dashboard) {
		dashboard.scheduleData.value = [];
		dashboard.members = [];
		raw = raw.data;
		for (var name = 0; name < raw.data.length; name++) {
			if (raw.data[name].visits.length != 0) {
				dashboard.members.push({id: raw.data[name].memberId, name: raw.data[name].name});
			}
			for (var i = 0; i < raw.data[name].visits.length; i++) {
				var start = new Date(raw.data[name].visits[i].time_entered * 1000);
				if (raw.data[name].visits[i].time_left != "") {
					var end = new Date(raw.data[name].visits[i].time_left * 1000);
				} else {
					var end = new Date();	// if end date is null, then set end date to current time
				}

				dashboard.scheduleData.value.push([
					raw.data[name].name,
					raw.data[name].visits[i].location,
					new Date(0, 0, 0, start.getHours(), start.getMinutes()),
					new Date(0, 0, 0, end.getHours(), end.getMinutes()),
				]);
				console.log(dashboard.scheduleData.value);
			}
		}
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

	$scope.viewDashboard();

}

var DatepickerCtrl = function ($scope) {
	  $scope.yesterday = function() {
	    $scope.dt = new Date();
	    $scope.dt.setDate($scope.dt.getDate() - 1);
	  };
	  $scope.yesterday();

	  $scope.showWeeks = true;
	  $scope.toggleWeeks = function () {
	    $scope.showWeeks = ! $scope.showWeeks;
	  };

	  $scope.clear = function () {
	    $scope.dt = null;
	  };

	  $scope.maxDate = new Date();
	  $scope.maxDate.setDate($scope.maxDate.getDate() - 1);	

	  $scope.open = function($event) {
	    $event.preventDefault();
	    $event.stopPropagation();

	    $scope.opened = true;
	  };

	  $scope.dateOptions = {
	    'year-format': "'yy'",
	    'starting-day': 0
	  };

	  $scope.format = 'dd-MMMM-yyyy';

		$scope.$watch('dt', function() {
			$scope.$emit('datepicker-change',  Math.floor($scope.dt.getTime()/1000));
		});
};
