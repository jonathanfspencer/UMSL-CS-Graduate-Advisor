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
    .controller('ScheduleCtrl', ['$scope', 'classService', function($scope, classSvc) {
      // TODO Don't hardcode these
      $scope.years = [ '2014', '2015', '2016', '2017' ];
      $scope.courses = [];
      classSvc.courses().then(function(courses) {
        $scope.courses = courses;
      });
      $scope.auto = function(prefs) {
        classSvc.autoSchedule({
          maxClassesPerSemester: prefs.maxClasses,
          minClassesPerSemester: prefs.minClasses,
          canTakeDayClasses: false,
          maxSemestersToComplete: prefs.maxSemesters,
          numberOfHoursCompleted: 0,
          numberOfHourseScheduled: 0,
          numberOfHoursRemaining: 0,
          numberOf6000HoursScheduled: 0,
          numberOf5000HoursScheduled: 0,
          numberOf4000HoursScheduled: 0,
          courses: $scope.courses
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
