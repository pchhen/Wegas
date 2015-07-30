/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */

/*global Variable, gameModel, self, Y, PMGSimulation, debug, com, java, Java */
var PMGDashboard = (function() {
    "use strict";
    var Long = Java.type("java.lang.Long");

    function questionAnswered(teamId, currentPhase, currentPeriod) {
        if (Variable.find(gameModel, 'questions').size() < currentPhase) {
            return "0/0";
        }
        var q = Variable.find(gameModel, 'questions').item(currentPhase - 1),
            i = 0, items = new java.util.LinkedList(), questions, item, inst, count = 0, total = 0;
        if (q) {
            for (i = 0; i < q.size(); i += 1) {
                item = q.item(i);
                if (item instanceof com.wegas.mcq.persistence.QuestionDescriptor) {
                    items.add(item);
                } else if (i === currentPeriod - 1 &&
                           item instanceof com.wegas.core.persistence.variable.ListDescriptor) {
                    items.addAll(item.flatten());
                }
            }
        }

        for (i = 0; i < items.length; i += 1) {
            inst = items[i].getScope().getVariableInstances()[teamId];
            if (inst instanceof com.wegas.mcq.persistence.QuestionInstance) {
                count += (inst.getReplies().size() > 0 && inst.getActive()) ? 1 : 0;
                total += inst.getActive() ? 1 : 0;
            }
        }
        return count + "/" + total;
    }

    function getInstances(name) {
        return Variable.find(gameModel, name).getScope().getVariableInstances();
    }

    function getLabel(name) {
        return Variable.find(gameModel, name).getLabel();
    }

    function dashboard() {
        // Find all values
        var teams = self.getGame().getTeams(),
            currentPhase = getInstances("currentPhase"),
            currentPeriod = getInstances("currentPeriod"),
            management = getInstances("managementApproval"),
            user = getInstances("userApproval"),
            quality = getInstances("quality"),
            cost = getInstances("costs"),
            schedule = getInstances("delay"),
            arr = [], teamId, t,
            phaseName = ['Initiation', 'Planning', 'Executing', 'Closing'],
            managementLabel = getLabel("managementApproval"),
            userLabel = getLabel("userApproval"),
            
            // Formatter function
            formatter = function(bloc, value){
                bloc.one(".value")
                    .setStyle("background-color", (value < 75 ? "#ff4a03" : (value > 125 ? "#4caf50" : "#ffa709")))
                    .setStyle("color", "white")
                    .setStyle("font-weight", "bold")
                    .setStyle("border-radius", "2px");
            },
            
            // Columns & data object structure
            monitoring = {
                "columns":[
                    { "label":"Phase", "formatter":null },
                    { "label":"Period", "formatter":null },
                    { "label":"Questions", "formatter":null },
                    { "label":"Quality", "formatter":formatter },
                    { "label":"Costs", "formatter":formatter },
                    { "label":"Schedule", "formatter":formatter },
                    { "label":managementLabel, "formatter":formatter },
                    { "label":userLabel, "formatter":formatter }
                ],
                "data":{}
            };
            
        // Find data by team
        for (t = 0; t < teams.size(); t++) {
            teamId = new Long(teams.get(t).getId());
            currentPeriod = Variable.find(gameModel, 'currentPeriod').item(currentPhase[teamId].getValue() - 1).getScope().getVariableInstances()[teamId].getValue();
            monitoring.data[teamId] = {
                "Phase": phaseName[currentPhase[teamId].getValue() - 1],
                "Period": currentPeriod,
                "Questions": questionAnswered(teamId, currentPhase[teamId].getValue(), currentPeriod),
                "Quality": quality[teamId].getValue(),
                "Costs": cost[teamId].getValue(),
                "Schedule": schedule[teamId].getValue()
            };
            monitoring.data[teamId][managementLabel] = management[teamId].getValue();
            monitoring.data[teamId][userLabel] = user[teamId].getValue();
        }
        
        // Stringify formatter functions
        monitoring.columns.forEach(function(column){
            column.formatter =  column.formatter + "";
        });
        // Return stringified object
        return JSON.stringify(monitoring);
    }
    return {
        dashboard: dashboard
    };
})();
