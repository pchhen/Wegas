/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.persistence.variable.statemachine;

import com.wegas.core.Helper;
import com.wegas.core.persistence.AbstractEntity;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 *
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
@Entity
public class DialogueTransition extends Transition {

    private static final long serialVersionUID = 1L;
    @Lob
    private String actionText;

    @Override
    public Boolean containsAll(List<String> criterias) {
        if (Helper.insensitiveContainsAll(this.getActionText(), criterias)) {
            return true;
        }
        return super.containsAll(criterias);
    }

    /**
     * @return the actionText
     */
    public String getActionText() {
        return actionText;
    }

    /**
     * @param actionText the actionText to set
     */
    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    @Override
    public void merge(AbstractEntity other) {
        DialogueTransition otherDialogue = (DialogueTransition) other;
        this.actionText = otherDialogue.actionText;
        super.merge(other);
    }
}
