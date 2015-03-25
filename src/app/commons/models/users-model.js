'use strict';
angular.module('wegas.models.users', [])
.service('UsersModel', function ($http, $q) {
    var model = this,
    users;

    model.getUsers = function() {
        return "Here is all users";
    };

    model.autocomplete = function(pattern) {
        var deferred = $q.defer();

        var url = "rest/User/AutoComplete/" + pattern;

        $http
        .get(ServiceURL + url)
        .success(function(data){
            deferred.resolve(data);
        }).error(function(data) {
            deferred.resolve([]);
        });
        return deferred.promise;
    }
})
;
