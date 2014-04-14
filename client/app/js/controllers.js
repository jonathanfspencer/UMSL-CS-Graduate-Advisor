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
      return function(courses, session) {
        return courses.filter(function(course) {
          if(course.offerings) {
            return course.offerings.some(function(offering) {
              return offering.session == session.term 
                && offering.year == session.year
                && offering.timeCodes.length > 0;
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
      $scope.schedule = function(course, year, term) {
        if(course.status == 'N') {
          course.status = 'S';
          course.scheduled = {
            year: year,
            term: term
          };
        } else if(course.scheduled.year != year || course.scheduled.term != term) {
          course.scheduled = {
            year: year,
            term: term
          };
        } else {
          course.status = 'N';
          delete course.scheduled;
        }
        $scope.$emit('course_changed', $scope.courses);
      };

    }]);
}());
