/*
 * Wegas
 * http://www.albasim.ch/wegas/
 *
 * Copyright (c) 2013 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.leaderway.ejb;

import com.wegas.core.ejb.VariableDescriptorFacade;
import com.wegas.core.ejb.VariableInstanceFacade;
import com.wegas.leaderway.persistence.Activity;
import com.wegas.leaderway.persistence.Assignment;
import com.wegas.leaderway.persistence.ResourceInstance;
import com.wegas.leaderway.persistence.TaskDescriptor;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
@Stateless
@LocalBean
public class ResourceFacade {

    /**
     *
     */
    @PersistenceContext(unitName = "wegasPU")
    private EntityManager em;

    /**
     *
     */
    public ResourceFacade() {
    }
    /**
     *
     */
    @EJB
    private VariableInstanceFacade variableInstanceFacade;
    /**
     *
     */
    @EJB
    private VariableDescriptorFacade variableDescriptorFacade;

    /**
     *
     * @param resourceInstance
     * @param taskInstance
     */
    public Assignment assign(ResourceInstance resourceInstance, TaskDescriptor taskDescriptor) {
        resourceInstance = (ResourceInstance) variableInstanceFacade.find(resourceInstance.getId());
        return resourceInstance.assign(taskDescriptor);
    }

    /**
     *
     * @param p
     * @param resourceDescriptorId
     * @param taskDescriptorId
     */
    public Assignment assign(Long resourceInstanceId, Long taskDescriptorId) {
        return this.assign((ResourceInstance) variableInstanceFacade.find(resourceInstanceId), (TaskDescriptor) variableDescriptorFacade.find(taskDescriptorId));
    }

    /**
     *
     * @param resourceInstance
     * @param taskInstance
     */
    public Activity assignActivity(ResourceInstance resourceInstance, TaskDescriptor taskDescriptor) {
        resourceInstance = (ResourceInstance) variableInstanceFacade.find(resourceInstance.getId());
        return resourceInstance.assignActivity(taskDescriptor);
    }

    /**
     *
     * @param resourceInstance
     * @param taskInstance
     */
    public Activity assignActivity(Long resourceInstanceId, Long taskDescriptorId) {
        return this.assignActivity((ResourceInstance) variableInstanceFacade.find(resourceInstanceId),
                (TaskDescriptor) variableDescriptorFacade.find(taskDescriptorId));
    }

    /**
     *
     * @param assignementId
     * @param index
     */
    public void moveAssignment(final Long assignementId, final int index) {
        final Assignment assignement = this.em.find(Assignment.class, assignementId);
        assignement.getResourceInstance().getAssignments().remove(assignement);
        assignement.getResourceInstance().getAssignments().add(index, assignement);
    }

//    public void test(WRequirement requirement, Long taskId, Long resId, Player player) {
//        TaskDescriptor td = this.em.find(TaskDescriptor.class, taskId);
//        ResourceDescriptor rd = this.em.find(ResourceDescriptor.class, resId);
//        td.getRequirements().add(requirement);
//
//        //assigne activity between resource to task and assigne Activity between requirement and resource
//        Activity activity = this.assignActivity(rd.getInstance(player).getId(), td.getInstance(player).getId());
//        activity.setWrequirement(requirement);
//    }
}
