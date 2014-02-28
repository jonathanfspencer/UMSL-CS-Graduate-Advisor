(function() {
	'use strict';

	angular.module('advisor.controllers', []).
		controller('MainCtrl', ['$scope', function($scope) {
			$scope.classes = [
				{name: 'CS 1435', status: 'N'},
				{name: 'CS 1234', status: 'N'},
				{name: 'CS 2342', status: 'N'},
				{name: 'CS 2342', status: 'N'}
			];
		}]);
}());
