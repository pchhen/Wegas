/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wegas.core.persistence.variable.statemachine;

import com.wegas.core.persistence.AbstractEntity;
import com.wegas.core.persistence.variable.VariableDescriptorEntity;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlType;
import org.codehaus.jackson.annotate.JsonSubTypes;

/**
 *
 * @author Cyril Junod <cyril.junod at gmail.com>
 */
@Entity
@Table(name = "FSMDescriptor")
@XmlType(name = "FSMDescriptor")
@JsonSubTypes(value = {
    @JsonSubTypes.Type(name = "TriggerDescriptor", value = TriggerDescriptorEntity.class)
})
public class StateMachineDescriptorEntity extends VariableDescriptorEntity<StateMachineInstanceEntity> {

    private Long initialStateId;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
    //@MapKey(name = "id")
    @JoinColumn(name = "statemachine_id", referencedColumnName = "variabledescriptor_id")
    @MapKeyColumn(name="fsm_statekey")
    private Map<Long, State> states = new HashMap<>();

    public StateMachineDescriptorEntity() {
    }

    public Long getInitialStateId() {
        return initialStateId;
    }

    public void setInitialStateId(Long initialStateId) {
        this.initialStateId = initialStateId;
    }

    public Map<Long, State> getStates() {
        return states;
    }

    public void setStates(HashMap<Long, State> states) {
        this.states = states;
    }

    @Override
    public String toString() {
        return "StateMachineDescriptorEntity{id=" + this.getId() + ", initialStateId=" + initialStateId + ", states=" + states + '}';
    }

    @Override
    public void merge(AbstractEntity a) {
        //TODO: MAP initialState to State
        StateMachineDescriptorEntity smDescriptor = (StateMachineDescriptorEntity) a;
        this.setStates((HashMap<Long, State>)smDescriptor.getStates());
        this.initialStateId = smDescriptor.initialStateId;
        super.merge(smDescriptor);
    }

}
