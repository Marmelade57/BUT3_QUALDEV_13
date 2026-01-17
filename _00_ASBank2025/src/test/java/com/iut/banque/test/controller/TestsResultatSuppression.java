package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.ResultatSuppression;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Utilisateur;

public class TestsResultatSuppression {
    private ResultatSuppression action;
    private BanqueFacade facade;

    @Before
    public void setUp() throws Exception {
        facade = mock(BanqueFacade.class);
        action = new ResultatSuppression(facade);
    }

    @Test
    public void testExecuteWithMessage() throws Exception {
        action.setMessage("NONUNIQUEID");
        action.setResult(true);
        action.setCompteId("ACC1");
        String res = action.execute();
        assertNotNull(res);
    }

    @Test
    public void testSettersGetters() throws Exception {
        action.setMessage("SUCCESS");
        assertEquals("SUCCESS", action.getMessage());
    }
}

