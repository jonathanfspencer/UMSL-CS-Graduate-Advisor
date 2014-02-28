(function() {
	'use strict';

	angular.module('advisor', [
		'ngRoute',
		'advisor.controllers',
		'advisor.directives'
	])
	.config(['$routeProvider', function($routeProvider) {
		$routeProvider.when('/', {
			templateUrl: 'partials/main.html',
			controller: 'MainCtrl'
		}).otherwise({ redirectTo: '/' });
	}]);
}());
