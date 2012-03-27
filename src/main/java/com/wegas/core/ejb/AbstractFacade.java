/*
 * Wegas.
 * http://www.albasim.com/wegas/
 *
 * School of Business and Engineering Vaud, http://www.heig-vd.ch/
 * Media Engineering :: Information Technology Managment :: Comem
 *
 * Copyright (C) 2011
 */
package com.wegas.core.ejb;

import com.wegas.core.persistence.AbstractEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @param <T>
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
public abstract class AbstractFacade<T extends AbstractEntity> {

    /**
     *
     */
    final Class<T> entityClass;

    /**
     *
     * @param entityClass
     */
    public AbstractFacade(final Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     *
     * @return
     */
    protected abstract EntityManager getEntityManager();

    /**
     *
     */
    public void flush() {
        getEntityManager().flush();
    }

    /**
     *
     * @param entity
     */
    public void create(T entity) {
        getEntityManager().persist(entity);
        getEntityManager().flush();
    }

    /**
     *
     * @param entity
     */
    public void edit(final T entity) {
        getEntityManager().merge(entity);
    }

    /**
     *
     * @param entityId
     * @param entity
     * @return
     */
    public T update(final Long entityId, final T entity) {
        T oldEntity = this.find(entityId);
        oldEntity.merge(entity);
        return oldEntity;
    }

    /**
     *
     * @param entity
     */
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /**
     *
     * @param id
     * @return
     */
    public T find(final Object id) {
        return getEntityManager().find(entityClass, id);
    }

    /**
     *
     * @return
     */
    public List<T> findAll() {
        CriteriaQuery cq =
                getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     *
     * @param range
     * @return
     */
    public List<T> findRange(int[] range) {
        CriteriaQuery cq =
                getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    /**
     *
     * @return
     */
    public int count() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ( (Long) q.getSingleResult() ).intValue();
    }
}
