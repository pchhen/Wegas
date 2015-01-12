/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
/**
 * @fileoverview
 * @author Maxence Laurent <maxence.laurent> <gmail.com>
 */
/*global YUI*/
YUI.add("wegas-pmg-abstractpert", function(Y) {
    "use strict";

    var Wegas = Y.Wegas, AbstractPert;

    /**
     *  @class abstract plugin for pert-based plugin
     *  @name Y.Plugin.AbstractPert
     *  @extends Y.Plugin.Base
     *  @constructor
     */
    AbstractPert = Y.Base.create("wegas-pmg-abstractpert", Y.Plugin.Base, [Wegas.Plugin, Wegas.Editable], {
        /** @lends Y.Plugin.AbstractPert */
        initializer: function() {
            //this.taskTable;
            Y.log("initializer", "info", "Wegas.AbstractPert");
        },
        _plannedPeriods: function(taskInstance) {
            return Y.Array.unique(taskInstance.get("plannification"));
        },
        timeSolde: function(taskDesc) {
            var taskInst = taskDesc.getInstance(),
                properties = taskInst.get("properties"), timeSolde,
                plannedPeriods = this._plannedPeriods(taskInst);

            if (plannedPeriods.length > 0) {
                timeSolde = (1 - parseInt(properties.completeness, 10) / 100) * plannedPeriods.length;
            } else {
                timeSolde = (1 - parseInt(properties.completeness, 10) / 100) * taskInst.get("duration");
            }
            return timeSolde;
        },
        startPlannif: function(taskDesc) {
            var taskInst = taskDesc.getInstance(), planning = this._plannedPeriods(taskInst), min;
            min = Math.min.apply(Math, planning);
            if (min !== Infinity) {
                return min;
            } else {
                return 0;
            }
        },
        /**
         * fill each task table entry with:
         *   - planned : periods numbers the task is planned on   (e.g. [3, 5, 6]
         *   - beginAt : "real" time the work on task will start (e.g 3.25)
         *   - endAt : "real" time the work on task will start (e.g 6.18)
         *
         * @param {type} taskTable
         * @param {type} currentPeriod
         * @returns {undefined}
         */
        computePert: function(taskTable, currentPeriod, currentStage) {
            var taskId, taskDesc, initialPlanning,
                predecessors, i, minBeginAt, delta,
                allPredDefine, predecessorId, stillMissing,
                deltaMissing, queue = [],
                lastPlanned, max,
                taskInstance, stillPlanned;

            if (currentStage < 3) {
                // do not compute pert before stage3 but return the planning planned by players
                for (taskId in taskTable) {
                    taskDesc = taskTable[taskId];
                    initialPlanning = this._plannedPeriods(taskDesc.getInstance()).sort(Y.Array.numericSort);
                    taskDesc.planned = initialPlanning;
                    taskDesc.beginAt = 0;
                    taskDesc.endAt = 0;

                    if (initialPlanning.length > 0) {
                        taskDesc.beginAt = initialPlanning[0];
                        taskDesc.endAt = initialPlanning[initialPlanning.length - 1];
                    }

                    Y.log("TASK PREVISION (" + taskDesc.get("label") + ")");
                    Y.log(" -beginAt: " + taskDesc.beginAt);
                    Y.log(" -endAt: " + taskDesc.endAt);
                    Y.log(" -planned: " + taskDesc.planned);
                }
            } else {
                queue = [];

                for (taskId in taskTable) {
                    queue.push(taskId);
                }
                while (taskId = queue.shift()) {
                    taskDesc = taskTable[taskId];

                    minBeginAt = currentPeriod;
                    allPredDefine = true;
                    predecessors = taskDesc.get("predecessors");

                    // Check predecessors
                    for (i = 0; i < predecessors.length; i += 1) {
                        predecessorId = predecessors[i].get("id");
                        if (taskTable[predecessorId]) {
                            if (taskTable[predecessorId].endAt) {
                                if (minBeginAt < taskTable[predecessorId].endAt) {
                                    minBeginAt = taskTable[predecessorId].endAt;
                                }
                            } else {
                                // At least one precedecessor has not been processed
                                allPredDefine = false;
                                break;
                            }
                        }
                    }

                    if (allPredDefine) {
                        // all require data are available, let's compute pert for the task
                        taskInstance = taskDesc.getInstance();
                        stillPlanned = this._plannedPeriods(taskInstance).filter(function(n) {
                            return n >= minBeginAt;
                        }, this).sort(Y.Array.numericSort);

                        delta = minBeginAt - parseInt(minBeginAt, 10);
                        stillMissing = this.timeSolde(taskDesc);


                        // postpone task that could start in the second part of period
                        if (minBeginAt - parseInt(minBeginAt, 10) > 0.50) {
                            minBeginAt = parseInt(minBeginAt, 10) + 1;
                        } else {
                            minBeginAt = parseInt(minBeginAt, 10);
                            stillMissing += delta;
                        }
                        if (stillPlanned.length > 0 && stillPlanned[0] > minBeginAt) {
                            minBeginAt = stillPlanned[0];
                            stillMissing -= delta;
                        }
                        taskDesc.beginAt = minBeginAt;

                        if (stillMissing === 0) {
                            taskDesc.endAt = minBeginAt;
                            taskDesc.planned = [];
                        } else if (stillPlanned.length >= stillMissing) {
                            // enough or too many planned period
                            deltaMissing = stillMissing - parseInt(stillMissing, 10);
                            if (deltaMissing === 0) {
                                taskDesc.planned = stillPlanned.slice(0, parseInt(stillMissing, 10));
                                taskDesc.endAt = taskDesc.planned[taskDesc.planned.length - 1] + 1;
                            } else {
                                taskDesc.planned = stillPlanned.slice(0, Math.ceil(stillMissing));
                                taskDesc.endAt = taskDesc.planned[taskDesc.planned.length - 1] || taskDesc.beginAt;
                                taskDesc.endAt += deltaMissing;
                            }
                        } else {
                            // not enough planned period
                            taskDesc.planned = stillPlanned.slice();
                            if (stillPlanned.length === 0) {
                                lastPlanned = minBeginAt - 1;
                                // nothing planned -> stack
                                taskDesc.endAt = minBeginAt + stillMissing;
                            } else {
                                lastPlanned = stillPlanned[stillPlanned.length - 1];
                                taskDesc.endAt = lastPlanned + stillMissing - stillPlanned.length + 1;
                            }
                            max = Math.ceil(stillMissing) - stillPlanned.length;
                            for (i = 0; i < max; i += 1) {
                                taskDesc.planned.push(lastPlanned + i + 1);
                            }
                        }

                        Y.log("TASK PREVISION (" + taskDesc.get("label") + ")");
                        Y.log(" -beginAt: " + taskDesc.beginAt);
                        Y.log(" -endAt: " + taskDesc.endAt);
                        Y.log(" -planned: " + taskDesc.planned);

                    } else {
                        queue.push(taskId);
                    }
                }
            }
        }
    }, {
        NS: "abstractpert",
        NAME: "abstractpert"
    });
    Y.Plugin.AbstractPert = AbstractPert;
});
