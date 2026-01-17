package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.CreerUtilisateur;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.TechnicalException;

public class TestsCreerUtilisateur {
    private CreerUtilisateur action;
    private BanqueFacade facade;

    @Before
    public void setUp() {
        facade = mock(BanqueFacade.class);
        action = new CreerUtilisateur(facade);
    }

    @Test
    public void testCreationClientSuccess() throws Exception {
        action.setClient(true);
        // Utiliser un ID utilisateur valide
        action.setUserId("a.test5");
        action.setUserPwd("pwd12345");
        action.setNom("N");
        action.setPrenom("P");
        action.setAdresse("A");
        // Utiliser un numéro de client valide (10 chiffres)
        action.setNumClient("1234567890");
        String res = action.creationUtilisateur();
        assertEquals("SUCCESS", res);
    }

    @Test
    public void testCreationClientAlreadyExists() throws Exception {
        action.setClient(true);
        // Utiliser un ID utilisateur valide
        action.setUserId("a.test6");
        action.setUserPwd("pwd12345");
        action.setNom("N");
        action.setPrenom("P");
        action.setAdresse("A");
        // Utiliser un numéro de client valide (10 chiffres)
        action.setNumClient("1234567891");
        
        // Configurer le mock pour lancer une exception lors de la création
        doThrow(new IllegalOperationException("dup"))
            .when(facade).createClient(eq("a.test6"), eq("pwd12345"), eq("N"), eq("P"), eq("A"), anyBoolean(), eq("1234567891"));
        
        String res = action.creationUtilisateur();
        assertEquals("ERROR", res);
    }
}

