(function() {
  'use strict';

  angular.module('advisor.controllers', [])
	.controller('MainCtrl', ['$scope', 'classService', function($scope, classService) {
      classService.descriptions().success(function(data) {
        $scope.classes = angular.forEach(data.course, function(c) {
		  c.status = 'N';
	    });
	  });

      $scope.saveClasses = function() {
        classService.save($scope.classes.filter(function(c) {
          return c.status === 'N';
        }));
      };
	}])
    .filter('available', function() {
      return function(courses, session) {
        var out = [];
        angular.forEach(courses, function(course) {
          angular.forEach(course.rotation_term, function(term) {
            if(term.term === session && term.time_code !== '') {
              out.push(course);
            }
          });
        });

        return out;
      };
    })
    .filter('needed', ['classService', function(classService) {
      // TODO make this more efficient / less messy
      return function(courses) {
        var savedClasses = classService.retrieve();
        return courses.filter(function(c) {
          var keeper = false;
          savedClasses.forEach(function(sc) {
            if(c.subject == sc.subject && c.course_number == sc.course_number) {
              if(sc.status === 'N') {
                keeper = true;
                c.status = 'N';
              }
            }
          });
          return keeper;
        });
      };
    }])
    .filter('shortenIntro', function() {
      return function(courseName) {
        return courseName.replace('Introduction', 'Intro');
      };
    })
    .controller('ScheduleCtrl', ['$scope', 'classService', function($scope, classService) {
      classService.rotations().success(function(data) {
        $scope.years = data.rotation_year;
      });

    }]);
}());
