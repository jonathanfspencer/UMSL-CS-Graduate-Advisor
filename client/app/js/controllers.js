(function() {
  'use strict';

  angular.module('advisor.controllers', [])
    .controller('MasterCtrl', ['$scope', '$location', '$route', 'classService', function($scope, $location, $route, classSvc) {
      $scope.clearData = function() {
        classSvc.clear();
        if($location.path() == '/') {
          $route.reload();
        } else {
          $location.path('/');
        }
      };
      $scope.$on('course_changed', function(e, courses) {
        classSvc.save(courses);
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
    .controller('ScheduleCtrl', ['$scope', 'classService', 'completion', function($scope, classSvc, completionSvc) {
      // TODO Don't hardcode these
      $scope.years = [ '2014', '2015', '2016', '2017' ];
      $scope.courses = [];
      classSvc.courses().then(function(courses) {
        $scope.courses = courses;
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
