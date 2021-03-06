/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.messaging.persistence;

import com.wegas.core.persistence.AbstractEntity;
import com.wegas.core.persistence.variable.VariableInstance;
import com.wegas.core.rest.util.Views;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
//import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
@Entity
//@XmlType(name = "InboxInstance")
public class InboxInstance extends VariableInstance {

    /**
     *
     */
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(InboxInstance.class);
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @JsonView(Views.ExtendedI.class)
    @OneToMany(mappedBy = "inboxInstance", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @OrderBy("sentTime DESC, id")
    @JsonManagedReference("inbox-message")
    private List<Message> messages = new ArrayList<>();

    /**
     * @return the replies
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * @param messages
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
        for (Message r : this.messages) {
            r.setInboxInstance(this);
        }
    }

    /**
     *
     * @param message
     */
    public void addMessage(Message message) {
        this.messages.add(message);
        message.setInboxInstance(this);
    }

    @Override
    public void merge(AbstractEntity a) {
        InboxInstance other = (InboxInstance) a;
        this.messages.clear();
        for (Message m : other.getMessages()) {
            try {
                this.addMessage((Message) m.duplicate());
            } catch (IOException ex) {
                logger.error("Exception duplicating {}", m);
            }
        }
    }

    /**
     *
     * @param message
     */
    public void sendMessage(Message message) {
        this.addMessage(message);
    }

    /**
     *
     * @param from
     * @param subject
     * @param body
     * @return 
     */
    public Message sendMessage(String from, String subject, String body) {
        Message msg = new Message(from, subject, body);
        this.sendMessage(msg);
        return msg;
    }

    /**
     *
     * @param from
     * @param subject
     * @param body
     * @return 
     */
    public Message sendMessage(String from, String subject, String body, String date) {
        Message msg = new Message(from, subject, body, date);
        this.sendMessage(msg);
        return msg;
    }

    /**
    /**
     *
     * @param from
     * @param subject
     * @param body
     * @param attachements
     * @return 
     */
    public Message sendMessage(final String from, final String subject, final String body, final List<String> attachements) {
        final Message msg = new Message(from, subject, body, attachements);
        this.sendMessage(msg);
        return msg;
    }

    /**
     *
     * @param from
     * @param subject
     * @param body
     * @param date
     * @param attachements
     * @return 
     */
    public Message sendMessage(final String from, final String subject, final String body, final String date, final List<String> attachements) {
        final Message msg = new Message(from, subject, body, date, attachements);
        this.sendMessage(msg);
        return msg;
    }


    /**
     *
     * @return int unread message count
     */
    public int getUnreadCount() {
        int unread = 0;
        List<Message> listMessages = this.getMessages();
        for (Message m : listMessages) {
            if (m.getUnread()) {
                unread += 1;
            }
        }
        return unread;
    }

    /**
     *
     * @param count
     */
    public void setUnreadCount(int count) {
        // only used to explicitely ignore while serializing
    }

    /**
     *
     * @param subject
     * @return
     */
    public Message getMessageBySubject(String subject) {
        for (Message m: this.getMessages()) {
            if (m.getSubject().equals(subject)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "( " + getId() + ", msgs: " + this.getMessages().size() + " )";
    }
}
