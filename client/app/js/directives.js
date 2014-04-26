(function() {
	'use strict';

	angular.module('advisor.directives', [])
    .directive('advSticky', ['$window', function($window) {
      return {
        restrict: 'A',
        link: function(scope, element) {
          $window.addEventListener('scroll', function(event) {
            if($window.pageYOffset >= 61) {
              element.addClass('sticky');
            } else {
              element.removeClass('sticky');
            }
          });
        }
      };
    }])
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
                  T: 'W',
                  W: 'N',
                  S: 'S'
                }[course.status];

                $scope.$emit('course_changed', $scope.courses);
			  };
			},
			templateUrl: 'partials/classlist.html'
		};
	}])
  .directive('advProgress', ['classService', '$q', 'completion', function(classSvc, $q, completionSvc) {
    
    return {
      restrict: 'E',
      templateUrl: 'partials/progress.html',
      replace: true,
      link: function(scope, element, attrs) {

        var refresh = function(courses) {
          
          var completion = completionSvc(courses, scope.required);

          scope.completedPct = Math.min(100, (completion.completed / scope.required.minTotalHours) * 100);
          scope.completed = completion.completed;
          scope.scheduledPct = Math.min(100 - scope.completedPct, (completion.scheduled / scope.required.minTotalHours) * 100);
          scope.scheduled = completion.scheduled;
          scope.requiredPct = Math.max(0, 100 - scope.completedPct - scope.scheduledPct);
        };
        var promises = { courses: classSvc.courses(), req: classSvc.requirements() };
        $q.all(promises).then(function(vals) {
          scope.required = vals.req;
          refresh(vals.courses);
        });
        classSvc.onChange(refresh);
      }
    };
  }])
  .directive('advModal', [function() {
    return {
      restrict: 'E',
      scope: {
        show: '='
      },
      replace: true,
      transclude: true,
      link: function(scope, element, attrs) {

      },
      templateUrl: 'partials/modal.html'
    };
  }]);
}());
