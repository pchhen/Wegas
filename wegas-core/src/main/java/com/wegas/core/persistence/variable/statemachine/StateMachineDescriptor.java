/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.persistence.variable.statemachine;

import com.wegas.core.persistence.AbstractEntity;
import com.wegas.core.persistence.game.Player;
import com.wegas.core.persistence.variable.VariableDescriptor;
import com.wegas.core.rest.util.Views;
import java.util.*;
import java.util.Map.Entry;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import com.wegas.core.exception.client.WegasErrorMessage;
import com.wegas.core.persistence.game.Script;
import com.wegas.core.persistence.variable.Scripted;

/**
 *
 * @author Cyril Junod <cyril.junod at gmail.com>
 */
@Entity
@Table(name = "FSMDescriptor")
@JsonTypeName(value = "FSMDescriptor")
@JsonSubTypes(value = {
    @JsonSubTypes.Type(name = "TriggerDescriptor", value = TriggerDescriptor.class),
    @JsonSubTypes.Type(name = "DialogueDescriptor", value = DialogueDescriptor.class)
})
public class StateMachineDescriptor extends VariableDescriptor<StateMachineInstance> implements Scripted {

    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "statemachine_id", referencedColumnName = "variabledescriptor_id")
    @MapKeyColumn(name = "fsm_statekey")
    @JsonView(Views.ExtendedI.class)
    private Map<Long, State> states = new HashMap<>();

    /**
     *
     */
    public StateMachineDescriptor() {
    }

    @Override
    public List<Script> getScripts() {
        List<Script> ret = new ArrayList<>();
        for (State state: this.getStates().values()){
             ret.addAll(state.getScripts());
        }
        return ret;
    }

    /**
     *
     * @return
     */
    public Map<Long, State> getStates() {
        return states;
    }

    /**
     *
     * @param states
     */
    public void setStates(HashMap<Long, State> states) {
        this.states = states;
    }

    @Override
    public String toString() {
        return "StateMachineDescriptor{id=" + this.getId() + ", states=" + states + '}';
    }

    @Override
    public void merge(AbstractEntity a) {
        if (a instanceof StateMachineDescriptor) {
            StateMachineDescriptor smDescriptor = (StateMachineDescriptor) a;
            this.mergeStates((HashMap<Long, State>) smDescriptor.getStates());
            super.merge(smDescriptor);
        } else {
            throw WegasErrorMessage.error("Incompatible type");
        }
    }

    /*
     * script methods
     */
    /**
     *
     * @param p
     */
    public void enable(Player p) {
        this.getInstance(p).setEnabled(Boolean.TRUE);
    }

    /**
     *
     * @param p
     */
    public void disable(Player p) {
        this.getInstance(p).setEnabled(Boolean.FALSE);
    }

    /**
     *
     * @param p
     *
     * @return
     */
    public boolean isEnabled(Player p) {
        return this.getInstance(p).getEnabled();
    }

    /**
     *
     * @param p
     *
     * @return
     */
    public boolean isDisabled(Player p) {
        return !this.getInstance(p).getEnabled();
    }

    private void mergeStates(HashMap<Long, State> newStates) {
        for (Iterator<Entry<Long, State>> it = this.states.entrySet().iterator(); it.hasNext();) {
            Entry<Long, State> oldState = it.next();
            Long oldKeys = oldState.getKey();
            if (newStates.get(oldKeys) == null) {
                it.remove();
            } else {
                oldState.getValue().merge(newStates.get(oldKeys));
            }
        }
        for (Iterator<Entry<Long, State>> it = newStates.entrySet().iterator(); it.hasNext();) {
            Entry<Long, State> newState = it.next();
            Long newKey = newState.getKey();
            if (this.states.get(newKey) == null) {
                this.states.put(newKey, newState.getValue());
            }
        }
    }

    @Override
    public Boolean containsAll(List<String> criterias) {
        if (super.containsAll(criterias)) {
            return true;
        }
        for (State s : this.getStates().values()) {
            if (s.containsAll(criterias)) {
                return true;
            }
        }
        return false;
    }
}
