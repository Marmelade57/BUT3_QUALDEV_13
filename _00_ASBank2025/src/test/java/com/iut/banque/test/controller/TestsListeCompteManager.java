package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.ListeCompteManager;
import com.iut.banque.facade.BanqueFacade;

public class TestsListeCompteManager {
    private ListeCompteManager action;
    private BanqueFacade facade;

    @Before
    public void setUp() {
        facade = mock(BanqueFacade.class);
        action = new ListeCompteManager(facade);
    }

    @Test
    public void testExecuteSuccess() {
        // Préparer une map de clients simulés et vérifier que getAllClients la retourne
        HashMap<String, com.iut.banque.modele.Client> map = new HashMap<>();
        com.iut.banque.modele.Client client = mock(com.iut.banque.modele.Client.class);
        map.put("c1", client);
        when(facade.getAllClients()).thenReturn(map);

        // Appel de la méthode réelle
        java.util.Map<String, com.iut.banque.modele.Client> result = action.getAllClients();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("c1"));
    }

    @Test
    public void testExecuteNoClient() {
        when(facade.getAllClients()).thenReturn(new HashMap<>());
        java.util.Map<String, com.iut.banque.modele.Client> result = action.getAllClients();
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
