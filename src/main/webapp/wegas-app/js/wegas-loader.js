/*
 * Wegas
 * http://www.albasim.com/wegas/
 *
 * School of Business and Engineering Vaud, http://www.heig-vd.ch/
 * Media Engineering :: Information Technology Managment :: Comem
 *
 * Copyright (C) 2012
 */

/**
 * Wegas loader, contains module definitions.
 *
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
YUI().use(function (Y) {
    "use strict";

    var CONFIG = {
        groups: {
            'wegas': {
                combine: true,
                base: './',
                root: '/',
                modules: {
                    /** Base **/
                    'wegas-app': {
                        path: 'wegas-app/js/wegas-app-min.js',
                        requires: [
                        'wegas-datasourcerest', 'wegas-scripteval',
                        'wegas-entity', 'wegas-mcq-entities', 'wegas-statemachine-entities',
                        'stylesheet', 'wegas-widget',
                        'wegas-pageloader', 'wegas-button',
                        'wegas-gallery',
                        //'wegas-appcss',                                       // @fixme There is a bug in css include order, this one got hardcoded in the jsp file
                        ]
                    },
                    'wegas-appcss': {
                        path: 'wegas-app/css/wegas-app.css',
                        type: 'css'
                    },
                    'wegas-datasourcerest': {
                        path: 'wegas-app/js/wegas-datasourcerest-min.js',
                        requires: ['plugin', 'json', 'array-extras', 'io-base', "datasource-io", "datasource-jsonschema", "datasource-cache"]
                    },
                    'wegas-scripteval':{
                        path:'wegas-app/js/wegas-scripteval-min.js',
                        requires:['plugin']
                    },

                    /** Persistence **/
                    'wegas-entity': {
                        path: 'wegas-app/js/persistence/wegas-entity-min.js',
                        requires: [ 'base', 'wegas-inputex', 'inputex-jsonschema' ]
                    },
                    'wegas-statemachine-entities': {
                        path: 'wegas-app/js/persistence/wegas-statemachine-entities-min.js',
                        requires: [ 'wegas-entity' ]
                    },
                    'wegas-mcq-entities': {
                        path: 'wegas-app/js/persistence/wegas-mcq-entities-min.js',
                        requires: [ 'wegas-entity' ]
                    },

                    /** Widgets **/
                    'wegas-widget': {
                        path: 'wegas-app/js/widget/wegas-widget-min.js',
                        requires: ['widget', 'widget-parent', 'widget-child']
                    },
                    'wegas-pageloader': {
                        path: 'wegas-app/js/widget/wegas-pageloader-min.js',
                        ix_provides: 'PageLoader'
                    },
                    'wegas-button': {
                        path: 'wegas-app/js/widget/wegas-button-min.js',
                        requires: [ 'wegas-widget', 'wegas-widgetmenu', 'wegas-action', 'wegas-tooltip', 'plugin', 'button' ],
                        ix_provides: 'Button'
                    },
                    'wegas-action': {
                        path: 'wegas-app/js/widget/wegas-action-min.js',
                        requires: [ 'plugin' ]
                    },
                    'wegas-tooltip': {
                        path: 'wegas-app/js/widget/wegas-tooltip-min.js',
                        requires: [ "event-mouseenter", "widget", "widget-position", "widget-stack" ],
                        ix_provides: 'Button'
                    },

                    'wegas-chat': {
                        path: 'wegas-app/js/widget/wegas-chat-min.js',
                        ix_provides: 'Chat'
                    },
                    'wegas-layout': {
                        path: 'wegas-app/js/widget/wegas-layout-min.js',
                        requires: [ 'wegas-widget', 'widget-stdmod', 'event-resize', 'resize'],
                        ix_provides: 'Layout'
                    },
                    'wegas-list': {
                        path: 'wegas-app/js/widget/wegas-list-min.js',
                        requires: ["substitute", "node-focusmanager"],
                        ix_provides: 'List'
                    },
                    'wegas-text': {
                        path: 'wegas-app/js/widget/wegas-text-min.js',
                        ix_provides: "Text"
                    },
                    'wegas-tabview': {
                        path: 'wegas-app/js/widget/wegas-tabview-min.js',
                        requires: ['tabview', 'button'],
                        ix_provides: 'TabView'
                    },
                    'wegas-variabledisplay': {
                        path: 'wegas-app/js/widget/wegas-variabledisplay-min.js',
                        requires: [ /*'excanvas'*/ ],
                        ix_provides: 'VariableDisplay'
                    },
                    'wegas-inbox': {
                        path: 'wegas-app/js/widget/wegas-inbox-min.js',
                        requires: [ "tabview" ],
                        ix_provides: 'InboxDisplay'
                    },
                    'wegas-form': {
                        path: 'wegas-app/js/widget/wegas-form-min.js',
                        requires: ['wegas-widget', 'wegas-inputex'],
                        ix_provides: "FormWidget"
                    },
                    'wegas-joingamewidget': {
                        path: 'wegas-app/js/widget/wegas-joingamewidget-min.js',
                        requires: ['wegas-widget', 'inputex-select', 'inputex-string', 'button'],
                        ix_provides: "JoinGameWidget"
                    },
                    'wegas-gallerycss':{
                        path:'wegas-app/css/wegas-gallery.css',
                        type:'css'
                    },
                    'wegas-gallery': {
                        path:'wegas-app/js/widget/wegas-gallery-min.js',
                        requires: ['wegas-widget', 'scrollview-base', 'scrollview-paginator', 'wegas-gallerycss', 'stylesheet'],
                        ix_provides: "WegasGallery"
                    },

                    /** Inputex Fields **/
                    'wegas-inputex': {
                        path: 'wegas-app/js/widget/wegas-inputex-min.js',
                        requires: [ 'inputex', 'inputex-field', 'inputex-string', 'inputex-keyvalue' ]
                    },
                    'wegas-inputex-rte': {
                        path: 'wegas-app/js/widget/wegas-inputex-rte-min.js',
                        requires: ['inputex-field', 'yui2-editor', 'panel', 'wegas-fileexplorer' ],
                        ix_provides: 'html'
                    },
                    'wegas-inputex-hashlist': {
                        path: 'wegas-app/js/widget/wegas-inputex-hashlist-min.js',
                        requires: ['inputex-list'],
                        ix_provides: 'hashlist'
                    },
                    'wegas-inputex-script': {
                        path: 'wegas-app/js/widget/wegas-inputex-script-min.js',
                        requires: [ 'inputex-textarea' ],
                        ix_provides: 'script'
                    },
                    'wegas-inputex-url': {
                        path: 'wegas-app/js/widget/wegas-inputex-url-min.js',
                        requires: [ 'inputex-url', 'panel', 'wegas-fileexplorer'],
                        ix_provides: 'wegasurl'
                    },
                    'wegas-inputex-ace': {
                        path: 'wegas-app/js/widget/wegas-inputex-ace-min.js',
                        requires: [ 'inputex-field', 'ace' ],
                        ix_provides: 'wegasurl'
                    },

                    /** Common Widgets **/
                    'wegas-widgetmenu': {
                        path: 'wegas-app/js/widget/wegas-widgetmenu-min.js',
                        requires: ['plugin', 'yui-later', 'event-mouseenter', 'event-outside',
                        'widget', 'widget-parent', 'widget-child', 'widget-stack',
                        'widget-position', 'widget-position-align', 'widget-position-constrain' ]
                    },
                    'treeview':{
                        path: 'wegas-editor/js/treeview-min.js',
                        requires: [ 'widget', 'widget-parent', 'widget-child', 'treeviewcss' ]
                    },
                    'treeviewcss': {
                        path: 'wegas-editor/css/treeview.css',
                        type: 'css'
                    },

                    /** Editor **/
                    'wegas-editor': {
                        path: 'wegas-editor/js/wegas-editor-min.js',
                        requires: [
                        'wegas-app', 'wegas-widgetmenu',
                        "wegas-editor-buttons", 'wegas-editor-action',
                        'wegas-form',
                        //'wegas-editorcss',                                    // @fixme There is a bug in css include order, this one got hardcoded in the jsp file
                        ]
                    },
                    'wegas-editorcss': {
                        path: 'wegas-editor/css/wegas-editor.css',
                        type: 'css'
                    },

                    /** Editor's Widgets **/
                    'wegas-editor-action': {
                        path: 'wegas-editor/js/wegas-editor-action-min.js',
                        requires: [ 'wegas-action' ]
                    },
                    'wegas-logger': {
                        path: 'wegas-editor/js/wegas-logger-min.js',
                        requires: ['console', 'console-filters'],
                        ix_provides: 'Logger'
                    },
                    'wegas-editor-buttons': {
                        path: 'wegas-editor/js/wegas-editor-buttons-min.js',
                        requires: [ 'wegas-button',  'wegas-widgetmenu' ],
                        ix_provides: 'SelectPlayerButton'
                    },
                    'wegas-csseditor': {
                        path: 'wegas-editor/js/wegas-csseditor-min.js',
                        requires: [ 'ace-css', 'wegas-inputex-ace' ],
                        ix_provides: 'CSSEditor'
                    },
                    'wegas-console': {
                        path: 'wegas-editor/js/wegas-console-min.js',
                        requires: ['ace-javascript', 'wegas-inputex-ace'],
                        ix_provides: 'Console'
                    },
                    'wegas-editor-treeview': {
                        path: 'wegas-editor/js/wegas-editor-treeview-min.js',
                        requires: [ 'wegas-widget',  "treeview", "wegas-widgetmenu" ],
                        ix_provides: [ 'EditorTreeView', "LobbyTreeView" ]
                    },
                    'wegas-datatable': {
                        path: 'wegas-editor/js/wegas-datatable-min.js',
                        requires: [ 'datatable', 'datatable-sort' ],
                        ix_provides: 'DataTable'
                    },
                    'wegas-menucss':{
                        path: 'wegas-app/css/wegas-menu.css',
                        type:'css'
                    },
                    'wegas-menu': {
                        path:'wegas-app/js/widget/wegas-menu-min.js',
                        requires:['button', 'wegas-menucss'],
                        ix_provides: 'WegasMenu'
                    },
                    'wegas-scriptlibrary': {
                        path: 'wegas-editor/js/wegas-scriptlibrary-min.js',
                        requires: [ 'ace-javascript', 'button', 'wegas-inputex-ace' ],
                        ix_provides: 'ScriptLibrary'
                    },
                    'wegas-fileexplorercss':{
                        path: 'wegas-editor/css/wegas-fileexplorer.css',
                        type:'css'
                    },
                    'wegas-fileexplorer': {
                        path: 'wegas-editor/js/wegas-fileexplorer-min.js',
                        requires: ['treeview', 'uploader-html5', 'wegas-menu', 'wegas-progressbar', 'wegas-fileexplorercss'],
                        ix_provides: "FileExplorer"
                    },
                    'wegas-progressbar': {
                        path: 'wegas-editor/js/wegas-progressbar-min.js',
                        requires: ['widget'],
                        ix_provides: 'ProgressBar'
                    },
                    'wegas-statemachineviewer': {
                        path: 'wegas-editor/js/wegas-statemachineviewer-min.js',
                        requires: ['dd-constrain','wegas-datasourcerest','wegas-statemachineviewercss', 'jsplumb-yui-all', 'button', 'wegas-statemachine-entities'],
                        ix_provides: 'StateMachineViewer'
                    },
                    'wegas-statemachineviewercss': {
                        path: 'wegas-editor/css/wegas-statemachineviewer.css'
                    },
                    'wegas-wysiwygeditor': {
                        path: 'wegas-editor/js/wegas-wysiwygeditor-min.js',
                        requires: ['inputex',
                            'inputex-hidden', 'inputex-jsonschema', 'inputex-select',
                            'esprima', 'escodegen'],
                        ix_provides: "WysiwygEditor"
                    },

                    /** Project Management Game **/
                    'wegas-projectmanagementgame': {
                        path: 'wegas-projectmanagementgame/js/wegas-projectmanagementgame-min.js'
                    },

                    /** CrimeSim **/
                    'wegas-mcqtabview': {
                        path: 'wegas-crimesim/js/wegas-mcqtabview-min.js',
                        requires: [ 'tabview' ],
                        ix_provides: "MCQTabView"
                    },
                    'wegas-crimesim-scheduledisplay': {
                        path: 'wegas-crimesim/js/wegas-crimesim-scheduledisplay-min.js',
                        requires: ['wegas-widget', 'wegas-widgetmenu', 'datatable' ],
                        ix_provides: "ScheduleDisplay"
                    },
                    'wegas-crimesim-resultsdisplay': {
                        path: 'wegas-crimesim/js/wegas-crimesim-resultsdisplay-min.js',
                        requires: ['wegas-widget', 'datatable' ],
                        ix_provides: "ResultsDisplay"
                    },

                    /**Leaderway**/
                    'wegas-leaderway': {
                        path: 'wegas-leaderway/js/wegas-leaderway-hrlist.js',
                        requires:['wegas-leaderway-folder', 'wegas-leaderway-tasklist', 'wegas-leaderway-score', 'wegas-leaderway-dialogue']/*,
                        ix_provides: "HRList"*/
                    },
                    'wegas-leaderway-folder':{
                        path: 'wegas-leaderway/js/wegas-leaderway-folder.js',
                        requires: ['tabview', 'panel', 'wegas-leaderway-tasklist'],
                        ix_provides: "Folder"
                    },
                    'wegas-leaderway-tasklist': {
                        path: 'wegas-leaderway/js/wegas-leaderway-tasklist.js',
                        requires: ['datatable'],
                        ix_provides: "TaskList"
                    },
                    'wegas-leaderway-score': {
                        path: 'wegas-leaderway/js/wegas-leaderway-score.js',
                        requires: ['datatable'],
                        ix_provides: "Score"
                    },
                    'wegas-leaderway-dialogue': {
                        path: 'wegas-leaderway/js/wegas-leaderway-dialogue.js',
                        /*!!!*/requires:['charts', 'charts-legend']/*!!!*/,
                        ix_provides: "Dialogue"
                    },

                    /** MMO **/
                    'wegas-mmo': {
                        path: 'wegas-mmo/js/wegas-mmo-min.js',
                        requires: ['wegas-widget', 'ace-javascript', 'wegas-inputex-ace'],
                        ix_provides: 'MMOWidget'
                    }
                }
            },

            /* Ace */
            ace: {
                //combine: true,
                base: './lib/ace/',
                root: '/lib/ace/',
                modules:  {
                    'ace': {
                        path: 'src/ace.js'
                    },
                    'ace-javascript': {
                        path: 'src/mode-javascript.js',
                        requires: [ 'ace' ]
                    },
                    'ace-css': {
                        path: 'src/mode-css.js',
                        requires: [ 'ace' ]
                    }
                }
            },

            /* jsPlumb */
            jsplumb: {
                combine: true,
                base: './lib/jsPlumb/',
                root: '/lib/jsPlumb/',
                modules: {
                    'jsplumb': {
                        path: 'jsPlumb-1.3.10-RC1.js',
                        requires: ['jsplumb-utils', 'dd']
                    },
                    'jsplumb-utils': {
                        path: 'jsPlumb-util-1.3.10-RC1.js',
                        requires: []
                    },
                    'jsplumb-svg': {
                        path: 'jsPlumb-renderers-svg-1.3.10-RC1.js',
                        requires: ['jsplumb']
                    },
                    'jsplumb-defaults': {
                        path: 'jsPlumb-defaults-1.3.10-RC1.js',
                        requires: ['jsplumb']
                    },
                    'jsplumb-statemachine': {
                        path: 'jsPlumb-connectors-statemachine-1.3.10-RC1.js',
                        requires: ['jsplumb', 'jsbezier']
                    },
                    'jsplumb-yui': {
                        path: 'yui.jsPlumb-1.3.10-RC1.js',
                        requires: ['jsplumb']
                    },
                    'jsplumb-yui-all': {
                        path:'yui.jsPlumb-1.3.10-all-min.js',
                        requires: ["node", "dd", "anim"/*, "node-event-simulate"*/]
                    },
                    'jsbezier': {
                        path: 'jsBezier-0.3-min.js'
                    }
                }
            },

            /* ExCanvas */
            excanvas: {
                combine: true,
                async: false,
                //combine: false,
                base: "./lib/excanvas/",
                root: "/excanvas/",
                modules:  {
                    'excanvas': {
                        path: 'excanvas.compiled.js'
                    }
                }
            },

            /* Esprima */
            esprima: {
                async: false,
                //combine: false,
                base: "./lib/",
                root: "/lib/",
                modules:  {
                    'esprima': {
                        path: 'esprima/esprima-min.js'
                    },
                    'escodegen': {
                        path: 'escodegen/escodegen-min.js'
                    }
                }
            }
        }
    };

    if (typeof YUI_config === 'undefined') {
        YUI_config = {
            groups: {}
        };
    }
    Y.mix(YUI_config.groups, CONFIG.groups);

    function loadModules(group) {
        var i, modules = group.modules,
        moduleName,
        allModules = [],
        modulesByType = {};
        for (moduleName in modules) {                                           // Loop through all modules
            if (modules.hasOwnProperty(moduleName)) {
                allModules.push(moduleName);                                    // Build a list of all modules

                if (modules[moduleName].ix_provides) {                          // Build a reverse index on which module provides what type

                    if (Y.Lang.isArray(modules[moduleName].ix_provides)) {
                        for (var i = 0; i < modules[moduleName].ix_provides.length; i = i + 1) {
                            modulesByType[modules[moduleName].ix_provides[i]] = moduleName;
                        }
                    } else {
                        modulesByType[modules[moduleName].ix_provides] = moduleName;
                    }
                }

            }
        }
        group.allModules = allModules;
        group.modulesByType = modulesByType;
    }

    loadModules(YUI_config.groups.wegas);
    loadModules(YUI_config.groups.ace);
    loadModules(YUI_config.groups.excanvas);
    loadModules(YUI_config.groups.esprima);
    loadModules(YUI_config.groups.jsplumb);
});
