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
  .directive('advNotify', ['classService', 'completion', function(classSvc, completionSvc) {
    return {
      restrict: 'E',
      templateUrl: 'partials/notify.html',
      replace: true,
      link: function(scope, element, attrs) {
        classSvc.onChange(function(courses) {

          classSvc.requirements().then(function(reqs) {
            var completion = completionSvc(courses, reqs);
            return { completion: completion, reqs: reqs };
          }).then(function(result) {
            var completion = result.completion;
            var reqs = result.reqs;
            return classSvc.validate({
              numberOfHoursCompleted: completion.completed,
              numberOfHoursScheduled: completion.scheduled,
              numberOfHoursRemaining: reqs.minTotalHours - (completion.completed + completion.scheduled),
              numberOf6000HoursScheduled: completion.credits6000Level || 0,
              numberOf5000HoursScheduled: completion.credits5000Level || 0,
              numberOf4000HoursScheduled: completion.credits4000Level || 0,
              courses: courses

            });
          }).then(function(result) {
            scope.notifications = result.notifications;
            element.addClass('active');
          });
        });
      }
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
          scope.coreRemaining = completion.coreRemaining;
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
