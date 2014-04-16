(function() {
	'use strict';

	angular.module('advisor', [
	  'ngRoute',
	  'advisor.controllers',
	  'advisor.directives',
      'advisor.services'
	])
    .constant('serviceUrl', 'http://' + location.hostname + ':8081/')
	.config(['$routeProvider', function($routeProvider) {
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
