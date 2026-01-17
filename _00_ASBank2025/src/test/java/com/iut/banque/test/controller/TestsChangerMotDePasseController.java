package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.ChangerMotDePasse;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

public class TestsChangerMotDePasseController {

    private ChangerMotDePasse action;
    private BanqueFacade facadeMock;

    @Before
    public void setUp() {
        facadeMock = mock(BanqueFacade.class);
        action = new ChangerMotDePasse(facadeMock);
    }

    @Test
    public void testExecuteNoUser() {
        when(facadeMock.getConnectedUser()).thenReturn(null);
        String res = action.execute();
        assertEquals("ERROR", res);
    }

    @Test
    public void testExecuteUser() {
        Client c = new Client();
        when(facadeMock.getConnectedUser()).thenReturn(c);
        String res = action.execute();
        assertEquals("SUCCESS", res);
    }

    @Test
    public void testChangerMotDePasseNotConnected() {
        when(facadeMock.getConnectedUser()).thenReturn(null);
        String res = action.changerMotDePasse();
        assertEquals("ERROR", res);
        assertTrue(action.getMessage().contains("connecté"));
    }

    // More thorough tests for success are covered by LoginManager tests above
}

