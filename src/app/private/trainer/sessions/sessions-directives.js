angular.module('private.trainer.sessions.directives', [
])
.directive('trainerSessionsIndex', function(SessionsModel){
  return {
    templateUrl: 'app/private/trainer/sessions/sessions-directives.tmpl/sessions-index.tmpl.html',
    controller : function(){
        var ctrl = this;
        ctrl.sessions = [];
        SessionsModel.getManagedSessions().then(function(sessions){
            ctrl.sessions = sessions;
        });
        ctrl.updateSessions = function(){
            SessionsModel.getManagedSessions().then(function(sessions){
                ctrl.sessions = sessions;
            });
        }
    }
  };
})
.directive('trainerSessionsAddForm', function(ScenariosModel, SessionsModel) {
  return {
    templateUrl: 'app/private/trainer/sessions/sessions-directives.tmpl/sessions-add-form.tmpl.html',
    scope: false, 
    require: "^trainerSessionsIndex",
    link : function(scope, element, attrs, parentCtrl){
        ScenariosModel.getScenarios().then(function(scenarios){
            scope.scenarios = scenarios;
        });
        scope.newSession = {
            name : "",
            scenarioId : 0 
        };
        scope.addSession = function(){
            if(scope.newSession.scenarioId != 0){
                SessionsModel.createManagedSession(scope.newSession.name, scope.newSession.scenarioId).then(function(data){
                    scope.newSession = {
                        name : "",
                        scenarioId : 0 
                    };
                    parentCtrl.updateSessions();
                });   
            }else{
                console.log("Todo - Send error callback - Choose scenarios");
            }         
        };
    }
  };
})
.directive('trainerSessionsList', function() {
  return {
    templateUrl: 'app/private/trainer/sessions/sessions-directives.tmpl/sessions-list.tmpl.html',
    scope: false,
    require: "^trainerSessionsIndex",
    link : function(scope, element, attrs, parentCtrl){
        scope.$watch(function(){
            return parentCtrl.sessions
        }, function(newSessions, oldSessions){
            scope.sessions = newSessions;
        });
    }
  };
})
.directive('trainerSession', function() {
    return {
        templateUrl: 'app/private/trainer/sessions/sessions-directives.tmpl/session-card.tmpl.html',
        restrict: 'A',
        scope: {
           session: '='
        },
        link : function(scope, element, attrs){
            console.log(scope);
        }
    }
});