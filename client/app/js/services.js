(function() {
  'use strict';

  angular.module('advisor.services', [])
    .factory('classService', ['$http', 'serviceUrl', function($http, serviceUrl) {

      var savedClasses;

      return {
        save: function(classes) {
          savedClasses = classes;
        },
        retrieve: function() {
          return savedClasses;
        },
        descriptions: function() {
          return $http.get(serviceUrl + '/descriptions');
        },
        schedule: function() {
          return $http.get(serviceUrl + '/schedule');
        },
        rotations: function() {

          return $http.get(serviceUrl + '/rotations').success(function(data) {
            //TODO Make this less ugly
            // angular.forEach(data.rotation_year, function(year) {
            //   year.spring = [];
            //   year.fall = [];

            //   angular.forEach(year.course, function(course) {
            //     angular.forEach(course.rotation_term, function(term) {
            //       if(term.term === 'Spring' && term.time_code !== '') {
            //         year.spring.push(course);
            //       } else if(term.term === 'Fall' && term.time_code !== '') {
            //         year.fall.push(course);
            //       }
            //     });
            //   });
            // });
          });
        }
      };
    }]);
})();
