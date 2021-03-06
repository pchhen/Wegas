/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013, 2014, 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.persistence.variable;

import com.wegas.core.persistence.LabelledEntity;
import com.wegas.core.Helper;
import com.wegas.core.persistence.AbstractEntity;
import com.wegas.core.persistence.NamedEntity;
import com.wegas.core.persistence.game.GameModel;
import com.wegas.core.persistence.game.Player;
import com.wegas.core.persistence.variable.primitive.BooleanDescriptor;
import com.wegas.core.persistence.variable.primitive.NumberDescriptor;
import com.wegas.core.persistence.variable.primitive.StringDescriptor;
import com.wegas.core.persistence.variable.primitive.TextDescriptor;
import com.wegas.core.persistence.variable.scope.AbstractScope;
import com.wegas.core.persistence.variable.scope.TeamScope;
import com.wegas.core.persistence.variable.statemachine.StateMachineDescriptor;
import com.wegas.core.rest.util.Views;
import com.wegas.resourceManagement.persistence.ResourceDescriptor;
import com.wegas.resourceManagement.persistence.TaskDescriptor;
import com.wegas.mcq.persistence.ChoiceDescriptor;
import com.wegas.mcq.persistence.QuestionDescriptor;
import com.wegas.mcq.persistence.SingleResultChoiceDescriptor;
import com.wegas.messaging.persistence.InboxDescriptor;
import com.wegas.core.persistence.variable.primitive.ObjectDescriptor;
import com.wegas.reviewing.persistence.PeerReviewDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonView;
import com.wegas.core.exception.client.WegasErrorMessage;
import com.wegas.core.persistence.Broadcastable;
import com.wegas.core.persistence.game.Game;
import com.wegas.core.persistence.game.Team;
import com.wegas.core.persistence.variable.scope.GameModelScope;
import com.wegas.core.persistence.variable.scope.GameScope;
import com.wegas.core.persistence.variable.scope.PlayerScope;
import java.util.HashMap;
import java.util.Map;
import com.wegas.resourceManagement.persistence.BurndownDescriptor;
import org.eclipse.persistence.annotations.JoinFetch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @param <T>
 *
 * @author Francois-Xavier Aeberhard <fx@red-agent.com>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
//@EntityListeners({GmVariableDescriptorListener.class})
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"gamemodel_gamemodelid", "name"}) // Name has to be unique for the whole game model
// @UniqueConstraint(columnNames = {"variabledescriptor_id", "name"})           // Name has to be unique within a list
// @UniqueConstraint(columnNames = {"rootgamemodel_id", "name"})                // Names have to be unique at the base of a game model (root elements)
}, indexes = {
    @Index(columnList = "defaultinstance_variableinstance_id")
})
@NamedQuery(name = "findVariableDescriptorsByRootGameModelId", query = "SELECT DISTINCT vd FROM VariableDescriptor vd LEFT JOIN vd.gameModel AS gm WHERE gm.id = :gameModelId")
@JsonSubTypes(value = {
    @JsonSubTypes.Type(name = "ListDescriptor", value = ListDescriptor.class),
    @JsonSubTypes.Type(name = "StringDescriptor", value = StringDescriptor.class),
    @JsonSubTypes.Type(name = "TextDescriptor", value = TextDescriptor.class),
    @JsonSubTypes.Type(name = "BooleanDescriptor", value = BooleanDescriptor.class),
    @JsonSubTypes.Type(name = "NumberDescriptor", value = NumberDescriptor.class),
    @JsonSubTypes.Type(name = "InboxDescriptor", value = InboxDescriptor.class),
    @JsonSubTypes.Type(name = "FSMDescriptor", value = StateMachineDescriptor.class),
    @JsonSubTypes.Type(name = "ResourceDescriptor", value = ResourceDescriptor.class),
    @JsonSubTypes.Type(name = "TaskDescriptor", value = TaskDescriptor.class),
    @JsonSubTypes.Type(name = "QuestionDescriptor", value = QuestionDescriptor.class),
    @JsonSubTypes.Type(name = "ChoiceDescriptor", value = ChoiceDescriptor.class),
    @JsonSubTypes.Type(name = "SingleResultChoiceDescriptor", value = SingleResultChoiceDescriptor.class),
    @JsonSubTypes.Type(name = "ObjectDescriptor", value = ObjectDescriptor.class),
    @JsonSubTypes.Type(name = "PeerReviewDescriptor", value = PeerReviewDescriptor.class),
    @JsonSubTypes.Type(name = "BurndownDescriptor", value = BurndownDescriptor.class)
})
abstract public class VariableDescriptor<T extends VariableInstance> extends NamedEntity implements Searchable, LabelledEntity, Broadcastable {

    static final private org.slf4j.Logger logger = LoggerFactory.getLogger(VariableDescriptor.class);

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @Lob
    @JsonView(value = Views.EditorI.class)
    @Column(name = "Descriptor_comments")
    private String comments;
    /**
     * Here we cannot use type T, otherwise jpa won't handle the db ref
     * correctly
     */
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, optional = false)
    @JsonView(value = Views.EditorExtendedI.class)
    private VariableInstance defaultInstance;
    /**
     *
     */
    //@JsonBackReference
    @ManyToOne
    @JoinColumn
    private GameModel gameModel;
    /**
     *
     */
    @Id
    @Column(name = "variabledescriptor_id")
    @GeneratedValue
    @JsonView(Views.IndexI.class)
    private Long id;
    /**
     *
     */
    //@JsonView(Views.EditorI.class)
    private String label;
    /*
     * @OneToOne(cascade = CascadeType.ALL) @NotNull @JoinColumn(name
     * ="SCOPE_ID", unique = true, nullable = false, insertable = true,
     * updatable = true)
     */
    //@BatchFetch(BatchFetchType.JOIN)
    //@JsonManagedReference
    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true, optional = false)
    @JoinFetch
    @JsonView(value = Views.WithScopeI.class)
    private AbstractScope scope;
    /**
     * Title displayed in the for the player, should be removed from variable
     * descriptor and placed in the required entities (MCQQuestionDrescriptor,
     * TriggerDescriptor, aso)
     */
    @Column(name = "editorLabel")
    private String title;
    /**
     *
     */
    //@JsonView(Views.EditorExtendedI.class)
    @NotNull
    @Basic(optional = false)
    protected String name;

    /**
     *
     */
    //@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    //@JoinTable(joinColumns = {
    //    @JoinColumn(referencedColumnName = "variabledescriptor_id")},
    //        inverseJoinColumns = {
    //    @JoinColumn(referencedColumnName = "tag_id")})
    //@XmlTransient
    //private List<Tag> tags;
    /**
     *
     */
    public VariableDescriptor() {
    }

    /**
     *
     * @param name
     */
    public VariableDescriptor(String name) {
        this.name = name;
    }

    /**
     *
     * @param name
     * @param defaultInstance
     */
    public VariableDescriptor(String name, T defaultInstance) {
        this.name = name;
        this.defaultInstance = defaultInstance;
    }

    /**
     *
     * @param defaultInstance
     */
    public VariableDescriptor(T defaultInstance) {
        this.defaultInstance = defaultInstance;
    }

    /**
     *
     * @return
     */
    public String getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @return the defaultInstance
     */
    public T getDefaultInstance() {
        return (T) defaultInstance;
    }

    /**
     * @param defaultInstance the defaultValue to set
     */
    public void setDefaultInstance(T defaultInstance) {
        this.defaultInstance = defaultInstance;
    }

    /**
     *
     * @return
     */
    @JsonIgnore
    public GameModel getGameModel() {
        return this.gameModel;
    }

    /**
     *
     * @param gameModel
     */
    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    /**
     *
     * @return
     */
    @JsonIgnore
    public int getGameModelId() {
        return this.gameModel.getId().intValue();
    }

    /**
     *
     * @return
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     *
     * @param player
     *
     * @return
     */
    public T getInstance(Player player) {
        return (T) this.scope.getVariableInstance(player);
    }

    /**
     *
     * @return
     */
    @JsonIgnore
    public T getInstance() {
        return (T) this.getScope().getInstance();
    }

    /**
     *
     * @param defaultInstance
     * @param player
     *
     * @return
     */
    @JsonIgnore
    public T getInstance(Boolean defaultInstance, Player player) {
        if (defaultInstance) {
            return this.getDefaultInstance();
        } else {
            return this.getInstance(player);
        }
    }

    /**
     * @return the label
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the scope
     */
    public AbstractScope getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     *
     * @fixme here we cannot use managed references since this.class is
     * abstract.
     */
    //@JsonManagedReference
    public void setScope(AbstractScope scope) {
        this.scope = scope;
        if (scope != null) {
            scope.setVariableDescscriptor(this);
        }
    }

    /**
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @param a
     */
    @Override
    public void merge(AbstractEntity a) {
        try {
            super.merge(a);
            VariableDescriptor other = (VariableDescriptor) a;
            this.setName(other.getName());
            this.setLabel(other.getLabel());
            this.setTitle(other.getTitle());
            this.setComments(other.getComments());
            this.defaultInstance.merge(other.getDefaultInstance());
            if (other.getScope() != null) {
                System.out.println("This.Scope: " + this.scope);
                this.scope.setBroadcastScope(other.getScope().getBroadcastScope());
            }
        } catch (PersistenceException pe) {
            throw WegasErrorMessage.error("The name is already in use");
        }
        //this.scope.merge(vd.getScope());
    }

    /**
     *
     */
    @PrePersist
    public void prePersist() {
        if (this.getScope() == null) {
            this.setScope(new TeamScope());
        }
    }

    /**
     *
     * @param context allow to circumscribe the propagation within the given
     *                context. It may be an instance of GameModel, Game, Team,
     *                or Player
     */
    public void propagateDefaultInstance(Object context) {
        int sFlag = 0;
        if (scope instanceof GameModelScope) { // gms
            sFlag = 4;
        } else if (scope instanceof GameScope) { // gs
            sFlag = 3;
        } else if (scope instanceof TeamScope) { // ts
            sFlag = 2;
        } else if (scope instanceof PlayerScope) { // ps
            sFlag = 1;
        }

        if ((context == null) // no-context
                || (context instanceof GameModel) // gm ctx -> do not skip anything
                || (context instanceof Game && sFlag < 4) // g ctx -> skip gms
                || (context instanceof Team && sFlag < 3) // t ctx -> skip gms, gs
                || (context instanceof Player && sFlag < 2)) { // p ctx -> skip gms, gs, ts
            scope.propagateDefaultInstance(context);
        }
    }

    @Override
    public Map<String, List<AbstractEntity>> getEntities() {
        Map<String, List<AbstractEntity>> map = new HashMap<>();
        ArrayList<AbstractEntity> entities = new ArrayList<>();
        entities.add(this);
        //logger.error("CHANNEL TOKEN: " + Helper.getAudienceToken(this.getGameModel()));
        map.put(Helper.getAudienceToken(this.getGameModel()), entities);
        return map;
    }

    /**
     *
     * @param criterias
     *
     * @return
     */
    @Override
    public Boolean containsAll(final List<String> criterias) {
        return Helper.insensitiveContainsAll(this.getName(), criterias)
                || Helper.insensitiveContainsAll(this.getLabel(), criterias)
                || Helper.insensitiveContainsAll(this.getTitle(), criterias)
                || Helper.insensitiveContainsAll(this.getComments(), criterias);
    }

    @Override
    public Boolean contains(final String criteria) {
        return this.containsAll(new ArrayList<String>() {
            {
                add(criteria);
            }
        });
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "( " + getId() + ", " + this.getName() + ")";
    }
}
