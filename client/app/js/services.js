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
          return localStorage[key] && JSON.parse(localStorage[key]);
        },
        clear: function() {
          localStorage.clear();
        }
      };
    }])
    .factory('classService', ['$http', '$q', 'serviceUrl', 'storage', function($http, $q, serviceUrl, storage) {

      var savedClasses
      ,   listeners = [];

      function url(suffix) {
        return serviceUrl + suffix;
      }

      return {
        save: function(courses) {
          if(!courses) return;
          savedClasses = courses;
          storage.save('courses', courses);
          listeners.forEach(function(cb) { cb(courses); });
          return courses;
        },
        retrieve: function() {
          return savedClasses;
        },
        clear: function() {
          savedClasses = undefined;
          storage.clear();
        },
        onChange: function(cb) {
          listeners.push(cb);
        },
        courses: function() {
          //TODO Handle multiple calls before original returns.
          if(savedClasses) {
            return $q.when(savedClasses);
          } else if(storage.get('courses')) {
            return $q.when(storage.get('courses'));
          } else {
            var that = this;
            return $http.get(url('/courses')).then(function(resp) {
              var courses = angular.forEach(resp.data, function(c) {
                c.status = 'N';
              });
              return that.save(courses);
            });
          }

        },
        requirements: function() {
          return $http.get(url('requirements')).then(
            function(resp) {
              return resp.data;
            });
        },
        descriptions: function() {
          return $http.get(url('/descriptions'));
        },
        schedule: function() {
          return $http.get(url('/schedule'));
        }
      };
    }]);
})();
