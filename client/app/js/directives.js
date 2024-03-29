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
  .directive('advNotify', ['classService', 'completion', 'userService', '$location', '$q', function(classSvc, completionSvc, userSvc, $location, $q) {
    return {
      restrict: 'E',
      templateUrl: 'partials/notify.html',
      replace: true,
      link: function(scope, element, attrs) {

        var notifyElement = angular.element(element.children()[1]);

        // Hacky way to force a notifications refresh.
        scope.$on('$routeChangeSuccess', function() {
          classSvc.courses().then(function(courses) {
            classSvc.save(courses);
          });
        });

        scope.$watch('active', function(active) {
          if(active) {
            notifyElement.addClass('active')
          } else {
            notifyElement.removeClass('active');
          }

        });

        classSvc.onChange(function(courses) {

          var completion;
          
          classSvc.requirements().then(function(reqs) {
            completion = completionSvc(courses, reqs);
            return $q.all([ reqs, classSvc.validate({
                numberOfHoursCompleted: completion.completed,
                numberOfHoursScheduled: completion.scheduled,
                numberOfHoursRemaining: reqs.minTotalHours - (completion.completed + completion.scheduled),
                numberOf6000HoursScheduled: completion.credits6000Level || 0,
                numberOf5000HoursScheduled: completion.credits5000Level || 0,
                numberOf4000HoursScheduled: completion.credits4000Level || 0,
                courses: courses,
                internationalStudent: userSvc.getUser().intl,
                restricted: userSvc.getUser().restricted
              }) ]);

          }).then(function(results) {

            var requirements = results[0]
            ,   validation = results[1];

            scope.notifications = validation.notifications;
            
            if($location.path().indexOf('schedule') >= 0) {
              scope.active = true;
            } else {
              scope.notifications = [];
            }

            if(completion.credits4000Level > requirements.max4000Hours) {
              scope.notifications.push("A maxiumum of 12 hours of 4000 level classes count towards your degree requirements");
              scope.active = true;
            }
            
            notifyElement.parent().next().css('margin-bottom', scope.notifications.length * 26 + 'px');
          });
        });
      }
    };
  }])
  .directive('advCourseInfo', [function() {
    return {
      restrict: 'E',
      templateUrl: 'partials/classinfo.html',
      replace: true,
      scope: {
        infoCourse: '=course'
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
          scope.remaining6000 = Math.max(0,scope.required.min6000Hours - completion.credits6000Level);
        };

        classSvc.onChange(function(courses) {
          classSvc.requirements().then(function(required) {
            scope.required = required;
            refresh(courses);
          });
        });
      }
    };
  }])
  .directive('advModal', ['$document', function($document) {
    return {
      restrict: 'E',
      scope: {
        show: '='
      },
      replace: true,
      transclude: true,
      link: function(scope, element) {
        
        // When the modal is showing, bind a keyup event listener
        // to close the modal if ESC or q is pressed.
        scope.$watch('show', function(newVal, oldVal, scope) {
          if(newVal) {
            $document.on('keyup', function(event) {
              // event.keyCode == ESC || q
              if(event.keyCode == 27 || event.keyCode == 81) {
                scope.show = false;
                scope.$apply();
              }
            });
          } else {
            $document.off('keydown');
          }
        });
      },
      templateUrl: 'partials/modal.html'
    };
  }]);
}());
