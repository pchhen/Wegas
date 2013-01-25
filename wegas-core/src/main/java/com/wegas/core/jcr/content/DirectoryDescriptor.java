/*
 * Wegas.
 *
 * http://www.albasim.com/wegas/
 *
 * School of Business and Engineering Vaud, http://www.heig-vd.ch/
 * Media Engineering :: Information Technology Managment :: Comem
 *
 * Copyright (C) 2012
 */
package com.wegas.core.jcr.content;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Cyril Junod <cyril.junod at gmail.com>
 */
@XmlRootElement
public class DirectoryDescriptor extends AbstractContentDescriptor {

    /**
     * Directory mime-type
     */
    public static final String MIME_TYPE = "application/wfs-directory";

    /**
     *
     * @param absolutePath
     * @param contentConnector
     */
    public DirectoryDescriptor(String absolutePath, ContentConnector contentConnector) {
        super(absolutePath, contentConnector);
        this.mimeType = MIME_TYPE;
    }

    /**
     *
     * @param name
     * @param path
     * @param contentConnector
     */
    public DirectoryDescriptor(String name, String path, ContentConnector contentConnector) {
        super(name, path, contentConnector);
        this.mimeType = MIME_TYPE;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public boolean isRootDirectory() {
        return this.fileSystemAbsolutePath.equals("/");
    }

    /**
     *
     * @return
     */
    @JsonProperty("bytes")
    @Override
    public Long getBytes() {
        List<AbstractContentDescriptor> nodes = new ArrayList<>();
        try {
            nodes = this.list();
        } catch (RepositoryException ex) {
        }
        Long sum = new Long(0);
        for (AbstractContentDescriptor n : nodes) {
            sum += n.getBytes();
        }
        return sum;
    }

    /**
     *
     * @return
     * @throws RepositoryException
     */
    @XmlTransient
    public List<AbstractContentDescriptor> list() throws RepositoryException {
        NodeIterator nodeIterator = this.connector.listChildren(this.fileSystemAbsolutePath);
        List<AbstractContentDescriptor> files = new ArrayList<>();
        while (nodeIterator.hasNext()) {
            files.add(DescriptorFactory.getDescriptor(nodeIterator.nextNode(), this.connector));
        }
        return files;
    }
}
