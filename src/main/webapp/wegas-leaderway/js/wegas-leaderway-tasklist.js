/**
* @author Benjamin Gerber <ger.benjamin@gmail.com>
*/

YUI.add('wegas-leaderway-tasklist', function (Y) {
    "use strict";

    var CONTENTBOX = 'contentBox', TaskList;

    TaskList = Y.Base.create("wegas-tasklist", Y.Widget, [Y.WidgetChild, Y.Wegas.Widget], {
        
        // *** Fields *** /
        table: null,
        data: new Array(),
        selectedTaskDescriptor:null,
        resourceDescriptor: null,
        nextPageId: null,
        handlers: new Array(),
        
        //*** Particular Methods ***/
        /**
         * Change this widget to its picking mode
         * The picking mode is used to add a task to a resource.
         * Add class "modePicking".
         * Display the surname of the selected resoure 
         * Display the button used to assign the task and display the informations of the selected task.
         * Set the identifiant of the next page reached by quitting this widget by a hit on the picking-mode's buttons 
         * @param ResourceDescriptor resourceDescriptor, the new resourceDescriptor
         * @param String nextPageId the identifiant of the nexte page id.
         */
        switchToPickingMode: function(resourceDescriptor, nextPageId){
            var cb = this.get(CONTENTBOX);
            this.resourceDescriptor = resourceDescriptor;
            if(this.resourceDescriptor != null){
                this.nextPageId = nextPageId;
                cb.addClass('modePicking');
                cb.one('.resourceName p').setHTML('Assigner un mandat à '+this.resourceDescriptor.getInstance().get('properties').surname);
                cb.one('.resourceName').show();
                cb.one('.footer').show();
            }                
        },
        
        /**
         * Add rows to the datatable. Get informations on the valide tasks
         * @param ListDescriptor listTasksDescriptor, A list of all tasks.
         */
        getTasksData: function(listTasksDescriptor){
            var i, j, k, termData, workers = new Array(), taskDescriptor, taskInstance, resourceDescriptor, resourceInstance, comment,
            listResourcesDescriptor = Y.Wegas.VariableDescriptorFacade.rest.find("name", "resources"),
            currentWeekInstance = Y.Wegas.VariableDescriptorFacade.rest.find("name", "week").getInstance();
            for (i = 0; i < listTasksDescriptor.get('items').length; i++) {
                workers.length = 0;
                taskDescriptor = listTasksDescriptor.get('items')[i];
                taskInstance = taskDescriptor.getInstance();
                for(j=0; j<listResourcesDescriptor.get('items').length; j++){
                    resourceDescriptor = listResourcesDescriptor.get('items')[j];
                    resourceInstance = resourceDescriptor.getInstance();
                    for(k=0; k<resourceInstance.get('assignments').length; k++){
                        if(taskDescriptor.get('id') == resourceInstance.get('assignments')[k].get('taskDescriptorId')){
                            workers.push(resourceInstance.get('properties').surname);
                            break;
                        }
                    }
                }
                if(taskInstance.get('active')){
                    termData = (workers.length <= 0)?(taskInstance.get('properties').disappearAtWeek - currentWeekInstance.get('value')) : "-";
                    comment = new Array();
                    if(taskInstance.get('properties').comment) comment.push(taskInstance.get('properties').comment);
                    if(taskInstance.get('properties').workWithLeader == 'true') comment.push("S'effectue en coopération avec le leader.");
                    if(comment.length <= 0) comment.push("-");
                    if(workers.length <= 0) workers.push("-");
                    this.data.push({
                        id:taskDescriptor.get('id'),
                        task:taskDescriptor.get('name'),
                        skill:this.getSkillsets(taskInstance),
                        duration:taskInstance.get('duration'),
                        term:termData,
                        salary:taskInstance.get('properties').salary,
                        comment:comment.join(" "),
                        worker: workers.join(",")
                    })
                }
            }
        },
        
        /**
         * @param TaskInstance taskInstance, the task to get skillsets
         * @return String, a texte including all the skillset of the given task.
         */
        getSkillsets: function(taskInstance){
            var temp = new Array();
            for (var key in taskInstance.get('skillset')){
                temp.push(key, ' (', taskInstance.get('skillset')[key], ')\n');
            }
            return temp.join("");
        },
        
        /**
         * @return the current selected task.
         */
        getSelectedTaskDescriptor: function(){
            return this.selectedTaskDescriptor;
        },
        
        /**
         * This function must be called by a click event.
         * set the current selected task
         * fire a event "rowSelected" with a object "taskDescriptor"
         * @param e, a click event on a row from the datatable.
         */
        selectRow: function(e){
            var i, cb = this.get(CONTENTBOX),
            listTasksDescriptor = Y.Wegas.VariableDescriptorFacade.rest.find("name", "tasks"),taskDescriptorId;
            taskDescriptorId = e.currentTarget._node.all[0].innerText;
            for (i = 0; i < listTasksDescriptor.get('items').length; i++) {
                if(listTasksDescriptor.get('items')[i].get('id') == taskDescriptorId){
                    this.selectedTaskDescriptor = listTasksDescriptor.get('items')[i];
                    this.fire("rowSelected", {
                        taskDescriptor: this.selectedTaskDescriptor
                    });
                    break;
                }
            }
            if(this.selectedTaskDescriptor!=null){
                cb.one('.footer .assignInfo').setHTML('Assigner le mandat : '+this.selectedTaskDescriptor.get('name'));
                cb.one('.footer .buttonOK').show();
            }
        },
        
        /**
         * Assign the given task to the given ressource.
         * @param ResourceDescriptor resourceDescriptor, the resource to assign a task.
         * @param TaskDescriptor taskDescriptor, the task to assign at a ressource. 
         */
        assignTask: function(resourceDescriptor, taskDescriptor){
            if(taskDescriptor != null && resourceDescriptor != null){
                Y.Wegas.VariableDescriptorFacade.rest.sendRequest({
                    request: "/Script/Run/Player/" + Y.Wegas.app.get('currentPlayer'),
                    headers:{
                        'Content-Type': 'application/json; charset=utf-8',
                        'Managed-Mode':'true'
                    },
                    cfg: {
                        method: "POST",
                        data: Y.JSON.stringify({
                            "@class": "Script",
                            "language": "JavaScript",
                            "content": "importPackage(com.wegas.core.script);\nassignTask("+resourceDescriptor.get('id')+","+taskDescriptor.get('id')+");"
                        }),
                        on: { //not work, call any function
//                            success: Y.bind(this.assignTaskResult, this, true, Y.one('.leaderway-feedback')),
//                            failure: Y.bind(this.assignTaskResult, this, false, Y.one('.leaderway-feedback'))
                            success: Y.bind(this.assignTaskResult, this, true, Y.one('.leaderway-feedback')),
                            failure: Y.bind(this.assignTaskResult, this, false, Y.one('.leaderway-feedback'))
                        }
                    }
                });
            }
            this.syncUI();
        },
        
        /**
         * Display a feedback on the operation "add task to a resource"
         * This feedback will disappear after 5 seconde. 
         * @param Boolean success, true if the opreation was a success, false otherwise.
         * @param Node feedbackNode, the node to insert the feedback.
         */
        assignTaskResult: function(success, feedbackNode){
            console.log('test'+success);
            feedbackNode.setStyle('display', 'block');
            if(success){
                feedbackNode.one('p').addClass('green');
                feedbackNode.one('p').insert("Le mandat à été délégué !");
            }
            else{
                feedbackNode.one('p').addClass('red');
                feedbackNode.one('p').insert("Le mandat n'a pas pu être délégué.");
            } 
            setTimeout(function(){
                feedbackNode.setHTML('<p></p>');
                feedbackNode.setStyle('display', 'none');
                feedbackNode.one('p').removeClass('green');
                feedbackNode.one('p').removeClass('red');
            }, 5000);
        },

        // *** Lifecycle Methods *** //
        /**
         * Create the widget
         * Publish event 'rowSelected' at this widget.
         */
        initializer: function() {
            this.publish("rowSelected", {});
        },
        
        /**
         * Render the widget.
         * Hide node corresponding used only with the "picking mode" of this widget 
         */
        renderUI: function (){
            var cb = this.get(CONTENTBOX);
            this.table = new Y.DataTable({
                columns: [
                {
                    key:"id",
                    className: 'hidden'
                },
                {
                    key:"task", 
                    label:"Mandat",
                    sortable:true
                },
                {
                    key:"skill", 
                    label:"Compétence",
                    sortable:true
                },
                {
                    key:"duration", 
                    label:"Durée",
                    sortable:true
                },
                {
                    key:"term", 
                    label:"Echéance",
                    sortable:true
                },
                {
                    key:"salary", 
                    label:"Remunération",
                    sortable:true
                },
                {
                    key:"comment", 
                    label:"Remarque",
                    sortable: true
                },
                {
                    key:"worker", 
                    label:"Employé",
                    sortable: true
                }    
                ]
            });
            cb.insert('<div class="resourceName"><p></p></div>');
            this.table.render(cb);
            cb.insert('<div class="footer"><div class="assignInfo"></div><div class="buttons"><div class="buttonOK">Assigner</div><div class="buttonCancel">Annuler</div></div></div>');
            cb.one('.resourceName').hide();
            cb.one('.footer').hide();
            cb.one('.footer .buttonOK').hide();
        },
          
        /**
         * Bind some function at nodes of this widget
         */
        bindUI: function() {
             var cb = this.get(CONTENTBOX);
            this.handlers.push(Y.Wegas.app.dataSources.VariableDescriptor.after("response", this.syncUI, this));
            this.handlers.push(Y.Wegas.app.after('currentPlayerChange', this.syncUI, this));
            this.handlers.push(this.table.delegate('click', function (e) {
                this.selectRow(e);
            }, '.yui3-datatable-data tr', this));
            
            this.handlers.push(cb.one('.buttons').delegate('click', function (e) {
                var targetPageLoader = Y.Wegas.PageLoader.find(this.get('targetPageLoaderId'));
                targetPageLoader.once("widgetChange", function(e) {
                    if(e.newVal.setResourceDescriptor){
                        e.newVal.setResourceDescriptor(this.resourceDescriptor);
                    }
                },{resourceDescriptor:this.resourceDescriptor});
                this.assignTask(this.resourceDescriptor, this.selectedTaskDescriptor);
                targetPageLoader.set("pageId", this.nextPageId);                
            }, '.buttonOK', this));
            
            this.handlers.push(cb.one('.buttons').delegate('click', function (e) {
                var targetPageLoader = Y.Wegas.PageLoader.find(this.get('targetPageLoaderId'));
                targetPageLoader.once("widgetChange", function(e) {
                    if(e.newVal.setResourceDescriptor){
                        e.newVal.setResourceDescriptor(this.resourceDescriptor);
                    }
                },{resourceDescriptor:this.resourceDescriptor});
                targetPageLoader.set("pageId", this.nextPageId);
            }, '.buttonCancel', this));
            
        },
        
        /**
         * Synchronise the content of this widget.
         */
        syncUI: function (){
            var listTasksDescriptor = Y.Wegas.VariableDescriptorFacade.rest.find("name", "tasks");
            if(!listTasksDescriptor) return;
            this.data.length = 0;
            this.getTasksData(listTasksDescriptor);
            this.table.addRows(this.data);
            if(this.data[0] == null){
                this.table.showMessage("Aucun mandat n'est disponible.");
            }
            else{
                this.table.hideMessage();
            }
            this.goToFinalPage();// ! hack function
        },
        
         /*
         * Destroy all child widget and all function
         */
        destroy: function(){
            var i;
            this.table.destroy();
            for (i=0; i<this.handlers.length;i++) {
                this.handlers[i].detach();
            }
        },
        
        // *** hack Methods *** //
        /**
         * if current week > max value of week value, then
         * change the current widget to go on the "dialogue" widget.
         */
        goToFinalPage: function(){
            var currentWeek = Y.Wegas.VariableDescriptorFacade.rest.find("name", "week");
            var targetPageLoader = Y.Wegas.PageLoader.find(this.get('targetPageLoaderId'));
            if(parseInt(currentWeek.getInstance().get('value')) > currentWeek.get('maxValue')){
                targetPageLoader.set("pageId", this.get('dialoguePageId'));    
            }
        }
        
    },
    {
        ATTRS : {
            dialoguePageId: {
                value:null,
                validator: function (s){
                    return s === null || Y.Lang.isString(s);
                }
            },
            targetPageLoaderId: {
                value:null,
                validator: function (s){
                    return s === null || Y.Lang.isString(s);
                }
            }
         }
    });

    Y.namespace('Wegas').TaskList = TaskList;
});