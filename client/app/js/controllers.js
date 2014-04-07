(function() {
  'use strict';

  angular.module('advisor.controllers', [])
    .controller('MasterCtrl', ['$scope', '$location', 'classService', function($scope, $location, classSvc) {
      $scope.clearData = function() {
        classSvc.clear();
        $location.path('/');
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
      
      $scope.$on('$routeChangeStart', function() {
        classSvc.save($scope.courses);
        console.log('saved');
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
  //TODO this filter can probably be replaced with an ng-show
    .filter('needed', ['classService', function(classSvc) {
      return function(courses) {
        return courses.filter(function(course) {
          return course.status === 'N';
        });
      };
    }])
    .filter('shortenIntro', function() {
      return function(courseName) {
        return courseName.replace('Introduction', 'Intro');
      };
    })
    .controller('ScheduleCtrl', ['$scope', 'classService', function($scope, classSvc) {
      // TODO Don't hardcode these
      $scope.years = [ '2014', '2015', '2016', '2017' ];
      $scope.courses = [];
      classSvc.courses().then(function(courses) {
        $scope.courses = courses;
      });
    }]);
}());
