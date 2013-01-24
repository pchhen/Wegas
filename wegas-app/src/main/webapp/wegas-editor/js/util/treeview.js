/**
 *
 *
 */

YUI.add('treeview', function(Y) {
    var getClassName = Y.ClassNameManager.getClassName,
        TREEVIEW = 'treeview',
        TREENODE = 'treenode',
        TREELEAF = 'treeleaf',
        CONTENT_BOX = "contentBox",
        BOUNDING_BOX = "boundingBox",
        classNames = {
            loading: getClassName(TREENODE, "loading"),
            collapsed: getClassName(TREENODE, "collapsed"),
            visibleRightWidget: getClassName(TREEVIEW, "visible-right")
        },
        RIGHTWIDGETSETTERFN = function(v) {
            if (this.get("rightWidget") !== v && this.get("rightWidget")) {// Remove existing child
                if (this.get("rightWidget") instanceof Y.Node) {        // Case 1: Y.Node
                    this.get("rightWidget").remove();
                } else {
                    this.get("rightWidget").get(BOUNDING_BOX).remove(); // Case 2: Y.Widget
                    this.get("rightWidget").removeTarget(this);
                    this.set("parent", null);
                }
            }
            if (v) {                                                    // Append the new widget
                if (v instanceof Y.Node) {                              // Case 1: Y.Node
                    v.appendTo("#" + this.get("id") + "_right");
                } else {                                                // Case 2: Y.Widget
                    if (v.get("rendered")) {
                        v.get(BOUNDING_BOX).appendTo("#" + this.get("id") + "_right");
                    } else {
                        v.render("#" + this.get("id") + "_right");
                    }
                    v.set("parent", this);
                    v.addTarget(this);
                }
            }
            return v;
        };

    Y.TreeView = Y.Base.create("treeview", Y.Widget, [Y.WidgetParent], {
        BOUNDING_TEMPLATE: "<div></div>",
        CONTENT_TEMPLATE: "<ul></ul>",
        initializer: function() {
        },
        bindUI: function() {
            this.on("*:click", function(e) {
                if (e.node && e.node !== this) {
                    this.deselectAll();
                    e.node.set("selected", 2);
                }
            });
        },
        renderUI: function() {
            if (this.get("visibleRightWidget")) {
                this.get(CONTENT_BOX).addClass(classNames.visibleRightWidget);
            }
        },
        expandAll: function() {
            this.each(function(item) {
                if (item.expandAll) {
                    item.expandAll();
                }
            });
        },
        collapseAll: function() {
            this.each(function(item) {
                if (item.collapseAll) {
                    item.collapseAll();
                }
            });
        }
    }, {
        NAME: 'treeview',
        ATTRS: {
            visibleRightWidget: {
                value: false,
                validator: Y.Lang.isBoolean
            },
            defaultChildType: {
                value: "TreeLeaf"
            },
            multiple: {
                value: false
            }
        }
    });

    Y.TreeNode = Y.Base.create("treenode", Y.Widget, [Y.WidgetParent, Y.WidgetChild], {
        BOUNDING_TEMPLATE: "<li></li>",
        CONTENT_TEMPLATE: "<ul></ul>",
        toggleNode: null,
        labelNode: null,
        iconNode: null,
        currentIconCSS: null,
        menuNode: null,
        eventInstances: {},
        initializer: function() {
            this.publish("toggleClick", {
                bubbles: false,
                broadcast: false,
                defaultFn: this.toggleTree
            });
            this.publish("nodeExpanded", {
                bubbles: true
            });
            this.publish("nodeCollapsed", {
                bubbles: true
            });
            this.publish("iconClick", {
                bubbles: true
            });
            this.publish("labelClick", {
                bubbles: true
            });
            this.publish("click", {
                bubbles: true
            });
        },
        renderUI: function() {
            var bb = this.get(BOUNDING_BOX), header;
            header = Y.Node.create("<div class='content-header " + this.getClassName("content", "header") + "'></div>");
            bb.insertBefore(header, this.get(CONTENT_BOX));
            this.toggleNode = Y.Node.create("<span class='" + this.getClassName("content", "toggle") + "'></span>");
            this.iconNode = Y.Node.create("<span class='" + this.getClassName("content", "icon") + "'></span>");
            this.labelNode = Y.Node.create("<span class='" + this.getClassName("content", "label") + "'></span>");
            header.append(this.toggleNode);
            header.append(this.iconNode);
            header.append(this.labelNode);
            header.append("<div id=\"" + this.get("id") + "_right\" class=\"" + this.getClassName("content", "rightwidget") + "\">");
            if (this.get('collapsed') && !bb.hasClass(classNames.collapsed)) {
                bb.addClass(classNames.collapsed);
            }
        },
        bindUI: function() {
            /*
             * Force event firing for an exisiting and none 0 attribute
             * "selected" on initialization
             */
            this.onceAfter("renderedChange", function(e) {
                var val = this.get("selected");
                if (val && !this.get("selection")) {                            /* check for last selected node */
                    this.set("selected", 0);
                    this.set("selected", val);
                }
            });
            this.after("selectedChange", function(e) {
                if (e.newVal && !e.target.get("selection")) {
                    e.target.get(BOUNDING_BOX).addClass("selected");
                } else {
                    e.target.get(BOUNDING_BOX).removeClass("selected");
                }
            });
            this.eventInstances.click = this.toggleNode.on("click", function(e) {
                e.stopPropagation();
                this.fire("toggleClick", {
                    node: this
                });
            },
            this);
            this.eventInstances.fullClick = this.get(BOUNDING_BOX).one("." + this.getClassName("content", "header")).on("click", function(e) {
                var node = e.target;
                e.stopPropagation();
                if (node.hasClass(this.getClassName("content", "icon"))) {
                    this.fire("iconClick", {
                        node: this
                    });
                }
                if (node.hasClass(this.getClassName("content", "label"))) {
                    this.fire("labelClick", {
                        node: this
                    });
                }
                this.fire("click", {
                    node: this,
                    domEvent: e
                });

            }, this);
            this.get(BOUNDING_BOX).on("click", function(e) {
                e.stopPropagation();
            });
        },
        syncUI: function() {
            this.set("loading", this.get("loading"));
            this.set("iconCSS", this.get("iconCSS"));
            this.set("label", this.get("label"));
            this.set("rightWidget", this.get("rightWidget"));
            this.set("collapsed", this.get("collapsed"));
        },
        destructor: function() {
            var event;
            this.blur();                                                        //remove a focused node generates some errors
            for (event in this.eventInstances) {
                this.eventInstances[event].detach();
            }
            if (this.get("rightWidget")) {
                this.get("rightWidget").destroy();
            }
        },
        toggleTree: function() {
            this.get(BOUNDING_BOX).toggleClass(classNames.collapsed);
            if (this.get(BOUNDING_BOX).hasClass(classNames.collapsed)) {
                this.fire('nodeCollapsed', {
                    node: this
                });
            } else {
                this.fire('nodeExpanded', {
                    node: this
                });
            }
        },
        expand: function(fireEvent) {
            this.set("collapsed", false);
            fireEvent = (fireEvent === undefined) ? true : fireEvent;
            if (fireEvent) {
                this.fire('nodeExpanded', {
                    node: this
                });
            }
        },
        collapse: function(fireEvent) {
            this.set("collapsed", true);
            fireEvent = (fireEvent === undefined) ? true : fireEvent;
            if (fireEvent) {
                this.fire('nodeCollapsed', {
                    node: this
                });
            }
        },
        expandAll: function() {
            this.expand(false);
            this.each(function(item) {
                if (item.expandAll) {
                    item.expandAll();
                }
            });
        },
        collapseAll: function() {
            this.collapse(false);
            this.each(function(item) {
                if (item.collapseAll) {
                    item.collapseAll();
                }
            });
        },
        destroyChildren: function() {
            var i, widgets = this.removeAll();
            for (i in widgets) {
                widgets.each(this.destroyChild, this);
            }
        },
        destroyChild: function(item, index) {
            item.destroy();
        }
    }, {
        NAME: "TreeNode",
        ATTRS: {
            label: {
                value: "",
                validator: Y.Lang.isString,
                setter: function(v) {
                    if (this.labelNode) {
                        this.labelNode.setContent(v);
                    }
                    return v;
                }
            },
            initialSelected: {
                writeOnce: "initOnly"
            },
            collapsed: {
                value: true,
                validator: Y.Lang.isBoolean,
                setter: function(v) {
                    if (v) {
                        this.get(BOUNDING_BOX).addClass(classNames.collapsed);
                    } else {
                        this.get(BOUNDING_BOX).removeClass(classNames.collapsed);
                    }
                    return v;
                }
            },
            tabIndex: {
                value: 1
            },
            rightWidget: {
                value: null,
                validator: function(o) {
                    return o instanceof Y.Widget || o instanceof Y.Node || o === null;
                },
                setter: RIGHTWIDGETSETTERFN
            },
            loading: {
                value: false,
                validator: Y.Lang.isBoolean,
                setter: function(v) {
                    if (v) {
                        this.get(BOUNDING_BOX).addClass(classNames.loading);
                    } else {
                        this.get(BOUNDING_BOX).removeClass(classNames.loading);
                    }
                    return v;
                }
            },
            iconCSS: {
                value: getClassName("treenode", "default-icon"),
                validator: Y.Lang.isString,
                setter: function(v) {
                    if (this.currentIconCSS) {
                        this.iconNode.removeClass(this.currentIconCSS);
                    }
                    this.iconNode.addClass(v);
                    this.currentIconCSS = v;
                    return v;
                }
            },
            defaultChildType: {
                value: "TreeLeaf"
            },
            multiple: {
                value: false
            },
            data: {}
        }
    });

    /**
     * TreeLeaf widget. Default child type for TreeView.
     * It extends  WidgetChild, please refer to it's documentation for more info.
     * @class TreeLeaf
     * @constructor
     * @uses WidgetChild
     * @extends Widget
     * @param {Object} config User configuration object.
     */
    Y.TreeLeaf = Y.Base.create("treeleaf", Y.Widget, [Y.WidgetChild], {
        CONTENT_TEMPLATE: "<div></div>",
        BOUNDING_TEMPLATE: "<li></li>",
        menuNode: null,
        iconNode: null,
        currentIconCSS: null,
        labelNode: null,
        events: {},
        initializer: function() {
            this.publish("iconClick", {
                bubbles: true
            });
            this.publish("labelClick", {
                bubbles: true
            });
            this.publish("click", {
                bubbles: true
            });
        },
        renderUI: function() {
            var cb = this.get(CONTENT_BOX), header;
            header = Y.Node.create("<div class='content-header " + this.getClassName("content", "header") + "'></div>");
            cb.append(header);
            this.iconNode = Y.Node.create("<span class='" + this.getClassName("content", "icon") + "'></span>");
            this.labelNode = Y.Node.create("<span class='" + this.getClassName("content", "label") + "'></span>");
            header.append(this.iconNode);
            header.append(this.labelNode);
            header.append("<div id=\"" + this.get("id") + "_right\" class=\"" + this.getClassName("content", "rightwidget") + "\">");
        },
        bindUI: function() {
            this.onceAfter("renderedChange", function(e) {
                var val = this.get("selected");
                if (val) {
                    this.set("selected", 0);
                    this.set("selected", val);
                }
            });
            this.after("selectedChange", function(e) {
                if (e.newVal && !e.target.get("selection")) {
                    e.target.get(BOUNDING_BOX).addClass("selected");
                } else {
                    e.target.get(BOUNDING_BOX).removeClass("selected");
                }
            });
            this.events.fullClick = this.get(CONTENT_BOX).one("." + this.getClassName("content", "header")).on("click", function(e) {
                var node = e.target;
                e.stopImmediatePropagation();
                if (node.hasClass(this.getClassName("content", "icon"))) {
                    this.fire("iconClick", {
                        node: this
                    });
                }
                if (node.hasClass(this.getClassName("content", "label"))) {
                    this.fire("labelClick", {
                        node: this
                    });
                }
                this.fire("click", {
                    node: this,
                    domEvent: e
                });
            }, this);

            //one line, prevent special chars
            this.labelNode.on("blur", function(e) {
                e.target.setContent(e.target.getContent().replace(/&[^;]*;/gm, "").replace(/(\r\n|\n|\r|<br>|<br\/>)/gm, "").replace(/(<|>|\|\\|:|;)/gm, "").replace(/^\s+/g, '').replace(/\s+$/g, ''));
            }, this);
        },
        syncUI: function() {
            this.set("label", this.get("label"));
            this.set("iconCSS", this.get("iconCSS"));
            this.set("editable", this.get("editable"));
            this.set("loading", this.get("loading"));
            this.set("rightWidget", this.get("rightWidget"));
        },
        destructor: function() {
            this.blur();                                                        //remove a focused node generates some errors
            if (this.get("rightWidget") && this.get("rightWidget").destroy) {
                try {
                    this.get("rightWidget").destroy();
                } catch (e) {
                }
            }
            this.iconNode.remove(true);
            this.labelNode.remove(true);
        }
    }, {
        NAME: "TreeLeaf",
        ATTRS: {
            label: {
                value: "",
                validator: Y.Lang.isString,
                setter: function(v) {
                    if (this.labelNode) {
                        this.labelNode.setContent(v);
                    }
                    return v;
                },
                getter: function(v) {
                    return this.labelNode.getContent();
                }
            },
            tabIndex: {
                value: -1
            },
            initialSelected: {
                writeOnce: "initOnly"
            },
            rightWidget: {
                value: null,
                validator: function(o) {
                    return o instanceof Y.Widget || o instanceof Y.Node || o === null;
                },
                setter: RIGHTWIDGETSETTERFN
            },
            editable: {
                value: false,
                validator: Y.Lang.isBoolean,
                setter: function(v) {
                    if (v) {
                        this.labelNode.setAttribute("contenteditable", "true");
                    } else {
                        this.labelNode.setAttribute("contenteditable", "false");
                    }
                    return v;
                }
            },
            loading: {
                value: false,
                validator: Y.Lang.isBoolean,
                setter: function(v) {
                    if (v) {
                        this.get(CONTENT_BOX).addClass(classNames.loading);
                    } else {
                        this.get(CONTENT_BOX).removeClass(classNames.loading);
                    }
                    return v;
                }
            },
            iconCSS: {
                value: getClassName("treeleaf", "default-icon"),
                validator: Y.Lang.isString,
                setter: function(v) {
                    if (this.currentIconCSS) {
                        this.iconNode.removeClass(this.currentIconCSS);
                    }
                    this.iconNode.addClass(v);
                    this.currentIconCSS = v;
                    return v;
                }
            },
            data: {}
        }
    });
});