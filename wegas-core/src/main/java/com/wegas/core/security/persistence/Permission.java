/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.security.persistence;

import com.wegas.core.persistence.AbstractEntity;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
@Entity
public class Permission extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @Basic
    @Column(name = "permissions")
    private String value;
    /**
     *
     */
    @Basic
    private String inducedPermission;
    /**
     *
     */
    @Id
    @GeneratedValue
    private Long id;
    /**
     *
     */
    @ManyToOne
    @JsonIgnore
    private AbstractAccount account;
    /**
     *
     */
    @ManyToOne
    @JsonIgnore
    private Role role;

    /**
     *
     */
    public Permission() {
    }

    /**
     *
     * @param value
     */
    public Permission(String value) {
        this.value = value;
    }

    /**
     *
     * @param value
     * @param inducedPermission
     */
    public Permission(String value, String inducedPermission) {
        this.value = value;
        this.inducedPermission = inducedPermission;
    }

    @Override
    public void merge(AbstractEntity other) {
        Permission o = (Permission) other;
        this.setValue(o.getValue());
        this.setInducedPermission(o.getInducedPermission());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        } else {
            return (obj != null
                    && obj instanceof Permission
                    && this.value.equals(((Permission) obj).getValue()));
        }

    }

    /**
     * @return the inducedPermission
     */
    public String getInducedPermission() {
        return inducedPermission;
    }

    /**
     * @param inducedPermission the inducedPermission to set
     */
    public void setInducedPermission(String inducedPermission) {
        this.inducedPermission = inducedPermission;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Permission(" + this.value + ")";
    }

    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the account
     */
    public AbstractAccount getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(AbstractAccount account) {
        this.account = account;
    }

    /**
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }
}
