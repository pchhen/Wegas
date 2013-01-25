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
 * @fileoverview
 * @author Yannick Lagger <lagger.yannick@gmail.com>
 */

YUI.add('wegas-jointeamwidget', function (Y) {
    "use strict";

    var JoinTeamWidget;

    /**
    * @name Y.Wegas.JoinTeamWidget
    * @extends Y.Wegas.JoinGameWidget
    * @class  class for join a team
    * @constructor
    * @description Allows just to join a team
    */
    JoinTeamWidget = Y.Base.create("wegas-jointeamwidget", Y.Wegas.JoinGameWidget, [Y.WidgetChild, Y.Wegas.Widget], {
        
        /**
         * @methodOf Y.Wegas.JoinTeamWidget#
         * @private
         * @name renderUI
         * @description Difference compared to the parent class:
         * 1) token is get from the url
         * 2) then call directly the sendJoinGame()
         */
        renderUI: function () {
            JoinTeamWidget.superclass.renderUI.apply(this);
            this.tokenField.setValue(document.location.search.substring(7));
            this.sendJoinGame();
        },

        /**
         * @methodOf Y.Wegas.JoinTeamWidget#
         * @private
         * @name bindUI
         */
        bindUI: function () {            
            JoinTeamWidget.superclass.bindUI.apply(this);
        },
        
        /**
         * @methodOf Y.Wegas.JoinTeamWidget#
         * @private
         * @name joinTeamSuccess
         * @description Reload the same page
         */
        joinTeamSuccess: function () {
            this.showMessage("success", "Game joined", 10000);
            window.location.reload();
        }
    });

    Y.namespace('Wegas').JoinTeamWidget = JoinTeamWidget;
});
