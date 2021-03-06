angular.module('wegas.service.pusher', [])
    .service('WegasPusher', function($http, $q) {
        var service = this, pusher, channels = [];
        service.start = function(){
            var deferred = $q.defer();
            $http.get(ServiceURL + "rest/Pusher/ApplicationKey").success(function(key) {
                if(key){                    
                    pusher = new Pusher(key, {
                        authEndpoint: ServiceURL + "rest/Pusher/auth"
                    });
                    channels["presence-global"] = pusher.subscribe('presence-global');
                }
                deferred.resolve();
            });
            return deferred.promise;
        };
    });
