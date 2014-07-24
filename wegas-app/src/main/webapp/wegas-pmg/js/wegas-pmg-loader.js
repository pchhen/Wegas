/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
/**
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
"use strict";

YUI.addGroup("wegas-pmg", {
    base: './wegas-pmg/',
    root: '/wegas-pmg/',
    modules: {
        "wegas-scheduledatatable": {
            ws_provides: 'ScheduleDT'
        },
        'wegas-pmgwidget-css': {
            path: 'css/wegas-pmgwidget-min.css',
            type: 'css'
        },
        'wegas-pmg-breadcrumb': {
            ws_provides: "PmgBreadcrumb"
        },
        'wegas-pmg-datatable': {
            requires: ['wegas-datatable', 'datatable', 'datatable-mutable', "template"], //Using simple datatable
            ws_provides: "PmgDatatable"
        },
        'wegas-pmg-slidepanel': {
            requires: ['anim', 'wegas-pmgwidget-css', "wegas-pmg-datatable", "wegas-pmg-reservation",
                "wegas-pmg-occupationcolor", "wegas-pmg-activitycolor", "wegas-pmg-assignment",
                "wegas-scheduledatatable", "wegas-text"],
            ws_provides: ["PmgSlidePanel", "PmgResourcesPanels"]
        },
        "wegas-pmg-reservation": {
            path: 'js/plugin/wegas-pmg-reservation-min.js',
            ws_provides: 'Reservation'
        },
        "wegas-pmg-occupationcolor": {
            path: 'js/plugin/wegas-pmg-occupationcolor-min.js',
            requires: 'wegas-pmgwidget-css',
            ws_provides: 'OccupationColor'
        },
        "wegas-pmg-linefilter": {
            path: 'js/plugin/wegas-pmg-linefilter-min.js',
            requires: 'wegas-pmgwidget-css',
            ws_provides: ['PMGLineFilter', 'PMGLineCompleteness']
        },
        "wegas-pmg-activitycolor": {
            path: 'js/plugin/wegas-pmg-activitycolor-min.js',
            requires: 'wegas-pmgwidget-css',
            ws_provides: 'ActivityColor'
        },
        "wegas-pmg-assignment": {
            path: 'js/plugin/wegas-pmg-assignment-min.js',
            requires: ['sortable', 'wegas-pmgwidget-css', 'wegas-widgetmenu', 'event-hover'],
            ws_provides: 'Assignment'
        },
        "wegas-pmg-planification": {
            path: 'js/plugin/wegas-pmg-planification-min.js',
            ws_provides: 'Planification'
        },
        "wegas-pmg-plannificationcolor": {
            path: 'js/plugin/wegas-pmg-plannificationcolor-min.js',
            requires: 'wegas-pmgwidget-css',
            ws_provides: 'Plannificationcolor'
        },
        "wegas-pmg-plannificationactivitycolor": {
            path: 'js/plugin/wegas-pmg-plannificationactivitycolor-min.js',
            requires: 'wegas-pmgwidget-css',
            ws_provides: 'PlannificationActivityColor'
        },
        "wegas-pmg-plannificationprogresscolor": {
            path: 'js/plugin/wegas-pmg-plannificationprogresscolor-min.js',
            requires: 'wegas-pmgwidget-css',
            ws_provides: 'PlannificationProgressColor'
        },
        "wegas-pmg-bac": {
            path: 'js/plugin/wegas-pmg-bac-min.js',
            ws_provides: 'Bac'
        },
        "wegas-pmg-tablepopup": {
            path: 'js/plugin/wegas-pmg-tablepopup-min.js',
            requires: 'wegas-widgetmenu',
            ws_provides: 'Tablepopup'
        }
    }
});