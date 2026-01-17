package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.CreerCompte;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.exceptions.IllegalFormatException;

public class TestsCreerCompte {
    private CreerCompte action;
    private BanqueFacade facade;

    @Before
    public void setUp() {
        facade = mock(BanqueFacade.class);
        action = new CreerCompte(facade);
    }

    @Test
    public void testCreationCompteSuccess() throws Exception {
        // Créer un client avec un ID valide
        Client client = new Client("Doe", "John", "123 Street", true, "j.doe1", "password", "1234567890");
        action.setClient(client);
        action.setNumeroCompte("FR0123456789");
        
        // Configurer le mock pour retourner null (compte n'existe pas)
when(facade.getCompte("FR0123456789")).thenReturn(null);
        
        String res = action.creationCompte();
        assertEquals("SUCCESS", res);
    }



    @Test
    public void testCreationCompteNonUnique() throws Exception {
        // Créer un client avec un ID valide
        Client client = new Client("Doe", "John", "123 Street", true, "j.doe3", "password", "1234567892");
        action.setClient(client);
        action.setNumeroCompte("FR0111111111");
        
        // Configurer le mock pour lancer une TechnicalException
        doThrow(new TechnicalException("dup"))
            .when(facade).createAccount(eq("FR0111111111"), eq(client));
            
        String res = action.creationCompte();
        assertEquals("NONUNIQUEID", res);
    }
}
