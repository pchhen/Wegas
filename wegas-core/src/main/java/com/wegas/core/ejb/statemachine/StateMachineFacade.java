/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.ejb.statemachine;

import com.wegas.core.ejb.*;
import com.wegas.core.event.internal.PlayerAction;
import com.wegas.core.event.internal.ResetEvent;
import com.wegas.core.exception.client.WegasErrorMessage;
import com.wegas.core.exception.client.WegasRuntimeException;
import com.wegas.core.exception.client.WegasScriptException;
import com.wegas.core.exception.internal.NoPlayerException;
import com.wegas.core.persistence.AbstractEntity;
import com.wegas.core.persistence.game.Player;
import com.wegas.core.persistence.game.Script;
import com.wegas.core.persistence.variable.VariableDescriptor;
import com.wegas.core.persistence.variable.VariableInstance;
import com.wegas.core.persistence.variable.statemachine.*;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run state machines.
 *
 * @author Cyril Junod <cyril.junod at gmail.com>
 */
@Stateless
@LocalBean
public class StateMachineFacade {

    static final private org.slf4j.Logger logger = LoggerFactory.getLogger(StateMachineFacade.class);

    /**
     * Event parameter will be passed in a function with named parameter
     * {@value #EVENT_PARAMETER_NAME}
     */
    static final private String EVENT_PARAMETER_NAME = "param";

    @EJB
    private VariableDescriptorFacade variableDescriptorFacade;

    @EJB
    private VariableInstanceFacade variableInstanceFacade;

    @EJB
    private ScriptFacade scriptManager;

    @Inject
    private RequestManager requestManager;

    @Inject
    private ScriptEventFacade scriptEvent;

    /**
     * Manage internal event transition.
     */
    private InternalStateMachineEventCounter stateMachineEventsCounter;

    /**
     *
     */
    public StateMachineFacade() {
    }

    /**
     * @param playerAction
     * @throws com.wegas.core.exception.internal.NoPlayerException
     */
    public void playerActionListener(@Observes PlayerAction playerAction) throws NoPlayerException, WegasScriptException {
        logger.debug("Received PlayerAction event");
        Player player = playerAction.getPlayer();
        if (player == null) {
            for (Entry<String, List<AbstractEntity>> entry : requestManager.getUpdatedEntities().entrySet()) {
                for (AbstractEntity entity : entry.getValue()) {
                    if (entity instanceof VariableInstance) {
                        player = variableInstanceFacade.findAPlayer((VariableInstance) entity);
                        break;
                    }
                }
                if (player != null) {
                    break;
                }
            }
            if (player == null) {
                throw new NoPlayerException("StateMachine Facade: NO PLAYER");
            }
        }
        this.runForPlayer(player);
    }

    /**
     * @param resetEvent
     */
    public void resetEventListener(@Observes ResetEvent resetEvent) throws WegasScriptException {
        logger.debug("Received Reset event");
        //System.out.println("ResetEvent");
        for (Player player : resetEvent.getConcernedPlayers()) {
            this.runForPlayer(player);
        }
    }

    private void runForPlayer(Player player) throws WegasScriptException {
        List<StateMachineDescriptor> statemachines = variableDescriptorFacade.findByClass(player.getGameModel(), StateMachineDescriptor.class);
        List<Transition> passed = new ArrayList<>();
        stateMachineEventsCounter = new InternalStateMachineEventCounter();
        Integer steps = this.doSteps(player, passed, statemachines, 0);
        logger.info("#steps[" + steps + "] - Player {} triggered transition(s):{}", player.getName(), passed);
    }

    private Integer doSteps(Player player, List<Transition> passedTransitions, List<StateMachineDescriptor> stateMachineDescriptors, Integer steps) throws WegasScriptException {

        List<Script> impacts = new ArrayList<>();
        List<Script> preImpacts = new ArrayList<>();
        List<Transition> transitions;
        StateMachineInstance smi;
        Boolean validTransition;
        Boolean transitionPassed = false;

        for (VariableDescriptor sm : stateMachineDescriptors) {
            validTransition = false;
            smi = (StateMachineInstance) sm.getInstance(player);
            if (!smi.getEnabled() || smi.getCurrentState() == null) { // a state may not be defined : remove statemachine's state when a player is inside that state
                continue;
            }
            transitions = smi.getCurrentState().getTransitions();
            for (Transition transition : transitions) {
                if (validTransition) {
                    break; // already have a valid transition
                }
                if (transition instanceof DialogueTransition
                        && ((DialogueTransition) transition).getActionText() != null
                        && !((DialogueTransition) transition).getActionText().isEmpty()) {                 // Dialogue, don't eval if not null or empty
                    continue;
                } else if (this.isNotDefined(transition.getTriggerCondition())) {
                    validTransition = true;
                } else if (transition.getTriggerCondition().getContent().contains("Event.fired")) { //TODO: better way to find out which are event transition.
                    if (passedTransitions.contains(transition)) {
                        /*
                         * Loop prevention : that player already passed through
                         * this transiton
                         */
                        logger.debug("Loop detected, already marked {} IN {}", transition, passedTransitions);
                    } else {
                        try {
                            if (this.eventTransition(player, transition, smi)) {
                                passedTransitions.add(transition);
                                transitionPassed = true;
                                smi.transitionHistoryAdd(transition.getId());
                            }
                        } catch (WegasScriptException ex) {
                            ex.setScript("Variable " + sm.getLabel());
                            requestManager.addException(ex);
                            //validTransition still false
                        }
                    }

                } else {
                    try {
                        validTransition = (Boolean) scriptManager.eval(player, transition.getTriggerCondition());
                    } catch (WegasScriptException ex) {
                        ex.setScript("Variable " + sm.getLabel());
                        requestManager.addException(ex);
                        //validTransition still false
                    }
                }
                if (validTransition == null) {
                    throw WegasErrorMessage.error("Please review condition [" + sm.getLabel() + "]:\n"
                            + transition.getTriggerCondition().getContent());
                } else if (validTransition) {
                    if (passedTransitions.contains(transition)) {
                        /*
                         * Loop prevention : that player already passed through
                         * this transiton
                         */
                        logger.debug("Loop detected, already marked {} IN {}", transition, passedTransitions);
                    } else {
                        passedTransitions.add(transition);
                        smi.setCurrentStateId(transition.getNextStateId());
                        preImpacts.add(transition.getPreStateImpact());
                        impacts.add(smi.getCurrentState().getOnEnterEvent());
                        smi.transitionHistoryAdd(transition.getId());
                        transitionPassed = true;
                        if (sm instanceof TriggerDescriptor) {
                            TriggerDescriptor td = (TriggerDescriptor) sm;
                            if (td.isDisableSelf()) {
                                smi.setEnabled(false);
                            }
                        }
                    }
                }
            }
        }
        if (transitionPassed) {
            /*@DIRTY, @TODO : find something else : Running scripts overrides previous state change Only for first Player (resetEvent). */
            variableDescriptorFacade.findByClass(player.getGameModel(), StateMachineDescriptor.class);
            preImpacts.addAll(impacts);
            try {
                scriptManager.eval(player, preImpacts);
            } catch (WegasScriptException ex) {
                ex.setScript("StateMachines impacts");
                requestManager.addException(ex);
                logger.warn("Script failed ", ex);
            }
            steps++;
            steps = this.doSteps(player, passedTransitions, stateMachineDescriptors, steps);
        }
        return steps;

    }

    /**
     * Manage event transition
     *
     * @param player     current {@link Player}
     * @param transition the event transition
     * @param smi        current {@link StateMachineInstance} passing transition
     * @return
     */
    private Boolean eventTransition(Player player, Transition transition, StateMachineInstance smi) throws WegasScriptException {
        String script = transition.getTriggerCondition().getContent();
        Pattern pattern = Pattern.compile("Event\\.fired\\([ ]*(['\"])(.+?)\\1[ ]*\\)");
        Matcher matcher = pattern.matcher(script);
        String event;
        if (matcher.find()) {
            event = matcher.group(2);
        } else {
            return false;
        }
        Object[] firedParams = scriptEvent.getFiredParameters(event);
        /* events and more ? */
        if (!(Boolean) scriptManager.eval(player, transition.getTriggerCondition())) {
            return false;
        }
        final Integer instanceEventCount = stateMachineEventsCounter.count(smi, event);
        if (firedParams.length > instanceEventCount) {
            smi.setCurrentStateId(transition.getNextStateId());
            stateMachineEventsCounter.increase(smi, event);
            if (!this.isNotDefined(transition.getPreStateImpact())) {
                this.evalEventImpact(player, transition.getPreStateImpact(), firedParams[instanceEventCount]);
            }
            if (!this.isNotDefined(smi.getCurrentState().getOnEnterEvent())) {
                this.evalEventImpact(player, smi.getCurrentState().getOnEnterEvent(), firedParams[instanceEventCount]);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Run given script with given parameter. Script may use named parameter
     * {@value #EVENT_PARAMETER_NAME} to access passed parameter.
     *
     * @param script the script to run
     * @param param  the parameter to pass to the script
     * @see #EVENT_PARAMETER_NAME
     */
    private void evalEventImpact(Player player, final Script script, final Object param) throws WegasRuntimeException {
        //try {
        final Object impactFunc;
        if (script.getLanguage().toLowerCase().equals("javascript")) {
            impactFunc = scriptManager.eval(new Script(script.getLanguage(),
                    String.format("function(%s){%s}", EVENT_PARAMETER_NAME, script.getContent()) // A JavaScript Function. should check for engine type
            ));
        } else {
            return; // define other language here
        }
        try {
            if (param instanceof ScriptEventFacade.EmptyObject) {
                ((Invocable) requestManager.getCurrentEngine()).invokeMethod(impactFunc, "call", impactFunc);
            } else {
                ((Invocable) requestManager.getCurrentEngine()).invokeMethod(impactFunc, "call", impactFunc, param);
            }
        } catch (ScriptException ex) {
            throw new WegasScriptException(ex.getFileName(), ex.getLineNumber(), ex.getMessage());
        } catch (NoSuchMethodException ex) {
            logger.debug("Event transition script failed", ex);
        } catch (WegasRuntimeException ex) { // throw our exception as-is
            throw ex;
        } catch (RuntimeException ex) {
            throw new WegasScriptException(ex.getMessage());
        }
    }

    /**
     * Test if a script is not defined, ie empty or null
     *
     * @param script to test
     * @return
     */
    private Boolean isNotDefined(Script script) {
        return script == null || script.getContent() == null
                || script.getContent().equals("");
    }

    /**
     * Used to store Events during run. Prevent passing multiple event
     * transitions with same event if less events where thrown.
     * StateMachineInstance dependant.
     */
    private static class InternalStateMachineEventCounter {

        private final Map<StateMachineInstance, Map<String, Integer>> smEvents;

        private InternalStateMachineEventCounter() {
            this.smEvents = new HashMap<>();
        }

        private Integer count(StateMachineInstance instance, String event) {
            if (!smEvents.containsKey(instance)) {
                smEvents.put(instance, new HashMap<>());
            }
            if (smEvents.get(instance).containsKey(event)) {
                return smEvents.get(instance).get(event);
            } else {
                return 0;
            }
        }

        private void increase(StateMachineInstance instance, String event) {
            if (!smEvents.containsKey(instance)) {
                smEvents.put(instance, new HashMap<>());
            }
            smEvents.get(instance).put(event, this.count(instance, event) + 1);
        }

    }
}
