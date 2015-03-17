angular.module('private', [
    'wegas.models.sessions',
    'wegas.service.viewInfos',
    'private.player',
    'private.trainer',
    'private.scenarist'
])
.config(function ($stateProvider) {
    $stateProvider
        .state('wegas.private', {
            url: '',
            abstract:true,
            views: {
                'main@': {
                    controller: 'PrivateCtrl as privateCtrl',
                    templateUrl: 'app/private/private.tmpl.html'
                }
            }
        })
    ;
})
.controller('PrivateCtrl', function PrivateCtrl($state, Auth) {
    var privateCtrl = this;
    Auth.getAuthenticatedUser().then(function(user){
        if(user == null){
            $state.go("wegas.public");
        }
        privateCtrl.user = user;
    }); 
})
.directive('privateSidebar', function($state, ViewInfos, Auth) {
  return {
    templateUrl: 'app/private/private-sidebar.tmpl.html',
    link: function (scope, element, attrs) {
        Auth.getAuthenticatedUser().then(function(user){
            scope.user = user;
        });
        scope.$watch(function(){
            return ViewInfos.name;
        }, function(newVal, oldVal){
            scope.name = newVal;
        });
        scope.logout = function(){
            Auth.logout().then(function(){
                $state.go("wegas.public.login");
            });
        };
    }
  };
});