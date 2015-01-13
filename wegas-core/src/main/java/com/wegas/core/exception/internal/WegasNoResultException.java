/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.exception.internal;


/**
 *
 * @author Maxence Laurent <maxence.laurent at gmail.com>
 */
public class WegasNoResultException extends WegasInternalException {

    /**
     *
     */
    public WegasNoResultException() {
        super();
    }

    /**
     *
     * @param message
     */
    public WegasNoResultException(String message) {
        super(message);
    }

    /**
     *
     * @param cause
     */
    public WegasNoResultException(final Throwable cause) {
        super(cause);
    }
}
