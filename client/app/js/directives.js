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
              
              // Some classes have a variable number of credit hours
              // TODO Default to the minimum number for now. Prompt user
              // for this later.
              if(currVal.credits.length > 1) {
                credits = parseInt(currVal.credits[0]);
              } else {
                credits = parseInt(currVal.credits);
              }

              switch(currVal.status) {
              case 'T':
                if(parseInt(currVal.number) < 5000) {
                  if(counters.credits4000Level < parseInt(scope.required.max4000Hours)) {
                    counters.completed += credits;
                  }
                  counters.credits4000Level += credits;
                } else {
                    counters.completed += credits;
                }
                break;
              case 'N':
                counters.left += credits;
                break;
              case 'S':
                if(parseInt(currVal.number) < 5000) {
                  if(counters.credits4000Level < parseInt(scope.required.max4000Hours)) {
                    counters.scheduled  += credits;
                  }
                  counters.credits4000Level += credits;
                } else {
                    counters.scheduled  += credits;
                }
                break;
              }

              return counters;
            },
            { completed: 0, left: 0, scheduled: 0, credits4000Level: 0 });

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
