/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.ejb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegas.core.AlphanumericComparator;
import com.wegas.core.Helper;
import com.wegas.core.event.internal.DescriptorRevivedEvent;
import com.wegas.core.exception.client.WegasErrorMessage;
import com.wegas.core.exception.internal.WegasNoResultException;
import com.wegas.core.persistence.game.GameModel;
import com.wegas.core.persistence.variable.DescriptorListI;
import com.wegas.core.persistence.variable.ListDescriptor;
import com.wegas.core.persistence.variable.VariableDescriptor;
import com.wegas.core.rest.util.JacksonMapperProvider;
import com.wegas.core.rest.util.Views;
import com.wegas.core.security.persistence.User;
import com.wegas.mcq.persistence.ChoiceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
@Stateless
@LocalBean
public class VariableDescriptorFacade extends BaseFacade<VariableDescriptor> {

    private static final Logger logger = LoggerFactory.getLogger(VariableDescriptorFacade.class);

    /**
     *
     */
    @EJB
    private GameModelFacade gameModelFacade;

    /**
     *
     */
    @Inject
    private Event<DescriptorRevivedEvent> descriptorRevivedEvent;

    /**
     *
     */
    public VariableDescriptorFacade() {
        super(VariableDescriptor.class);
    }

    /**
     * @param variableDescriptor
     */
    @Override
    public void create(final VariableDescriptor variableDescriptor) {
        throw WegasErrorMessage.error("Unable to call create on Variable descriptor. Use create(gameModelId, variableDescriptor) instead.");
    }

    /**
     * @return
     */
    @Override
    public VariableDescriptor update(final Long entityId, final VariableDescriptor entity) {
        final VariableDescriptor vd = this.find(entityId);
        entity.setGameModel(vd.getGameModel());
        this.revive(entity);
        vd.merge(entity);
        return vd;
    }

    /**
     * @param gameModel
     * @param list
     * @param entity
     * @return
     */
    public DescriptorListI createChild(final GameModel gameModel, final DescriptorListI list, final VariableDescriptor entity) {
        List<String> usedNames = this.findDistinctNames(gameModel);
        List<String> usedLabels = this.findDistinctLabels(gameModel);

        list.addItem(entity);

        boolean hasName = !Helper.isNullOrEmpty(entity.getName());
        boolean hasLabel = !Helper.isNullOrEmpty(entity.getLabel());

        if (hasName && !hasLabel) {
            entity.setLabel(entity.getName());
        } else if (hasLabel && !hasName) {
            entity.setName(entity.getLabel());
        }


        Helper.setUniqueName(entity, usedNames);
        Helper.setUniqueLabel(entity, usedLabels);

        this.revive(entity);
        return list;
    }

    /**
     * @param entity
     */
    public void revive(VariableDescriptor entity) {

        descriptorRevivedEvent.fire(new DescriptorRevivedEvent(entity));

        if (entity instanceof DescriptorListI) {
            this.reviveItems((DescriptorListI) entity);
        }
    }

    /**
     * @param entity
     */
    public void reviveItems(DescriptorListI entity) {
        for (Object vd : ((DescriptorListI) entity).getItems()) {
            this.revive((VariableDescriptor) vd);
        }
    }

    /**
     * @param variableDescriptorId
     * @param entity
     * @return
     */
    public DescriptorListI createChild(final Long variableDescriptorId, final VariableDescriptor entity) {
        VariableDescriptor find = this.find(variableDescriptorId);
        return this.createChild(find.getGameModel(), (DescriptorListI) find, entity);
    }

    /**
     * @param gameModelId
     * @param variableDescriptor
     */
    public void create(final Long gameModelId, final VariableDescriptor variableDescriptor) {
        GameModel find = this.gameModelFacade.find(gameModelId);
        this.createChild(find, find, variableDescriptor);
    }

    /**
     * @param entityId
     * @return
     * @throws IOException
     */
    @Override
    public VariableDescriptor duplicate(final Long entityId) throws IOException {

        final VariableDescriptor oldEntity = this.find(entityId);               // Retrieve the entity to duplicate

        final ObjectMapper mapper = JacksonMapperProvider.getMapper();          // Retrieve a jackson mapper instance
        final String serialized = mapper.writerWithView(Views.Export.class).
                writeValueAsString(oldEntity);                                  // Serialize the entity
        final VariableDescriptor newEntity
                = mapper.readValue(serialized, oldEntity.getClass());           // and deserialize it

        final DescriptorListI list = this.findParentList(oldEntity);
        this.createChild(oldEntity.getGameModel(), list, newEntity);
        return newEntity;
    }

    /**
     * @param vd
     * @return
     */
    public DescriptorListI findParentList(VariableDescriptor vd) throws NoResultException {
        if (vd instanceof ChoiceDescriptor) {                                   // QuestionDescriptor descriptor case
            return ((ChoiceDescriptor) vd).getQuestion();
        } else {
            try {
                return this.findParentListDescriptor(vd);                       // ListDescriptor case
            } catch (WegasNoResultException e) {                                     // Descriptor is at root level
                return vd.getGameModel();
            }
        }
    }

    /**
     * @param item
     * @return
     * @throws com.wegas.core.exception.internal.WegasNoResultException
     */
    public ListDescriptor findParentListDescriptor(final VariableDescriptor item) throws WegasNoResultException {
        Query findListDescriptorByChildId = getEntityManager().createNamedQuery("findListDescriptorByChildId");
        findListDescriptorByChildId.setParameter("itemId", item.getId());
        try {
            return (ListDescriptor) findListDescriptorByChildId.getSingleResult();
        } catch (NoResultException ex) {
            throw new WegasNoResultException(ex);
        }
    }

    /**
     * @param gameModel
     * @param name
     * @return
     * @throws com.wegas.core.exception.internal.WegasNoResultException
     */
    public VariableDescriptor find(final GameModel gameModel, final String name) throws WegasNoResultException {
        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        final CriteriaQuery cq = cb.createQuery();
        final Root<User> variableDescriptor = cq.from(VariableDescriptor.class);
        cq.where(cb.and(
                cb.equal(variableDescriptor.get("gameModel"), gameModel),
                cb.equal(variableDescriptor.get("name"), name)));
        final Query q = getEntityManager().createQuery(cq);
        try {
            return (VariableDescriptor) q.getSingleResult();
        } catch (NoResultException ex) {
            throw new WegasNoResultException(ex);
        }
    }

    /**
     * @param gameModel
     * @return
     */
    public List<String> findDistinctNames(final GameModel gameModel) {
        Query distinctNames = getEntityManager().createQuery("SELECT DISTINCT(var.name) FROM VariableDescriptor var WHERE var.gameModel = :gameModel");
        distinctNames.setParameter("gameModel", gameModel);
        return distinctNames.getResultList();
    }

    /**
     * @param gameModel
     * @return
     */
    public List<String> findDistinctLabels(final GameModel gameModel) {
        Query distinctNames = getEntityManager().createQuery("SELECT DISTINCT(var.label) FROM VariableDescriptor var WHERE var.gameModel = :gameModel");
        distinctNames.setParameter("gameModel", gameModel);
        return distinctNames.getResultList();
    }

    /**
     * For backward compatibility, use find(final GameModel gameModel, final
     * String name) instead.
     *
     * @param gameModel
     * @param name
     * @return
     * @throws com.wegas.core.exception.internal.WegasNoResultException
     * @deprecated
     */
    public VariableDescriptor findByName(final GameModel gameModel, final String name) throws WegasNoResultException {
        return this.find(gameModel, name);
    }

    /**
     * @param gameModel
     * @param label
     * @return
     * @throws com.wegas.core.exception.internal.WegasNoResultException
     */
    public VariableDescriptor findByLabel(final GameModel gameModel, final String label) throws WegasNoResultException {
        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        final CriteriaQuery cq = cb.createQuery();
        final Root<User> variableDescriptor = cq.from(VariableDescriptor.class);
        cq.where(cb.and(
                cb.equal(variableDescriptor.get("gameModel"), gameModel),
                cb.equal(variableDescriptor.get("label"), label)));
        final Query q = getEntityManager().createQuery(cq);
        try {
            return (VariableDescriptor) q.getSingleResult();
        } catch (NoResultException ex) {
            throw new WegasNoResultException(ex);
        }
    }

    /**
     * @param gameModel
     * @param title
     * @return
     */
    public List<VariableDescriptor> findByTitle(final GameModel gameModel, final String title) {
        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        final CriteriaQuery cq = cb.createQuery();
        final Root<User> variableDescriptor = cq.from(VariableDescriptor.class);
        cq.where(cb.and(
                cb.equal(variableDescriptor.get("gameModel"), gameModel),
                cb.equal(variableDescriptor.get("title"), title)));
        final Query q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }

    /**
     * @param gameModelId
     * @return
     */
    public List<VariableDescriptor> findAll(final Long gameModelId) {
        TypedQuery<VariableDescriptor> findByRootGameModelId = getEntityManager().createNamedQuery("findVariableDescriptorsByRootGameModelId", VariableDescriptor.class);
        findByRootGameModelId.setParameter("gameModelId", gameModelId);
        return findByRootGameModelId.getResultList();
    }

    /**
     * @param gameModelId
     * @return
     */
    public List<VariableDescriptor> findByGameModelId(final Long gameModelId) {
        return gameModelFacade.find(gameModelId).getChildVariableDescriptors();
    }

    /**
     * @param <T>
     * @param gamemodel
     * @param variableDescriptorClass the filtering class
     * @return All specified classes and subclasses belonging to the game model.
     */
    public <T extends VariableDescriptor> List<T> findByClass(final GameModel gamemodel, final Class<T> variableDescriptorClass) {

        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        final CriteriaQuery cq = cb.createQuery();
        final Root<User> variableDescriptor = cq.from(variableDescriptorClass);
        cq.where(cb.equal(variableDescriptor.get("gameModel"), gamemodel));
        final Query q = getEntityManager().createQuery(cq);
        return q.getResultList();

        //Query findVariableDescriptorsByClass = getEntityManager().createQuery("SELECT DISTINCT variableDescriptor FROM " + variableDescriptorClass.getSimpleName() + " variableDescriptor LEFT JOIN variableDescriptor.gameModel AS gm WHERE gm.id =" + gameModelId, variableDescriptorClass);
        //return findVariableDescriptorsByClass.getResultList();
    }

    private void move(final Long descriptorId, final DescriptorListI targetListDescriptor, final int index) {
        final VariableDescriptor vd = this.find(descriptorId);                  // Remove from the previous list
        this.findParentList(vd).remove(vd);

        targetListDescriptor.addItem(index, vd);                                // Then add to the new one
    }

    /**
     * This method will move the target entity to the root level of the game
     * model at index i
     *
     * @param descriptorId
     * @param index
     */
    public void move(final Long descriptorId, final int index) {
        this.move(descriptorId, this.find(descriptorId).getGameModel(), index);
    }

    /**
     * @param descriptorId
     * @param targetListDescriptorId
     * @param index
     */
    public void move(final Long descriptorId, final Long targetListDescriptorId, final int index) {
        this.move(descriptorId, (DescriptorListI) this.find(targetListDescriptorId), index);
    }

    /**
     * Sort naturally items in ListDescriptor by label
     *
     * @param descriptorId ListDescriptor's id to sort
     * @return sorted VariableDescriptor
     */
    public VariableDescriptor sort(final Long descriptorId) {
        VariableDescriptor variableDescriptor = this.find(descriptorId);
        if (variableDescriptor instanceof ListDescriptor) {
            /*
             * Collection cannot be sorted directly, must pass through methods remove / add
             */
            ListDescriptor listDescriptor = (ListDescriptor) variableDescriptor;
            List<VariableDescriptor> list = new ArrayList<>(listDescriptor.getItems());
            final AlphanumericComparator<String> alphanumericComparator = new AlphanumericComparator<>();
            final Comparator<VariableDescriptor> comparator = new Comparator<VariableDescriptor>() {
                @Override
                public int compare(VariableDescriptor o1, VariableDescriptor o2) {
                    return alphanumericComparator.compare(o1.getLabel(), o2.getLabel());
                }
            };

            Collections.sort(list, comparator);

            for (VariableDescriptor vd : list) {
                listDescriptor.remove(vd);
                listDescriptor.addItem(vd);
            }
        }
        return variableDescriptor;
    }

    /**
     * @return
     */
    public static VariableDescriptorFacade lookup() {
        try {
            return Helper.lookupBy(VariableDescriptorFacade.class);
        } catch (NamingException ex) {
            logger.error("Error retrieving requestmanager", ex);
            return null;
        }
    }
}
