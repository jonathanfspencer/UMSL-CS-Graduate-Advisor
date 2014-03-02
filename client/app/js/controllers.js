(function() {
	'use strict';

	angular.module('advisor.controllers', []).
		controller('MainCtrl', ['$scope', '$http',  function($scope, $http) {
			$http.get('http://gorinsky.net:8080/CSGraduateAdvisorWS/descriptions')
			  .success(function(data) {
				  $scope.classes = data.course.filter(function(c) {
					  c.status = 'N';
					  return c.course_number > 4000;
				  });
			  });
		}]);
}());
