(function() {
	'use strict';

	angular.module('advisor', [
	  'ngRoute',
	  'advisor.controllers',
	  'advisor.directives',
      'advisor.services'
	])
    .constant('serviceUrl', 'http://' + location.hostname + ':8081/')
	.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
      $locationProvider.html5Mode(true);
	  $routeProvider.when('/', {
		templateUrl: 'partials/main.html',
		controller: 'MainCtrl'
	  })
      .when('/schedule', {
        templateUrl: 'partials/schedule.html',
        controller: 'ScheduleCtrl'
      });
	}]);
})();
