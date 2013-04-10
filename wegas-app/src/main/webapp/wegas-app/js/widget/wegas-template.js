/*
 * Wegas
 * http://www.albasim.ch/wegas/
 *
 * Copyright (c) 2013 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */

/**
 * @fileOverview
 * @author Cyril Junod <cyril.junod at gmail.com>
 */

YUI.add("wegas-template", function(Y) {
    "use strict";
    var TEMPLATE,
            engine = new Y.Template(Y.Template.Micro),
            undefinedToEmpty = function(value) {
        return Y.Lang.isUndefined(value) ? "" : "" + value;
    };
    TEMPLATE = {
        TEXT: "<div class='wegas-text-template'><span><%= this.label || '{label}' %></span><br/><span><%= this.value || '{value}' %></span></div>",
        BOX: "<div class='wegas-box-template'><%= this.label || '{label}'%><br/><span><% for(var i=0; i < this.value; i+=1){%>" +
                "<div class='wegas-template-box-unit'></div><% } %></span><br/>" +
                "<span>(<%= this.value || '{value}' %><% if(this.default_value != ''){ %><%= '/' + (this.default_value || '{default_value}') %><% } %>)</span></div>",
        VALUEBOX: "<div class='wegas-valuebox-template'><%= this.label || '{label}'%><br/><span><% for(var i=+this.min_value; i < +this.max_value + 1; i+=1){%>" +
                "<div class='wegas-template-valuebox-unit<%= +i === +this.value ? ' wegas-template-valuebox-selected' : '' %>'><%= ''+i %></div><% } %></span><br/>" +
                "</span></div>",
        TITLE: "<div class='wegas-title-template'><%= this.label || '{label}'%></div>",
        FRACTION: "<div class='wegas-fraction-template'><%= (this.min_value || '{min_value}') + '/' + (this.value || '{label}') + '/' + (this.max_value || '{max_value}') %></div>"
    };
    /**
     * @name Y.Wegas.Template
     * @extends Y.Widget
     * @borrows Y.WidgetChild, Y.Wegas.Widget, Y.Wegas.Editable
     * @class
     * @constructor
     * @description  Display  Wegas variables instance (or/and descriptor) under
     * specifique templates : text, title, box, fraction and valuebox.
     * It is also possible to create a custom template.
     */
    Y.namespace("Wegas").Template = Y.Base.create("wegas-template", Y.Widget,
            [Y.WidgetChild, Y.Wegas.Widget, Y.Wegas.Editable], {
        /*@lends Y.Wegas.Template#*/
        TEMPLATES: {
            text: engine.compile(TEMPLATE.TEXT),
            box: engine.compile(TEMPLATE.BOX),
            valuebox: engine.compile(TEMPLATE.VALUEBOX),
            title: engine.compile(TEMPLATE.TITLE),
            fraction: engine.compile(TEMPLATE.FRACTION)
        },
        initializer: function() {
        },
        renderUI: function() {
            this.set("variable", this.get("variable"));
        },
        syncUI: function() {
            var template = this.get("custom"), hashCode = "" + Y.Wegas.Helper.hashCode(template),
                    data = this.computeData();
            if (template === "" && this.TEMPLATES[this.get("template")]) {
                this.get("contentBox").setHTML(this.TEMPLATES[this.get("template")](data));
            } else {
                if (Y.Lang.isUndefined(this.TEMPLATES[hashCode])) {
                    this.TEMPLATES[hashCode] = engine.compile(template);
                }
                this.get("contentBox").setHTML(this.TEMPLATES[hashCode](data));
            }

        },
        bindUI: function() {
            this.after(["dataChange", "variableChange", "templateChange"], this.syncUI, this);
            Y.Wegas.Facade.VariableDescriptor.after("update", this.syncUI, this);
        },
        computeData: function() {
            var data = {}, desc = this.get("variable.evaluated");
            data.label = undefinedToEmpty(desc.getPublicLabel());
            data.value = undefinedToEmpty(desc.getInstance().get("value"));
            data.max_value = undefinedToEmpty(desc.get("maxValue"));
            data.min_value = undefinedToEmpty(desc.get("minValue"));
            data.default_value = undefinedToEmpty(desc.get("defaultInstance").get("value"));
            data.variable = desc;
            return Y.mix(Y.merge(this.get("data")), data, false, null, 0, true);
        },
        destructor: function() {

        }
    },
    {/*@lends Y.Wegas.Template*/
        EDITORNAME: "Variable template",
        ATTRS: {
            /**
             * The target variable, returned either based on the variableName attribute,
             * and if absent by evaluating the expr attribute.
             */
            variable: {
                getter: Y.Wegas.Widget.VARIABLEDESCRIPTORGETTER,
                _inputex: {
                    _type: "variableselect",
                    label: "variable"
                }
            },
            template: {
                value: "TEXT",
                type: "string",
                choices: [{
                        value: "text"
                    }, {
                        value: "title"
                    }, {
                        value: "box"
                    }, {
                        value: "valuebox"
                    }, {
                        value: "fraction"
                    }],
                _inputex: {
                    label: "Predefined template"
                }
            },
            custom: {
                type: "string",
                optional: true,
                value: "",
                _inputex: {
                    label: "Custom template",
                    description: "Takes precedence over predefined templates",
                    _type: "html"
                }
            },
            data: {
                value: {},
                _inputex: {
                    _type: "object",
                    useButtons: true,
                    required: false
                }
            }
        }
    });
});