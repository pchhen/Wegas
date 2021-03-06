/*
 * Wegas
 * http://wegas.albasim.ch
  
 * Copyright (c) 2014 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
package com.wegas.core.ejb;

import com.wegas.core.exception.client.WegasErrorMessage;
import com.wegas.core.exception.client.WegasRuntimeException;
import com.wegas.core.exception.client.WegasScriptException;
import com.wegas.core.persistence.game.Player;
import com.wegas.core.persistence.game.Script;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Script validation bean
 *
 * @author Cyril Junod <cyril.junod at gmail.com>
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class ScriptCheck {

    private static final Logger logger = LoggerFactory.getLogger(ScriptCheck.class);
    @Resource
    private UserTransaction utx;
    @EJB
    private RequestFacade requestFacade;
    @EJB
    private ScriptFacade scriptFacade;

    /**
     * Validate a given script, searching for errors in it.
     *
     * @param script the script to test
     * @param player the player the script should run for
     *
     * @return Exception the exception found in script or null if none occured
     */
    public WegasScriptException validate(Script script, Player player) {
        ScriptEngine engine = scriptFacade.instanciateEngine(player, script.getLanguage());

        return this.rollbackEval(engine, script, player.getId());

    }

    /**
     * Execute a script before rolling it back.
     *
     * @param engine   the engine on which the script should execute
     * @param script   the script
     * @param playerId the player's id, needed to set up Java env.
     *
     * @return Exception the exception found in script or null if none occured
     */
    private WegasScriptException rollbackEval(ScriptEngine engine, Script script, Long playerId) {

        try {
            utx.begin();
            requestFacade.setPlayer(playerId);
            try {
                engine.eval(script.getContent());
            } catch (ScriptException ex) {
                logger.debug("Script Error: {} \n {}", script.getContent(), ex.getMessage(), ex.getStackTrace());
                return new WegasScriptException(script.getContent(), ex.getLineNumber(), ex.getMessage());
            } catch (WegasRuntimeException ex) {                                // "Expected" Exception
                return null;
            } catch (RuntimeException ex) {                                     // Java exception
                logger.debug("Script Error: {} \n {}", script.getContent(), ex.getMessage(), ex.getStackTrace());
                return new WegasScriptException(script.getContent(), ex.getMessage());
            } finally {
                utx.rollback();
            }
        } catch (NotSupportedException | SystemException ex) {
            logger.error("Transaction failed", ex);
            throw WegasErrorMessage.error("I tried hardly, unfortunately without success");
        }

        return null;
    }
}
