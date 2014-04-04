(function() {
  'use strict';

  angular.module('advisor.services', [])
    .factory('storage', [function() {
      return {
        save: function(key, obj) {
          if(typeof key !== 'string') throw new TypeError('key must be a string');
          if(!(obj instanceof Object)) throw new TypeError('obj [' + typeof obj + '] must be an instance of Object');

          localStorage[key] = JSON.stringify(obj);
          return obj;
        },
        get: function(key) {
          if(localStorage[key]) {
            return JSON.parse(localStorage[key]);
          } else {
            return undefined;
          }
        }
      };
    }])
    .factory('classService', ['$http', '$q', 'serviceUrl', 'storage', function($http, $q, serviceUrl, storage) {

      var savedClasses;

      function url(suffix) {
        return serviceUrl + suffix;
      }

      return {
        save: function(classes) {
          savedClasses = classes;
        },
        retrieve: function() {
          return savedClasses;
        },
        courses: function() {
          if(savedClasses) {
            return $q.when(savedClasses);
          } else if(storage.get('courses')) {
            return $q.when(storage.get('courses'));
          } else {
            return $http.get(url('/courses')).success(function(courses) {
              courses = angular.forEach(courses, function(c) {
                c.status = 'N';
              }).reverse();
              return storage.save('courses', courses);
            });
          }

        },
        requirements: function() {
          return $http.get(url('requirements'));
        },
        descriptions: function() {
          return $http.get(url('/descriptions'));
        },
        schedule: function() {
          return $http.get(url('/schedule'));
        },
        rotations: function() {

          return $http.get(url('/rotations')).success(function(data) {
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
