/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.jcr.content;

import com.wegas.core.Helper;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Cyril Junod <cyril.junod at gmail.com>
 */
final class WFSConfig {

    /**
     * JNDI name for Repository lookup {@value #jndiRepo}
     */
    static final String jndiRepo = "jcr/jackrabbit";
    /**
     * WeGAS file system namespace prefix for use with XPATH
     * <b>{@value #WeGAS_FILE_SYSTEM_PREFIX}</b>
     */
    static final String WeGAS_FILE_SYSTEM_PREFIX = "wfs:";
    /**
     * WeGAS file system data property name <b>{@value #WFS_DATA}</b>
     */
    static final String WFS_DATA = WeGAS_FILE_SYSTEM_PREFIX + "data";
    /**
     * WeGAS file system mime-type property name <b>{@value #WFS_MIME_TYPE}</b>
     */
    static final String WFS_MIME_TYPE = WeGAS_FILE_SYSTEM_PREFIX + "contentType";
    /**
     * WeGAS file system last modified property name <b>{@value #WFS_DATA}</b>
     */
    static final String WFS_LAST_MODIFIED = WeGAS_FILE_SYSTEM_PREFIX + "lastModified";
    /**
     * WeGAS file system note property name <b>{@value #WFS_NOTE}</b>
     */
    static final String WFS_NOTE = WeGAS_FILE_SYSTEM_PREFIX + "note";
    /**
     * WeGAS file system description property name
     * <b>{@value #WFS_DESCRIPTION}</b>
     */
    static final String WFS_DESCRIPTION = WeGAS_FILE_SYSTEM_PREFIX + "description";
    /**
     * WeGAS file system private property name <b>{@value #WFS_PRIVATE}</b>
     */
    static final String WFS_PRIVATE = WeGAS_FILE_SYSTEM_PREFIX + "private";
    /**
     * File size limit in bytes
     */
    static final Long MAX_FILE_SIZE = Long.valueOf(Helper.getWegasProperty("jcr.file.maxsize"));
    /**
     * Repository size limit in bytes
     */
    static final Long MAX_REPO_SIZE = Long.valueOf(Helper.getWegasProperty("jcr.repository.maxsize"));
    /**
     * Custom namespaces registered with JCR.
     */
    static final Map<String, String> namespaces = new HashMap<String, String>() {
        {
            put("wfs", "http://wegas.albasim.ch/wfs/1.0");                         //WeGAS File System
        }
    };
}
