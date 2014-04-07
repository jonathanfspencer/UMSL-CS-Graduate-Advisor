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
			  $scope.toggle = function(course) {
                course.status = {
                  N: 'T',
                  T: 'N',
                  S: 'S'
                }[course.status];

                $scope.$emit('course_changed', $scope.courses);
			  };
			},
			templateUrl: 'partials/classlist.html'
		};
	}])
  .directive('advProgress', ['classService', '$q', function(classSvc, $q) {
    
    return {
      restrict: 'E',
      templateUrl: 'partials/progress.html',
      replace: true,
      link: function(scope, element, attrs) {

        var refresh = function(courses) {
          var completion = courses.reduce(
            function(counters, currVal, index, arr) {
              var credits = 0;
              if(currVal.credits.length > 1) {
                credits = parseInt(currVal.credits[0]);
              } else {
                credits = parseInt(currVal.credits);
              }
              switch(currVal.status) {
              case 'T':
              case 'W':
                counters.completed += credits;
                break;
              case 'N':
                counters.left += credits;
                break;
              case 'S':
                counters.scheduled += credits;
                break;
              }

              return counters;
            },
            { completed: 0, left: 0, scheduled: 0 });
          scope.completedPct = Math.min(100, (completion.completed / scope.required) * 100);
          scope.completed = completion.completed;
          scope.scheduledPct = Math.min(100 - scope.completedPct, (completion.scheduled / scope.required) * 100);
          scope.scheduled = completion.scheduled;
          scope.requiredPct = Math.max(0, 100 - scope.completedPct - scope.scheduledPct);
        };
        var promises = { courses: classSvc.courses(), req: classSvc.requirements() };
        $q.all(promises).then(function(vals) {
          scope.required = vals.req.minTotalHours;
          refresh(vals.courses);
        });
        classSvc.onChange(refresh);
      }
    };
  }]);
}());
