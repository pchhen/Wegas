/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.ejb;

import com.wegas.core.Helper;
import com.wegas.core.event.client.ClientEvent;
import com.wegas.core.event.client.CustomEvent;
import com.wegas.core.event.client.ExceptionEvent;
import com.wegas.core.exception.client.WegasRuntimeException;
import com.wegas.core.persistence.AbstractEntity;
import com.wegas.core.persistence.game.GameModel;
import com.wegas.core.persistence.game.Player;
import com.wegas.core.persistence.variable.VariableInstance;
import com.wegas.core.persistence.variable.primitive.NumberInstance;
import com.wegas.core.rest.util.Views;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.ScriptObject;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
@Named("RequestManager")
@RequestScoped
public class RequestManager {

    private static Logger logger = LoggerFactory.getLogger(RequestManager.class);

    @Inject
    private Event<NumberUpdate> updatedNumber;

    /**
     *
     */
    private Class view = Views.Public.class;

    /**
     *
     */
    private Player currentPlayer;

    /**
     * Contains all updated entities
     */
    private Map<String, List<AbstractEntity>> updatedEntities = new HashMap<>();

    private Map<String, List<AbstractEntity>> outdatedEntities = new HashMap<>();

    private Map<String, List<AbstractEntity>> destroyedEntities = new HashMap<>();

    /**
     *
     */
    private List<ClientEvent> events = new ArrayList<>();

    /**
     *
     */
    private Locale locale;

    /**
     *
     */
    private ScriptEngine currentEngine = null;

    public void addUpdatedEntities(Map<String, List<AbstractEntity>> entities) {
        this.addEntities(entities, updatedEntities);
    }

    public void addOutofdateEntities(Map<String, List<AbstractEntity>> entities) {
        this.addEntities(entities, outdatedEntities);
    }

    public void addDestroyedEntities(Map<String, List<AbstractEntity>> entities) {
        this.addEntities(entities, destroyedEntities);
    }

    public void addEntities(Map<String, List<AbstractEntity>> entities, Map<String, List<AbstractEntity>> container) {
        if (entities != null) {
            for (String audience : entities.keySet()) {
                this.addEntity(audience, entities.get(audience), container);
            }
        }
    }

    public void addEntity(String audience, List<AbstractEntity> updated, Map<String, List<AbstractEntity>> container) {
        for (AbstractEntity entity : updated) {
            this.addEntity(audience, entity, container);
        }
    }

    public void addEntity(String audience, AbstractEntity updated, Map<String, List<AbstractEntity>> container) {
        if (!container.containsKey(audience)) {
            container.put(audience, new ArrayList<>());
        }
        List<AbstractEntity> entities = container.get(audience);
        if (!entities.contains(updated)) {
            entities.add(updated);
        }
    }

    /**
     * https://java.net/jira/browse/GLASSFISH-21195 this event should be fired
     * from {@link com.wegas.core.persistence.NumberListener}
     *
     * @param numberInstance to be forwarded to event
     */
    public void numberChanged(NumberInstance numberInstance) { // @TODO remove me
        updatedNumber.fire(new NumberUpdate(this.getPlayer(), numberInstance));
    }

    /**
     * @return the currentPlayer
     */
    public Player getPlayer() {
        return currentPlayer;
    }

    /**
     * @param currentPlayer the currentPlayer to set
     */
    public void setPlayer(Player currentPlayer) {
        if (this.currentPlayer == null || !this.currentPlayer.equals(currentPlayer)) {
            this.setCurrentEngine(null);
        }
        this.currentPlayer = currentPlayer;
    }

    /**
     * @return
     */
    public GameModel getCurrentGameModel() {
        return this.getPlayer().getGameModel();
    }

    /**
     * @return the currentEngine
     */
    public ScriptEngine getCurrentEngine() {
        return currentEngine;
    }

    /**
     * @param currentEngine the currentEngine to set
     */
    public void setCurrentEngine(ScriptEngine currentEngine) {
        this.currentEngine = currentEngine;
    }

    /**
     *
     */
    public void clearUpdatedEntities() {
        this.updatedEntities.clear();
    }

    /**
     *
     * @return
     */
    public Map<String, List<AbstractEntity>> getUpdatedEntities() {
        return updatedEntities;
    }

    /**
     *
     */
    public void clearDestroyedEntities() {
        this.destroyedEntities.clear();
    }

    public Map<String, List<AbstractEntity>> getDestroyedEntities() {
        return destroyedEntities;
    }

    public void clearOutdatedEntities() {
        this.outdatedEntities.clear();
    }

    public Map<String, List<AbstractEntity>> getOutdatedEntities() {
        return outdatedEntities;
    }

    /**
     * @return
     */
    public List<ClientEvent> getClientEvents() {
        return events;
    }

    /**
     * @param event
     */
    public void addEvent(ClientEvent event) {
        this.events.add(event);
    }

    /**
     * @param e
     */
    public void addException(WegasRuntimeException e) {
        ArrayList<WegasRuntimeException> exceptions = new ArrayList();
        exceptions.add(e);
        this.addEvent(new ExceptionEvent(exceptions));
    }

    /**
     * Method used to send custom events
     *
     * @param type    event name
     * @param payload object associated with that event
     */
    public void sendCustomEvent(String type, Object payload) {
        // @hack check payload type against "jdk.nashorn.internal"
        if (payload.getClass().getName().startsWith("jdk.nashorn.internal")) {
            this.addEvent(new CustomEvent(type, ScriptUtils.wrap((ScriptObject) payload)));
        } else {
            this.addEvent(new CustomEvent(type, payload));
        }
    }

    /**
     * @return the view
     */
    public Class getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(Class view) {
        this.view = view;
    }

    /**
     * @param bundle
     * @return the ResourceBundle
     */
    public ResourceBundle getBundle(String bundle) {
        return ResourceBundle.getBundle(bundle, this.locale);
    }

    /**
     * @return the local
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param local the local to set
     */
    public void setLocale(Locale local) {
        this.locale = local;
    }

    public class NumberUpdate {

        final public Player player;

        final public NumberInstance number;

        NumberUpdate(Player player, NumberInstance number) {
            this.number = number;
            this.player = player;
        }
    }
}
