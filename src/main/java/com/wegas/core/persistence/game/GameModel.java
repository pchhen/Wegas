/*
 * Wegas.
 * http://www.albasim.com/wegas/
 *
 * School of Business and Engineering Vaud, http://www.heig-vd.ch/
 * Media Engineering :: Information Technology Managment :: Comem
 *
 * Copyright (C) 2012
 */
package com.wegas.core.persistence.game;

import com.wegas.core.persistence.NamedEntity;
import com.wegas.core.persistence.layout.Widget;
import com.wegas.core.persistence.variable.VariableDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonManagedReference;

/**
 *
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
@Entity
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = "name"))
public class GameModel extends NamedEntity implements Serializable {

    private static final Logger logger = Logger.getLogger("GameModelEntity");
    //private static final Pattern p = Pattern.compile("(^get\\()([a-zA-Z0-9_\"]+)(\\)$)");
    /**
     *
     */
    @Id
    @Column(name = "gamemodelid")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    /**
     *
     */
    @NotNull
    //@XmlID
    //@Pattern(regexp = "^\\w+$")
    private String name;
    /**
     *
     */
    @OneToMany(mappedBy = "gameModel", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @XmlTransient
    private List<VariableDescriptor> variableDescriptors;
    /**
     * A list of Variable Descriptors that are at the root level of the
     * hierarchy (other VariableDescriptor can be placed inside of a
     * ListDescriptor's items List).
     */
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "rootgamemodel_id")
    //@JsonManagedReference
    private List<VariableDescriptor> childVariableDescriptors;
    /**
     *
     */
    @OneToMany(mappedBy = "gameModel", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<Game> games = new ArrayList<Game>();
    /**
     *
     */
    @OneToMany(mappedBy = "gameModel", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference("gamemodel-widget")
    private List<Widget> widgets;
    /**
     * Holds all the scripts contained in current game model.
     *
     * @FIXME the @Lob annotation has no effect on ElementCollection and
     * Postgresql
     *
     */
    //@Lob
    @ElementCollection(fetch = FetchType.LAZY)
    @Column(length = 10485760)
    // @Column(columnDefinition = "BLOB NOT NULL")
    private Map<String, String> scriptLibrary = new HashMap<>();
    /**
     * @fixme temporary solutions to store pages
     */
    private String cssUri;
    /**
     *
     * @fixme temporary solutions to store widgets
     */
    private String widgetsUri;

    /**
     *
     */
    public GameModel() {
    }

    /**
     *
     * @param name
     */
    public GameModel(String name) {
        this.name = name;
    }

    /**
     *
     * @param force
     */
    public void propagateDefaultVariableInstance(boolean force) {
        for (VariableDescriptor vd : this.getVariableDescriptors()) {
            vd.propagateDefaultInstance(force);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
//    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public List<VariableDescriptor> getVariableDescriptors() {
        return variableDescriptors;
    }

    /**
     *
     * @param variableDescriptors
     */
    public void setVariableDescriptors(List<VariableDescriptor> variableDescriptors) {
        this.variableDescriptors = variableDescriptors;
    }

    /**
     *
     * @return a list of Variable Descriptors that are at the root level of the
     * hierarchy (other VariableDescriptor can be placed inside of a
     * ListDescriptor's items List)
     */
    //@JsonManagedReference
    public List<VariableDescriptor> getChildVariableDescriptors() {
        return childVariableDescriptors;
    }

    /**
     *
     * @param variableDescriptors
     */
    //@JsonManagedReference
    public void setChildVariableDescriptors(List<VariableDescriptor> variableDescriptors) {
        this.childVariableDescriptors = variableDescriptors;
        this.variableDescriptors = variableDescriptors;
        for (VariableDescriptor vd : variableDescriptors) {
            vd.setGameModel(this);
        }
    }

    /**
     *
     * @param variableDescriptor
     */
    public void addVariableDescriptor(VariableDescriptor variableDescriptor) {
        this.childVariableDescriptors.add(variableDescriptor);
        this.variableDescriptors.add(variableDescriptor);
        variableDescriptor.setGameModel(this);
    }

    /**
     * @return the games
     */
    @JsonManagedReference
    @XmlTransient
    public List<Game> getGames() {
        return games;
    }

    /**
     * @param games the games to set
     */
    @JsonManagedReference
    public void setGames(List<Game> games) {
        this.games = games;
    }

    /**
     *
     * @param game
     */
    public void addGame(Game game) {
        this.games.add(game);
        game.setGameModel(this);
    }

    /**
     * @return the widgets
     */
    @JsonManagedReference("gamemodel-widget")
    @XmlTransient
    public List<Widget> getWidgets() {
        return widgets;
    }

    /**
     * @param widgets the widgets to set
     */
    @JsonManagedReference("gamemodel-widget")
    public void setWidgets(List<Widget> widgets) {
        this.widgets = widgets;
    }

    /**
     * @return the cssUri
     */
    public String getCssUri() {
        return cssUri;
    }

    /**
     * @param cssUri the cssUri to set
     */
    public void setCssUri(String cssUri) {
        this.cssUri = cssUri;
    }

    /**
     * @return the widgetsUri
     */
    public String getWidgetsUri() {
        return widgetsUri;
    }

    /**
     * @param widgetsUri the widgetsUri to set
     */
    public void setWidgetsUri(String widgetsUri) {
        this.widgetsUri = widgetsUri;
    }

    /**
     * @return the scriptLibrary
     */
    public Map<String, String> getScriptLibrary() {
        return scriptLibrary;
    }

    /**
     * @param scriptLibrary the scriptLibrary to set
     */
    public void setScriptLibrary(Map<String, String> scriptLibrary) {
        this.scriptLibrary = scriptLibrary;
    }
}