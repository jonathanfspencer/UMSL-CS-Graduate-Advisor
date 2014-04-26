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
    .factory('completion', [function() {
        return function(courses, required) {
          return courses.reduce(
            function(counters, currVal, index, arr) {
              var credits = 0;
              
              // Some classes have a variable number of credit hours
              // TODO Default to the minimum number for now. Prompt user
              // for this later.
              if(currVal.credits.length > 1) {
                credits = 3
              } else {
                credits = parseInt(currVal.credits);
              }

              switch(currVal.status) {
              case 'T':
                if(parseInt(currVal.number) < 5000) {
                  if(counters.credits4000Level < parseInt(required.max4000Hours)) {
                    counters.completed += credits;
                  }
                  counters.credits4000Level += credits;
                } else if(parseInt(currVal.number) < 6000) {
                  counters.completed += credits;
                  counters.credits5000Level += credits;
                } else {
                  counters.completed += credits;
                  counters.credits6000Level += credits;
                }
                break;
              case 'N':
                if(required.coreCourses.indexOf(currVal.number) >= 0) {
                  counters.coreRemaining.push(currVal.number);
                }
                break;
              case 'S':
                if(parseInt(currVal.number) < 5000) {
                  if(counters.credits4000Level < parseInt(required.max4000Hours)) {
                    counters.scheduled  += credits;
                  }
                  counters.credits4000Level += credits;
                } else if(parseInt(currVal.number) < 6000) {
                  counters.scheduled  += credits;
                  counters.credits5000Level += credits;
                } else {
                  counters.scheduled  += credits;
                  counters.credits6000Level += credits;
                }
                break;
              }

              return counters;
            },
            { completed: 0, 
              scheduled: 0, 
              credits4000Level: 0, 
              credits5000Level: 0, 
              credits6000Level: 0, 
              coreRemaining: []
            });
        }
    }])
    .factory('classService', ['$http', '$q', 'serviceUrl', 'storage', function($http, $q, serviceUrl, storage) {

      var coursePromise
      ,   listeners = [];

      function url(suffix) {
        return serviceUrl + suffix;
      }

      return {
        save: function(courses) {
          if(!courses) return;
          coursePromise = $q.when(courses);
          storage.save('courses', courses);
          listeners.forEach(function(cb) { cb(courses); });
          return courses;
        },
        clear: function() {
          coursePromise = undefined;
          storage.clear();
        },
        onChange: function(cb) {
          listeners.push(cb);
        },
        courses: function() {
           if(storage.get('courses')) {
            return $q.when(storage.get('courses'));
          } else {
            var that = this;
            coursePromise = coursePromise || $http.get(url('/courses')).then(function(resp) {
              var courses = angular.forEach(resp.data, function(c) {
                c.status = 'N';
              });
              return that.save(courses);
            });
            
            return coursePromise;
          }

        },
        requirements: function() {
          return $http.get(url('requirements')).then(
            function(resp) {
              return resp.data;
            });
        },
        validate: function(prefs) {
          return $http.post(url('preferences/validate'), prefs).then(function(resp) {
            return resp.data;
          });
        },
        autoSchedule: function(preferences) {
          var that = this;
          return $http.post(url('preferences/apply'), preferences).then(function(resp) {
           return that.save(resp.data.courses);
         });
        }
      };
    }]);
})();
