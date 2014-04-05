(function() {
	'use strict';

	angular.module('advisor.directives', [])
	.directive('advClasslist', ['classService', function(classSvc) {
		return {
			restrict: 'E',
			scope: {
				courses: '=courses'
			},
			link: function($scope) {
			  $scope.toggle = function(clazz) {
				if(clazz.status === 'N') {
				  clazz.status = 'T'
				} else if(clazz.status === 'T') {
				  clazz.status = 'W'
				} else if(clazz.status === 'W') {
				  clazz.status = 'N'
				}
			  };
			},
			templateUrl: 'partials/classlist.html'
		};
	}]);
}());
