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
      });

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
    .filter('shortenIntro', function() {
      return function(courseName) {
        return courseName
          .replace('Introduction', 'Intro')
          .replace('Advanced', 'Adv.');
      };
    })
    .controller('ScheduleCtrl', ['$scope', 'classService', 'completion', 'userService',function($scope, classSvc, completionSvc, userSvc) {
      // TODO Don't hardcode these
      $scope.years = [ '2014', '2015', '2016', '2017' ];
      $scope.courses = [];
      $scope.prefs = {};
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

    }]);
}());
