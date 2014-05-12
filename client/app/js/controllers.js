(function() {
  'use strict';

  angular.module('advisor.controllers', [])
    .controller('MasterCtrl', ['$scope', 'classService', 'userService', function($scope, classSvc, userSvc) {

      $scope.$on('course_changed', function(e, courses) {
        classSvc.save(courses);
      });

      $scope.$watch('user', function(newValue, oldValue) {
        if(newValue) {
          userSvc.setUser(newValue);
          
          // Trigger validation refresh, as though a course has
          // changed. There's probably a better way to do this.
          classSvc.courses().then(function(courses) {
            classSvc.save(courses);
          });
        }
      }, true);

    }])
	.controller('MainCtrl', ['$scope', 'classService', function($scope, classSvc) {

      classSvc.courses().then(
        function(courses) {
          $scope.courses = courses;
	    });

	}])

    .filter('available', function() {
      return function(courses, obj) {
        return courses.filter(function(course) {
          if(course.offerings) {
            return course.offerings.some(function(offering) {
              return offering.session == obj.session
                && offering.year == obj.year
                && (offering.timeCodes == undefined || offering.timeCodes.length > 0);
            });
          } else {
            return false;
          }
        });
      }
    })
    .filter('restricted', ['userService', function(userSvc) {
      return function(courses) {

        if(!courses) return;

        var restricted = userSvc.getUser().restricted;

        // If in restricted mode, show all courses. Including required
        // undergrad courses. Otherwise, filter out all undergrad courses.
        if(restricted) {
          return courses;
        } else {
          return courses.filter(function(course) {
            return !(course.restrictedCourse);
          });
        }
      };
    }])
    .filter('shortenIntro', function() {
      return function(courseName) {
        return courseName
          .replace('Introduction', 'Intro')
          .replace('Advanced', 'Adv.');
      };
    })
    .controller('ScheduleCtrl', ['$scope', 'classService', 'completion', 'userService',function($scope, classSvc, completionSvc, userSvc) {

      classSvc.courseYears().then(function(years) {
        $scope.years = years.sort();
      });

      $scope.courses = [];
      $scope.prefs = { maxClasses: 12};
      classSvc.courses().then(function(courses) {
        $scope.courses = courses;
      });

      // Prepopulates minimum hours to 9 if the student is
      // international.
      $scope.$watch('startAuto', function(startAuto) {
        if(startAuto) {
          if(userSvc.getUser().intl) {
            $scope.prefs.minClasses = 9;
          }
        }
      });

      $scope.auto = function(prefs) {
        classSvc.requirements().then(
          function(reqs) {

            var completion = completionSvc($scope.courses, reqs);
            return classSvc.autoSchedule({
              summerSchedulable: prefs.scheduleSummer,
              internationalStudent: userSvc.getUser().intl,
              restricted: userSvc.getUser().restricted,
              maxClassesPerSemester: prefs.maxClasses,
              minClassesPerSemester: prefs.minClasses,
              canTakeDayClasses: false,
              maxSemestersToComplete: prefs.maxSemesters,
              numberOfHoursCompleted: completion.completed,
              numberOfHoursScheduled: completion.scheduled,
              numberOfHoursRemaining: reqs.minTotalHours - (completion.completed + completion.scheduled),
              numberOf6000HoursScheduled: completion.credits6000Level || 0,
              numberOf5000HoursScheduled: completion.credits5000Level || 0,
              numberOf4000HoursScheduled: completion.credits4000Level || 0,
              courses: $scope.courses
            });
          }).then(
            function(courses) {
              $scope.courses = courses;
              $scope.startAuto = false;
              //TODO Save the newly scheduled courses somewhere.
            });
        return false;
      };
      $scope.schedule = function(course, year, session) {
        if(course.status == 'N') {
          course.status = 'S';
          course.scheduledOffering = {
            year: year,
            session: session
          };
        } else if(course.scheduledOffering.year != year 
                  || course.scheduledOffering.session != session) {
          course.scheduledOffering = {
            year: year,
            session: session
          };
        } else {
          course.status = 'N';
          delete course.scheduledOffering;
        }
        $scope.$emit('course_changed', $scope.courses);
      };

    }])
    .controller('SummaryCtrl', ['$scope', 'classService', 'completion', 'userService', function($scope, classSvc, completionSvc, userSvc) {

      classSvc.courses().then(function(courses) {

        $scope.completed = courses.filter(function(course) {
          return course.status == 'T';
        });

        $scope.waived = courses.filter(function(course) {
          return course.status == 'W';
        });

        courses.filter(function(course) {
          if(course.restrictedCourse && !(userSvc.getUser().restricted)) {
            return false;
          } else {
            return course.scheduledOffering 
          }
        }).forEach(function(course) {

          var schedule = $scope.schedule = $scope.schedule || {};
          var offering = course.scheduledOffering;
          var year = schedule[offering.year] = schedule[offering.year] || {};
          var session = year[offering.session] = year[offering.session] || [];
          session.push(course);
        });

        classSvc.requirements().then(function(reqs) {
          var completion = completionSvc(courses, reqs);
          $scope.totalHours = completion.completed + completion.scheduled;
          return classSvc.validate({
            numberOfHoursCompleted: completion.completed,
            numberOfHoursScheduled: completion.scheduled,
            numberOfHoursRemaining: reqs.minTotalHours - (completion.completed + completion.scheduled),
            numberOf6000HoursScheduled: completion.credits6000Level || 0,
            numberOf5000HoursScheduled: completion.credits5000Level || 0,
            numberOf4000HoursScheduled: completion.credits4000Level || 0,
            courses: courses,
            internationalStudent: userSvc.getUser().intl,
            restricted: userSvc.getUser().restricted
          });
        }).then(function(validation) {
          $scope.notifications = validation.notifications;
        });
      });

    }]);
}());
