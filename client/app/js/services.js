(function() {
  'use strict';

  angular.module('advisor.services', [])
    .factory('storage', [function() {

      var storage = {};

      return {
        save: function(key, obj) {
          storage[key] = obj;
          return obj;
        },
        get: function(key) {
          return storage[key];
        },
        clear: function() {
          storage = {};
        }
      };
    }])
    .factory('completion', [function() {
        return function(courses, required) {
          courses.sort(function(a, b) {
            if(a.status === 'T') {
              return -1;
            } else if(a.status === 'W') {
              return -1;
            } else {
              return 1;
            }
          });
          return courses.reduce(
            function(counters, currVal, index, arr) {
              var credits = 0;
              
              // Some classes have a variable number of credit hours
              // Default to 3 if that is the case, per Dr. Janikow.
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
    .factory('userService', [function() {

      var user = {};
      return  {
        setUser: function(newUser) {
          user = newUser;
        },
        getUser: function() {
          return user;
        }
      };
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
        },
        courseYears: function() {
          return this.courses().then(function(courses) {

            var years = [];

            courses.forEach(function(course) {
              course.offerings.forEach(function(offering) {
                if(years.indexOf(offering.year) < 0 && offering.timeCodes && offering.timeCodes.length > 0) {
                  years.push(offering.year);
                }
              });
            });

            return years;
          });
        }
      };
    }]);
})();
