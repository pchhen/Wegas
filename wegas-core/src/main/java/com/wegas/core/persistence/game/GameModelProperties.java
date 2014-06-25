/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.persistence.game;

import com.wegas.core.rest.util.Views;
import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 *
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
@Embeddable
@XmlRootElement
@XmlType(name = "")
public class GameModelProperties implements Serializable {

    /**
     *
     */
    private Boolean freeForAll = false;
    /**
     *
     */
    @JsonView({Views.ExtendedI.class})
    private String pagesUri = "";
    /**
     *
     */
    @JsonView({Views.ExtendedI.class})
    private String cssUri = "";
    /**
     *
     */
    @JsonView({Views.ExtendedI.class})
    private String websocket = "";
    /**
     *
     */
    @JsonView({Views.ExtendedI.class})
    private String scriptFiles = "";
    /**
     *
     */
    private String imageUri = "";
    /**
     *
     */
    private String iconUri = "";

    /**
     *
     */
    public GameModelProperties() {
    }

    /**
     *
     * @param other
     */
    public void merge(GameModelProperties other) {
        this.setFreeForAll(other.getFreeForAll());
        this.setPagesUri(other.getPagesUri());
        this.setIconUri(other.getIconUri());
        this.setImageUri(other.getImageUri());
        this.setWebsocket(other.getWebsocket());
        this.setCssUri(other.getCssUri());
        this.setScriptFiles(other.getScriptFiles());
    }

    /**
     * @return the freeForAll
     */
    public Boolean getFreeForAll() {
        return freeForAll;
    }

    /**
     * @param freeForAll the freeForAll to set
     */
    public void setFreeForAll(Boolean freeForAll) {
        this.freeForAll = freeForAll;
    }

    /**
     * @return the pagesUri
     */
    public String getPagesUri() {
        return pagesUri;
    }

    /**
     * @param pagesUri the pagesUri to set
     */
    public void setPagesUri(String pagesUri) {
        this.pagesUri = pagesUri;
    }

    /**
     * @return the cssUri
     */
    public String getCssUri() {
        return cssUri;
    }

    /**
     * @param cssUri the cssUri to set
     */
    public void setCssUri(String cssUri) {
        this.cssUri = cssUri;
    }

    /**
     * @return the websocket
     */
    public String getWebsocket() {
        return websocket;
    }

    /**
     * @param websocket the websocket to set
     */
    public void setWebsocket(String websocket) {
        this.websocket = websocket;
    }

    /**
     *
     * @return
     */
    public String getScriptFiles() {
        return scriptFiles;
    }

    /**
     *
     * @param scriptFiles
     */
    public void setScriptFiles(String scriptFiles) {
        this.scriptFiles = scriptFiles;
    }

    /**
     * @return the imageUri
     */
    public String getImageUri() {
        return imageUri;
    }

    /**
     * @param imageUri the imageUri to set
     */
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    /**
     * @return the iconUri
     */
    public String getIconUri() {
        return iconUri;
    }

    /**
     * @param iconUri the iconUri to set
     */
    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

}
